package com.seasugar.registry.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static sun.awt.FontConfiguration.loadProperties;

public class PropertiesUtils {
    private final static String PROPERTY_PATH = "config.properties";

    private PropertiesUtils() {

    }

    // resources下的配置文件
    public static Properties loadProperties() {
        Properties prop = new Properties();
        try {
            ClassLoader classLoader = PropertiesUtils.class.getClassLoader();
//            System.out.println(classLoader);
            prop.load(classLoader.getResourceAsStream(PROPERTY_PATH));
            return prop;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 由用户指定配置文件路径
    public static void loadPropertiesKafka(String path) {
        Properties props = new Properties();
        try (InputStream propStream = new FileInputStream(path)) {
            props.load(propStream);
            System.out.println(props.getProperty("id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        loadPropertiesKafka("/data/javaProjects/my-project/service-registry/config/config.properties");
//        loadPropertiesKafka("config/config.properties");
//    }
}
