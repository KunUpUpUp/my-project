package com.raft;

/**
 * 投票响应消息
 */
public class VoteResponse {
    /** 当前任期号，用于候选者更新自己 */
    private long term;
    
    /** 是否投票给候选者 */
    private boolean voteGranted;

    public VoteResponse(long term, boolean voteGranted) {
        this.term = term;
        this.voteGranted = voteGranted;
    }

    public long getTerm() {
        return term;
    }

    public boolean isVoteGranted() {
        return voteGranted;
    }
}

