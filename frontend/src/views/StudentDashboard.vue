<template>
  <div class="common-layout">
    <el-container>
      <el-header class="header">
        <div class="logo">西安文理学院医保系统 - 学生端</div>
        <el-button type="danger" @click="logout">退出登录</el-button>
      </el-header>
      <el-main>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="个人信息" name="info">
            <StudentInfo />
          </el-tab-pane>
          <el-tab-pane label="我的医保" name="insurance">
            <MyInsurance />
          </el-tab-pane>
          <el-tab-pane label="体检预约" name="appointment">
            <ExamAppointment />
          </el-tab-pane>
          <el-tab-pane label="体检结果" name="result">
            <MyExamResult />
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
import StudentInfo from '../components/student/StudentInfo.vue'
import MyInsurance from '../components/student/MyInsurance.vue'
import ExamAppointment from '../components/student/ExamAppointment.vue'
import MyExamResult from '../components/student/MyExamResult.vue'
import Profile from '../components/Profile.vue'

const router = useRouter()
const activeTab = ref('info')

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.header {
  background-color: #67C23A;
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
