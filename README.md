# 西安文理学院医疗保险管理系统

## 项目描述
这是为西安文理学院开发的综合医疗保险和体检管理系统。系统实现了基于角色的访问控制（RBAC），支持管理员、教职工和学生三种角色。

## 技术栈
- **后端**: Spring Boot 3, MyBatis Plus, MySQL 8
- **前端**: Vue 3, Element Plus, Vite
- **运维**: Docker, Docker Compose

## 如何运行 (How to Run)
请确保您的环境中已安装 Docker 和 Docker Compose。

1.  在项目根目录下打开终端。
2.  运行以下命令：
    ```bash
    docker compose up -d
    ```
3.  等待容器启动。数据库初始化可能需要几秒钟。

## 服务列表 (Services)
系统启动并运行后，您可以访问以下服务：

- **前端应用**: http://localhost:3000
- **后端 API**: http://localhost:8080
- **MySQL 数据库**: `localhost:3308` (用户名: `root`, 密码: `password`, 数据库: `medical_insurance`)

## 验证 - 基本验证方式 (Verification)
您可以使用以下默认账号登录系统以验证功能：

| 角色 | 用户名 | 密码 | 功能权限 |
| :--- | :--- | :--- | :--- |
| **管理员 (Administrator)** | `admin` | `admin` | 用户管理, 系统配置 |
| **学生 (Student)** | `student1` | `123456` | 查看保险, 预约体检, 查看结果 |
| **医务人员 (Staff)** | `staff1` | `123456` | 录入体检结果, 查看统计 |

## 项目结构
- `backend/`: Spring Boot 后端源代码。
- `frontend/`: Vue 3 前端源代码。
- `database/`: SQL 初始化脚本。
- `docker-compose.yml`: 容器编排配置。
