import algorithm.datastruct.HuffmanNode;

import java.util.Map;

import static algorithm.tree.HuffmanCoder.*;

public class HuffmanTest {
    // 示例用法
    public static void main(String[] args) {
        String text = "this is an example for huffman encoding";
//        String text = "AAAAAABBCDDEEEEEF";

        // 构建哈夫曼树
        HuffmanNode root = buildHuffmanTree(text);

        // 生成编码表
        Map<Character, String> codeMap = generateCodes(root);
        System.out.println("【哈夫曼编码表】");
        codeMap.forEach((k, v) -> System.out.printf("'%c': %s\n", k, v));

        // 编码与解码
        String encoded = encode(text, codeMap);
        String decoded = decode(encoded, root);

        // 输出结果
        System.out.println("\n【原始文本】: " + text);
        System.out.println("【编码结果】: " + encoded);
        System.out.println("【解码验证】: " + (text.equals(decoded) ? "✓" : "✗"));
        System.out.printf("【压缩率】: %.2f%%\n", calculateCompressionRatio(text, encoded));
    }
}
