package com.xian.medical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xian.medical.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password, String role);
}
