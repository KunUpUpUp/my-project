package collection;

import java.util.Deque;
import java.util.LinkedList;

public class DequeDemo {


    public static void main(String[] args) {
        Deque<Integer> deque = new LinkedList<>();
        deque.addFirst(1);
        deque.addFirst(2);
        for (int i =0; i < deque.size(); i++) {
            if (deque.element() == 1) {

            }
        }
        System.out.println(deque.getFirst());

    }
}
