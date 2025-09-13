package collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapDemo {
    public static void main(String[] args) {
        Map<String, String> myMap = new HashMap<String, String>();
        myMap.put("1", "1");
        myMap.put("2", "2");
        myMap.put("3", "3");
        Set<String> keySet = myMap.keySet();
        keySet.remove("1");
        for (String s : keySet) {
            System.out.println(s);
        }
        for (Map.Entry<String, String> stringStringEntry : myMap.entrySet()) {
            System.out.println(stringStringEntry);
        }
    }
}
