# 学生体检全流程：从预约到查看结果

## 一、整体流程图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        学生体检完整流程                                │
└─────────────────────────────────────────────────────────────────────┘

 ①登录        ②浏览套餐       ③提交预约        ④职工录入结果      ⑤查看结果
┌──────┐    ┌──────────┐   ┌───────────┐   ┌──────────────┐   ┌──────────┐
│ 学生  │───>│ 体检套餐  │──>│ 创建预约   │──>│  录入体检结果  │──>│ 查看结果  │
│ 登录  │    │ 列表展示  │   │ 记录(待录入)│   │  (填写数据)   │   │ (含/待录入)│
└──────┘    └──────────┘   └───────────┘   └──────────────┘   └──────────┘
   学生         学生            学生             职工             学生
```

## 二、分步详细说明

---

### 第①步：学生登录

**角色**：学生  
**前端页面**：`Login.vue`  
**后端接口**：`POST /api/user/login`

学生打开系统首页，输入账号、密码并选择"学生"角色进行登录。

**代码细节**：
- 前端将 `{username, password, role}` 发送到 `POST /api/user/login`
- 后端 `UserServiceImpl.login()` 通过 MyBatis-Plus 的 `QueryWrapper` 查询 `sys_user` 表，三字段精确匹配：

```java
// UserServiceImpl.java:13
queryWrapper.eq("username", username)
            .eq("password", password)
            .eq("role", role);
