package com.raft;

/**
 * RPC 客户端接口
 * 用于节点之间的通信
 */
public interface RpcClient {
    /**
     * 发送投票请求
     */
    VoteResponse requestVote(int nodeId, VoteRequest request);
    
    /**
     * 发送追加条目请求（心跳或日志复制）
     */
    AppendEntriesResponse appendEntries(int nodeId, AppendEntriesRequest request);
}

