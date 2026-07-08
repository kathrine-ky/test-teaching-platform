<template>
  <div class="task-list">
    <div class="page-header">
      <div class="header-left">
        <span class="header-icon">🎯</span>
        <h3>实验任务列表</h3>
      </div>
      <div class="header-meta">
        <span class="meta-count">{{ taskList.length }} 个任务</span>
      </div>
    </div>

    <div class="task-grid" v-loading="loading">
      <div class="game-card task-card" v-for="task in taskList" :key="task.id">
        <div class="task-card-header">
          <div class="task-number">#{{ task.id }}</div>
          <div class="task-status-tag">
            <span v-if="task.myScore != null" class="status-tag tag-scored">已批改</span>
            <span v-else-if="task.submitted" class="status-tag tag-submitted">已提交</span>
            <span v-else class="status-tag tag-pending">未提交</span>
          </div>
        </div>
        <h4 class="task-title">{{ task.title }}</h4>
        <p class="task-desc">{{ task.description || '暂无描述' }}</p>
        <div class="task-deadline" :class="{ 'deadline-urgent': isUrgent(task.deadline) }">
          <i class="el-icon-alarm-clock"></i>
          <span>{{ task.deadline || '无截止时间' }}</span>
        </div>
        <div class="task-score" v-if="task.myScore != null">
          <div class="score-ring">
            <span class="score-value">{{ task.myScore }}</span>
            <span class="score-unit">分</span>
          </div>
        </div>
        <div class="task-actions">
          <el-button size="small" plain round @click="showDetailDialog(task)">查看详情</el-button>
          <el-button size="small" type="primary" round @click="goSubmit(task)">
            <i class="el-icon-upload2"></i> 提交作业
          </el-button>
        </div>
      </div>
      <EmptyState v-if="!loading && taskList.length === 0" text="暂无实验任务" subtext="教师发布作业后将在此显示" />
    </div>

    <el-pagination
      v-if="total > 0"
      :current-page.sync="query.current"
      :page-size.sync="query.size"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="fetchTasks"
      class="table-pagination"
    />

    <!-- 任务详情弹窗 -->
    <el-dialog :title="'任务详情 - ' + detailTask.title" :visible.sync="detailVisible" width="700px" class="game-dialog">
      <div class="detail-body">
        <div class="detail-section">
          <div class="detail-label">
            <i class="el-icon-document"></i> 任务描述
          </div>
          <div class="detail-content">{{ detailTask.description || '暂无' }}</div>
        </div>
        <div class="detail-section">
          <div class="detail-label">
            <i class="el-icon-edit-outline"></i> 实验要求
          </div>
          <div class="detail-content detail-requirement">{{ detailTask.requirement || '暂无' }}</div>
        </div>
        <div class="detail-section">
          <div class="detail-label">
            <i class="el-icon-time"></i> 截止时间
          </div>
          <div class="detail-content deadline-highlight">{{ detailTask.deadline || '未设置' }}</div>
        </div>
        <div class="detail-section">
          <div class="detail-label">
            <i class="el-icon-paperclip"></i> 教师附件资料
          </div>
          <div v-if="detailFiles.length === 0" class="detail-content detail-empty">暂无附件</div>
          <div v-else class="detail-file-list">
            <div v-for="f in detailFiles" :key="f.id" class="detail-file-item" @click="downloadFile(f)">
              <span class="file-icon-box"><i :class="fileIcon(f.fileType)"></i></span>
              <span class="file-name-text">{{ f.fileName }}</span>
              <span class="file-size-text">{{ formatSize(f.fileSize) }}</span>
              <el-button type="text" size="small" icon="el-icon-download" class="dl-btn" @click.stop="downloadFile(f)"></el-button>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false" size="medium" round>关闭</el-button>
        <el-button type="primary" @click="goSubmit(detailTask)" size="medium" round>去提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTasks, getTaskDetail, downloadTaskFile } from '@/TaskModule/api'
