package com.raft;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Raft 节点实现
 * 实现了 Raft 算法的核心选主逻辑
 */
public class RaftNode {
    // ========== 持久化状态（所有服务器上，在响应 RPC 请求之前更新到稳定存储） ==========
    /** 当前任期号，单调递增 */
    private final AtomicLong currentTerm = new AtomicLong(0);
    
    /** 在当前任期内收到选票的候选者 ID，如果没有投给任何候选者则为 null */
    private volatile Integer votedFor = null;
    
    /** 日志条目；每个条目包含状态机的命令，以及收到该条目时的任期号 */
    private final List<LogEntry> log = new CopyOnWriteArrayList<>();
    
    // ========== 易失性状态（所有服务器上） ==========
    /** 已知已提交的最高的日志条目索引（初始化为 0，单调递增） */
    private volatile long commitIndex = 0;
    
    /** 应用到状态机的最高日志条目索引（初始化为 0，单调递增） */
    private volatile long lastApplied = 0;
    
    // ========== Leader 的易失性状态（选举后重新初始化） ==========
    /** 对于每个服务器，发送到该服务器的下一个日志条目的索引（初始化为 Leader 最后一条日志的索引 + 1） */
    private final Map<Integer, Long> nextIndex = new ConcurrentHashMap<>();
    
    /** 对于每个服务器，已知的该服务器复制的最高日志条目的索引（初始化为 0，单调递增） */
    private final Map<Integer, Long> matchIndex = new ConcurrentHashMap<>();
    
    // ========== 节点配置 ==========
    /** 节点 ID */
    private final int nodeId;
    
    /** 所有节点的 ID 列表 */
    private final List<Integer> allNodeIds;
    
    /** 当前节点状态 */
    private volatile NodeState state = NodeState.FOLLOWER;
    
    /** 当前 Leader 的 ID */
    private volatile Integer leaderId = null;
    
    /** 选举超时时间（毫秒），随机范围 */
    private static final int ELECTION_TIMEOUT_MIN = 150;
    private static final int ELECTION_TIMEOUT_MAX = 300;
    
    /** 心跳间隔（毫秒） */
    private static final int HEARTBEAT_INTERVAL = 50;
    
    /** 选举超时定时器 */
    private ScheduledFuture<?> electionTimer;
    
    /** 心跳定时器（仅 Leader 使用） */
    private ScheduledFuture<?> heartbeatTimer;
    
    /** 线程池 */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    /** 随机数生成器 */
    private final Random random = new Random();
    
    /** RPC 客户端接口 */
    private final RpcClient rpcClient;
    
    /** 锁，用于保护状态转换 */
    private final ReentrantLock lock = new ReentrantLock();
    
    public RaftNode(int nodeId, List<Integer> allNodeIds, RpcClient rpcClient) {
        this.nodeId = nodeId;
        this.allNodeIds = allNodeIds;
        this.rpcClient = rpcClient;
        // 初始化日志，索引从 1 开始（索引 0 是占位符）
        this.log.add(new LogEntry(0, null));
    }
    
    /**
     * 启动节点
     */
    public void start() {
        System.out.println(String.format("[节点 %d] 启动，初始状态: FOLLOWER", nodeId));
        becomeFollower(0, null);
    }
    
