#!/bin/bash

# 创建编译输出目录
mkdir -p target/classes

# 编译所有 Java 文件
echo "正在编译 Raft 选主算法..."
javac -d target/classes -encoding UTF-8 src/main/java/com/raft/*.java

if [ $? -eq 0 ]; then
    echo "编译成功！"
    echo ""
    echo "运行演示程序："
    echo "  cd target/classes"
    echo "  java com.raft.RaftElectionDemo"
else
    echo "编译失败！"
    exit 1
fi

