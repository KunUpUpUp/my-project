package algorithm.datastruct;

// 哈夫曼树节点类（核心数据结构）
public class HuffmanNode implements Comparable<HuffmanNode> {
    public char character;          // 字符（非叶节点为'\0'）
    public int frequency;           // 频率/权重
    public HuffmanNode left;
    public HuffmanNode right; // 左右子节点

    public HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    // 判断叶节点（无子节点）
    public boolean isLeaf() {
        return left == null && right == null;
    }

    // 实现Comparable接口用于优先队列排序
    @Override
    public int compareTo(HuffmanNode other) {
        return this.frequency - other.frequency;
    }
}