package collection;

import java.util.*;

public class TreeMapDemo {
    public static void main(String[] args) {
        Map<Integer, Integer> map = new TreeMap<>();
        map.put(2, 2);
        map.put(-6, -5);
        map.put(50, 2);
        map.put(3, 500);

        // 按值排序并插入到新的 TreeMap
        Map<Integer, Integer> sortedByValue = sortByValue(map);

        // 打印排序后的结果
        for (Map.Entry<Integer, Integer> entry : sortedByValue.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    // 按值排序的方法
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}