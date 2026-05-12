package com.xian.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("insurance")
public class Insurance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Integer status; // 0: Uninsured, 1: Insured
    private String startYear;
    private Integer duration; // Years
    private BigDecimal amount;
}
