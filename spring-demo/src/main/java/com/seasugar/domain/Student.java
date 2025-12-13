package com.seasugar.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Student {
    private static final Logger log = LoggerFactory.getLogger(Student.class);
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
