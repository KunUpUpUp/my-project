package com.seasugar.controller;

import com.seasugar.domain.Student;
import com.seasugar.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stu")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/get")
    public Student get(){
        return studentService.getById(1L);
    }
}
