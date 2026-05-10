package com.xian.medical.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xian.medical.common.Result;
import com.xian.medical.entity.Staff;
import com.xian.medical.entity.User;
import com.xian.medical.service.StaffService;
import com.xian.medical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<Staff>> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String sortField,
                                    @RequestParam(required = false) String sortOrder) {
        QueryWrapper<Staff> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword).or().like("employee_number", keyword);
        }
        if (StringUtils.hasText(sortField)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        }
        return Result.success(staffService.list(queryWrapper));
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Staff staff) {
        if (StringUtils.hasText(staff.getUsername()) && StringUtils.hasText(staff.getPassword())) {
            User user = new User();
            user.setUsername(staff.getUsername());
            user.setPassword(staff.getPassword());
            user.setRole("STAFF");
            userService.save(user);
            staff.setUserId(user.getId());
        }
        return Result.success(staffService.save(staff));
    }

    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody Staff staff) {
        return Result.success(staffService.updateById(staff));
    }

    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Staff staff = staffService.getById(id);
        if (staff != null && staff.getUserId() != null) {
            userService.removeById(staff.getUserId()); // Cascade
            return Result.success(true);
        }
        return Result.success(staffService.removeById(id));
    }

    @GetMapping("/getByUserId/{userId}")
    public Result<Staff> getByUserId(@PathVariable Long userId) {
        QueryWrapper<Staff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Result.success(staffService.getOne(queryWrapper));
    }
}
