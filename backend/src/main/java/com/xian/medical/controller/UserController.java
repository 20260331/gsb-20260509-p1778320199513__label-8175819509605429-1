package com.xian.medical.controller;

import com.xian.medical.common.Result;
import com.xian.medical.entity.User;
import com.xian.medical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword(), user.getRole());
        if (loginUser != null) {
            return Result.success(loginUser);
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/updatePassword")
    public Result<Boolean> updatePassword(@RequestBody User user) {
        boolean success = userService.updateById(user);
        return success ? Result.success(true) : Result.error("修改失败");
    }
}
