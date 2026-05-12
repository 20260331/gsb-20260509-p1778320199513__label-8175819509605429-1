<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>体检预约</span>
      </div>
    </template>
    
    <div v-if="currentAppointment">
      <el-alert title="您已预约体检" type="success" :description="`套餐: ${currentAppointment.packageName}`" show-icon />
    </div>

    <div v-else>
      <el-table :data="packages" style="width: 100%">
        <el-table-column prop="name" label="套餐名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="price" label="价格">
          <template #default="scope">
            ¥ {{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleBook(scope.row)">预约</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const packages = ref([])
const currentAppointment = ref(null)
const student = ref({})
const user = JSON.parse(localStorage.getItem('user'))

const init = async () => {
  // 1. Get Student
  const stuRes = await axios.get(`/api/student/getByUserId/${user.id}`)
  if (stuRes.data.code === 200 && stuRes.data.data) {
    student.value = stuRes.data.data
    
    // 2. Check existing appointments (ExamResult with empty data?)
    // Actually, let's just fetch all results and see if any is "pending" (no result data)
    // For simplicity, let's just list packages. If strict, we check.
    // The requirement is "Student can view different packages".
    // "Staff enters result based on selected package".
    // So student MUST select.
    
    // Let's check if there is an exam result entry for this student that has no result data.
    const resRes = await axios.get(`/api/exam-result/getByStudentId/${student.value.id}`)
    if (resRes.data.code === 200) {
      const results = resRes.data.data
      // Assuming if resultData is null or empty, it's an appointment
      const pending = results.find(r => !r.resultData)
      if (pending) {
        currentAppointment.value = pending
      }
    }
  }

  // 3. Get Packages
  const pkgRes = await axios.get('/api/exam-package/list')
  if (pkgRes.data.code === 200) {
    packages.value = pkgRes.data.data
  }
}

const handleBook = (pkg) => {
  ElMessageBox.confirm(`确定要预约 ${pkg.name} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    const data = {
      studentId: student.value.id,
      packageId: pkg.id,
      createTime: new Date()
      // resultData is null
    }
    const res = await axios.post('/api/exam-result/save', data)
    if (res.data.code === 200) {
      ElMessage.success('预约成功')
      init()
    } else {
      ElMessage.error(res.data.message)
    }
  })
}

onMounted(() => {
  init()
})
</script>
