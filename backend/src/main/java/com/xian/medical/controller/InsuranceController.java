package com.xian.medical.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xian.medical.common.Result;
import com.xian.medical.entity.Insurance;
import com.xian.medical.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/insurance")
@CrossOrigin
public class InsuranceController {

    @Autowired
    private InsuranceService insuranceService;

    // Student: Get my insurance
    @GetMapping("/getByStudentId/{studentId}")
    public Result<Insurance> getByStudentId(@PathVariable Long studentId) {
        QueryWrapper<Insurance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        return Result.success(insuranceService.getOne(queryWrapper));
    }

    // Student: Update (Opt-in/Opt-out)
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody Insurance insurance) {
        // If it's a new record (not existing), save. If existing, update.
        // Logic: The prompt implies toggling.
        // But the input is an object.
        if (insurance.getId() == null) {
            return Result.success(insuranceService.save(insurance));
        }
        return Result.success(insuranceService.updateById(insurance));
    }

    // Staff: Get statistics
    @GetMapping("/stats")
    public Result<Map<String, Integer>> getStats() {
        long insuredCount = insuranceService.count(new QueryWrapper<Insurance>().eq("status", 1));
        long uninsuredCount = insuranceService.count(new QueryWrapper<Insurance>().eq("status", 0));
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("insured", (int) insuredCount);
        stats.put("uninsured", (int) uninsuredCount);
        return Result.success(stats);
    }
    
    // Staff: List all (if needed for table view)
    @GetMapping("/list")
    public Result<List<Insurance>> list() {
        return Result.success(insuranceService.list());
    }
}
