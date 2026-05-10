package com.xian.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("staff")
public class Staff {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String employeeNumber;
    private String name;
    private String phone;
    private String department;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String password;
}
