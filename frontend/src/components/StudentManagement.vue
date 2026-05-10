<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="demo-form-inline">
      <el-form-item label="关键词">
        <el-input v-model="searchForm.keyword" placeholder="姓名/学号" />
      </el-form-item>
      <el-form-item label="排序字段">
        <el-select v-model="searchForm.sortField" placeholder="选择字段" clearable>
           <el-option label="学号" value="student_number" />
           <el-option label="姓名" value="name" />
        </el-select>
      </el-form-item>
       <el-form-item label="排序方式">
        <el-select v-model="searchForm.sortOrder" placeholder="选择顺序" clearable>
           <el-option label="升序" value="asc" />
           <el-option label="降序" value="desc" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchData">查询</el-button>
        <el-button type="success" @click="handleAdd">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="studentNumber" label="学号" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="gender" label="性别" />
      <el-table-column prop="major" label="专业" />
      <el-table-column prop="department" label="院系" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增学生' : '编辑学生'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="账号" v-if="dialogType === 'add'">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" v-if="dialogType === 'add'">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="form.studentNumber" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birthDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="证件类型">
          <el-input v-model="form.idType" />
        </el-form-item>
        <el-form-item label="证件号">
          <el-input v-model="form.idNumber" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="form.major" />
        </el-form-item>
        <el-form-item label="院系">
          <el-input v-model="form.department" />
        </el-form-item>
        <el-form-item label="户籍性质">
          <el-input v-model="form.householdType" />
        </el-form-item>
        <el-form-item label="电子邮件">
          <el-input v-model="form.email" />
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
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = ref({
  keyword: '',
  sortField: '',
  sortOrder: ''
})

const tableData = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add') // add or edit
const form = ref({})

const fetchData = async () => {
  const res = await axios.get('/api/student/list', { params: searchForm.value })
  if (res.data.code === 200) {
    tableData.value = res.data.data
  }
}

const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {}
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该学生吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await axios.post(`/api/student/delete/${row.id}`)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    } else {
      ElMessage.error(res.data.message)
    }
  })
}

const submitForm = async () => {
  const url = dialogType.value === 'add' ? '/api/student/add' : '/api/student/update'
  const res = await axios.post(url, form.value)
  if (res.data.code === 200) {
    ElMessage.success(dialogType.value === 'add' ? '新增成功' : '修改成功')
    dialogVisible.value = false
    fetchData()
  } else {
    ElMessage.error(res.data.message)
  }
}

onMounted(() => {
  fetchData()
})
</script>
