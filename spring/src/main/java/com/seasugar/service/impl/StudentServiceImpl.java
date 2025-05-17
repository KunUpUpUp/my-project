package com.seasugar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seasugar.dao.StudentMapper;
import com.seasugar.domain.Student;
import com.seasugar.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    private Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    public StudentServiceImpl() {
        log.info("StudentServiceImpl构造方法触发");
    }
}
