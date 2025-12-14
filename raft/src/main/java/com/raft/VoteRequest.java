package com.raft;

/**
 * 投票请求消息
 */
public class VoteRequest {
    /** 候选者的任期号 */
    private long term;
    
    /** 候选者 ID */
    private int candidateId;
    
    /** 候选者最后一条日志条目的索引 */
    private long lastLogIndex;
    
    /** 候选者最后一条日志条目的任期号 */
    private long lastLogTerm;

    public VoteRequest(long term, int candidateId, long lastLogIndex, long lastLogTerm) {
        this.term = term;
        this.candidateId = candidateId;
        this.lastLogIndex = lastLogIndex;
        this.lastLogTerm = lastLogTerm;
    }

    public long getTerm() {
        return term;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public long getLastLogTerm() {
        return lastLogTerm;
    }
}

