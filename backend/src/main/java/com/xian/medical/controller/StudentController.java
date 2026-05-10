package com.xian.medical.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xian.medical.common.Result;
import com.xian.medical.entity.Student;
import com.xian.medical.entity.User;
import com.xian.medical.service.StudentService;
import com.xian.medical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    // Admin: List all or search
    @GetMapping("/list")
    public Result<List<Student>> list(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String sortField,
                                      @RequestParam(required = false) String sortOrder) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword).or().like("student_number", keyword);
        }
        if (StringUtils.hasText(sortField)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        }
        return Result.success(studentService.list(queryWrapper));
    }

    // Admin: Add
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Student student) {
        if (StringUtils.hasText(student.getUsername()) && StringUtils.hasText(student.getPassword())) {
            User user = new User();
            user.setUsername(student.getUsername());
            user.setPassword(student.getPassword());
            user.setRole("STUDENT");
            userService.save(user);
            student.setUserId(user.getId());
        }
        return Result.success(studentService.save(student));
    }

    // Admin & Student: Update
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody Student student) {
        return Result.success(studentService.updateById(student));
    }

    // Admin: Delete
    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Student student = studentService.getById(id);
        if (student != null && student.getUserId() != null) {
            userService.removeById(student.getUserId()); // Cascade deletes student
            return Result.success(true);
        }
        return Result.success(studentService.removeById(id));
    }
    
    // Get by User ID (for Student login)
    @GetMapping("/getByUserId/{userId}")
    public Result<Student> getByUserId(@PathVariable Long userId) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Result.success(studentService.getOne(queryWrapper));
    }
}
