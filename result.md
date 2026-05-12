# 学生体检预约到结果查看完整流程

## 一、流程总览

```
┌─────────────┐     ┌──────────────┐     ┌───────────────┐     ┌──────────────┐
│  学生登录    │────▶│ 选择体检套餐  │────▶│  提交预约申请  │────▶│ 预约记录生成  │
└─────────────┘     └──────────────┘     └───────────────┘     └──────┬───────┘
                                                                      │
                                                                      ▼
┌─────────────┐     ┌──────────────┐     ┌───────────────┐     ┌──────────────┐
│  结果已录入  │◀────│ 工作人员录入 │◀────│ 工作人员查看   │◀────│ 待体检状态   │
└──────┬──────┘     └──────────────┘     └───────────────┘     └──────────────┘
       │
       ▼
┌─────────────┐     ┌──────────────┐
│  学生查看   │◀────│  结果查询    │
└─────────────┘     └──────────────┘
```

---

## 二、详细流程分步说明

### 阶段一：学生预约体检

#### 步骤1：学生登录系统
- **数据来源**：用户通过用户名密码登录
- **核心表**：`sys_user` 表验证身份
- **代码细节**：
  ```java
  // 登录后获取用户信息，存储在localStorage
  const user = JSON.parse(localStorage.getItem('user'))
  ```

#### 步骤2：获取学生信息
- **前端**：`ExamAppointment.vue:44` 调用接口获取学生详情
  ```javascript
  const stuRes = await axios.get(`/api/student/getByUserId/${user.id}`)
  ```
- **后端**：`StudentController.java:77-82` 根据userId查询学生
  ```java
  @GetMapping("/getByUserId/{userId}")
  public Result<Student> getByUserId(@PathVariable Long userId) {
      QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_id", userId);
      return Result.success(studentService.getOne(queryWrapper));
  }
  ```
- **数据流转**：`sys_user.id` → `student.user_id` → 获取 `student.id`

#### 步骤3：查询体检套餐列表
- **前端**：`ExamAppointment.vue:68` 获取所有可用套餐
  ```javascript
  const pkgRes = await axios.get('/api/exam-package/list')
  ```
- **后端**：`ExamPackageController.java:19-22` 查询所有套餐
  ```java
  @GetMapping("/list")
  public Result<List<ExamPackage>> list() {
      return Result.success(examPackageService.list());
  }
  ```
- **数据表**：`exam_package` 表存储套餐信息（基础/标准/全面套餐）

#### 步骤4：检查是否已有预约
- **前端**：`ExamAppointment.vue:56-64` 查询该学生的体检记录
  ```javascript
  const resRes = await axios.get(`/api/exam-result/getByStudentId/${student.value.id}`)
  // 判断resultData是否为空，空表示已预约但未体检
  const pending = results.find(r => !r.resultData)
  ```
- **逻辑说明**：系统通过 `exam_result.resultData` 字段判断状态
  - 空值/null → 已预约，待体检
  - 有内容 → 已完成体检

#### 步骤5：提交预约
- **前端**：`ExamAppointment.vue:74-94` 用户点击预约按钮
  ```javascript
  const data = {
      studentId: student.value.id,
      packageId: pkg.id,
      createTime: new Date()
      // resultData: null (未设置)
  }
  const res = await axios.post('/api/exam-result/save', data)
  ```
- **后端**：`ExamResultController.java:66-73` 保存预约记录
  ```java
  @PostMapping("/save")
  public Result<Boolean> save(@RequestBody ExamResult examResult) {
      if (examResult.getId() == null) {
          examResult.setCreateTime(LocalDateTime.now());
          return Result.success(examResultService.save(examResult));
      }
      return Result.success(examResultService.updateById(examResult));
  }
  ```
- **数据写入**：向 `exam_result` 表插入一条新记录
  | 字段 | 值 | 说明 |
  |------|----|------|
  | student_id | 学生ID | 关联学生表 |
  | package_id | 套餐ID | 关联套餐表 |
  | result_data | NULL | 待录入状态 |
  | create_time | 当前时间 | 预约时间 |
  | staff_id | NULL | 暂未分配 |

---

### 阶段二：工作人员录入体检结果

#### 步骤1：工作人员查询待录入记录
- **前端**：`ExamEntry.vue:65-70` 获取所有体检记录（可按专业筛选）
  ```javascript
  const res = await axios.get('/api/exam-result/list', { params: searchForm.value })
  ```
- **后端**：`ExamResultController.java:76-97` 支持按专业筛选
  ```java
  @GetMapping("/list")
  public Result<List<ExamResult>> list(@RequestParam(required = false) String major) {
      // 1. 如果有专业筛选，先查询该专业的所有学生ID
      // 2. 根据学生ID查询对应的体检记录
      // 3. 填充学生姓名和套餐名称
      populateNames(list); // 关联查询学生和套餐名称
  }
  ```
- **数据关联**：`populateNames()` 方法处理多表关联
  - `exam_result.student_id` → `student.name`
  - `exam_result.package_id` → `exam_package.name`

