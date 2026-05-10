<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>个人基础信息</span>
        <el-button type="primary" @click="updateInfo">保存修改</el-button>
      </div>
    </template>
    <el-form :model="form" label-width="100px" :disabled="loading">
      <el-row>
        <el-col :span="12">
          <el-form-item label="学号">
            <el-input v-model="form.studentNumber" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="姓名">
            <el-input v-model="form.name" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="性别">
            <el-input v-model="form.gender" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出生日期">
            <el-input v-model="form.birthDate" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="证件类型">
            <el-input v-model="form.idType" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="证件号">
            <el-input v-model="form.idNumber" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-divider content-position="left">可修改信息</el-divider>
      <el-row>
        <el-col :span="12">
          <el-form-item label="电话">
            <el-input v-model="form.phone" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="专业">
            <el-input v-model="form.major" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="院系">
            <el-input v-model="form.department" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="户籍性质">
            <el-input v-model="form.householdType" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="电子邮件">
            <el-input v-model="form.email" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const form = ref({})
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('user'))

const fetchInfo = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/student/getByUserId/${user.id}`)
    if (res.data.code === 200 && res.data.data) {
      form.value = res.data.data
    }
  } finally {
    loading.value = false
  }
}

const updateInfo = async () => {
  const res = await axios.post('/api/student/update', form.value)
  if (res.data.code === 200) {
    ElMessage.success('保存成功')
  } else {
    ElMessage.error(res.data.message)
  }
}

onMounted(() => {
  fetchInfo()
})
</script>
