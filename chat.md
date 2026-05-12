# 学生体检流程与数据流转

> **Workspace:** d:\code\trae\gsb\20260509\B-589-代码理解-1\TestM_2

---

## User
整理学生从预约体检到查看结果的完整流程，中间经过哪些环节、数据怎么流转。用流程图或分步说明的方式呈现，核心逻辑可以加上一点代码细节说明，将结果存在 result.md 中。

## Assistant
现在让我来阅读关键的源文件，以理解完整的流程。我将从与核心工作流相关的实体、控制器和服务开始。

现在我来阅读前端组件，以了解 UI 流程：

我还需要阅读路由和登录视图，以理解完整的用户旅程。

现在我已经完全理解了代码库。让我来编写 `result.md` 文件：

已完成！`result.md` 已保存到项目根目录，内容涵盖以下部分：

1. **整体流程图** — 用 ASCII 图展示从登录→浏览套餐→预约→职工录入→查看结果的五步流程，标注每一步的角色归属

2. **分步详细说明**（共 5 步）：
   - **第①步 学生登录**：`Login.vue` → `POST /api/user/login` → `UserServiceImpl.login()` 三字段精确匹配
   - **第②步 浏览套餐**：`ExamAppointment.vue` 加载学生信息、检查已有预约、拉取套餐列表
   - **第③步 提交预约**：在 `exam_result` 表插入一条 `resultData=null` 的记录作为预约凭证
   - **第④步 职工录入**：`ExamEntry.vue` 支持按专业筛选，通过 `populateNames()` 批量填充姓名/套餐名，提交时关联 `staffId`，走 `updateById` 更新
   - **第⑤步 查看结果**：`MyExamResult.vue` 根据 `resultData` 是否为空显示结果内容或"待录入"标签

3. **核心数据模型关系** — 展示 `sys_user`、`student`、`staff`、`exam_package`、`exam_result`、`insurance` 六张表的关联关系

4. **预约状态流转** — 以 `result_data` 字段为核心的状态机：未预约 → 已预约/待录入 → 已完成

5. **关键代码文件索引** — 按环节列出前端和后端的对应文件

