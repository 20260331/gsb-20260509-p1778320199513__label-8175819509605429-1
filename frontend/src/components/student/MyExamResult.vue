<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>我的体检结果</span>
      </div>
    </template>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="packageName" label="套餐" />
      <el-table-column prop="createTime" label="时间" />
      <el-table-column prop="resultData" label="结果内容">
        <template #default="scope">
          <div v-if="scope.row.resultData">{{ scope.row.resultData }}</div>
          <el-tag v-else type="info">待录入</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const tableData = ref([])
const user = JSON.parse(localStorage.getItem('user'))

const fetchData = async () => {
  const stuRes = await axios.get(`/api/student/getByUserId/${user.id}`)
  if (stuRes.data.code === 200 && stuRes.data.data) {
    const studentId = stuRes.data.data.id
    const res = await axios.get(`/api/exam-result/getByStudentId/${studentId}`)
    if (res.data.code === 200) {
      tableData.value = res.data.data
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>
