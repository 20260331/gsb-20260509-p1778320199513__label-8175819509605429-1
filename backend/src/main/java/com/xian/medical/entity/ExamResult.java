package com.xian.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exam_result")
public class ExamResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long packageId;
    private String resultData;
    private LocalDateTime createTime;
    private Long staffId;

    @TableField(exist = false)
    private String studentName;
    
    @TableField(exist = false)
    private String packageName;
}
