package collection;

import java.util.*;

public class TreeMap {
    public static void main(String[] args) {
        // 示例：操作 Deque
        Deque<Integer> deque = new ArrayDeque<>();
        deque.add(1);
        deque.add(2);
        deque.add(3);
        deque.add(4);

        System.out.println("原始 Deque: " + deque);
        moveToHead(deque, 3); // 将元素 3 移动到队头
        System.out.println("移动后的 Deque: " + deque);
    }

    // 将指定元素移动到队头的方法
    public static <T> void moveToHead(Deque<T> deque, T target) {
        if (deque.contains(target)) {
            deque.remove(target); // 移除目标元素
            deque.addFirst(target); // 插入到队头
        }
    }
}