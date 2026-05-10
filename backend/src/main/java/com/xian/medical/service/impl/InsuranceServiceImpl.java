package com.xian.medical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xian.medical.entity.Insurance;
import com.xian.medical.mapper.InsuranceMapper;
import com.xian.medical.service.InsuranceService;
import org.springframework.stereotype.Service;

@Service
public class InsuranceServiceImpl extends ServiceImpl<InsuranceMapper, Insurance> implements InsuranceService {
}
