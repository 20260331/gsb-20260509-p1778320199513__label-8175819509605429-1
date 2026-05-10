package com.xian.medical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xian.medical.entity.ExamResult;
import com.xian.medical.mapper.ExamResultMapper;
import com.xian.medical.service.ExamResultService;
import org.springframework.stereotype.Service;

@Service
public class ExamResultServiceImpl extends ServiceImpl<ExamResultMapper, ExamResult> implements ExamResultService {
}
