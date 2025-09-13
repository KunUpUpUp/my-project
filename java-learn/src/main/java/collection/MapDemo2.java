package collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapDemo2 {
    public static void main(String[] args) {
        Map<String, String> registerValue = new HashMap<>();
        registerValue.put("1", "1");
        registerValue.put("2", "22");
        registerValue.put("3", "33");

        Map<String, String> ruleMap = new HashMap<>();
        ruleMap.put("1", "1");
        ruleMap.put("2", "2");
        ruleMap.put("3", "3");

        Iterator<Map.Entry<String, String>> registerIterator = registerValue.entrySet().iterator();
        while (registerIterator.hasNext()) {
            Map.Entry<String,String> register = registerIterator.next();
            if (!ruleMap.containsKey(register.getValue())) {
                registerIterator.remove();
            }
        }

        for (Map.Entry<String, String> stringStringEntry : registerValue.entrySet()) {
            System.out.println(stringStringEntry);
        }
    }
}
