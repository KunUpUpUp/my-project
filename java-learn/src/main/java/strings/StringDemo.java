package strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringDemo {
    public static void main(String[] args) {
        // 从连接字符串中提取协议
        String connectionString = "jdbc:mysql://localhost:3306/mydb";
        Matcher matcher = Pattern.compile("(?<scheme>[\\w\\+:%]+).*").matcher(connectionString);
        if (matcher.matches()) {
            String scheme = matcher.group("scheme");
            System.out.println("协议方案: " + scheme); // 输出: jdbc:mysql
        }

    }
}
