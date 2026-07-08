<template>
  <div class="my-scores">
    <div class="page-header">
      <div class="header-left">
        <span class="header-icon">📊</span>
        <h3>我的成绩</h3>
      </div>
      <div class="header-meta">
        <span class="meta-count">{{ scoreList.length }} 条记录</span>
      </div>
    </div>

    <!-- 成绩概览卡片 -->
    <div class="score-overview" v-if="!loading && scoreList.length > 0">
      <div class="game-card overview-card">
        <div class="overview-stat">
          <div class="ov-label">平均分</div>
          <div class="ov-value ov-primary">{{ avgScore }}</div>
        </div>
      </div>
      <div class="game-card overview-card">
        <div class="overview-stat">
          <div class="ov-label">最高分</div>
          <div class="ov-value ov-success">{{ maxScore }}</div>
        </div>
      </div>
      <div class="game-card overview-card">
        <div class="overview-stat">
          <div class="ov-label">已批改</div>
          <div class="ov-value ov-info">{{ scoredCount }} / {{ scoreList.length }}</div>
        </div>
      </div>
    </div>

    <!-- 成绩列表 -->
    <div class="scores-body" v-loading="loading">
      <div v-if="!loading && scoreList.length === 0">
        <EmptyState text="暂无成绩数据" subtext="请先提交作业，等待教师批改后查看" />
      </div>
      <div v-else class="scores-list">
        <div class="game-card score-card" v-for="item in scoreList" :key="item.submissionId || item.taskId">
          <div class="score-card-left">
            <div class="score-icon" :class="scoreIconClass(item.score)">
              {{ scoreEmoji(item.score) }}
            </div>
            <div class="score-info">
              <h4 class="score-task-title">{{ item.taskTitle }}</h4>
              <div class="score-times">
                <span v-if="item.submitTime"><i class="el-icon-upload2"></i> {{ item.submitTime }}</span>
                <span v-if="item.reviewTime"><i class="el-icon-check"></i> {{ item.reviewTime }}</span>
              </div>
              <p class="score-comment" v-if="item.comment">
                <i class="el-icon-chat-line-round"></i> {{ item.comment }}
              </p>
            </div>
          </div>
          <div class="score-card-right">
            <div v-if="item.score != null" class="score-badge" :class="scoreBadgeClass(item.score)">
              <span class="score-num">{{ item.score }}</span>
              <span class="score-unit">分</span>
            </div>
            <div v-else class="score-badge score-pending">
              <span class="score-num">--</span>
              <span class="score-unit">待批</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getMyScores } from '@/ScoreModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'MyScores',
  components: { EmptyState },
  data() {
    return {
      loading: false,
      scoreList: []
    }
  },
  computed: {
    avgScore() {
      const scores = this.scoreList.filter(s => s.score != null).map(s => s.score)
      if (scores.length === 0) return '--'
      return (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(1)
    },
    maxScore() {
      const scores = this.scoreList.filter(s => s.score != null).map(s => s.score)
      if (scores.length === 0) return '--'
      return Math.max(...scores)
    },
    scoredCount() {
      return this.scoreList.filter(s => s.score != null).length
    }
  },
  created() { this.fetchScores() },
  methods: {
    scoreEmoji(score) {
      if (score == null) return '⏳'
      if (score >= 90) return '🏆'
      if (score >= 80) return '🌟'
      if (score >= 60) return '👍'
      return '💪'
    },
    scoreIconClass(score) {
      if (score == null) return 'icon-pending'
      if (score >= 90) return 'icon-excellent'
      if (score >= 60) return 'icon-pass'
      return 'icon-fail'
    },
    scoreBadgeClass(score) {
      if (score == null) return ''
      if (score >= 90) return 'badge-excellent'
      if (score >= 60) return 'badge-pass'
      return 'badge-fail'
    },
    async fetchScores() {
      this.loading = true
      try {
        const res = await getMyScores()
        this.scoreList = res.data || []
      } catch (e) {
        console.error('获取成绩失败:', e)
      } finally { this.loading = false }
    }
  }
}
</script>

<style scoped>
.my-scores {
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

/* ========== 成绩概览 ========== */
.score-overview {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.overview-card {
  padding: 20px 24px;
  text-align: center;
}

.overview-stat .ov-label {
  font-size: 13px;
  color: #8A9B8F;
  margin-bottom: 6px;
}

.ov-value {
  font-size: 28px;
  font-weight: 800;
  line-height: 1.1;
}

.ov-primary { color: #55C98A; }
.ov-success { color: #3DCE7B; }
.ov-info { color: #6EC6FF; }

/* ========== 成绩列表 ========== */
.scores-body {
  min-height: 200px;
}

.scores-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.score-card {
  padding: 20px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.score-card:hover {
  transform: translateX(6px);
  box-shadow: 0 8px 24px rgba(85, 201, 138, 0.1) !important;
}

.score-card-left {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  flex: 1;
  min-width: 0;
}

.score-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.icon-excellent { background: linear-gradient(135deg, #E8FFF1, #C8F0D8); }
.icon-pass { background: linear-gradient(135deg, #FFF8E8, #FFE8B8); }
.icon-fail { background: linear-gradient(135deg, #FFE8E8, #FFD0D0); }
.icon-pending { background: linear-gradient(135deg, #F0F4F8, #E0E8F0); }

.score-info {
  flex: 1;
  min-width: 0;
}

.score-task-title {
  font-size: 16px;
  font-weight: 700;
  color: #2D3A2F;
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.score-times {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 12px;
  color: #B0BDB3;
  margin-bottom: 6px;
}

.score-times span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.score-comment {
  font-size: 13px;
  color: #6B7A6F;
  margin: 4px 0 0;
  line-height: 1.5;
  display: flex;
  align-items: flex-start;
  gap: 4px;
}

.score-comment i {
  color: #FFD66B;
  margin-top: 2px;
}

.score-card-right {
  flex-shrink: 0;
  margin-left: 20px;
}

.score-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  border-radius: 18px;
}

.badge-excellent {
  background: linear-gradient(135deg, #55C98A, #3DCE7B);
  color: #fff;
}

.badge-pass {
  background: linear-gradient(135deg, #FFD66B, #F5C44B);
  color: #fff;
}

.badge-fail {
  background: linear-gradient(135deg, #FF8A80, #FF6B6B);
  color: #fff;
}

.score-pending {
  background: #F0F4F8;
  color: #8A9B8F;
}

.score-num {
  font-size: 24px;
  font-weight: 800;
  line-height: 1;
}

.score-unit {
  font-size: 11px;
  margin-top: 2px;
  font-weight: 500;
  opacity: 0.85;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .score-overview {
    grid-template-columns: 1fr;
  }
  .score-card {
    flex-direction: column;
    align-items: flex-start;
  }
  .score-card-right {
    margin-left: 0;
    margin-top: 14px;
    align-self: flex-end;
  }
}
</style>
