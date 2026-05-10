-- Create database
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS medical_insurance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE medical_insurance;

-- User Table
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL -- ADMIN, STUDENT, STAFF
);

-- Student Table
CREATE TABLE IF NOT EXISTS student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    student_number VARCHAR(20) UNIQUE,
    name VARCHAR(50),
    gender VARCHAR(10),
    birth_date DATE,
    id_type VARCHAR(20),
    id_number VARCHAR(50),
    phone VARCHAR(20),
    major VARCHAR(50),
    department VARCHAR(50),
    household_type VARCHAR(20),
    email VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
);

-- Staff Table
CREATE TABLE IF NOT EXISTS staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    employee_number VARCHAR(20) UNIQUE,
    name VARCHAR(50),
    phone VARCHAR(20),
    department VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
);

-- Insurance Table
CREATE TABLE IF NOT EXISTS insurance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    status INT DEFAULT 0, -- 0: Uninsured, 1: Insured
    start_year VARCHAR(10),
    duration INT,
    amount DECIMAL(10, 2),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

-- Exam Package Table
CREATE TABLE IF NOT EXISTS exam_package (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    description VARCHAR(255),
    price DECIMAL(10, 2)
);

-- Exam Result Table
CREATE TABLE IF NOT EXISTS exam_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    package_id BIGINT,
    result_data TEXT,
    create_time DATETIME,
    staff_id BIGINT,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (package_id) REFERENCES exam_package(id),
    FOREIGN KEY (staff_id) REFERENCES staff(id)
);

-- Initial Data

-- Admin
INSERT INTO sys_user (username, password, role) VALUES ('admin', 'admin', 'ADMIN');

-- Students
INSERT INTO sys_user (username, password, role) VALUES ('student1', '123456', 'STUDENT');
INSERT INTO student (user_id, student_number, name, gender, birth_date, id_type, id_number, phone, major, department, household_type, email) 
VALUES (LAST_INSERT_ID(), 'S2024001', '张三', '男', '2000-01-01', '身份证', '610100200001011234', '13800000001', '计算机科学', '信息工程学院', '城镇', 'zhangsan@example.com');

INSERT INTO sys_user (username, password, role) VALUES ('student2', '123456', 'STUDENT');
INSERT INTO student (user_id, student_number, name, gender, birth_date, id_type, id_number, phone, major, department, household_type, email) 
VALUES (LAST_INSERT_ID(), 'S2024002', '李四', '女', '2001-02-02', '身份证', '610100200102021234', '13800000002', '英语', '外国语学院', '农村', 'lisi@example.com');

-- Staff
INSERT INTO sys_user (username, password, role) VALUES ('staff1', '123456', 'STAFF');
INSERT INTO staff (user_id, employee_number, name, phone, department) 
VALUES (LAST_INSERT_ID(), 'T2024001', '王老师', '13900000001', '校医院');

INSERT INTO sys_user (username, password, role) VALUES ('staff2', '123456', 'STAFF');
INSERT INTO staff (user_id, employee_number, name, phone, department) 
VALUES (LAST_INSERT_ID(), 'T2024002', '赵医生', '13900000002', '校医院');

-- Insurance
INSERT INTO insurance (student_id, status, start_year, duration, amount) VALUES (1, 1, '2024', 1, 350.00);
INSERT INTO insurance (student_id, status, start_year, duration, amount) VALUES (2, 0, NULL, 0, 0.00);

-- Exam Packages
INSERT INTO exam_package (name, description, price) VALUES ('基础套餐', '包含身高、体重、视力等基础检查', 50.00);
INSERT INTO exam_package (name, description, price) VALUES ('标准套餐', '包含基础检查及血常规、尿常规', 120.00);
INSERT INTO exam_package (name, description, price) VALUES ('全面套餐', '包含标准检查及心电图、胸透、B超', 280.00);

-- Exam Results
INSERT INTO exam_result (student_id, package_id, result_data, create_time, staff_id) 
VALUES (1, 1, '身高175cm, 体重70kg, 视力5.0, 内科正常', NOW(), 1);
