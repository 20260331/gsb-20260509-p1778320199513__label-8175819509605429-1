package com.xian.medical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xian.medical.entity.ExamPackage;
import com.xian.medical.mapper.ExamPackageMapper;
import com.xian.medical.service.ExamPackageService;
import org.springframework.stereotype.Service;

@Service
public class ExamPackageServiceImpl extends ServiceImpl<ExamPackageMapper, ExamPackage> implements ExamPackageService {
}
