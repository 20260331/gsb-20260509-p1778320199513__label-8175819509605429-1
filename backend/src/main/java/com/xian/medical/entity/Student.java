package com.xian.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("student")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String studentNumber;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String idType;
    private String idNumber;
    private String phone;
    private String major;
    private String department;
    private String householdType;
    private String email;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String password;
}