import { getMySubmission } from '@/SubmitModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'TaskList',
  components: { EmptyState },
  data() {
    return {
      loading: false, taskList: [], total: 0,
      query: { current: 1, size: 10 },
      detailVisible: false,
      detailTask: {},
      detailFiles: []
    }
  },
  created() { this.fetchTasks() },
  methods: {
    isUrgent(deadline) {
      if (!deadline) return false
      try {
        const d = new Date(deadline)
        const now = new Date()
        return d.getTime() - now.getTime() < 3 * 24 * 3600 * 1000
      } catch { return false }
    },
    fileIcon(type) {
      const map = { pdf: 'el-icon-document', doc: 'el-icon-document', docx: 'el-icon-document',
        xls: 'el-icon-s-data', xlsx: 'el-icon-s-data', ppt: 'el-icon-s-marketing', pptx: 'el-icon-s-marketing',
        zip: 'el-icon-folder', rar: 'el-icon-folder', jpg: 'el-icon-picture', png: 'el-icon-picture', txt: 'el-icon-tickets' }
      return map[(type || '').toLowerCase()] || 'el-icon-document'
    },
    formatSize(bytes) {
      if (!bytes) return ''
      if (bytes < 1024) return bytes + 'B'
      if (bytes < 1048576) return (bytes / 1024).toFixed(1) + 'KB'
      return (bytes / 1048576).toFixed(1) + 'MB'
    },
    async downloadFile(file) {
      try {
        const response = await downloadTaskFile(file.id)
        const blob = response.data
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = file.fileName || 'download'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (e) {
        console.error('文件下载失败:', e)
        this.$message.error('文件下载失败')
      }
    },
    async fetchTasks() {
      this.loading = true
      try {
        const res = await listTasks(this.query)
        const tasks = res.data.records || []
        for (const t of tasks) {
          try {
            const sub = await getMySubmission(t.id)
            const s = sub.data?.submission
            t.submitted = s != null
            if (s) { t.myScore = s.score }
          } catch (e) { t.submitted = false }
        }
        this.taskList = tasks
        this.total = res.data.total
      } catch (e) {
        console.error('获取任务列表失败:', e)
      } finally { this.loading = false }
    },
    async showDetailDialog(row) {
      this.detailTask = row
      this.detailFiles = []
      this.detailVisible = true
      try {
        const res = await getTaskDetail(row.id)
        if (res.data?.task) { this.detailTask = { ...row, ...res.data.task } }
        this.detailFiles = res.data?.files || []
      } catch (e) { console.error('获取任务详情失败:', e) }
    },
    goSubmit(row) {
      this.detailVisible = false
      this.$router.push(`/student/task/${row.id}/submit`)
    }
  }
}
</script>

<style scoped>
.task-list {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  font-size: 28px;
}

.header-left h3 {
  color: #2D3A2F;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.header-meta .meta-count {
  font-size: 13px;
  color: #8A9B8F;
  background: #EEF5F0;
  padding: 5px 14px;
  border-radius: 20px;
  font-weight: 500;
}

/* ========== 任务卡片网格 ========== */
.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.task-card {
  padding: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.task-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(85, 201, 138, 0.12) !important;
}

.task-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.task-number {
  font-size: 12px;
  font-weight: 700;
  color: #B0BDB3;
  letter-spacing: 0.5px;
}

.status-tag {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 12px;
  border-radius: 20px;
}

.tag-scored {
  background: #E8FFF1;
  color: #2E7A4A;
}

.tag-submitted {
  background: #FFF8E8;
  color: #8A6D2B;
}

.tag-pending {
  background: #F5F5F5;
  color: #8A8A8A;
}

.task-title {
  font-size: 17px;
  font-weight: 700;
  color: #2D3A2F;
  margin: 0 0 8px;
  line-height: 1.4;
}

.task-desc {
  font-size: 13px;
  color: #8A9B8F;
  margin: 0 0 14px;
  line-height: 1.5;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.task-deadline {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #B0BDB3;
  margin-bottom: 8px;
}

.task-deadline.deadline-urgent {
  color: #E8885A;
}

.task-deadline.deadline-urgent i {
  animation: pulse 1.5s ease-in-out infinite;
}

.task-score {
  margin-bottom: 16px;
}

.score-ring {
  display: inline-flex;
  align-items: baseline;
  gap: 2px;
  padding: 4px 14px;
  border-radius: 20px;
  background: linear-gradient(135deg, #E8FFF1, #D0F5DF);
}

.score-value {
  font-size: 22px;
  font-weight: 800;
  color: #55C98A;
}

.score-unit {
  font-size: 12px;
  color: #55C98A;
  font-weight: 600;
}

.task-actions {
  display: flex;
  gap: 10px;
  padding-top: 16px;
  border-top: 1px solid #EEF5F0;
}

/* ========== 分页 ========== */
.table-pagination {
  margin-top: 24px;
  text-align: center;
}

/* ========== 详情弹窗 ========== */
.game-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #E8FFF1, #F5FBF7);
  border-bottom: 1px solid #D8EFE0;
  padding: 20px 24px;
  border-radius: 16px 16px 0 0;
}

.game-dialog >>> .el-dialog__title {
  font-weight: 700;
  color: #2D3A2F;
  font-size: 16px;
}

.game-dialog >>> .el-dialog__body {
  padding: 24px !important;
}

.detail-body {
  font-size: 14px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-label {
  font-weight: 700;
  color: #2D3A2F;
  margin-bottom: 8px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-label i {
  color: #55C98A;
}

.detail-content {
  color: #5A6B5F;
  line-height: 1.7;
}

.detail-requirement {
  white-space: pre-wrap;
  background: #FAFCF8;
  padding: 14px 16px;
  border-radius: 10px;
  border: 1px solid #E8EFE8;
}

.deadline-highlight {
  font-weight: 600;
  color: #55C98A;
}

.detail-empty {
  color: #B0BDB3;
  font-style: italic;
}

.detail-file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-file-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  background: #FAFCF8;
  border: 1px solid #E8EFE8;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.detail-file-item:hover {
  background: #E8FFF1;
  border-color: rgba(85, 201, 138, 0.2);
}

.file-icon-box {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #E8FFF1;
  color: #55C98A;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.file-name-text {
  flex: 1;
  font-size: 13px;
  color: #2D3A2F;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size-text {
  font-size: 12px;
  color: #B0BDB3;
}

.dl-btn {
  color: #55C98A !important;
  font-size: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
