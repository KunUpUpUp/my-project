package com.seasugar.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seasugar.domain.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
