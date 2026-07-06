<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-text">
        <h3 class="welcome-title">欢迎使用软件测试实践教学管理平台</h3>
        <p class="welcome-desc">{{ isTeacher ? '管理课程、发布作业、批改提交、分析成绩，一站式教学管理' : '查看实验任务、在线提交作业、查询个人成绩，轻松完成课程实践' }}</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stat-row">
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon-wrap" style="background: #ecf5ff;">
            <i class="el-icon-s-order" style="color: #409EFF;"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.taskCount }}</div>
            <div class="stat-label">实验任务数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon-wrap" style="background: #f0f9eb;">
            <i class="el-icon-document-checked" style="color: #67C23A;"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.submitCount }}</div>
            <div class="stat-label">已提交作业</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon-wrap" style="background: #fdf6ec;">
            <i class="el-icon-reading" style="color: #E6A23C;"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.courseCount }}</div>
            <div class="stat-label">课程数量</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能介绍 -->
    <el-card shadow="never" class="feature-card">
      <div slot="header" class="card-header-custom">
        <i class="el-icon-info"></i>
        <span>平台功能介绍</span>
      </div>
      <el-collapse v-model="activeCollapse">
        <el-collapse-item title="教师端功能" name="teacher" v-if="isTeacher">
          <div class="feature-tags">
            <el-tag type="" size="medium">课程信息管理：新增、编辑、删除课程</el-tag>
            <el-tag type="success" size="medium">作业管理：发布线上作业</el-tag>
            <el-tag type="warning" size="medium">作业批改：批改学生提交的作业、填写评价</el-tag>
            <el-tag type="danger" size="medium">数据查看：学生成绩统计、成绩数据分析展示</el-tag>
          </div>
        </el-collapse-item>
        <el-collapse-item title="学生端功能" name="student" v-if="isStudent">
          <div class="feature-tags">
            <el-tag size="medium">查看教师发布的作业</el-tag>
            <el-tag type="success" size="medium">在线提交作业</el-tag>
            <el-tag type="info" size="medium">查询个人作业得分</el-tag>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  data() {
    return {
      activeCollapse: ['teacher', 'student'],
      stats: { taskCount: 3, submitCount: 1, courseCount: 2 }
    }
  },
  computed: {
    ...mapGetters(['isTeacher', 'isStudent'])
  }
}
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

/* 欢迎横幅 */
.welcome-banner {
  background: #ffffff;
  border-radius: 8px;
  padding: 24px 28px;
  margin-bottom: 24px;
  border: 1px solid #ebeef5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.welcome-title {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 8px;
}

.welcome-desc {
  font-size: 14px;
  color: #909399;
  margin: 0;
  line-height: 1.6;
}

/* 统计行 */
.stat-row {
  margin-bottom: 24px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid #ebeef5;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
}

.stat-card >>> .el-card__body {
  display: flex;
  align-items: center;
  padding: 20px 24px !important;
}

.stat-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 18px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-number {
  font-size: 30px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

/* 功能介绍卡片 */
.feature-card {
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

.feature-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 4px 0;
}

.feature-tags .el-tag {
  margin: 0;
  padding: 0 14px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
}
</style>
