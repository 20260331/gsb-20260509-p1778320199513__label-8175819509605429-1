package com.xian.medical.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xian.medical.common.Result;
import com.xian.medical.entity.ExamResult;
import com.xian.medical.entity.Student;
import com.xian.medical.service.ExamPackageService;
import com.xian.medical.service.ExamResultService;
import com.xian.medical.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exam-result")
@CrossOrigin
public class ExamResultController {

    @Autowired
    private ExamResultService examResultService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ExamPackageService examPackageService;

    private void populateNames(List<ExamResult> results) {
        if (results == null || results.isEmpty()) return;
        List<Long> studentIds = results.stream().map(ExamResult::getStudentId).distinct().collect(Collectors.toList());
        List<Long> packageIds = results.stream().map(ExamResult::getPackageId).distinct().collect(Collectors.toList());
        
        Map<Long, String> studentNames = new HashMap<>();
        if (!studentIds.isEmpty()) {
            studentService.listByIds(studentIds).forEach(s -> studentNames.put(s.getId(), s.getName()));
        }
        
        Map<Long, String> packageNames = new HashMap<>();
        if (!packageIds.isEmpty()) {
            examPackageService.listByIds(packageIds).forEach(p -> packageNames.put(p.getId(), p.getName()));
        }
        
        for (ExamResult r : results) {
            r.setStudentName(studentNames.get(r.getStudentId()));
            r.setPackageName(packageNames.get(r.getPackageId()));
        }
    }

    // Student: View own results
    @GetMapping("/getByStudentId/{studentId}")
    public Result<List<ExamResult>> getByStudentId(@PathVariable Long studentId) {
        QueryWrapper<ExamResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        List<ExamResult> list = examResultService.list(queryWrapper);
        populateNames(list);
        return Result.success(list);
    }

    // Staff: Create/Update result (Entry)
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody ExamResult examResult) {
        if (examResult.getId() == null) {
            examResult.setCreateTime(LocalDateTime.now());
            return Result.success(examResultService.save(examResult));
        }
        return Result.success(examResultService.updateById(examResult));
    }

    // Admin: View all, filter by major
    @GetMapping("/list")
    public Result<List<ExamResult>> list(@RequestParam(required = false) String major) {
        QueryWrapper<ExamResult> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(major)) {
            // Find students with this major
            QueryWrapper<Student> studentQuery = new QueryWrapper<>();
            studentQuery.eq("major", major);
            List<Student> students = studentService.list(studentQuery);
            
            if (students.isEmpty()) {
                return Result.success(List.of());
            }
            
            List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
            queryWrapper.in("student_id", studentIds);
        }
        
        List<ExamResult> list = examResultService.list(queryWrapper);
        populateNames(list);
        return Result.success(list);
    }
}
