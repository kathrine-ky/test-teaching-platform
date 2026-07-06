<template>
  <div class="course-manage">
    <div class="page-header">
      <h3>课程信息管理</h3>
      <el-button type="primary" icon="el-icon-plus" size="medium" @click="showCreateDialog">新增课程</el-button>
    </div>

    <el-card shadow="never" class="content-card">
      <el-table :data="courseList" stripe v-loading="loading" class="data-table">
        <el-table-column prop="id" label="序号" width="70" align="center" />
        <el-table-column prop="courseName" label="课程名称" min-width="200" align="center" />
        <el-table-column prop="description" label="课程描述" min-width="300" show-overflow-tooltip align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="small" type="primary" plain @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <EmptyState v-if="!loading && courseList.length === 0" text="暂无课程数据" subtext="点击「新增课程」按钮添加第一个课程" />
    </el-card>

    <!-- 新增/编辑课程弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="560px" @closed="resetForm" class="form-dialog">
      <el-form ref="courseForm" :model="form" :rules="formRules" label-width="100px" class="course-form">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入课程描述" maxlength="300" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="medium">取消</el-button>
        <el-button type="primary" @click="handleSave" size="medium">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCourses, createCourse, updateCourse, deleteCourse } from '@/CourseModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'CourseManage',
  components: { EmptyState },
  data() {
    return {
      loading: false,
      courseList: [],
      dialogVisible: false,
      dialogTitle: '新增课程',
      editingId: null,
      form: { courseName: '', description: '' },
      formRules: {
        courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
      }
    }
  },
  created() { this.fetchCourses() },
  methods: {
    async fetchCourses() {
      this.loading = true
      try {
        const res = await listCourses()
        this.courseList = res.data || []
      } finally { this.loading = false }
    },
    showCreateDialog() {
      this.dialogTitle = '新增课程'
      this.editingId = null
      this.form = { courseName: '', description: '' }
      this.dialogVisible = true
    },
    showEditDialog(row) {
      this.dialogTitle = '编辑课程'
      this.editingId = row.id
      this.form = { courseName: row.courseName, description: row.description }
      this.dialogVisible = true
    },
    resetForm() {
      this.$refs.courseForm?.resetFields()
    },
    async handleSave() {
      this.$refs.courseForm.validate(async valid => {
        if (!valid) return
        try {
          if (this.editingId) {
            await updateCourse(this.editingId, this.form)
          } else {
            await createCourse(this.form)
          }
          this.$message.success('保存成功')
          this.dialogVisible = false
          this.fetchCourses()
        } catch (e) { console.error(e) }
      })
    },
    async handleDelete(id) {
      try {
        await this.$confirm('确认删除该课程？', '提示', { type: 'warning' })
        await deleteCourse(id)
        this.$message.success('删除成功')
        this.fetchCourses()
      } catch (e) { /* cancelled */ }
    }
  }
}
</script>

<style scoped>
.course-manage {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h3 {
  color: #303133;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.content-card {
  border: 1px solid #ebeef5;
}

.data-table {
  font-size: 14px;
}

.data-table >>> th {
  background-color: #fafafa !important;
  font-weight: 600;
  color: #303133;
}

/* 弹窗 */
.form-dialog >>> .el-dialog__body {
  padding: 24px 32px !important;
}

.course-form .el-form-item {
  margin-bottom: 22px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
