package test;

import java.util.Random;

public class ArrayTest {
    private static String vm = "http://asdas,http:asdasd";
    private static String[] urls;

    public static void main(String[] args) {
        urls = vm.split(",");
        Random random = new Random();
        int num = random.nextInt(1000000) % urls.length;
        String url = urls[num];
        System.out.println(url);
    }
}