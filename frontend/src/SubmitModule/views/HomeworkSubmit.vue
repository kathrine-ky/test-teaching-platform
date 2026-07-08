<template>
  <div class="homework-submit">
    <div class="page-header">
      <el-button icon="el-icon-back" size="medium" round @click="$router.back()" class="back-btn">返回</el-button>
      <h3>提交作业</h3>
    </div>

    <div class="game-card content-card" v-loading="loading">
      <!-- 任务信息 -->
      <div v-if="taskDetail" class="task-info">
        <div class="task-info-header">
          <span class="task-badge">📋 当前任务</span>
          <h4 class="task-title">{{ taskDetail.title }}</h4>
        </div>
        <p class="task-desc">{{ taskDetail.description }}</p>
        <el-alert
          v-if="taskDetail.requirement"
          :title="'实验要求'"
          :description="taskDetail.requirement"
          type="info"
          :closable="false"
          show-icon
          class="requirement-alert"
        />
        <p v-if="deadline" class="deadline-info">
          <i class="el-icon-alarm-clock"></i> 截止时间：{{ deadline }}
        </p>
      </div>

      <el-divider content-position="left">
        <span class="divider-text">📝 提交作业</span>
      </el-divider>

      <!-- 已有提交提示 -->
      <el-alert
        v-if="existingSubmission"
        title="您已提交过作业，截止日前可重新提交覆盖"
        type="warning"
        :closable="false"
        show-icon
        class="submit-alert"
      />

      <!-- 已有评分 -->
      <div v-if="existingSubmission && existingSubmission.score != null" class="existing-score">
        <div class="score-header">
          <div class="score-badge-box" :class="existingSubmission.score >= 60 ? 'badge-pass' : 'badge-fail'">
            <span class="score-num">{{ existingSubmission.score }}</span>
            <span class="score-label">分</span>
          </div>
          <div class="score-status">
            <span class="status-text">{{ existingSubmission.score >= 60 ? '✅ 已通过' : '❌ 未通过' }}</span>
          </div>
        </div>
        <p v-if="existingSubmission.comment" class="comment-text">
          <i class="el-icon-chat-line-round"></i> 教师评语：{{ existingSubmission.comment }}
        </p>
      </div>

      <!-- 提交表单 -->
      <el-form ref="submitForm" label-width="110px" class="submit-form">
        <el-form-item label="测试用例">
          <el-input
            v-model="testCase"
            type="textarea"
            :rows="5"
            placeholder="请在此填写测试用例，如：等价类划分、边界值分析等..."
            class="game-textarea"
          />
          <span class="form-tip">💡 填写测试用例设计，包括输入条件、预期输出等</span>
        </el-form-item>

        <el-form-item label="缺陷报告">
          <el-input
            v-model="defectReport"
            type="textarea"
            :rows="5"
            placeholder="请在此填写缺陷报告，如：缺陷编号、严重程度、复现步骤等..."
            class="game-textarea"
          />
          <span class="form-tip">💡 填写发现的缺陷详情，包括缺陷描述、复现步骤、截图说明等</span>
        </el-form-item>

        <el-form-item label="测试总结">
          <el-input
            v-model="testSummary"
            type="textarea"
            :rows="5"
            placeholder="请在此填写测试总结，如：测试覆盖率、遗留风险、改进建议等..."
            class="game-textarea"
          />
          <span class="form-tip">💡 总结测试过程和结果，分析测试覆盖情况和存在的问题</span>
        </el-form-item>

        <!-- 附件1 -->
        <el-form-item label="Word文档">
          <div class="upload-area">
            <el-upload
              ref="uploadRef1"
              :limit="1"
              :file-list="fileList1"
              action=""
              :auto-upload="false"
              :on-change="(file, fl) => handleFileChange(file, fl, 1)"
              :on-remove="() => handleFileRemove(1)"
              accept=".doc,.docx"
              class="game-upload"
            >
              <el-button size="small" type="primary" round icon="el-icon-upload2">选择Word文档</el-button>
            </el-upload>
            <span v-if="existingSubmission?.fileUrl" class="existing-file">
              <i class="el-icon-paperclip"></i>
              已上传：
              <el-button type="text" @click="handleDownload(existingSubmission.fileUrl)" class="file-link">
                {{ existingSubmission.fileName || '查看文件' }}
              </el-button>
            </span>
          </div>
          <span class="form-tip">上传测试文档（.doc / .docx）</span>
        </el-form-item>

        <el-form-item label="测试脚本">
          <div class="upload-area">
            <el-upload
              ref="uploadRef2"
              :limit="1"
              :file-list="fileList2"
              action=""
              :auto-upload="false"
              :on-change="(file, fl) => handleFileChange(file, fl, 2)"
              :on-remove="() => handleFileRemove(2)"
              accept=".py,.java,.js,.sh,.bat,.zip,.rar"
              class="game-upload"
            >
              <el-button size="small" type="success" round icon="el-icon-upload2">选择脚本文件</el-button>
            </el-upload>
            <span v-if="existingSubmission?.fileUrl2" class="existing-file">
              <i class="el-icon-paperclip"></i>
              已上传：
              <el-button type="text" @click="handleDownload(existingSubmission.fileUrl2)" class="file-link">
                {{ existingSubmission.fileName2 || '查看文件' }}
              </el-button>
            </span>
          </div>
          <span class="form-tip">上传自动化测试脚本（.py / .java / .js / .zip 等）</span>
        </el-form-item>

        <el-form-item label="其他附件">
          <div class="upload-area">
            <el-upload
              ref="uploadRef3"
              :limit="1"
              :file-list="fileList3"
              action=""
              :auto-upload="false"
              :on-change="(file, fl) => handleFileChange(file, fl, 3)"
              :on-remove="() => handleFileRemove(3)"
              class="game-upload"
            >
              <el-button size="small" type="warning" round icon="el-icon-upload2">选择其他文件</el-button>
            </el-upload>
            <span v-if="existingSubmission?.fileUrl3" class="existing-file">
              <i class="el-icon-paperclip"></i>
              已上传：
              <el-button type="text" @click="handleDownload(existingSubmission.fileUrl3)" class="file-link">
                {{ existingSubmission.fileName3 || '查看文件' }}
              </el-button>
            </span>
          </div>
          <span class="form-tip">上传其他补充材料（截图、数据文件等）</span>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
            class="submit-btn"
            size="medium"
            round
          >
            <i class="el-icon-upload"></i> 提交作业
          </el-button>
          <span class="resubmit-tip">截止日前可重复修改重提交</span>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { getTaskDetail } from '@/TaskModule/api'
