package com.raft;

/**
 * 日志条目
 */
public class LogEntry {
    /** 日志条目的任期号 */
    private long term;
    
    /** 日志内容 */
    private String command;

    public LogEntry(long term, String command) {
        this.term = term;
        this.command = command;
    }

    public long getTerm() {
        return term;
    }

    public String getCommand() {
        return command;
    }
}