#### 步骤2：录入体检结果
- **前端**：`ExamEntry.vue:72-98` 弹出对话框录入结果
  ```javascript
  const staffRes = await axios.get(`/api/staff/getByUserId/${user.id}`)
  form.value.staffId = staffRes.data.data.id  // 设置录入人员
  const res = await axios.post('/api/exam-result/save', form.value)
  ```
- **后端**：`ExamResultController.java:66-73` 更新记录（ID不为空时执行update）
  ```java
  // examResult.getId() != null → 执行 updateById
  return Result.success(examResultService.updateById(examResult));
  ```
- **数据更新**：更新 `exam_result` 表记录
  | 字段 | 更新值 | 说明 |
  |------|--------|------|
  | result_data | 体检结果文本 | 如："身高175cm, 体重70kg..." |
  | staff_id | 工作人员ID | 录入人员标识 |

---

### 阶段三：学生查看体检结果

#### 步骤1：查询个人体检结果
- **前端**：`MyExamResult.vue:28-37` 查询当前学生的所有体检记录
  ```javascript
  const stuRes = await axios.get(`/api/student/getByUserId/${user.id}`)
  const studentId = stuRes.data.data.id
  const res = await axios.get(`/api/exam-result/getByStudentId/${studentId}`)
  ```
- **后端**：`ExamResultController.java:56-63` 根据学生ID查询
  ```java
  @GetMapping("/getByStudentId/{studentId}")
  public Result<List<ExamResult>> getByStudentId(@PathVariable Long studentId) {
      QueryWrapper<ExamResult> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("student_id", studentId);
      List<ExamResult> list = examResultService.list(queryWrapper);
      populateNames(list);  // 填充套餐名称
      return Result.success(list);
  }
  ```
- **前端展示判断**：`MyExamResult.vue:12-15`
  ```vue
  <div v-if="scope.row.resultData">{{ scope.row.resultData }}</div>
  <el-tag v-else type="info">待录入</el-tag>
  ```

---

## 三、核心数据表关系图

```
sys_user (用户表)
  ├─ id (PK)
  ├─ username
  ├─ password
  └─ role (ADMIN/STUDENT/STAFF)
       │
       ├─ student.user_id (FK) ────┐
       │                            │
       └─ staff.user_id (FK) ───┐   │
                                │   │
student (学生表)                │   │
  ├─ id (PK) ◄──────────────┐   │   │
  ├─ student_number         │   │   │
  └─ name                   │   │   │
                             │   │   │
staff (工作人员表)            │   │   │
  ├─ id (PK) ◄───────────┐   │   │   │
  └─ name                │   │   │   │
                          │   │   │   │
exam_package (体检套餐表)  │   │   │   │
  ├─ id (PK) ◄────────┐   │   │   │   │
  ├─ name             │   │   │   │   │
  └─ price            │   │   │   │   │
                       │   │   │   │   │
exam_result (体检结果表)  │   │   │   │
  ├─ id (PK)           │   │   │   │
  ├─ student_id (FK) ──┘   │   │   │
  ├─ package_id (FK) ──────┘   │   │
  ├─ staff_id (FK) ────────────┘   │
  ├─ result_data                   │
  └─ create_time                   │
                                   │
                                   └─ 关联查询，填充 studentName, packageName
```

---

## 四、关键代码设计说明

### 1. 预约状态判断设计
**设计思路**：系统没有单独的"预约"表，而是复用 `exam_result` 表，通过 `resultData` 字段的空值来表示预约状态：
- **优点**：简化表结构，预约和结果共用一张表
- **实现**：`resultData == null` 表示"已预约，待体检"

### 2. 多表关联查询优化
`ExamResultController.java:34-53` 的 `populateNames()` 方法：
```java
private void populateNames(List<ExamResult> results) {
    // 1. 收集所有需要查询的studentId和packageId
    // 2. 批量查询学生和套餐（避免N+1查询）
    // 3. 组装Map，填充名称字段
}
```
**优化点**：先批量查询，再内存组装，避免循环中单次查询产生N+1问题。

### 3. MyBatis-Plus 框架使用
- 所有 Service 继承 `IService<T>` 接口
- 所有 Mapper 继承 `BaseMapper<T>` 接口（代码中未显式展示）
- 提供通用 CRUD 操作：`save()`, `updateById()`, `list()`, `getOne()` 等

---

## 五、接口清单

| 接口 | 方法 | 功能 |
|------|------|------|
| `/api/student/getByUserId/{userId}` | GET | 根据用户ID获取学生信息 |
| `/api/exam-package/list` | GET | 获取体检套餐列表 |
| `/api/exam-result/getByStudentId/{studentId}` | GET | 学生查询自己的体检记录 |
| `/api/exam-result/save` | POST | 保存/更新体检记录（预约+录入） |
| `/api/exam-result/list` | GET | 工作人员查询所有体检记录（支持专业筛选） |
| `/api/staff/getByUserId/{userId}` | GET | 根据用户ID获取工作人员信息 |
