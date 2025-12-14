package com.raft;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存中的 RPC 客户端实现
 * 用于单机模拟多节点环境
 */
public class InMemoryRpcClient implements RpcClient {
    /** 节点 ID 到 RaftNode 的映射 */
    private final Map<Integer, RaftNode> nodes = new ConcurrentHashMap<>();
    
    /**
     * 注册节点
     */
    public void registerNode(RaftNode node) {
        nodes.put(node.getNodeId(), node);
    }
    
    /**
     * 注销节点
     */
    public void unregisterNode(int nodeId) {
        nodes.remove(nodeId);
    }
    
    @Override
    public VoteResponse requestVote(int nodeId, VoteRequest request) {
        RaftNode node = nodes.get(nodeId);
        if (node == null) {
            throw new RuntimeException("节点 " + nodeId + " 不存在");
        }
        return node.handleVoteRequest(request);
    }
    
    @Override
    public AppendEntriesResponse appendEntries(int nodeId, AppendEntriesRequest request) {
        RaftNode node = nodes.get(nodeId);
        if (node == null) {
            throw new RuntimeException("节点 " + nodeId + " 不存在");
        }
        return node.handleAppendEntries(request);
    }
}

