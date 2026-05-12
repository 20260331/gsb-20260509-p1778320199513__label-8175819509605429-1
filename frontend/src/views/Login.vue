<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>西安文理学院医保系统</h2>
        </div>
      </template>
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="账号">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="学生" value="STUDENT" />
            <el-option label="职工" value="STAFF" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const form = ref({
  username: '',
  password: '',
  role: 'STUDENT'
})

const handleLogin = async () => {
  try {
    const res = await axios.post('/api/user/login', form.value)
    if (res.data.code === 200) {
      const user = res.data.data
      localStorage.setItem('user', JSON.stringify(user))
      ElMessage.success('登录成功')
      if (user.role === 'ADMIN') {
        router.push('/admin')
      } else if (user.role === 'STUDENT') {
        router.push('/student')
      } else if (user.role === 'STAFF') {
        router.push('/staff')
      }
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('登录失败')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
}
.card-header {
  text-align: center;
}
</style>
