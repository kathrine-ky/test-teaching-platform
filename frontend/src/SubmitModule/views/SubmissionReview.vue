<template>
  <div class="submission-review">
    <div class="page-header">
      <el-button icon="el-icon-back" size="medium" @click="$router.back()">返回</el-button>
      <h3>学生提交列表与批改</h3>
    </div>

    <el-card shadow="never" class="content-card">
      <el-table :data="submissions" stripe v-loading="loading" class="data-table">
        <el-table-column prop="studentName" label="姓名" width="100" align="center" />
        <el-table-column prop="studentNo" label="学号" width="130" align="center" />
        <el-table-column prop="className" label="班级" width="140" align="center" />
        <el-table-column label="提交状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" type="warning" size="small">待批改</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="success" size="small">已批改</el-tag>
            <el-tag v-else type="info" size="small">未提交</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="附件" width="140" align="center">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.fileUrl"
              type="text"
              size="small"
              icon="el-icon-download"
              class="download-link"
              @click="handleDownload(scope.row.fileUrl)">
              {{ scope.row.fileName || '下载文件' }}
            </el-button>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>
        <el-table-column label="代码文本" min-width="160" show-overflow-tooltip align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.codeText" class="code-link" @click="showCodeDialog(scope.row)">
              {{ scope.row.codeText.substring(0, 40) + '...' }}
            </span>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="80" align="center">
          <template slot-scope="scope">
            <span :class="{ 'score-value': scope.row.score != null }">
              {{ scope.row.score != null ? scope.row.score : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 1" size="small" type="primary" @click="showReviewDialog(scope.row)">批改</el-button>
            <el-button v-if="scope.row.status === 2" size="small" type="info" plain @click="showReviewDialog(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <EmptyState v-if="!loading && submissions.length === 0" text="暂无学生提交数据" subtext="等待学生提交作业" />
    </el-card>

    <!-- 批改弹窗 -->
    <el-dialog title="批改作业" :visible.sync="reviewVisible" width="560px" class="review-dialog">
      <el-form ref="reviewForm" :model="reviewForm" label-width="80px" class="review-form">
        <el-form-item label="学生">
          <span class="student-info">{{ currentSubmission?.studentName }}（{{ currentSubmission?.studentNo }}）</span>
        </el-form-item>
        <el-form-item label="代码文本" v-if="currentSubmission?.codeText">
          <div class="code-block">{{ currentSubmission?.codeText }}</div>
        </el-form-item>
        <el-form-item label="附件" v-if="currentSubmission?.fileUrl">
          <el-button type="text" icon="el-icon-download" @click="handleDownload(currentSubmission.fileUrl)">
            {{ currentSubmission.fileName || '下载文件' }}
          </el-button>
        </el-form-item>
        <el-form-item label="评分" prop="score" :rules="[{ required: true, message: '请输入评分', trigger: 'blur' }]">
          <el-input-number v-model="reviewForm.score" :min="0" :max="100" :step="1" size="medium" />
          <span class="score-unit">分</span>
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="reviewForm.comment" type="textarea" :rows="3" placeholder="请输入评语（选填）" maxlength="300" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reviewVisible = false" size="medium">取消</el-button>
        <el-button type="primary" @click="handleReview" size="medium">提交批改</el-button>
      </div>
    </el-dialog>

    <!-- 代码查看弹窗 -->
    <el-dialog title="学生代码文本" :visible.sync="codeVisible" width="700px" class="code-dialog">
      <div class="code-viewer">{{ currentCodeText }}</div>
      <div slot="footer">
        <el-button @click="codeVisible = false" size="medium">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSubmissionsByTask, reviewSubmission, downloadFile } from '@/SubmitModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'SubmissionReview',
  components: { EmptyState },
  data() {
    return {
      loading: false,
      submissions: [],
      reviewVisible: false,
      codeVisible: false,
      currentCodeText: '',
      currentSubmission: null,
      reviewForm: { score: 0, comment: '' }
    }
  },
  created() { this.fetchSubmissions() },
  methods: {
    async fetchSubmissions() {
      this.loading = true
      try {
        const res = await getSubmissionsByTask(this.$route.params.id)
        this.submissions = res.data || []
      } finally { this.loading = false }
    },
    showReviewDialog(row) {
      this.currentSubmission = row
      this.reviewForm = { score: row.score || 0, comment: row.comment || '' }
      this.reviewVisible = true
    },
    showCodeDialog(row) {
      this.currentCodeText = row.codeText || ''
      this.codeVisible = true
    },
    async handleDownload(fileUrl) {
      if (!fileUrl) {
        this.$message.warning('文件路径为空，无法下载')
        return
      }
      // 通过后端 /api/submission/download 接口下载（支持认证）
      try {
        const res = await downloadFile(fileUrl)
        // 从响应头获取文件名
        const disposition = res.headers['content-disposition']
        let filename = 'download'
        if (disposition) {
          const match = disposition.match(/filename\*=UTF-8''(.+)/)
          if (match) {
            filename = decodeURIComponent(match[1])
          }
        }
        // 创建 Blob 并触发下载
        const blob = new Blob([res.data])
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = filename
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(url)
      } catch (e) {
        // 如果 API 下载失败，尝试直接打开（开发模式代理）
        console.warn('API下载失败，尝试直接访问:', fileUrl)
        window.open(fileUrl, '_blank')
      }
    },
    async handleReview() {
      if (this.reviewForm.score == null) { this.$message.warning('请输入评分'); return }
      try {
        await reviewSubmission({
          submissionId: this.currentSubmission.id,
          score: this.reviewForm.score,
          comment: this.reviewForm.comment
        })
        this.$message.success('批改成功')
        this.reviewVisible = false
        this.fetchSubmissions()
      } catch (e) { console.error(e) }
    }
  }
}
</script>

<style scoped>
.submission-review {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
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

.no-data {
  color: #c0c4cc;
}

.code-link {
  color: #409EFF;
  cursor: pointer;
  font-size: 13px;
  transition: color 0.2s;
}

.code-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.download-link {
  color: #409EFF;
  font-size: 13px;
}

.score-value {
  font-weight: 600;
  color: #303133;
}

/* 批改弹窗 */
.review-dialog >>> .el-dialog__body {
  padding: 24px 32px !important;
}

.review-form .el-form-item {
  margin-bottom: 20px;
}

.student-info {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.code-block {
  background: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px 16px;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 180px;
  overflow-y: auto;
  color: #303133;
}

.score-unit {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 代码查看弹窗 */
.code-viewer {
  background: #1e1e1e;
  color: #d4d4d4;
  border-radius: 6px;
  padding: 20px 24px;
  font-family: 'Consolas', 'Courier New', 'Source Code Pro', monospace;
  font-size: 13px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 450px;
  overflow-y: auto;
}
</style>