import { getMySubmission, submitHomework, downloadFile } from '@/SubmitModule/api'

export default {
  name: 'HomeworkSubmit',
  data() {
    return {
      loading: false, submitting: false,
      taskDetail: null, deadline: '', existingSubmission: null,
      testCase: '',
      defectReport: '',
      testSummary: '',
      selectedFile1: null,
      selectedFile2: null,
      selectedFile3: null,
      fileList1: [],
      fileList2: [],
      fileList3: []
    }
  },
  async created() {
    this.loading = true
    const taskId = this.$route.params.id
    try {
      const [taskRes, subRes] = await Promise.all([
        getTaskDetail(taskId), getMySubmission(taskId)
      ])
      this.taskDetail = taskRes.data?.task || taskRes.data
      this.deadline = taskRes.data?.deadline || this.taskDetail?.deadline
      this.existingSubmission = subRes.data?.submission
      if (this.existingSubmission) {
        this.testCase = this.existingSubmission.testCase || ''
        this.defectReport = this.existingSubmission.defectReport || ''
        this.testSummary = this.existingSubmission.testSummary || ''
      }
    } finally { this.loading = false }
  },
  methods: {
    handleFileChange(file, fileList, index) {
      if (index === 1) { this.selectedFile1 = file.raw; this.fileList1 = fileList }
      else if (index === 2) { this.selectedFile2 = file.raw; this.fileList2 = fileList }
      else if (index === 3) { this.selectedFile3 = file.raw; this.fileList3 = fileList }
    },
    handleFileRemove(index) {
      if (index === 1) { this.selectedFile1 = null; this.fileList1 = [] }
      else if (index === 2) { this.selectedFile2 = null; this.fileList2 = [] }
      else if (index === 3) { this.selectedFile3 = null; this.fileList3 = [] }
    },
    async handleDownload(fileUrl) {
      if (!fileUrl) { this.$message.warning('文件路径为空，无法下载'); return }
      try {
        const res = await downloadFile(fileUrl)
        const disposition = res.headers['content-disposition']
        let filename = 'download'
        if (disposition) {
          const match = disposition.match(/filename\*=UTF-8''(.+)/)
          if (match) { filename = decodeURIComponent(match[1]) }
        }
        const blob = new Blob([res.data])
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url; a.download = filename
        document.body.appendChild(a); a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(url)
      } catch (e) {
        console.warn('API下载失败，尝试直接访问:', fileUrl)
        window.open(fileUrl, '_blank')
      }
    },
    async handleSubmit() {
      if (!this.testCase && !this.defectReport && !this.testSummary
          && !this.selectedFile1 && !this.selectedFile2 && !this.selectedFile3) {
        this.$message.warning('请至少填写一项文本内容或选择文件')
        return
      }
      this.submitting = true
      try {
        await submitHomework(this.$route.params.id, {
          testCase: this.testCase,
          defectReport: this.defectReport,
          testSummary: this.testSummary,
          file: this.selectedFile1,
          file2: this.selectedFile2,
          file3: this.selectedFile3
        })
        this.$message.success('提交成功')
        const subRes = await getMySubmission(this.$route.params.id)
        this.existingSubmission = subRes.data?.submission
        this.selectedFile1 = null; this.fileList1 = []
        this.selectedFile2 = null; this.fileList2 = []
        this.selectedFile3 = null; this.fileList3 = []
      } catch (e) {
        console.error('提交失败:', e)
      } finally { this.submitting = false }
    }
  }
}
</script>

