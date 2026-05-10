<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>我的医保信息</span>
      </div>
    </template>
    <div v-if="loading">加载中...</div>
    <div v-else>
      <el-descriptions border :column="1">
        <el-descriptions-item label="参保状态">
          <el-tag :type="insurance.status === 1 ? 'success' : 'danger'">
            {{ insurance.status === 1 ? '已参保' : '未参保' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="起始年度" v-if="insurance.status === 1">
          {{ insurance.startYear }}
        </el-descriptions-item>
        <el-descriptions-item label="参保年数" v-if="insurance.status === 1">
          {{ insurance.duration }} 年
        </el-descriptions-item>
        <el-descriptions-item label="参保金额" v-if="insurance.status === 1">
          ¥ {{ insurance.amount }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div style="margin-top: 20px; text-align: center;">
        <el-button v-if="insurance.status === 1" type="danger" @click="handleInsurance(0)">停保</el-button>
        <el-button v-else type="primary" @click="handleInsurance(1)">参保</el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const insurance = ref({
  status: 0
})
const student = ref({})
const loading = ref(true)
const user = JSON.parse(localStorage.getItem('user'))

const fetchStudentAndInsurance = async () => {
  loading.value = true
  try {
    // 1. Get Student ID
    const stuRes = await axios.get(`/api/student/getByUserId/${user.id}`)
    if (stuRes.data.code === 200 && stuRes.data.data) {
      student.value = stuRes.data.data
      
      // 2. Get Insurance
      const insRes = await axios.get(`/api/insurance/getByStudentId/${student.value.id}`)
      if (insRes.data.code === 200 && insRes.data.data) {
        insurance.value = insRes.data.data
      } else {
        // No record implies uninsured
        insurance.value = { status: 0, studentId: student.value.id }
      }
    }
  } finally {
    loading.value = false
  }
}

const handleInsurance = (status) => {
  const action = status === 1 ? '参保' : '停保'
  ElMessageBox.confirm(`确定要${action}吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const data = { ...insurance.value, status, studentId: student.value.id }
    if (status === 1) {
      // Default values for new insurance
      const now = new Date()
      data.startYear = now.getFullYear().toString()
      data.duration = 1
      data.amount = 350.00 // Default amount
    }
    
    const res = await axios.post('/api/insurance/update', data)
    if (res.data.code === 200) {
      ElMessage.success(`${action}成功`)
      fetchStudentAndInsurance()
    } else {
      ElMessage.error(res.data.message)
    }
  })
}

onMounted(() => {
  fetchStudentAndInsurance()
})
</script>
