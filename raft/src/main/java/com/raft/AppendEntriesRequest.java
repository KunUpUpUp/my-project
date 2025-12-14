package com.raft;

/**
 * 追加条目请求（用于心跳和日志复制）
 */
public class AppendEntriesRequest {
    /** Leader 的任期号 */
    private long term;
    
    /** Leader ID */
    private int leaderId;
    
    /** 紧接新日志条目之前的日志条目的索引 */
    private long prevLogIndex;
    
    /** prevLogIndex 条目的任期号 */
    private long prevLogTerm;
    
    /** 需要存储的日志条目（心跳时为空） */
    private String[] entries;
    
    /** Leader 已经提交的日志的最高索引值 */
    private long leaderCommit;

    public AppendEntriesRequest(long term, int leaderId, long prevLogIndex, 
                               long prevLogTerm, String[] entries, long leaderCommit) {
        this.term = term;
        this.leaderId = leaderId;
        this.prevLogIndex = prevLogIndex;
        this.prevLogTerm = prevLogTerm;
        this.entries = entries;
        this.leaderCommit = leaderCommit;
    }

    public long getTerm() {
        return term;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public long getPrevLogIndex() {
        return prevLogIndex;
    }

    public long getPrevLogTerm() {
        return prevLogTerm;
    }

    public String[] getEntries() {
        return entries;
    }

    public long getLeaderCommit() {
        return leaderCommit;
    }
}

