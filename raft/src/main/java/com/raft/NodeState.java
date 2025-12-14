package com.raft;

/**
 * Raft 节点状态枚举
 */
public enum NodeState {
    /**
     * 跟随者状态：被动接收 Leader 的心跳和日志
     */
    FOLLOWER,
    
    /**
     * 候选者状态：正在参与选举
     */
    CANDIDATE,
    
    /**
     * 领导者状态：处理客户端请求，向其他节点发送心跳
     */
    LEADER
}

