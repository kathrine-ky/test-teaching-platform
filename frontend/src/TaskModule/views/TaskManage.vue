<template>
  <div class="task-manage">
    <div class="page-header">
      <div class="header-left">
        <span class="header-icon">📝</span>
        <h3>作业管理</h3>
      </div>
      <el-button type="primary" icon="el-icon-plus" size="medium" round @click="showCreateDialog">发布作业</el-button>
    </div>

    <div class="game-card content-card">
      <div class="filter-bar">
        <el-select v-model="query.courseId" placeholder="按课程筛选" clearable size="medium" class="filter-select" @change="fetchTasks">
          <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
        </el-select>
      </div>

      <el-table :data="taskList" stripe v-loading="loading" class="data-table">
        <el-table-column prop="id" label="序号" width="70" align="center" />
        <el-table-column prop="title" label="作业标题" min-width="180" align="center" />
        <el-table-column label="截止时间" width="170" align="center">
          <template slot-scope="scope">
            <span class="deadline-text">{{ scope.row.deadline || '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.status === 0" class="status-tag tag-draft">草稿</span>
            <span v-else-if="scope.row.status === 1" class="status-tag tag-published">已发布</span>
            <span v-else class="status-tag tag-closed">已截止</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="380" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="small" type="primary" plain round @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 0" size="small" type="success" plain round @click="handlePublish(scope.row.id)">发布</el-button>
            <el-button v-if="scope.row.status === 1" size="small" type="warning" plain round @click="handleWithdraw(scope.row.id)">撤回</el-button>
            <el-button size="small" type="info" plain round @click="goSubmissions(scope.row.id)">批改</el-button>
            <el-button size="small" type="warning" plain round @click="goStatistics(scope.row.id)">统计</el-button>
            <el-button size="small" type="danger" plain round @click="handleDelete(scope.row.id)">删除</el-button>
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
    </div>

    <!-- 创建/编辑作业弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="750px" @closed="resetForm" class="game-dialog">
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
        <el-form-item label="截止时间" prop="deadline" required>
          <el-date-picker
            v-model="form.deadline"
            type="datetime"
            placeholder="请选择截止时间（必填）"
            value-format="yyyy-MM-dd HH:mm:ss"
            class="full-width"
          />
        </el-form-item>
        <el-divider content-position="left">📎 附件资料（可选）</el-divider>
        <el-form-item label="上传文件">
          <el-upload
            ref="uploadRef"
            :action="''"
            :auto-upload="false"
            :file-list="form.files"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :limit="10"
            multiple
            accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.zip,.rar,.jpg,.png"
            class="task-upload"
          >
            <el-button size="small" type="primary" plain round icon="el-icon-upload2">选择文件</el-button>
            <div slot="tip" class="el-upload__tip">支持 PDF、Word、Excel、PPT、TXT、图片、压缩包等格式，单文件不超过50MB</div>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="editingId && existingFiles.length > 0" label="已有附件">
          <div class="existing-files">
            <div v-for="f in existingFiles" :key="f.id" class="file-chip">
              <i :class="fileIcon(f.fileType)"></i>
              <span class="file-name" :title="f.fileName">{{ f.fileName }}</span>
              <el-button type="text" icon="el-icon-delete" class="file-delete" @click="handleDeleteFile(f.id)"></el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="medium" round>取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving" size="medium" round>保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTasks, createTask, updateTask, deleteTask, publishTask, withdrawTask,
         uploadTaskFile, getTaskFiles, deleteTaskFile } from '@/TaskModule/api'
import { listCourses } from '@/CourseModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'TaskManage',
  components: { EmptyState },
  data() {
    return {
      loading: false, saving: false,
      taskList: [], total: 0, courses: [],
      query: { current: 1, size: 10, courseId: null },
      dialogVisible: false, dialogTitle: '发布作业', editingId: null, existingFiles: [],
      form: { title: '', description: '', requirement: '', deadline: '', courseId: null, files: [] },
      formRules: {
        title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
        description: [{ required: true, message: '请输入作业描述', trigger: 'blur' }],
        requirement: [{ required: true, message: '请输入实验要求', trigger: 'blur' }],
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
        deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
      }
    }
  },
  async created() {
    await this.fetchCourses()
    this.fetchTasks()
  },
  methods: {
    fileIcon(type) {
      const map = { pdf: 'el-icon-document', doc: 'el-icon-document', docx: 'el-icon-document',
        xls: 'el-icon-s-data', xlsx: 'el-icon-s-data', ppt: 'el-icon-s-marketing', pptx: 'el-icon-s-marketing',
        zip: 'el-icon-folder', rar: 'el-icon-folder', jpg: 'el-icon-picture', png: 'el-icon-picture' }
      return map[(type || '').toLowerCase()] || 'el-icon-document'
    },
    async fetchCourses() { try { const res = await listCourses(); this.courses = res.data || [] } catch (e) { console.error(e) } },
    async fetchTasks() { this.loading = true; try { const res = await listTasks(this.query); this.taskList = res.data.records || []; this.total = res.data.total } finally { this.loading = false } },
    showCreateDialog() { this.dialogTitle = '发布作业'; this.editingId = null; this.existingFiles = []; this.form = { title: '', description: '', requirement: '', deadline: '', courseId: null, files: [] }; this.dialogVisible = true; this.$nextTick(() => this.$refs.uploadRef?.clearFiles()) },
    async showEditDialog(row) { this.dialogTitle = '编辑作业'; this.editingId = row.id; this.form = { title: row.title, description: row.description, requirement: row.requirement, deadline: row.deadline, courseId: row.courseId, files: [] }; this.dialogVisible = true; this.$nextTick(() => this.$refs.uploadRef?.clearFiles()); try { const res = await getTaskFiles(row.id); this.existingFiles = res.data || [] } catch (e) { this.existingFiles = [] } },
    resetForm() { this.$refs.taskForm?.resetFields(); this.existingFiles = [] },
    handleFileChange(file, fileList) { this.form.files = fileList },
    handleFileRemove(file, fileList) { this.form.files = fileList },
    async handleDeleteFile(fileId) { try { await deleteTaskFile(fileId); this.existingFiles = this.existingFiles.filter(f => f.id !== fileId); this.$message.success('附件已删除') } catch (e) { console.error(e) } },
    buildTaskPayload() {
      const { title, description, requirement, deadline, courseId } = this.form
      return {
        title, description, requirement, courseId,
        deadline: deadline || null
      }
    },
    async handleSave() {
      this.$refs.taskForm.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          let taskId = this.editingId
          const payload = this.buildTaskPayload()
          if (taskId) {
            await updateTask({ id: taskId, ...payload })
          } else {
            const res = await createTask(payload)
            taskId = res.data?.id
            if (!taskId) {
              this.$message.error('创建失败，未获取到任务ID')
              return
            }
          }
          // 上传新附件
          if (taskId && this.form.files.length > 0) {
            let failCount = 0
            for (const f of this.form.files) {
              if (!f.raw) continue
              const fd = new FormData()
              fd.append('file', f.raw)
              try { await uploadTaskFile(taskId, fd) } catch (e) { failCount++; console.error('上传失败:', f.name, e) }
            }
            if (failCount > 0) {
              this.$message.warning(`任务已保存，但有 ${failCount} 个附件上传失败`)
            }
          }
          this.$message.success('保存成功')
          this.dialogVisible = false
          this.fetchTasks()
        } catch (e) {
          // 错误消息已由请求拦截器统一弹出，此处不再重复
          console.error('[保存作业失败]', e)
        } finally {
          this.saving = false
        }
      })
    },
    async handlePublish(id) { try { await publishTask(id); this.$message.success('发布成功'); this.fetchTasks() } catch (e) { console.error(e) } },
    async handleWithdraw(id) { try { await this.$confirm('撤回后任务将变为草稿状态，学生将不可见，确认撤回？', '提示', { type: 'warning' }); await withdrawTask(id); this.$message.success('已撤回为草稿'); this.fetchTasks() } catch (e) {} },
    async handleDelete(id) { try { await this.$confirm('确认删除该作业？删除后关联附件和学生提交也将清除。', '提示', { type: 'warning' }); await deleteTask(id); this.$message.success('删除成功'); this.fetchTasks() } catch (e) {} },
    goSubmissions(taskId) { this.$router.push(`/teacher/task/${taskId}/submissions`) },
    goStatistics(taskId) { this.$router.push(`/teacher/task/${taskId}/statistics`) }
  }
}
</script>

