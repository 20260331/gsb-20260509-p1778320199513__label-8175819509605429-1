package com.xian.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xian.medical.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
