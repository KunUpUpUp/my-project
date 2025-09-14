package algorithm.project;

import java.io.IOException;
import java.util.Scanner;

public class LRUTest {
    public static void main(String[] args) throws IOException {
        LRUCache cache = new LRUCache(1);
        cache.put(2, 1);
        System.out.println(cache.get(2));
        cache.put(3, 2);
        System.out.println(cache.get(2));
        System.out.println(cache.get(3));
        System.in.read();
    }
}
