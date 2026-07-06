<template>
  <div class="my-scores">
    <div class="page-header">
      <h3>我的成绩</h3>
    </div>

    <el-card shadow="never" class="content-card">
      <el-table :data="scoreList" stripe v-loading="loading" class="data-table">
        <el-table-column prop="taskTitle" label="作业标题" min-width="200" align="center" />
        <el-table-column prop="submitTime" label="提交时间" width="180" align="center" />
        <el-table-column prop="reviewTime" label="批改时间" width="180" align="center" />
        <el-table-column label="得分" width="110" align="center">
          <template slot-scope="scope">
            <el-tag
              v-if="scope.row.score != null"
              :type="scope.row.score >= 90 ? 'success' : scope.row.score >= 60 ? 'warning' : 'danger'"
              size="medium"
            >
              {{ scope.row.score }} 分
            </el-tag>
            <el-tag v-else type="info" size="medium">待批改</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="教师评语" min-width="200" show-overflow-tooltip align="center" />
      </el-table>
      <EmptyState v-if="!loading && scoreList.length === 0" text="暂无成绩数据" subtext="请先提交作业，等待教师批改后查看" />
    </el-card>
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
  created() { this.fetchScores() },
  methods: {
    async fetchScores() {
      this.loading = true
      try {
        const res = await getMyScores()
        this.scoreList = res.data || []
      } catch (e) {
        console.error('获取成绩失败:', e)
      } finally {
        this.loading = false
      }
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
</style>
