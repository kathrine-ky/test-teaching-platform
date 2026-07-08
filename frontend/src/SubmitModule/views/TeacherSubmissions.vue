<template>
  <div class="teacher-submissions">
    <div class="page-header">
      <div class="header-left">
        <span class="header-icon">📋</span>
        <h3>已提交总览</h3>
      </div>
      <div class="header-meta">
        <span class="meta-count">{{ summaryList.length }} 个任务</span>
      </div>
    </div>

    <div class="summary-body" v-loading="loading">
      <div v-if="!loading && summaryList.length === 0">
        <EmptyState text="暂无已发布任务" subtext="请先在作业管理中发布任务" />
      </div>
      <div v-else class="summary-list">
        <div class="game-card summary-card" v-for="item in summaryList" :key="item.taskId">
          <div class="summary-card-header">
            <div class="task-title-row">
              <span class="task-number">#{{ item.taskId }}</span>
              <h4 class="task-title">{{ item.taskTitle }}</h4>
            </div>
            <div class="task-course-tag">
              <i class="el-icon-reading"></i> {{ item.courseName || '未关联课程' }}
            </div>
          </div>

          <div class="summary-stats">
            <div class="stat-item">
              <div class="stat-value total">{{ item.totalStudents || 0 }}</div>
              <div class="stat-label">总人数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value submitted">{{ item.submittedCount || 0 }}</div>
              <div class="stat-label">已提交</div>
            </div>
            <div class="stat-item">
              <div class="stat-value reviewed">{{ item.reviewedCount || 0 }}</div>
              <div class="stat-label">已批改</div>
            </div>
            <div class="stat-item">
              <div class="stat-value not-submitted">{{ item.notSubmittedCount || 0 }}</div>
              <div class="stat-label">未提交</div>
            </div>
          </div>

          <!-- 进度条 -->
          <div class="progress-bar-wrap">
            <div class="progress-bar">
              <div class="progress-reviewed" :style="{ width: reviewedPercent(item) + '%' }"></div>
              <div class="progress-submitted" :style="{ width: submittedOnlyPercent(item) + '%', left: reviewedPercent(item) + '%' }"></div>
            </div>
            <span class="progress-text">{{ submitPercent(item) }}% 已提交</span>
          </div>

          <div class="summary-actions">
            <el-button size="small" type="primary" plain round @click="goSubmissions(item.taskId)">
              <i class="el-icon-view"></i> 查看提交列表
            </el-button>
            <el-button size="small" type="success" plain round @click="goStatistics(item.taskId)">
              <i class="el-icon-s-data"></i> 成绩统计
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getTeacherSubmissionSummary } from '@/SubmitModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'TeacherSubmissions',
  components: { EmptyState },
  data() {
    return {
      loading: false,
      summaryList: []
    }
  },
  created() {
    this.fetchSummary()
  },
  methods: {
    async fetchSummary() {
      this.loading = true
      try {
        const res = await getTeacherSubmissionSummary()
        this.summaryList = res.data || []
      } catch (e) {
        console.error('获取提交汇总失败:', e)
      } finally {
        this.loading = false
      }
    },
    reviewedPercent(item) {
      const total = item.totalStudents || 1
      return Math.round(((item.reviewedCount || 0) / total) * 100)
    },
    submittedOnlyPercent(item) {
      const total = item.totalStudents || 1
      const onlySubmitted = (item.submittedCount || 0) - (item.reviewedCount || 0)
      return Math.round((onlySubmitted / total) * 100)
    },
    submitPercent(item) {
      const total = item.totalStudents || 1
      return Math.round(((item.submittedCount || 0) / total) * 100)
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
.teacher-submissions {
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

/* ========== 汇总列表 ========== */
.summary-body {
  min-height: 200px;
}

.summary-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-card {
  padding: 20px 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.summary-card:hover {
  transform: translateX(4px);
  box-shadow: 0 8px 24px rgba(85, 201, 138, 0.1) !important;
}

.summary-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.task-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.task-number {
  font-size: 12px;
  font-weight: 700;
  color: #B0BDB3;
  letter-spacing: 0.5px;
}

.task-title {
  font-size: 17px;
  font-weight: 700;
  color: #2D3A2F;
  margin: 0;
}

.task-course-tag {
  font-size: 12px;
  color: #55C98A;
  background: #E8FFF1;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* ========== 统计数字 ========== */
.summary-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.stat-item {
  text-align: center;
  min-width: 60px;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  line-height: 1.1;
}

.stat-value.total { color: #6EC6FF; }
.stat-value.submitted { color: #FFD66B; }
.stat-value.reviewed { color: #55C98A; }
.stat-value.not-submitted { color: #FF8A80; }

.stat-item .stat-label {
  font-size: 12px;
  color: #8A9B8F;
  margin-top: 4px;
}

/* ========== 进度条 ========== */
.progress-bar-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.progress-bar {
  flex: 1;
  height: 10px;
  background: #EEF5F0;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.progress-reviewed {
  height: 100%;
  background: linear-gradient(90deg, #55C98A, #3DCE7B);
  border-radius: 10px;
  position: absolute;
  left: 0;
  top: 0;
  transition: width 0.6s ease;
}

.progress-submitted {
  height: 100%;
  background: linear-gradient(90deg, #FFD66B, #F0C060);
  border-radius: 10px;
  position: absolute;
  top: 0;
  transition: width 0.6s ease;
}

.progress-text {
  font-size: 12px;
  color: #8A9B8F;
  font-weight: 600;
  white-space: nowrap;
  flex-shrink: 0;
}

/* ========== 操作按钮 ========== */
.summary-actions {
  display: flex;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px solid #EEF5F0;
}
</style>
