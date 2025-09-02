package algorithm.datastruct;

public class MyLinkedList {
    public int val;
    //
    public Object next;
    // 自引用
//    public MyLinkedList next;

    public MyLinkedList(int val) {
        this.val = val;
    }

    public MyLinkedList(int val, Object next) {
        this.val = val;
        this.next = next;
    }
}
