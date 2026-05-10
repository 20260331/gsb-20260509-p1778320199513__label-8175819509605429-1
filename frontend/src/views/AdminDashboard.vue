<template>
  <div class="common-layout">
    <el-container>
      <el-header class="header">
        <div class="logo">西安文理学院医保系统 - 管理员端</div>
        <el-button type="danger" @click="logout">退出登录</el-button>
      </el-header>
      <el-main>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="学生信息管理" name="student">
            <StudentManagement />
          </el-tab-pane>
          <el-tab-pane label="职工信息管理" name="staff">
            <StaffManagement />
          </el-tab-pane>
          <el-tab-pane label="学生体检信息" name="exam">
            <ExamInfo />
          </el-tab-pane>
          <el-tab-pane label="个人中心" name="profile">
            <Profile />
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import StudentManagement from '../components/StudentManagement.vue'
import StaffManagement from '../components/StaffManagement.vue'
import ExamInfo from '../components/ExamInfo.vue'
import Profile from '../components/Profile.vue'

const router = useRouter()
const activeTab = ref('student')

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.logo {
  font-size: 20px;
  font-weight: bold;
}
</style>
