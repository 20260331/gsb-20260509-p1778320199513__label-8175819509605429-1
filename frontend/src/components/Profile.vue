<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>修改密码</span>
      </div>
    </template>
    <el-form :model="form" label-width="100px">
      <el-form-item label="新密码">
        <el-input v-model="form.password" type="password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updatePassword">确认修改</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const user = JSON.parse(localStorage.getItem('user'))
const form = ref({
  id: user.id,
  password: ''
})

const updatePassword = async () => {
  try {
    const res = await axios.post('/api/user/updatePassword', form.value)
    if (res.data.code === 200) {
      ElMessage.success('密码修改成功')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('修改失败')
  }
}
</script>
