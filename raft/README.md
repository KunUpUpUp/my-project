# Raft 选主算法实现

这是一个完整的 Raft 分布式一致性算法的选主（Leader Election）实现。

## 核心特性

### 1. 节点状态
- **FOLLOWER（跟随者）**：被动接收 Leader 的心跳和日志
- **CANDIDATE（候选者）**：正在参与选举
- **LEADER（领导者）**：处理客户端请求，向其他节点发送心跳

### 2. 选举机制

#### 选举触发条件
- Follower 在选举超时时间内没有收到 Leader 的心跳
- Candidate 在选举超时时间内没有获得足够的选票

#### 选举过程
1. Follower 超时后转换为 Candidate
2. 增加自己的任期号
3. 投票给自己
4. 向所有其他节点发送投票请求（RequestVote RPC）
5. 如果获得大多数选票（超过半数），成为 Leader
6. 如果收到更高任期的消息，转换为 Follower

#### 投票规则
节点会投票给候选者，当且仅当：
- 候选者的任期号 >= 当前节点的任期号
- 当前节点还没有投票给其他候选者（或已经投票给该候选者）
- 候选者的日志至少和当前节点一样新（比较最后一条日志的任期号和索引）

### 3. 心跳机制

- Leader 定期（50ms）向所有 Follower 发送心跳（AppendEntries RPC）
- Follower 收到心跳后重置选举超时定时器
- 如果 Follower 长时间没有收到心跳，会触发新的选举

### 4. 选举超时

- 随机超时时间：150-300ms
- 随机化可以避免多个节点同时发起选举（split vote）

## 代码结构

```
raft/
├── src/main/java/com/raft/
│   ├── NodeState.java              # 节点状态枚举
│   ├── VoteRequest.java            # 投票请求消息
│   ├── VoteResponse.java           # 投票响应消息
│   ├── AppendEntriesRequest.java   # 追加条目请求（心跳）
│   ├── AppendEntriesResponse.java  # 追加条目响应
│   ├── LogEntry.java               # 日志条目
│   ├── RaftNode.java               # Raft 节点核心实现
│   ├── RpcClient.java              # RPC 客户端接口
│   ├── InMemoryRpcClient.java      # 内存 RPC 实现（用于演示）
│   └── RaftElectionDemo.java       # 演示程序
└── README.md
```

## 运行方式

### 编译
```bash
cd raft
javac -d target/classes src/main/java/com/raft/*.java
```

### 运行演示
```bash
cd target/classes
java com.raft.RaftElectionDemo
```

## 关键实现细节

### 1. 线程安全
- 使用 `ReentrantLock` 保护状态转换
- 使用 `AtomicLong` 管理任期号
- 使用 `CopyOnWriteArrayList` 存储日志

### 2. 定时器管理
- 使用 `ScheduledExecutorService` 管理选举超时和心跳
- 状态转换时正确取消和重新安排定时器

### 3. 大多数原则
- 选举需要获得超过半数的选票：`voteCount > allNodeIds.size() / 2`
- 这确保了同一任期内最多只有一个 Leader

## Raft 选主算法要点

### 为什么需要随机超时？
- 避免多个节点同时发起选举
- 如果多个节点同时成为 Candidate，可能都无法获得大多数选票
- 随机超时让某个节点更可能先发起选举并成功

### 为什么需要比较日志新旧？
- 确保 Leader 包含所有已提交的日志条目
- 通过比较最后一条日志的任期号和索引来判断日志新旧

### 为什么需要大多数选票？
- 防止出现多个 Leader（脑裂）
- 大多数原则确保同一任期内最多只有一个 Leader
- 即使发生网络分区，也最多只有一个分区能选出 Leader

## 扩展方向

1. **日志复制**：实现完整的日志复制机制
2. **持久化**：将持久化状态保存到磁盘
3. **网络通信**：实现真实的网络 RPC（如 gRPC、Netty）
4. **配置变更**：支持动态添加/删除节点
5. **快照**：实现日志压缩和快照机制

## 参考资料

- [Raft 论文](https://raft.github.io/raft.pdf)
- [Raft 可视化](https://raft.github.io/)