    /**
     * 停止节点
     */
    public void stop() {
        if (electionTimer != null) {
            electionTimer.cancel(false);
        }
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel(false);
        }
        scheduler.shutdown();
    }
    
    /**
     * 转换为 Follower 状态
     */
    private void becomeFollower(long term, Integer leaderId) {
        lock.lock();
        try {
            if (term > currentTerm.get()) {
                currentTerm.set(term);
                votedFor = null;
            }
            this.state = NodeState.FOLLOWER;
            this.leaderId = leaderId;
            
            // 取消之前的定时器
            cancelTimers();
            
            // 启动选举超时定时器
            scheduleElectionTimeout();
            
            System.out.println(String.format("[节点 %d] 转换为 FOLLOWER，任期: %d, Leader: %s", 
                nodeId, currentTerm.get(), leaderId));
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 转换为 Candidate 状态并开始选举
     */
    private void becomeCandidate() {
        lock.lock();
        try {
            // 增加任期号
            long newTerm = currentTerm.incrementAndGet();
            state = NodeState.CANDIDATE;
            votedFor = nodeId; // 投票给自己
            leaderId = null;
            
            // 取消之前的定时器
            cancelTimers();
            
            System.out.println(String.format("[节点 %d] 转换为 CANDIDATE，开始选举，任期: %d", 
                nodeId, newTerm));
            
            // 启动选举超时定时器
            scheduleElectionTimeout();
            
            // 发起选举
            startElection(newTerm);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 转换为 Leader 状态
     */
    private void becomeLeader() {
        lock.lock();
        try {
            state = NodeState.LEADER;
            leaderId = nodeId;
            
            // 取消选举超时定时器
            if (electionTimer != null) {
                electionTimer.cancel(false);
            }
            
            // 初始化 Leader 状态
            long lastLogIndex = log.size() - 1;
            for (int nodeId : allNodeIds) {
                if (nodeId != this.nodeId) {
                    nextIndex.put(nodeId, lastLogIndex + 1);
                    matchIndex.put(nodeId, 0L);
                }
            }
            
            System.out.println(String.format("[节点 %d] 转换为 LEADER，任期: %d", 
                nodeId, currentTerm.get()));
            
            // 立即发送一次心跳
            sendHeartbeat();
            
            // 启动心跳定时器
            heartbeatTimer = scheduler.scheduleAtFixedRate(
                this::sendHeartbeat,
                HEARTBEAT_INTERVAL,
                HEARTBEAT_INTERVAL,
                TimeUnit.MILLISECONDS
            );
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 安排选举超时
     */
    private void scheduleElectionTimeout() {
        int timeout = ELECTION_TIMEOUT_MIN + 
                     random.nextInt(ELECTION_TIMEOUT_MAX - ELECTION_TIMEOUT_MIN);
        
        electionTimer = scheduler.schedule(() -> {
            lock.lock();
            try {
                // 如果仍然是 Follower 或 Candidate，说明超时了
                if (state == NodeState.FOLLOWER || state == NodeState.CANDIDATE) {
                    if (state == NodeState.FOLLOWER) {
                        becomeCandidate();
                    } else {
                        // Candidate 超时，重新选举
                        System.out.println(String.format("[节点 %d] 选举超时，重新开始选举", nodeId));
                        becomeCandidate();
                    }
                }
            } finally {
                lock.unlock();
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 取消所有定时器
     */
    private void cancelTimers() {
        if (electionTimer != null) {
            electionTimer.cancel(false);
            electionTimer = null;
        }
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel(false);
            heartbeatTimer = null;
        }
    }
    
    /**
     * 开始选举
     */
    private void startElection(long term) {
        // 获取最后一条日志的信息
        long lastLogIndex = log.size() - 1;
        long lastLogTerm = lastLogIndex > 0 ? log.get((int)lastLogIndex).getTerm() : 0;
        
        // 创建投票请求
        VoteRequest request = new VoteRequest(term, nodeId, lastLogIndex, lastLogTerm);
        
        // 统计投票
        final AtomicLong votes = new AtomicLong(1); // 自己的一票
        final CountDownLatch latch = new CountDownLatch(allNodeIds.size() - 1);
        
        // 向所有其他节点发送投票请求
        for (int otherNodeId : allNodeIds) {
            if (otherNodeId == nodeId) {
                continue;
            }
            
            scheduler.submit(() -> {
                try {
                    VoteResponse response = rpcClient.requestVote(otherNodeId, request);
                    
                    lock.lock();
                    try {
                        // 如果收到更大的任期号，转换为 Follower
                        if (response.getTerm() > currentTerm.get()) {
                            becomeFollower(response.getTerm(), null);
                            return;
                        }
                        
                        // 如果投票成功且仍然是 Candidate
                        if (response.isVoteGranted() && state == NodeState.CANDIDATE && 
                            currentTerm.get() == term) {
                            long voteCount = votes.incrementAndGet();
                            
                            // 如果获得大多数选票，成为 Leader
                            if (voteCount > allNodeIds.size() / 2) {
                                becomeLeader();
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                } catch (Exception e) {
                    System.err.println(String.format("[节点 %d] 向节点 %d 请求投票失败: %s", 
                        nodeId, otherNodeId, e.getMessage()));
                } finally {
                    latch.countDown();
                }
            });
        }
    }
    
    /**
     * 发送心跳（仅 Leader 调用）
     */
    private void sendHeartbeat() {
        if (state != NodeState.LEADER) {
            return;
        }
        
        long term = currentTerm.get();
        long prevLogIndex = log.size() - 1;
        long prevLogTerm = prevLogIndex > 0 ? log.get((int)prevLogIndex).getTerm() : 0;
        
        AppendEntriesRequest request = new AppendEntriesRequest(
            term, nodeId, prevLogIndex, prevLogTerm, null, commitIndex
        );
        
        // 向所有 Follower 发送心跳
        for (int followerId : allNodeIds) {
            if (followerId == nodeId) {
                continue;
            }
            
            scheduler.submit(() -> {
                try {
                    AppendEntriesResponse response = rpcClient.appendEntries(followerId, request);
                    
                    lock.lock();
                    try {
                        // 如果收到更大的任期号，转换为 Follower
                        if (response.getTerm() > currentTerm.get()) {
                            becomeFollower(response.getTerm(), null);
                        }
                    } finally {
                        lock.unlock();
                    }
                } catch (Exception e) {
                    // 忽略网络错误，下次心跳会重试
                }
            });
        }
    }
    
    // ========== RPC 处理函数 ==========
    
    /**
     * 处理投票请求
     */
    public VoteResponse handleVoteRequest(VoteRequest request) {
        lock.lock();
        try {
            // 如果请求的任期号小于当前任期号，拒绝投票
            if (request.getTerm() < currentTerm.get()) {
                return new VoteResponse(currentTerm.get(), false);
            }
            
            // 如果请求的任期号大于当前任期号，更新任期并转换为 Follower
            if (request.getTerm() > currentTerm.get()) {
                becomeFollower(request.getTerm(), null);
            }
            
            // 检查是否已经投票给其他候选者
            boolean canVote = (votedFor == null || votedFor == request.getCandidateId());
            
            // 检查候选者的日志是否至少和当前节点一样新
            long lastLogIndex = log.size() - 1;
            long lastLogTerm = lastLogIndex > 0 ? log.get((int)lastLogIndex).getTerm() : 0;
            boolean logUpToDate = (request.getLastLogTerm() > lastLogTerm) ||
                                  (request.getLastLogTerm() == lastLogTerm && 
                                   request.getLastLogIndex() >= lastLogIndex);
            
            if (canVote && logUpToDate) {
                votedFor = request.getCandidateId();
                return new VoteResponse(currentTerm.get(), true);
            } else {
                return new VoteResponse(currentTerm.get(), false);
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 处理追加条目请求（心跳或日志复制）
     */
    public AppendEntriesResponse handleAppendEntries(AppendEntriesRequest request) {
        lock.lock();
        try {
            // 如果请求的任期号小于当前任期号，拒绝
            if (request.getTerm() < currentTerm.get()) {
                return new AppendEntriesResponse(currentTerm.get(), false);
            }
            
            // 如果请求的任期号大于等于当前任期号，转换为 Follower
            if (request.getTerm() >= currentTerm.get()) {
                becomeFollower(request.getTerm(), request.getLeaderId());
            }
            
            // 重置选举超时（收到 Leader 的心跳）
            if (state == NodeState.FOLLOWER) {
                cancelTimers();
                scheduleElectionTimeout();
            }
            
            // 简化版：心跳总是成功（实际实现需要检查日志一致性）
            return new AppendEntriesResponse(currentTerm.get(), true);
        } finally {
            lock.unlock();
        }
    }
    
    // ========== Getter 方法 ==========
    
    public int getNodeId() {
        return nodeId;
    }
    
    public NodeState getState() {
        return state;
    }
    
    public long getCurrentTerm() {
        return currentTerm.get();
    }
    
    public Integer getLeaderId() {
        return leaderId;
    }
}

