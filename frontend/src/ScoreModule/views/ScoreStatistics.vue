<template>
  <div class="score-statistics">
    <div class="page-header">
      <el-button icon="el-icon-back" size="medium" round @click="$router.back()" class="back-btn">返回</el-button>
      <h3>成绩统计分析</h3>
      <el-button type="success" icon="el-icon-download" size="medium" round @click="handleExport">导出Excel</el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="game-card stat-card">
          <div class="stat-icon-box stat-icon-avg">📊</div>
          <div class="stat-label">平均分</div>
          <div class="stat-value avg">{{ stats.avgScore }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="game-card stat-card">
          <div class="stat-icon-box stat-icon-max">🏆</div>
          <div class="stat-label">最高分</div>
          <div class="stat-value max">{{ stats.maxScore }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="game-card stat-card">
          <div class="stat-icon-box stat-icon-min">📉</div>
          <div class="stat-label">最低分</div>
          <div class="stat-value min">{{ stats.minScore }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="game-card stat-card">
          <div class="stat-icon-box stat-icon-count">✅</div>
          <div class="stat-label">已批改人数</div>
          <div class="stat-value">{{ stats.count }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 提交率统计 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="8">
        <div class="game-card stat-card stat-sm">
          <div class="stat-label">总人数</div>
          <div class="stat-value">{{ stats.totalStudents || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="game-card stat-card stat-sm">
          <div class="stat-label">已提交</div>
          <div class="stat-value avg">{{ (stats.submittedCount || 0) + (stats.count || 0) }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="game-card stat-card stat-sm">
          <div class="stat-label">未提交</div>
          <div class="stat-value min">{{ stats.notSubmitted || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 成绩分布柱状图 -->
    <div class="game-card chart-card" v-if="hasDistribution">
      <div class="chart-header">
        <span class="chart-icon-box"><i class="el-icon-s-data"></i></span>
        <span>成绩分布</span>
      </div>
      <div class="bar-chart">
        <div class="bar-item" v-for="item in distItems" :key="item.label">
          <div class="bar-label">{{ item.label }}</div>
          <div class="bar-track">
            <div class="bar-fill" :class="item.colorClass" :style="{ width: item.percent + '%' }">
              <span v-if="item.count > 0" class="bar-count">{{ item.count }}人</span>
            </div>
          </div>
          <div class="bar-num">{{ item.count }}</div>
        </div>
      </div>
    </div>

    <!-- 成绩明细表格 -->
    <div class="game-card detail-card">
      <div class="chart-header">
        <span class="chart-icon-box"><i class="el-icon-document"></i></span>
        <span>成绩明细</span>
      </div>
      <el-table :data="stats.details || []" stripe class="data-table">
        <el-table-column prop="studentNo" label="学号" width="150" align="center" />
        <el-table-column prop="studentName" label="姓名" width="130" align="center" />
        <el-table-column prop="className" label="班级" width="160" align="center" />
        <el-table-column label="成绩" width="120" align="center">
          <template slot-scope="scope">
            <span class="detail-score" :class="scope.row.score >= 90 ? 'score-a' : scope.row.score >= 60 ? 'score-b' : 'score-c'">
              {{ scope.row.score }} 分
            </span>
          </template>
        </el-table-column>
      </el-table>
      <EmptyState v-if="(stats.details || []).length === 0" text="暂无成绩数据" subtext="请先批改学生提交的作业" />
    </div>
  </div>
</template>

<script>
import { getScoreStatistics, exportScores } from '@/ScoreModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'ScoreStatistics',
  components: { EmptyState },
  data() {
    return { stats: { avgScore: 0, maxScore: 0, minScore: 0, count: 0, details: [], distribution: {}, notSubmitted: 0, submittedCount: 0, totalStudents: 0 } }
  },
  computed: {
    hasDistribution() { const d = this.stats.distribution || {}; return Object.values(d).some(v => v > 0) },
    distItems() { const d = this.stats.distribution || {}; const total = Math.max(this.stats.count || 1, 1); const items = [{ label: '优秀(90-100)', count: d.excellent || 0, colorClass: 'bar-excellent' }, { label: '良好(80-89)', count: d.good || 0, colorClass: 'bar-good' }, { label: '中等(70-79)', count: d.medium || 0, colorClass: 'bar-medium' }, { label: '及格(60-69)', count: d.pass || 0, colorClass: 'bar-pass' }, { label: '不及格(<60)', count: d.fail || 0, colorClass: 'bar-fail' }]; return items.map(i => ({ ...i, percent: Math.round((i.count / total) * 100) })) }
  },
  created() { this.fetchStats() },
  methods: {
    async fetchStats() { try { const res = await getScoreStatistics(this.$route.params.id); this.stats = res.data } catch (e) { console.error(e) } },
    async handleExport() { try { const res = await exportScores(this.$route.params.id); const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }); const url = URL.createObjectURL(blob); const a = document.createElement('a'); a.href = url; a.download = '成绩表.xlsx'; a.click(); URL.revokeObjectURL(url); this.$message.success('导出成功') } catch (e) { console.error(e) } }
  }
}
</script>

<style scoped>
.score-statistics { max-width: 1200px; margin: 0 auto; }

.page-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.back-btn { border-radius: 20px; }
.page-header h3 { flex: 1; color: #2D3A2F; font-size: 22px; font-weight: 700; margin: 0; }

.stat-row { margin-bottom: 20px; }

.stat-card { padding: 20px; text-align: center; cursor: pointer; }
.stat-card:hover { transform: translateY(-4px); box-shadow: 0 12px 28px rgba(85, 201, 138, 0.12) !important; }
.stat-sm { padding: 16px 20px; }

.stat-icon-box { font-size: 28px; margin-bottom: 8px; }
.stat-label { font-size: 13px; color: #8A9B8F; margin-bottom: 8px; font-weight: 500; }
.stat-value { font-size: 32px; font-weight: 800; color: #2D3A2F; line-height: 1; }
.stat-value.avg { color: #55C98A; }
.stat-value.max { color: #3DCE7B; }
.stat-value.min { color: #FF8A80; }

/* Chart */
.chart-card { padding: 24px; margin-bottom: 24px; }
.chart-header { display: flex; align-items: center; gap: 10px; font-weight: 700; font-size: 16px; color: #2D3A2F; margin-bottom: 20px; }
.chart-icon-box { width: 34px; height: 34px; border-radius: 10px; background: #E8FFF1; color: #55C98A; display: flex; align-items: center; justify-content: center; font-size: 16px; }

.bar-chart { padding: 4px 0; }
.bar-item { display: flex; align-items: center; margin-bottom: 16px; gap: 12px; }
.bar-label { width: 110px; font-size: 13px; font-weight: 600; color: #5A6B5F; text-align: right; flex-shrink: 0; }
.bar-track { flex: 1; height: 34px; background: #EEF5F0; border-radius: 10px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 10px; min-width: 0; transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1); display: flex; align-items: center; padding-left: 12px; }
.bar-count { font-size: 12px; font-weight: 600; color: #fff; white-space: nowrap; }
.bar-num { width: 32px; font-size: 16px; font-weight: 700; color: #2D3A2F; text-align: center; flex-shrink: 0; }

.bar-excellent { background: linear-gradient(90deg, #55C98A, #3DCE7B); }
.bar-good { background: linear-gradient(90deg, #6EC6FF, #55C98A); }
.bar-medium { background: linear-gradient(90deg, #FFD66B, #F0C060); }
.bar-pass { background: linear-gradient(90deg, #FFB74D, #FF8A65); }
.bar-fail { background: linear-gradient(90deg, #FF8A80, #FF6B6B); }

/* Detail */
.detail-card { padding: 24px; }
.data-table { font-size: 14px; }
.data-table >>> th { background-color: #F5FBF7 !important; font-weight: 700; color: #2D3A2F; }

.detail-score { font-weight: 700; font-size: 14px; }
.score-a { color: #55C98A; }
.score-b { color: #FFD66B; }
.score-c { color: #FF8A80; }
</style>