<style scoped>
.homework-submit {
  max-width: 960px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.back-btn {
  border-radius: 20px;
}

.page-header h3 {
  color: #2D3A2F;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.content-card {
  padding: 28px 32px;
  border: none !important;
}

/* ========== 任务信息 ========== */
.task-info {
  margin-bottom: 8px;
}

.task-info-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.task-badge {
  font-size: 12px;
  background: #E8FFF1;
  color: #55C98A;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 600;
}

.task-title {
  font-size: 18px;
  font-weight: 700;
  color: #2D3A2F;
  margin: 0;
}

.task-desc {
  color: #5A6B5F;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 16px;
}

.requirement-alert {
  margin-bottom: 16px;
  border-radius: 14px !important;
  border: 1px solid #D8EFE0 !important;
}

.requirement-alert >>> .el-alert__description {
  white-space: pre-wrap;
  font-size: 13px;
  line-height: 1.6;
  color: #5A6B5F;
}

.deadline-info {
  color: #E8885A;
  font-size: 14px;
  font-weight: 600;
  margin: 16px 0 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.divider-text {
  font-size: 15px;
  font-weight: 600;
  color: #2D3A2F;
}

/* ========== 已有评分 ========== */
.submit-alert {
  margin-bottom: 16px;
  border-radius: 14px !important;
}

.existing-score {
  background: linear-gradient(135deg, #FAFCF8, #F0F8F2);
  border: 1px solid #D8EFE0;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 20px;
}

.score-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 10px;
}

.score-badge-box {
  display: flex;
  align-items: baseline;
  gap: 2px;
  padding: 8px 18px;
  border-radius: 14px;
}

.badge-pass {
  background: linear-gradient(135deg, #55C98A, #3DCE7B);
  color: #fff;
}

.badge-fail {
  background: linear-gradient(135deg, #FF8A80, #FF6B6B);
  color: #fff;
}

.score-num {
  font-size: 26px;
  font-weight: 800;
}

.score-label {
  font-size: 13px;
  opacity: 0.85;
}

.score-status .status-text {
  font-size: 14px;
  font-weight: 600;
  color: #2D3A2F;
}

.comment-text {
  font-size: 13px;
  color: #5A6B5F;
  margin: 6px 0 0;
  line-height: 1.5;
  display: flex;
  align-items: flex-start;
  gap: 4px;
}

.comment-text i {
  color: #FFD66B;
  margin-top: 2px;
}

/* ========== 表单 ========== */
.submit-form {
  margin-top: 8px;
}

.submit-form .el-form-item {
  margin-bottom: 24px;
}

.game-textarea >>> textarea {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  border-radius: 14px;
  background: #FAFCF8;
  border-color: #E0E8E0;
}

.game-textarea >>> textarea:focus {
  border-color: #55C98A;
  box-shadow: 0 0 0 3px rgba(85, 201, 138, 0.1);
}

.form-tip {
  display: block;
  font-size: 12px;
  color: #B0BDB3;
  margin-top: 6px;
}

.upload-area {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.game-upload >>> .el-upload-list {
  margin-top: 8px;
}

.existing-file {
  font-size: 13px;
  color: #55C98A;
  display: flex;
  align-items: center;
  gap: 4px;
}

.file-link {
  color: #55C98A !important;
  font-size: 13px;
  padding: 0;
}

.submit-btn {
  width: 200px;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.submit-btn:hover {
  transform: scale(1.04);
}

.resubmit-tip {
  margin-left: 16px;
  color: #B0BDB3;
  font-size: 12px;
}
</style>
