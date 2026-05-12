<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-form-item label="专业筛选">
        <el-input v-model="searchForm.major" placeholder="专业" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="studentName" label="学生姓名" />
      <el-table-column prop="packageName" label="套餐" />
      <el-table-column prop="createTime" label="预约时间" />
      <el-table-column prop="resultData" label="结果">
        <template #default="scope">
           <span v-if="scope.row.resultData">{{ scope.row.resultData }}</span>
           <el-tag v-else type="warning">待录入</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleEdit(scope.row)">录入结果</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="录入体检结果">
      <el-form :model="form" label-width="100px">
        <el-form-item label="学生">
          <el-input v-model="form.studentName" disabled />
        </el-form-item>
        <el-form-item label="套餐">
          <el-input v-model="form.packageName" disabled />
        </el-form-item>
        <el-form-item label="体检结果">
          <el-input v-model="form.resultData" type="textarea" :rows="4" placeholder="请输入体检各项指标结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const searchForm = ref({
  major: ''
})

const tableData = ref([])
const dialogVisible = ref(false)
const form = ref({})
const user = JSON.parse(localStorage.getItem('user'))

const fetchData = async () => {
  const res = await axios.get('/api/exam-result/list', { params: searchForm.value })
  if (res.data.code === 200) {
    tableData.value = res.data.data
  }
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  // Need to get staff ID from user ID
  // For simplicity, I'll fetch staff info or just send staff ID if I had it in session.
  // The backend user object has ID, but Staff entity has its own ID.
  // Let's assume we fetch staff ID first.
  
  const staffRes = await axios.get(`/api/staff/getByUserId/${user.id}`)
  if (staffRes.data.code === 200 && staffRes.data.data) {
    form.value.staffId = staffRes.data.data.id
    
    const res = await axios.post('/api/exam-result/save', form.value)
    if (res.data.code === 200) {
      ElMessage.success('录入成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.data.message)
    }
  } else {
    ElMessage.error('无法获取职工信息')
  }
}

onMounted(() => {
  fetchData()
})
</script>
