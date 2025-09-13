package algorithm.project;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {

    // LRU其实就是想让你手写一个LinkedHashMap
    class DLinkedNode {
        private int key;
        private int val;
        private DLinkedNode pre;
        private DLinkedNode next;

        public DLinkedNode() {
        }

        public DLinkedNode(int key, int val) {
            this.key = key;
            this.val = val;
        }
    }

    private int capacity;
    private Map<Integer, DLinkedNode> cache = new HashMap<>();
    private int size;
    private DLinkedNode head, last;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head = new DLinkedNode();
        last = new DLinkedNode();
        head.next = last;
        last.pre = head;
    }

    public int get(int key) {
        if (cache.containsKey(key)) {
            moveToHead(cache.get(key));
            return cache.get(key).val;
        }
        return -1;
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            moveToHead(cache.get(key));
            cache.get(key).val = value;
            return;
        }

        if (size == capacity) {
            cache.remove(last.pre.key);
            removeLast();
        } else {
            size++;
        }
        DLinkedNode node = new DLinkedNode(key, value);
        addToHead(node);
        cache.put(key, node);
    }

    private void addToHead(DLinkedNode node) {
        node.pre = head;
        node.next = head.next;
        head.next.pre = node;
        head.next = node;
    }

    private void moveToHead(DLinkedNode node) {
        remove(node);
        addToHead(node);
    }

    private void remove(DLinkedNode node) {
        // 无需更改node的指向，没人指向node后自然会被gc
        node.pre.next = node.next;
        node.next.pre = node.pre;
    }

    private void removeLast() {
        remove(last.pre);
    }

}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */