<template>
  <div class="score-statistics">
    <div class="page-header">
      <el-button icon="el-icon-back" size="medium" @click="$router.back()">返回</el-button>
      <h3>成绩统计分析</h3>
      <el-button type="success" icon="el-icon-download" size="medium" @click="handleExport">导出Excel</el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stat-row">
      <el-col :span="6">
        <el-card shadow="never" class="stat-item">
          <div class="stat-label">平均分</div>
          <div class="stat-value primary">{{ stats.avgScore }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-item">
          <div class="stat-label">最高分</div>
          <div class="stat-value success">{{ stats.maxScore }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-item">
          <div class="stat-label">最低分</div>
          <div class="stat-value danger">{{ stats.minScore }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-item">
          <div class="stat-label">已批改人数</div>
          <div class="stat-value">{{ stats.count }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成绩明细表格 -->
    <el-card shadow="never" class="detail-card">
      <div slot="header" class="card-header-custom">
        <i class="el-icon-document"></i>
        <span>成绩明细</span>
      </div>
      <el-table :data="stats.details || []" stripe class="data-table">
        <el-table-column prop="studentNo" label="学号" width="150" align="center" />
        <el-table-column prop="studentName" label="姓名" width="130" align="center" />
        <el-table-column prop="className" label="班级" width="160" align="center" />
        <el-table-column label="成绩" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.score >= 90 ? 'success' : scope.row.score >= 60 ? 'warning' : 'danger'" size="medium">
              {{ scope.row.score }} 分
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <EmptyState v-if="(stats.details || []).length === 0" text="暂无成绩数据" subtext="请先批改学生提交的作业" />
    </el-card>
  </div>
</template>

<script>
import { getScoreStatistics, exportScores } from '@/ScoreModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'ScoreStatistics',
  components: { EmptyState },
  data() {
    return {
      stats: { avgScore: 0, maxScore: 0, minScore: 0, count: 0, details: [] }
    }
  },
  created() { this.fetchStats() },
  methods: {
    async fetchStats() {
      try {
        const res = await getScoreStatistics(this.$route.params.id)
        this.stats = res.data
      } catch (e) { console.error(e) }
    },
    async handleExport() {
      try {
        const res = await exportScores(this.$route.params.id)
        const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url; a.download = '成绩表.xlsx'; a.click()
        URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      } catch (e) { console.error(e) }
    }
  }
}
</script>

<style scoped>
.score-statistics {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.page-header h3 {
  flex: 1;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

/* 统计卡片行 */
.stat-row {
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
  border: 1px solid #ebeef5;
  cursor: default;
  transition: transform 0.2s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
}

.stat-item >>> .el-card__body {
  padding: 24px 16px !important;
}

.stat-item .stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 12px;
}

.stat-item .stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #409EFF;
  line-height: 1;
}

.stat-item .stat-value.primary {
  color: #409EFF;
}

.stat-item .stat-value.success {
  color: #67C23A;
}

.stat-item .stat-value.danger {
  color: #F56C6C;
}

/* 成绩明细卡片 */
.detail-card {
  border: 1px solid #ebeef5;
}

.card-header-custom {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.card-header-custom i {
  color: #409EFF;
  font-size: 18px;
}

.data-table {
  font-size: 14px;
}

.data-table >>> th {
  background-color: #fafafa !important;
  font-weight: 600;
  color: #303133;
}
</style>
