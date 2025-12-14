#!/bin/bash

# 切换到编译输出目录
cd target/classes

# 运行演示程序
echo "启动 Raft 选主算法演示..."
java com.raft.RaftElectionDemo

