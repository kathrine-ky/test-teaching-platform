<template>
  <div class="course-detail-page">
    <!-- 返回按钮 -->
    <div class="back-bar">
      <el-button type="text" icon="el-icon-arrow-left" @click="$router.push('/dashboard')" class="back-btn">
        返回首页
      </el-button>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>课程详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 课程基本信息 -->
    <div class="game-card info-card" v-loading="loading">
      <div class="card-header-custom">
        <span class="header-icon-box"><i class="el-icon-reading"></i></span>
        <span>课程信息</span>
      </div>
      <el-descriptions :column="2" border size="medium">
        <el-descriptions-item label="课程名称">{{ course.courseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ course.teacherName || '未指定教师' }}</el-descriptions-item>
        <el-descriptions-item label="课程描述" :span="2">{{ course.description || '暂无描述' }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 选课学生列表 -->
    <div class="game-card table-card" v-loading="studentLoading">
      <div class="card-header-custom">
        <span class="header-icon-box"><i class="el-icon-user-solid"></i></span>
        <span>选课学生列表</span>
        <el-tag size="small" class="count-tag" type="info">共 {{ studentList.length }} 人</el-tag>
      </div>
      <el-table :data="studentList" stripe style="width: 100%" v-if="studentList.length > 0">
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
      </el-table>
      <div v-else class="empty-state">
        <div class="empty-icon">📭</div>
        <p class="empty-text">该课程暂无选课学生</p>
      </div>
    </div>
  </div>
</template>

<script>
import { getCourseDetail } from '@/CourseModule/api'
import { getCourseStudents } from '@/CourseModule/api'

export default {
  name: 'AdminCourseDetail',
  data() {
    return {
      loading: false,
      studentLoading: false,
      course: {},
      studentList: []
    }
  },
  created() {
    const courseId = this.$route.params.id
    if (courseId) {
      this.loadData(courseId)
    } else {
      this.$message.warning('缺少课程ID')
      this.$router.push('/dashboard')
    }
  },
  methods: {
    async loadData(courseId) {
      this.loading = true
      this.studentLoading = true
      try {
        const [courseRes, studentsRes] = await Promise.all([
          getCourseDetail(courseId),
          getCourseStudents(courseId)
        ])
        this.course = courseRes.data || {}
        this.studentList = studentsRes.data || []
      } catch (e) {
        console.error('加载课程详情失败:', e)
        this.$message.error('加载课程详情失败')
      } finally {
        this.loading = false
        this.studentLoading = false
      }
    }
  }
}
</script>

<style scoped>
.course-detail-page {
  max-width: 1000px;
  margin: 0 auto;
}

.back-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.back-btn {
  font-size: 14px;
  font-weight: 600;
  color: #55C98A;
}

.back-btn:hover {
  color: #3DCE7B;
}

.game-card {
  border-radius: 16px;
  border: 1px solid #E8EFE8;
  background: #fff;
  margin-bottom: 20px;
}

.info-card {
  padding: 24px;
}

.table-card {
  padding: 24px;
}

.card-header-custom {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  font-size: 16px;
  color: #2D3A2F;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #EEF5F0;
}

.header-icon-box {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: #E8FFF1;
  color: #55C98A;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.count-tag {
  margin-left: auto;
}

.empty-state {
  text-align: center;
  padding: 48px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 15px;
  color: #8A9B8F;
  margin: 0;
  font-weight: 500;
}
</style>