<style scoped>
.task-manage { max-width: 1200px; margin: 0 auto; }

.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }

.header-left { display: flex; align-items: center; gap: 10px; }
.header-icon { font-size: 28px; }
.header-left h3 { color: #2D3A2F; font-size: 22px; font-weight: 700; margin: 0; }

.content-card { padding: 20px 24px; border: none !important; }

.filter-bar { margin-bottom: 20px; display: flex; align-items: center; gap: 12px; }
.filter-select { width: 220px; }

.data-table { font-size: 14px; }
.data-table >>> th { background-color: #F5FBF7 !important; font-weight: 700; color: #2D3A2F; }

.deadline-text { color: #5A6B5F; font-size: 13px; }

.status-tag { font-size: 11px; font-weight: 600; padding: 3px 12px; border-radius: 20px; }
.tag-draft { background: #F0F4F8; color: #6A7A8A; }
.tag-published { background: #E8FFF1; color: #2E7A4A; }
.tag-closed { background: #FFE8E0; color: #A85A3A; }

.table-pagination { padding: 20px 0 4px; text-align: right; }

/* Dialog */
.game-dialog >>> .el-dialog__header { background: linear-gradient(135deg, #E8FFF1, #F5FBF7); border-bottom: 1px solid #D8EFE0; padding: 20px 24px; border-radius: 16px 16px 0 0; }
.game-dialog >>> .el-dialog__title { font-weight: 700; color: #2D3A2F; font-size: 16px; }
.game-dialog >>> .el-dialog__body { padding: 24px !important; }

.task-form .el-form-item { margin-bottom: 22px; }
.full-width { width: 100%; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
.task-upload { width: 100%; }

.existing-files { display: flex; flex-wrap: wrap; gap: 8px; }
.file-chip { display: flex; align-items: center; gap: 6px; padding: 6px 14px; background: #F5FBF7; border: 1px solid #E0EFE5; border-radius: 10px; font-size: 13px; color: #2D3A2F; }
.file-chip .file-name { max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.file-delete { color: #E8885A !important; padding: 4px; }
</style>
