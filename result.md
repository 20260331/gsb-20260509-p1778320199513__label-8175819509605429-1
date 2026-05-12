# 学生体检预约到结果查看完整流程

## 一、流程图概览

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐
│  学生登录    │────▶│  选择体检套餐  │────▶│  提交预约请求  │
└─────────────┘     └──────────────┘     └──────────────┘
                                                    │
                                                    ▼
┌─────────────┐     ┌──────────────┐     ┌──────────────┐
│  学生查看结果 │◀────│  录入体检结果  │◀────│  创建预约记录  │
└─────────────┘     └──────────────┘     └──────────────┘
                              │
                              ▼
                     ┌──────────────┐
                     │  教职工登录    │
                     └──────────────┘
```

---

## 二、详细分步说明

### 阶段1：学生预约体检

**步骤1：学生登录系统**
- 前端：`Login.vue`
- 后端：`UserController` 负责身份验证
- 登录成功后，用户信息（包含 `user.id`）存储在 `localStorage` 中

**步骤2：获取学生信息**
- 前端组件：`ExamAppointment.vue:44`
- 调用接口：`GET /api/student/getByUserId/{userId}`
- 后端实现：`StudentController.java:77-82`
- 通过登录用户的 `userId` 查询对应的学生信息，获取 `student.id`

**步骤3：查询可用体检套餐**
- 前端组件：`ExamAppointment.vue:68`
- 调用接口：`GET /api/exam-package/list`
- 后端实现：`ExamPackageController.java:19-22`
- 返回所有体检套餐列表（基础套餐、标准套餐、全面套餐）

**步骤4：检查是否已有未完成的预约**
- 前端组件：`ExamAppointment.vue:56-64`
- 调用接口：`GET /api/exam-result/getByStudentId/{studentId}`
- 检查逻辑：如果存在 `resultData` 为空的记录，则视为已预约
- 后端实现：`ExamResultController.java:56-63`

**步骤5：选择套餐并提交预约**
- 前端组件：`ExamAppointment.vue:74-94`
- 用户点击"预约"按钮后，弹出确认框
- 确认后调用接口：`POST /api/exam-result/save`
- 提交数据：
  ```javascript
  {
    studentId: student.value.id,    // 学生ID
    packageId: pkg.id,              // 套餐ID
    createTime: new Date(),         // 当前时间
    // resultData 为空，表示待录入
  }
  ```
- 后端实现：`ExamResultController.java:66-73`
- 数据存储：在 `exam_result` 表中创建一条记录，`result_data` 字段为 NULL

---

### 阶段2：教职工录入体检结果

**步骤1：教职工登录系统**
- 同学生登录流程，角色为 `STAFF`

**步骤2：获取教职工信息**
- 前端组件：`ExamEntry.vue:83`
- 调用接口：`GET /api/staff/getByUserId/{userId}`
- 获取 `staff.id`，用于关联录入结果的教职工

**步骤3：查询所有预约/体检记录**
- 前端组件：`ExamEntry.vue:66`
- 调用接口：`GET /api/exam-result/list`（可按专业筛选）
- 后端实现：`ExamResultController.java:76-97`
- 返回数据会自动填充学生姓名和套餐名称（`populateNames` 方法）

**步骤4：录入体检结果**
- 前端组件：`ExamEntry.vue:72-98`
- 点击"录入结果"按钮，弹出对话框
- 填写体检结果后提交
- 调用接口：`POST /api/exam-result/save`
- 提交数据：
  ```javascript
  {
    id: form.value.id,           // 体检记录ID（用于更新）
    resultData: form.value.resultData,  // 体检结果内容
    staffId: staff.id            // 录入教职工ID
  }
  ```
- 后端实现：`ExamResultController.java:66-73`
- 更新 `exam_result` 表的 `result_data` 和 `staff_id` 字段

---

### 阶段3：学生查看体检结果

**步骤1：学生登录系统**

**步骤2：查询体检结果**
- 前端组件：`MyExamResult.vue:28-37`
- 先通过 `userId` 获取 `studentId`
- 调用接口：`GET /api/exam-result/getByStudentId/{studentId}`
- 后端实现：`ExamResultController.java:56-63`

**步骤3：展示结果**
- 前端组件：`MyExamResult.vue:8-17`
- 表格展示：套餐名称、时间、结果内容
- 如果 `resultData` 为空，显示"待录入"标签

---

## 三、数据流转与核心逻辑

### 数据表结构

| 表名 | 关键字段 | 说明 |
|------|----------|------|
| `sys_user` | id, username, password, role | 用户表 |
| `student` | id, user_id, student_number, name | 学生表，通过 user_id 关联用户 |
| `exam_package` | id, name, description, price | 体检套餐表 |
| `exam_result` | id, student_id, package_id, result_data, create_time, staff_id | 体检结果表（同时承载预约记录） |

### 核心代码说明

**1. 预约创建逻辑（后端）**
`ExamResultController.java:66-73`
```java
@PostMapping("/save")
public Result<Boolean> save(@RequestBody ExamResult examResult) {
    if (examResult.getId() == null) {
        // 新建记录：设置创建时间，resultData 为空表示预约
        examResult.setCreateTime(LocalDateTime.now());
        return Result.success(examResultService.save(examResult));
    }
    // 更新记录：录入体检结果
    return Result.success(examResultService.updateById(examResult));
}
```

**2. 预约判断逻辑（前端）**
`ExamAppointment.vue:56-64`
```javascript
const resRes = await axios.get(`/api/exam-result/getByStudentId/${student.value.id}`)
if (resRes.data.code === 200) {
    const results = resRes.data.data
    // resultData 为空表示已预约但未出结果
    const pending = results.find(r => !r.resultData)
    if (pending) {
        currentAppointment.value = pending
    }
}
```

**3. 结果展示增强（后端）**
`ExamResultController.java:34-53`
```java
private void populateNames(List<ExamResult> results) {
    // 批量查询学生名称和套餐名称
    // 避免 N+1 查询问题
    Map<Long, String> studentNames = new HashMap<>();
    Map<Long, String> packageNames = new HashMap<>();
    // ... 查询并填充名称
    for (ExamResult r : results) {
        r.setStudentName(studentNames.get(r.getStudentId()));
        r.setPackageName(packageNames.get(r.getPackageId()));
    }
}
```

---

## 四、关键设计要点

1. **单表复用设计**：`exam_result` 表同时承载预约记录和体检结果
   - `result_data = NULL` 表示已预约但未出结果
   - `result_data 有值` 表示体检已完成

2. **懒加载优化**：`populateNames` 方法采用批量查询策略，避免 N+1 查询问题

3. **状态流转清晰**：
   ```
   预约（resultData空） ───▶  录入结果（resultData有值） ───▶  学生查看
   ```

4. **数据关联**：
   - 用户（sys_user）→ 学生（student）：通过 user_id
   - 体检结果 → 学生：通过 student_id
   - 体检结果 → 套餐：通过 package_id
   - 体检结果 → 教职工：通过 staff_id