```

- 登录成功后，`User` 对象（含 `id`, `username`, `role`）存入 `localStorage`，前端根据 `role` 路由到 `/student` 页面

**数据流转**：
```
前端表单 → POST /api/user/login → sys_user 表查询 → 返回 User 对象 → localStorage 存储 → 路由到 /student
```

---

### 第②步：浏览体检套餐

**角色**：学生  
**前端页面**：`ExamAppointment.vue`  
**后端接口**：`GET /api/exam-package/list`

学生进入"体检预约"标签页，系统加载所有可选的体检套餐。

**代码细节**：
- 组件 `onMounted` 时调用 `init()` 方法，依次获取：
  1. 学生信息：`GET /api/student/getByUserId/{userId}` — 通过登录用户的 `user.id` 查询 `student` 表获取 `student.id`
  2. 已有预约：`GET /api/exam-result/getByStudentId/{studentId}` — 检查是否有 `resultData` 为空的记录（即"待录入"状态的预约）
  3. 套餐列表：`GET /api/exam-package/list` — 查询 `exam_package` 表全部套餐

```javascript
// ExamAppointment.vue:56-62 — 检查是否已有待录入的预约
const pending = results.find(r => !r.resultData)
if (pending) {
  currentAppointment.value = pending  // 已预约则显示提示，不展示套餐选择
}
```

**数据流转**：
```
localStorage(user.id) → GET /api/student/getByUserId/{userId} → student 表 → 获取 student.id
student.id → GET /api/exam-result/getByStudentId/{studentId} → exam_result 表 → 检查有无待录入预约
GET /api/exam-package/list → exam_package 表 → 返回全部套餐列表
```

**数据库 exam_package 表结构**：
| 字段 | 说明 |
|------|------|
| id | 套餐ID |
| name | 套餐名称（基础套餐/标准套餐/全面套餐） |
| description | 描述 |
| price | 价格 |

---

### 第③步：提交预约

**角色**：学生  
**前端页面**：`ExamAppointment.vue`  
**后端接口**：`POST /api/exam-result/save`

学生选择某个套餐后点击"预约"按钮，系统在 `exam_result` 表创建一条记录，`resultData` 为空（表示待录入）。

**代码细节**：
- 前端弹窗确认后，构造请求体：

```javascript
// ExamAppointment.vue:80-85
const data = {
  studentId: student.value.id,   // 学生ID
  packageId: pkg.id,             // 选择的套餐ID
  createTime: new Date()         // 预约时间
  // resultData 不传，默认为 null → 表示"待录入"
}
const res = await axios.post('/api/exam-result/save', data)
```

- 后端 `ExamResultController.save()` 判断 `id` 是否为 `null`，新建时自动设置 `createTime`：

```java
// ExamResultController.java:67-72
if (examResult.getId() == null) {
  examResult.setCreateTime(LocalDateTime.now());
  return Result.success(examResultService.save(examResult));
}
```

**预约成功的判断逻辑**：前端在 `init()` 中通过 `results.find(r => !r.resultData)` 检查是否存在待录入记录，若存在则隐藏套餐表格，显示"您已预约体检"的提示。

**数据流转**：
```
用户选择套餐 → POST /api/exam-result/save → exam_result 表插入记录(studentId, packageId, createTime, resultData=null)
→ 前端刷新 → init() 检测到 pending 记录 → 显示"已预约"提示
```

**数据库 exam_result 表（预约时写入的字段）**：
| 字段 | 值 | 说明 |
|------|------|------|
| student_id | 学生ID | ✅ 已填 |
| package_id | 套餐ID | ✅ 已填 |
| create_time | 预约时间 | ✅ 已填 |
| result_data | NULL | ❌ 待职工录入 |
| staff_id | NULL | ❌ 待职工录入时写入 |

---

### 第④步：职工录入体检结果

**角色**：职工（校医院工作人员）  
**前端页面**：`ExamEntry.vue`  
**后端接口**：`GET /api/exam-result/list` → `POST /api/exam-result/save`

职工登录后进入"体检录入"标签页，可查看所有学生的预约记录（支持按专业筛选），对"待录入"的记录填写体检结果。

**代码细节**：
- 加载预约列表：`GET /api/exam-result/list?major=xxx`，后端支持按专业筛选：

```java
// ExamResultController.java:77-95
if (StringUtils.hasText(major)) {
  // 先查出该专业的学生ID列表
  QueryWrapper<Student> studentQuery = new QueryWrapper<>();
  studentQuery.eq("major", major);
  List<Student> students = studentService.list(studentQuery);
  List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
  queryWrapper.in("student_id", studentIds);
}
```

- `populateNames()` 方法批量填充 `studentName` 和 `packageName`，避免 N+1 查询：

```java
// ExamResultController.java:34-52
Map<Long, String> studentNames = new HashMap<>();
studentService.listByIds(studentIds).forEach(s -> studentNames.put(s.getId(), s.getName()));
Map<Long, String> packageNames = new HashMap<>();
examPackageService.listByIds(packageIds).forEach(p -> packageNames.put(p.getId(), p.getName()));
```

- 前端表格中，`resultData` 为空时显示"待录入"标签，否则显示结果文本
- 点击"录入结果"按钮弹出对话框，填写体检结果后提交：
  1. 先通过 `GET /api/staff/getByUserId/{userId}` 获取职工的 `staff.id`
  2. 将 `staffId` 和 `resultData` 一起提交到 `POST /api/exam-result/save`

```javascript
// ExamEntry.vue:83-88
const staffRes = await axios.get(`/api/staff/getByUserId/${user.id}`)
if (staffRes.data.code === 200 && staffRes.data.data) {
  form.value.staffId = staffRes.data.data.id   // 关联录入职工
  const res = await axios.post('/api/exam-result/save', form.value)
}
```

- 后端因为记录已有 `id`，走 `updateById` 更新逻辑：

```java
// ExamResultController.java:72
return Result.success(examResultService.updateById(examResult));
```

**数据流转**：
```
GET /api/exam-result/list → exam_result + student + exam_package 三表关联查询 → 展示预约列表
→ 职工填写结果 → GET /api/staff/getByUserId/{userId} → 获取 staffId
→ POST /api/exam-result/save {id, studentId, packageId, resultData, staffId} → UPDATE exam_result
```

**数据库 exam_result 表（录入后完整字段）**：
| 字段 | 值 | 说明 |
|------|------|------|
| student_id | 学生ID | ✅ |
| package_id | 套餐ID | ✅ |
| create_time | 预约时间 | ✅ |
| result_data | "身高175cm, 体重70kg..." | ✅ 职工已录入 |
| staff_id | 职工ID | ✅ 录入职工 |

---

### 第⑤步：学生查看体检结果

**角色**：学生  
**前端页面**：`MyExamResult.vue`  
**后端接口**：`GET /api/exam-result/getByStudentId/{studentId}`

学生切换到"体检结果"标签页，查看所有预约记录及其结果。

**代码细节**：
- 加载逻辑：先用 `user.id` 查 `student.id`，再查该学生的所有 `exam_result` 记录

```javascript
// MyExamResult.vue:29-35
const stuRes = await axios.get(`/api/student/getByUserId/${user.id}`)
const studentId = stuRes.data.data.id
const res = await axios.get(`/api/exam-result/getByStudentId/${studentId}`)
tableData.value = res.data.data
```

- 结果展示：`resultData` 有值则显示内容，为空则显示"待录入"标签：

```html
<!-- MyExamResult.vue:13-15 -->
<div v-if="scope.row.resultData">{{ scope.row.resultData }}</div>
<el-tag v-else type="info">待录入</el-tag>
```

- 后端 `populateNames()` 会自动填充 `studentName` 和 `packageName`，使前端可以直接展示套餐名称

**数据流转**：
```
localStorage(user.id) → GET /api/student/getByUserId/{userId} → student.id
→ GET /api/exam-result/getByStudentId/{studentId} → exam_result 表查询
→ populateNames() 填充 studentName/packageName → 前端表格展示
```

---

## 三、核心数据模型关系

```
sys_user (1) ──── (1) student (1) ──── (N) exam_result (N) ──── (1) exam_package
                         │                    │
                         │                    └─── (1) staff
                         │
                         └──── (1) insurance
