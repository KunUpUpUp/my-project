package com.raft;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Raft 选主算法演示程序
 */
public class RaftElectionDemo {
    public static void main(String[] args) throws InterruptedException {
        // 创建 5 个节点的集群
        List<Integer> nodeIds = Arrays.asList(1, 2, 3, 4, 5);
        
        // 创建共享的 RPC 客户端
        InMemoryRpcClient rpcClient = new InMemoryRpcClient();
        
        // 创建所有节点
        List<RaftNode> nodes = new ArrayList<>();
        for (int nodeId : nodeIds) {
            RaftNode node = new RaftNode(nodeId, nodeIds, rpcClient);
            rpcClient.registerNode(node);
            nodes.add(node);
        }
        
        // 启动所有节点
        System.out.println("========== 启动 Raft 集群 ==========");
        for (RaftNode node : nodes) {
            node.start();
        }
        
        // 等待选举完成
        Thread.sleep(2000);
        
        // 打印集群状态
        printClusterStatus(nodes);
        
        // 模拟 Leader 宕机
        Thread.sleep(3000);
        System.out.println("\n========== 模拟 Leader 宕机 ==========");
        RaftNode leader = findLeader(nodes);
        if (leader != null) {
            System.out.println(String.format("节点 %d (Leader) 宕机", leader.getNodeId()));
            leader.stop();
            rpcClient.unregisterNode(leader.getNodeId());
        }
        
        // 等待重新选举
        Thread.sleep(2000);
        printClusterStatus(nodes);
        
        // 运行一段时间后停止
        Thread.sleep(5000);
        System.out.println("\n========== 停止集群 ==========");
        for (RaftNode node : nodes) {
            if (node.getState() != null) {
                node.stop();
            }
        }
    }
    
    private static void printClusterStatus(List<RaftNode> nodes) {
        System.out.println("\n========== 集群状态 ==========");
        for (RaftNode node : nodes) {
            if (node.getState() == null) {
                continue;
            }
            String leaderInfo = node.getLeaderId() != null ? 
                String.format("Leader: %d", node.getLeaderId()) : "无 Leader";
            System.out.println(String.format(
                "节点 %d: 状态=%s, 任期=%d, %s",
                node.getNodeId(),
                node.getState(),
                node.getCurrentTerm(),
                leaderInfo
            ));
        }
        
        RaftNode leader = findLeader(nodes);
        if (leader != null) {
            System.out.println(String.format("\n✓ 当前 Leader: 节点 %d (任期 %d)", 
                leader.getNodeId(), leader.getCurrentTerm()));
        } else {
            System.out.println("\n✗ 当前无 Leader");
        }
    }
    
    private static RaftNode findLeader(List<RaftNode> nodes) {
        for (RaftNode node : nodes) {
            if (node.getState() == NodeState.LEADER) {
                return node;
            }
        }
        return null;
    }
}

