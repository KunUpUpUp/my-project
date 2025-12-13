package com.seasugar.spi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

public class LearnSPI {
    public static void main(String[] args) throws Exception {
//        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "root");
//        System.out.println(connection);
        // 查找所有匹配的资源，包括：
        // 1. 当前JAR中的资源
        // 2. 依赖JAR中的资源
        // 3. 类路径中的资源resource
        Enumeration<URL> myResources = LearnSPI.class.getClassLoader().getResources("META-INF/services/com.seasugar.spi.Demo");
        Enumeration<URL> jarResources = LearnSPI.class.getClassLoader().getResources("META-INF/services/java.sql.Driver");
        while (myResources.hasMoreElements()) {
            URL url = myResources.nextElement();
            System.out.println("找到SPI配置文件: " + url.getPath());

            // 读取文件内容
            try (InputStream inputStream = url.openStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                System.out.println("文件内容:");
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                System.out.println("读取文件内容失败: " + e.getMessage());
            }
            System.out.println("---");
        }
        while (jarResources.hasMoreElements()) {
            URL url = jarResources.nextElement();
            System.out.println("找到SPI配置文件: " + url.getPath());

            // 读取文件内容
            try (InputStream inputStream = url.openStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                System.out.println("文件内容:");
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                System.out.println("读取文件内容失败: " + e.getMessage());
            }
            System.out.println("---");
        }
    }
}
