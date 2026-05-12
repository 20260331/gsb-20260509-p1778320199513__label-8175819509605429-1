package com.xian.medical.controller;

import com.xian.medical.common.Result;
import com.xian.medical.entity.ExamPackage;
import com.xian.medical.service.ExamPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-package")
@CrossOrigin
public class ExamPackageController {

    @Autowired
    private ExamPackageService examPackageService;

    @GetMapping("/list")
    public Result<List<ExamPackage>> list() {
        return Result.success(examPackageService.list());
    }
    
    // Admin management if needed (optional based on prompt but good to have)
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody ExamPackage examPackage) {
        return Result.success(examPackageService.save(examPackage));
    }
}
