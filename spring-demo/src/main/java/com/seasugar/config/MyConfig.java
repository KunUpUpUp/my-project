package com.seasugar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {

//    @Autowired
//    public MyConfig() {
//        System.out.println("MyConfig执行了");
//        init();
//    }

    void init() {
        System.out.println("init");
    }
}
