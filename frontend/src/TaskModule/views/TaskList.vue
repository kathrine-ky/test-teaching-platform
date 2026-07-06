<template>
  <div class="task-list">
    <div class="page-header">
      <h3>实验任务列表</h3>
    </div>

    <el-card shadow="never" class="content-card">
      <el-table :data="taskList" stripe v-loading="loading" class="data-table">
        <el-table-column prop="id" label="序号" width="70" align="center" />
        <el-table-column prop="title" label="任务标题" min-width="200" align="center" />
        <el-table-column prop="description" label="任务描述" min-width="280" show-overflow-tooltip align="center" />
        <el-table-column label="截止时间" width="180" align="center">
          <template slot-scope="scope">
            <span class="deadline-text">{{ scope.row.deadline || '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="我的状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.myScore != null" type="success" size="small">已批改</el-tag>
            <el-tag v-else-if="scope.row.submitted" type="warning" size="small">已提交</el-tag>
            <el-tag v-else type="info" size="small">未提交</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="得分" width="90" align="center">
          <template slot-scope="scope">
            <span :class="{ 'score-highlight': scope.row.myScore != null }">
              {{ scope.row.myScore != null ? scope.row.myScore : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="small" type="primary" @click="goSubmit(scope.row)">提交作业</el-button>
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

      <EmptyState v-if="!loading && taskList.length === 0" text="暂无实验任务" subtext="教师发布作业后将在此显示" />
    </el-card>
  </div>
</template>

<script>
import { listTasks } from '@/TaskModule/api'
import { getMySubmission } from '@/SubmitModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'TaskList',
  components: { EmptyState },
  data() {
    return {
      loading: false, taskList: [], total: 0,
      query: { current: 1, size: 10 }
    }
  },
  created() { this.fetchTasks() },
  methods: {
    async fetchTasks() {
      this.loading = true
      try {
        const res = await listTasks(this.query)
        const tasks = res.data.records || []
        // 并行查询每个任务的提交状态
        for (const t of tasks) {
          try {
            const sub = await getMySubmission(t.id)
            const s = sub.data?.submission
            t.submitted = s != null
            if (s) {
              t.myScore = s.score
            }
          } catch (e) { t.submitted = false }
        }
        this.taskList = tasks
        this.total = res.data.total
      } catch (e) {
        console.error('获取任务列表失败:', e)
      } finally {
        this.loading = false
      }
    },
    goSubmit(row) {
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

.deadline-text {
  color: #606266;
  font-size: 13px;
}

.score-highlight {
  font-weight: 600;
  color: #303133;
}

.table-pagination {
  padding: 20px 0 4px;
  text-align: right;
}
</style>
