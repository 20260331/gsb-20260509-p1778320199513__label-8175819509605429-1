<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-form-item label="专业筛选">
        <el-input v-model="searchForm.major" placeholder="输入专业名称" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="studentName" label="学生姓名" />
      <el-table-column prop="packageName" label="体检套餐" />
      <el-table-column prop="resultData" label="体检结果" />
      <el-table-column prop="createTime" label="体检时间" />
      <el-table-column prop="staffId" label="录入职工ID" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const searchForm = ref({
  major: ''
})

const tableData = ref([])

const fetchData = async () => {
  const res = await axios.get('/api/exam-result/list', { params: searchForm.value })
  if (res.data.code === 200) {
    tableData.value = res.data.data
  }
}

onMounted(() => {
  fetchData()
})
</script>