```

| 表 | 核心字段 | 说明 |
|------|------|------|
| `sys_user` | id, username, password, role | 统一登录账号，role 区分 ADMIN/STUDENT/STAFF |
| `student` | id, user_id, student_number, name, major, ... | 学生详情，通过 user_id 关联登录账号 |
| `staff` | id, user_id, employee_number, name, department | 职工详情，通过 user_id 关联登录账号 |
| `exam_package` | id, name, description, price | 体检套餐，预置基础/标准/全面三种 |
| `exam_result` | id, student_id, package_id, result_data, create_time, staff_id | 体检记录，**预约时 result_data 为空，录入后才有值** |
| `insurance` | id, student_id, status, start_year, duration, amount | 医保信息，status: 0未参保/1已参保 |

## 四、预约状态流转

`exam_result` 记录的 `result_data` 字段是状态判定的核心：

```
                    预约                           录入
  不存在记录 ──────────────> result_data = NULL ──────────────> result_data = "体检数据..."
  (未预约)                   (已预约/待录入)                    (已完成)
```

| 状态 | result_data | 学生端显示 | 职工端显示 | 触发操作 |
|------|-------------|-----------|-----------|---------|
| 未预约 | 无记录 | 显示套餐列表 | — | — |
| 已预约/待录入 | NULL | "您已预约体检" | "待录入"标签 + 录入按钮 | 学生点击预约 |
| 已完成 | 体检数据文本 | 显示结果内容 | 显示结果内容 | 职工点击录入 |

## 五、关键代码文件索引

| 环节 | 前端文件 | 后端文件 |
|------|---------|---------|
| 登录 | `frontend/src/views/Login.vue` | `UserController.java` → `UserServiceImpl.java` |
| 预约体检 | `frontend/src/components/student/ExamAppointment.vue` | `ExamPackageController.java` + `ExamResultController.java` |
| 录入结果 | `frontend/src/components/staff/ExamEntry.vue` | `ExamResultController.java` (save + list) |
| 查看结果 | `frontend/src/components/student/MyExamResult.vue` | `ExamResultController.java` (getByStudentId) |
| 医保管理 | `frontend/src/components/student/MyInsurance.vue` | `InsuranceController.java` |
| 学生面板 | `frontend/src/views/StudentDashboard.vue` | — |
| 职工面板 | `frontend/src/views/StaffDashboard.vue` | — |
| 数据库 | `database/init.sql` | — |
