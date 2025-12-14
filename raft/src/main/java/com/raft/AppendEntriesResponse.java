package com.raft;

/**
 * 追加条目响应
 */
public class AppendEntriesResponse {
    /** 当前任期号，用于 Leader 更新自己 */
    private long term;
    
    /** 如果 Follower 包含匹配 prevLogIndex 和 prevLogTerm 的条目则为 true */
    private boolean success;

    public AppendEntriesResponse(long term, boolean success) {
        this.term = term;
        this.success = success;
    }

    public long getTerm() {
        return term;
    }

    public boolean isSuccess() {
        return success;
    }
}

