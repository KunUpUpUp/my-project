package algorithm.tree;

import algorithm.datastruct.HuffmanNode;

import java.util.*;
import java.util.stream.Collectors;


public class HuffmanCoder {
    // 1. 构建哈夫曼树（核心算法）[2,4,6](@ref)
    public static HuffmanNode buildHuffmanTree(String text) {
        // 统计字符频率
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // 初始化优先队列（最小堆）
        PriorityQueue<HuffmanNode> minHeap = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            minHeap.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        // 合并节点直至形成完整树
        while (minHeap.size() > 1) {
            HuffmanNode left = minHeap.poll();
            HuffmanNode right = minHeap.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            minHeap.add(parent);
        }
        return minHeap.poll(); // 返回根节点
    }

    // 2. 生成编码表（DFS遍历）[4](@ref)
    public static Map<Character, String> generateCodes(HuffmanNode root) {
        Map<Character, String> codeMap = new HashMap<>();
        traverseTree(root, "", codeMap);
        return codeMap;
    }

    private static void traverseTree(HuffmanNode node, String code, Map<Character, String> codeMap) {
        if (node == null) return;

        if (node.isLeaf()) {
            codeMap.put(node.character, code.isEmpty() ? "0" : code); // 处理单字符情况
        }
        traverseTree(node.left, code + "0", codeMap);
        traverseTree(node.right, code + "1", codeMap);
    }

    // 3. 编码文本 → 二进制字符串[2](@ref)
    public static String encode(String text, Map<Character, String> codeMap) {
        return text.chars()
                .mapToObj(c -> (char) c)
                .map(codeMap::get)
                .collect(Collectors.joining());
    }

    // 4. 解码二进制字符串 → 原始文本[4](@ref)
    public static String decode(String encodedStr, HuffmanNode root) {
        if (root == null) return "";

        StringBuilder decoded = new StringBuilder();
        HuffmanNode current = root;

        for (char bit : encodedStr.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;
            if (current.isLeaf()) {
                decoded.append(current.character);
                current = root; // 重置到根节点
            }
        }
        return decoded.toString();
    }

    // 5. 计算压缩率（对比ASCII）[4](@ref)
    public static double calculateCompressionRatio(String original, String encoded) {
        double originalBits = original.length() * 8.0;
        double encodedBits = encoded.length();
        return encodedBits / originalBits * 100;
    }
}
