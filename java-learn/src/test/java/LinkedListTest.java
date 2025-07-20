import algorithm.datastruct.MyLinkedList;

public class LinkedListTest {
    public static void main(String[] args) {
        MyLinkedList list = new MyLinkedList(1, new MyLinkedList(2, new MyLinkedList(3, new MyLinkedList(4, new MyLinkedList(5, new MyLinkedList(6, new MyLinkedList(7, new MyLinkedList(8, new MyLinkedList(9, new MyLinkedList(10))))))))));
        while (list != null) {
            System.out.println(list.val);
            list = (MyLinkedList) list.next;
        }
    }
}
