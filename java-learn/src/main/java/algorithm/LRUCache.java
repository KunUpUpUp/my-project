package algorithm;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    private int capacity;
    private Map<Integer, Integer> map = new HashMap<>();
    private ListNode dummy = new ListNode();
    private ListNode head, last;
    int size;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        dummy.val = -1;
    }

    public int get(int key) {
        if (map.containsKey(key)) {
            ListNode p = head;
            int count = 0;
            while (count < size) {
                count++;
                if (p.val == key) {
                    if (size == count) {
                        // 最后一个
                        last = p.pre;
                    }
                    p.pre.next = p.next;
                    p.pre = dummy;
                    p.next  = dummy.next;
                    dummy.pre = last;
                    dummy.next = p;
                    head = p;
                    print(dummy);
                    return map.get(key);
                }
                p = p.next;
            }
        }
        print(dummy);
        return -1;
    }

    public void put(int key, int value) {
        // 存在就放头部更新
        if (map.containsKey(key)) {
            map.put(key, value);
            ListNode p = head;
            int count = 0;
            while (count < size) {
                count++;
                if (p.val == key) {
                    if (size == count) {
                        // 原来是最后一个
                        last = p.pre;
                    }
                    p.pre.next = p.next;
                    p.pre = dummy;
                    p.next  = dummy.next;
                    dummy.next = p;
                    head = p;
                    return;
                }
                p = p.next;
            }
        }

        if (size == capacity) {
            // 头插并移除最后一个
            map.remove(last.val);
            last.pre.next = last.next;
            last.next = null;
            last = last.pre;
        }

        ListNode next =  new ListNode();
        next.val = key;
        if (size == 0) {
            // 插入第一个
            dummy.pre = next;
            last = next;
        }

        next.pre = dummy;
        next.next = dummy.next;
        dummy.next = next;
        next.next.pre = next;
        head = next;
        map.put(key, value);
        if (size < capacity) {
            size++;
        }
        print(dummy);
    }

    class ListNode {
        private int val;
        private ListNode pre;
        private ListNode next;
    }

    private void print(ListNode p) {
        int count = 0;
        while (count <= size) {
            count++;
            System.out.print(p.val + "->");
            p = p.next;
        }
        System.out.println();
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */