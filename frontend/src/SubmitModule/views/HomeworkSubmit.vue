<template>
  <div class="homework-submit">
    <div class="page-header">
      <el-button icon="el-icon-back" size="medium" @click="$router.back()">返回</el-button>
      <h3>提交作业</h3>
    </div>

    <el-card shadow="never" class="content-card" v-loading="loading">
      <!-- 任务详情 -->
      <div v-if="taskDetail" class="task-info">
        <h4 class="task-title">{{ taskDetail.title }}</h4>
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
          <i class="el-icon-time"></i> 截止时间：{{ deadline }}
        </p>
      </div>

      <el-divider content-position="left">提交作业</el-divider>

      <!-- 已有提交提示 -->
      <el-alert
        v-if="existingSubmission"
        title="您已提交过作业，截止日前可重新提交覆盖"
        type="warning"
        :closable="false"
        show-icon
        class="submit-alert"
      />

      <div v-if="existingSubmission && existingSubmission.score != null" class="existing-score">
        <div class="score-row">
          <span class="score-label">评分：</span>
          <el-tag :type="existingSubmission.score >= 60 ? 'success' : 'danger'" size="medium">
            {{ existingSubmission.score }} 分
          </el-tag>
        </div>
        <p v-if="existingSubmission.comment" class="comment-text">
          <span class="score-label">评语：</span>{{ existingSubmission.comment }}
        </p>
      </div>

      <el-form ref="submitForm" label-width="100px" class="submit-form">
        <!-- 代码文本输入 -->
        <el-form-item label="代码文本">
          <el-input
            v-model="codeText"
            type="textarea"
            :rows="8"
            placeholder="请在此输入测试代码、测试用例或测试总结..."
            class="code-textarea"
          />
          <span class="form-tip">支持直接输入代码文本提交</span>
        </el-form-item>

        <!-- 文件上传 -->
        <el-form-item label="上传文件">
          <div class="upload-area">
            <el-upload
              ref="uploadRef"
              :limit="1"
              :file-list="fileList"
              action=""
              :auto-upload="false"
              :on-change="handleFileChange"
              :on-remove="handleFileRemove"
              class="custom-upload"
            >
              <el-button size="small" type="primary" icon="el-icon-upload2">选择文件</el-button>
            </el-upload>
            <span v-if="existingSubmission?.fileUrl" class="existing-file">
              <i class="el-icon-paperclip"></i>
              已上传：
              <a :href="getFileDownloadUrl(existingSubmission.fileUrl)" target="_blank" class="file-link">
                {{ existingSubmission.fileName || '查看文件' }}
              </a>
            </span>
          </div>
          <span class="form-tip">支持上传代码文件、文档等（仅限1个文件）</span>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
            class="submit-btn"
            size="medium"
          >
            <i class="el-icon-upload"></i> 提交作业
          </el-button>
          <span class="resubmit-tip">截止日前可重复修改重提交</span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getTaskDetail } from '@/TaskModule/api'
import { getMySubmission, submitHomework } from '@/SubmitModule/api'

export default {
  name: 'HomeworkSubmit',
  data() {
    return {
      loading: false, submitting: false,
      taskDetail: null, deadline: '', existingSubmission: null,
      codeText: '',
      selectedFile: null,
      fileList: []
    }
  },
  async created() {
    this.loading = true
    const taskId = this.$route.params.id
    try {
      const [taskRes, subRes] = await Promise.all([
        getTaskDetail(taskId), getMySubmission(taskId)
      ])
      this.taskDetail = taskRes.data
      this.deadline = taskRes.data.deadline
      this.existingSubmission = subRes.data?.submission
      if (this.existingSubmission?.codeText) {
        this.codeText = this.existingSubmission.codeText
      }
    } finally { this.loading = false }
  },
  methods: {
    getFileDownloadUrl(fileUrl) {
      // 直接返回相对路径，开发模式下由 vue.config.js 代理转发
      return fileUrl
    },
    handleFileChange(file, fileList) {
      // 使用 on-change 事件获取 File 对象（auto-upload=false 时 http-request 不会被调用）
      this.selectedFile = file.raw  // file.raw 是原生 File 对象
      this.fileList = fileList
    },
    handleFileRemove() {
      this.selectedFile = null
      this.fileList = []
    },
    async handleSubmit() {
      if (!this.codeText && !this.selectedFile) {
        this.$message.warning('请至少输入代码文本或选择文件')
        return
      }
      this.submitting = true
      try {
        await submitHomework(this.$route.params.id, {
          codeText: this.codeText,
          file: this.selectedFile
        })
        this.$message.success('提交成功')
        // 刷新状态
        const subRes = await getMySubmission(this.$route.params.id)
        this.existingSubmission = subRes.data?.submission
        this.selectedFile = null
        this.fileList = []
      } catch (e) {
        // error already handled by interceptor
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.homework-submit {
  max-width: 900px;
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

/* 任务详情 */
.task-info {
  margin-bottom: 8px;
}

.task-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 10px;
}

.task-desc {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 16px;
}

.requirement-alert {
  margin-bottom: 16px;
}

.requirement-alert >>> .el-alert__description {
  white-space: pre-wrap;
  font-size: 13px;
  line-height: 1.6;
  color: #606266;
}

.deadline-info {
  color: #F56C6C;
  font-size: 14px;
  font-weight: 500;
  margin: 16px 0 0;
}

.deadline-info i {
  margin-right: 4px;
}

/* 已有提交 */
.submit-alert {
  margin-bottom: 16px;
}

.existing-score {
  background: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 14px 18px;
  margin-bottom: 20px;
}

.score-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.score-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.comment-text {
  color: #909399;
  font-size: 13px;
  margin: 6px 0 0;
  line-height: 1.5;
}

/* 表单 */
.submit-form {
  margin-top: 8px;
}

.submit-form .el-form-item {
  margin-bottom: 24px;
}

.code-textarea >>> textarea {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.form-tip {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}

/* 上传区域 */
.upload-area {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.custom-upload >>> .el-upload-list {
  margin-top: 8px;
}

.existing-file {
  font-size: 13px;
  color: #67C23A;
}

.existing-file i {
  margin-right: 4px;
}

.file-link {
  color: #409EFF;
  text-decoration: none;
}

.file-link:hover {
  text-decoration: underline;
}

/* 提交按钮 */
.submit-btn {
  width: 200px;
  font-size: 15px;
  font-weight: 600;
}

.submit-btn i {
  margin-right: 4px;
}

.resubmit-tip {
  margin-left: 16px;
  color: #909399;
  font-size: 12px;
}
</style>
