<template>
  <div class="task-manage">
    <div class="page-header">
      <h3>作业管理</h3>
      <el-button type="primary" icon="el-icon-plus" size="medium" @click="showCreateDialog">发布作业</el-button>
    </div>

    <el-card shadow="never" class="content-card">
      <!-- 筛选区域 -->
      <div class="filter-bar">
        <el-select v-model="query.courseId" placeholder="按课程筛选" clearable size="medium" class="filter-select" @change="fetchTasks">
          <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
        </el-select>
      </div>

      <el-table :data="taskList" stripe v-loading="loading" class="data-table">
        <el-table-column prop="id" label="序号" width="70" align="center" />
        <el-table-column prop="title" label="作业标题" min-width="200" align="center" />
        <el-table-column label="截止时间" width="180" align="center">
          <template slot-scope="scope">
            <span class="deadline-text">{{ scope.row.deadline || '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" type="info" size="small" effect="plain">草稿</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success" size="small">已发布</el-tag>
            <el-tag v-else type="warning" size="small">已截止</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="340" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="small" type="primary" plain @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 0" size="small" type="success" plain @click="handlePublish(scope.row.id)">发布</el-button>
            <el-button size="small" type="info" plain @click="goSubmissions(scope.row.id)">批改</el-button>
            <el-button size="small" type="warning" plain @click="goStatistics(scope.row.id)">统计</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        :current-page.sync="query.current"
        :page-size.sync="query.size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchTasks"
        class="table-pagination"
      />

      <EmptyState v-if="!loading && taskList.length === 0" text="暂无作业数据" subtext="点击「发布作业」按钮创建第一个作业" />
    </el-card>

    <!-- 创建/编辑作业弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="680px" @closed="resetForm" class="form-dialog">
      <el-form ref="taskForm" :model="form" :rules="formRules" label-width="100px" class="task-form">
        <el-form-item label="关联课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程" class="full-width">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入作业标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="作业描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入作业描述" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="实验要求" prop="requirement">
          <el-input v-model="form.requirement" type="textarea" :rows="4" placeholder="请输入实验要求" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker
            v-model="form.deadline"
            type="datetime"
            placeholder="选择截止时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            class="full-width"
          />
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
import { listTasks, createTask, updateTask, deleteTask, publishTask } from '@/TaskModule/api'
import { listCourses } from '@/CourseModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'TaskManage',
  components: { EmptyState },
  data() {
    return {
      loading: false,
      taskList: [],
      total: 0,
      courses: [],
      query: { current: 1, size: 10, courseId: null },
      dialogVisible: false,
      dialogTitle: '发布作业',
      editingId: null,
      form: { title: '', description: '', requirement: '', deadline: '', courseId: null },
      formRules: {
        title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
        description: [{ required: true, message: '请输入作业描述', trigger: 'blur' }],
        requirement: [{ required: true, message: '请输入实验要求', trigger: 'blur' }],
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }]
      }
    }
  },
  async created() {
    await this.fetchCourses()
    this.fetchTasks()
  },
  methods: {
    async fetchCourses() {
      try {
        const res = await listCourses()
        this.courses = res.data || []
      } catch (e) { console.error(e) }
    },
    async fetchTasks() {
      this.loading = true
      try {
        const res = await listTasks(this.query)
        this.taskList = res.data.records || []
        this.total = res.data.total
      } finally { this.loading = false }
    },
    showCreateDialog() {
      this.dialogTitle = '发布作业'
      this.editingId = null
      this.form = { title: '', description: '', requirement: '', deadline: '', courseId: null }
      this.dialogVisible = true
    },
    showEditDialog(row) {
      this.dialogTitle = '编辑作业'
      this.editingId = row.id
      this.form = { title: row.title, description: row.description, requirement: row.requirement, deadline: row.deadline, courseId: row.courseId }
      this.dialogVisible = true
    },
    resetForm() {
      this.$refs.taskForm?.resetFields()
    },
    async handleSave() {
      this.$refs.taskForm.validate(async valid => {
        if (!valid) return
        try {
          if (this.editingId) {
            await updateTask({ id: this.editingId, ...this.form })
          } else {
            await createTask(this.form)
          }
          this.$message.success('保存成功')
          this.dialogVisible = false
          this.fetchTasks()
        } catch (e) { console.error(e) }
      })
    },
    async handlePublish(id) {
      try {
        await publishTask(id)
        this.$message.success('发布成功')
        this.fetchTasks()
      } catch (e) { console.error(e) }
    },
    async handleDelete(id) {
      try {
        await this.$confirm('确认删除该作业？', '提示', { type: 'warning' })
        await deleteTask(id)
        this.$message.success('删除成功')
        this.fetchTasks()
      } catch (e) { /* cancelled */ }
    },
    goSubmissions(taskId) {
      this.$router.push(`/teacher/task/${taskId}/submissions`)
    },
    goStatistics(taskId) {
      this.$router.push(`/teacher/task/${taskId}/statistics`)
    }
  }
}
</script>

<style scoped>
.task-manage {
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

/* 筛选栏 */
.filter-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select {
  width: 220px;
}

/* 表格 */
.data-table {
  font-size: 14px;
}

.data-table >>> th {
  background-color: #fafafa !important;
  font-weight: 600;
  color: #303133;
}

.deadline-text {
  color: #606266;
  font-size: 13px;
}

/* 分页 */
.table-pagination {
  padding: 20px 0 4px;
  text-align: right;
}

/* 弹窗 */
.form-dialog >>> .el-dialog__body {
  padding: 24px 32px !important;
}

.task-form .el-form-item {
  margin-bottom: 22px;
}

.full-width {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
