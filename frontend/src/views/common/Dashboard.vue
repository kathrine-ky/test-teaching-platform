<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner" :class="{ 'admin-banner': isAdmin }">
      <div class="banner-bg-shapes">
        <span class="banner-shape shape-1"></span>
        <span class="banner-shape shape-2"></span>
        <span class="banner-shape shape-3"></span>
      </div>
      <div class="banner-content">
        <div class="banner-left">
          <div class="banner-greeting">
            <span class="greeting-emoji">{{ greetingEmoji }}</span>
            <span class="greeting-text">{{ greetingText }}</span>
          </div>
          <h3 class="welcome-title">
            <template v-if="isAdmin">管理平台用户体系</template>
            <template v-else-if="isTeacher">一站式教学管理中心</template>
            <template v-else>轻松完成课程实践</template>
          </h3>
          <p class="welcome-desc">
            <template v-if="isAdmin">注册管理教师和学生账号，重置用户密码，维护平台用户体系</template>
            <template v-else-if="isTeacher">管理课程、发布作业、批改提交、分析成绩，让教学更高效</template>
            <template v-else>查看实验任务、在线提交作业、查询个人成绩，每一步都有成长</template>
          </p>
          <div class="banner-badges">
            <span class="banner-badge" :class="roleBadgeClass">{{ roleBadgeText }}</span>
            <span class="banner-badge banner-badge-sub">游戏化学习</span>
          </div>
        </div>
        <div class="banner-right">
          <div class="banner-illustration">
            <div class="illustration-circle circle-lg"></div>
            <div class="illustration-circle circle-md"></div>
            <div class="illustration-circle circle-sm"></div>
            <div class="illustration-icon">🎯</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片行 - 管理员端：只显示总用户 -->
    <el-row :gutter="20" class="stat-row" v-if="isAdmin">
      <el-col :span="6">
        <div class="game-card stat-card-item" @click="$router.push('/admin/users')">
          <div class="stat-icon-box stat-icon-user">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num">{{ stats.userCount }}</div>
            <div class="stat-label">总用户</div>
          </div>
          <div class="stat-badge">查看</div>
        </div>
      </el-col>
    </el-row>

    <!-- 统计卡片行 - 教师端：不显示环形进度图 -->
    <el-row :gutter="20" class="stat-row" v-if="isTeacher">
      <el-col :span="8">
        <div class="game-card stat-card-item" @click="handleStatClick('task')">
          <div class="stat-icon-box stat-icon-task">
            <i class="el-icon-s-order"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num">{{ stats.taskCount }}</div>
            <div class="stat-label">实验任务</div>
          </div>
          <div class="stat-badge">查看</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="game-card stat-card-item" @click="handleStatClick('submit')">
          <div class="stat-icon-box stat-icon-submit">
            <i class="el-icon-document-checked"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num">{{ stats.submitCount }}</div>
            <div class="stat-label">已提交</div>
          </div>
          <div class="stat-badge">查看</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="game-card stat-card-item" @click="handleStatClick('course')">
          <div class="stat-icon-box stat-icon-course">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num">{{ stats.courseCount }}</div>
            <div class="stat-label">课程数量</div>
          </div>
          <div class="stat-badge">查看</div>
        </div>
      </el-col>
    </el-row>

    <!-- 统计卡片行 - 学生端：保留环形进度图 -->
    <el-row :gutter="20" class="stat-row" v-if="isStudent">
      <el-col :span="16">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="game-card stat-card-item" @click="handleStatClick('task')">
              <div class="stat-icon-box stat-icon-task">
                <i class="el-icon-s-order"></i>
              </div>
              <div class="stat-info">
                <div class="stat-num">{{ stats.taskCount }}</div>
                <div class="stat-label">实验任务</div>
              </div>
              <div class="stat-badge">查看</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="game-card stat-card-item" @click="handleStatClick('submit')">
              <div class="stat-icon-box stat-icon-submit">
                <i class="el-icon-document-checked"></i>
              </div>
              <div class="stat-info">
                <div class="stat-num">{{ stats.submitCount }}</div>
                <div class="stat-label">已提交</div>
              </div>
              <div class="stat-badge">查看</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="game-card stat-card-item" @click="handleStatClick('course')">
              <div class="stat-icon-box stat-icon-course">
                <i class="el-icon-reading"></i>
              </div>
              <div class="stat-info">
                <div class="stat-num">{{ stats.courseCount }}</div>
                <div class="stat-label">课程数量</div>
              </div>
              <div class="stat-badge">查看</div>
            </div>
          </el-col>
        </el-row>
      </el-col>
      <!-- 环形进度图 - 仅学生端显示 -->
      <el-col :span="8">
        <div class="game-card ring-card" v-loading="loading">
          <div class="ring-header">
            <i class="el-icon-circle-check"></i>
            <span>整体完成进度</span>
          </div>
          <div class="ring-body">
            <svg class="ring-svg" viewBox="0 0 140 140">
              <defs>
                <linearGradient id="ringGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stop-color="#55C98A" />
                  <stop offset="100%" stop-color="#3DCE7B" />
                </linearGradient>
              </defs>
              <circle class="ring-track" cx="70" cy="70" r="54" fill="none" stroke="#E8FFF1" stroke-width="12" />
              <circle class="ring-progress" cx="70" cy="70" r="54" fill="none" stroke="url(#ringGrad)" stroke-width="12"
                stroke-linecap="round" :stroke-dasharray="ringDasharray" stroke-dashoffset="0"
                transform="rotate(-90 70 70)" />
            </svg>
            <div class="ring-center">
              <span class="ring-percent">{{ ringPercent }}%</span>
              <span class="ring-sub">{{ stats.submitCount }}/{{ stats.taskCount }}</span>
            </div>
          </div>
          <div class="ring-footer">
            <div class="ring-level" v-if="ringPercent >= 80">🎖️ 完成达人</div>
            <div class="ring-level" v-else-if="ringPercent >= 50">💪 继续加油</div>
            <div class="ring-level" v-else-if="ringPercent > 0">🌱 初露头角</div>
            <div class="ring-level" v-else>🚀 等待开始</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 课程总览 & 公告 -->
    <el-row :gutter="20" class="bottom-row">
      <el-col :span="isTeacher || isAdmin ? 16 : 24">
        <el-card shadow="never" class="game-card section-card">
          <div slot="header" class="card-header-custom">
            <div class="header-left">
              <span class="header-icon-box"><i class="el-icon-reading"></i></span>
              <span>课程总览</span>
            </div>
            <el-button v-if="courses.length && !isAdmin" type="text" class="header-link" @click="handleStatClick('course')">
              全部 <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
          <div class="course-grid" v-loading="loading">
            <div v-if="courses.length === 0" class="empty-state">
              <div class="empty-icon">📚</div>
              <p class="empty-text">暂无课程数据</p>
              <p class="empty-hint">开始你的学习之旅吧</p>
            </div>
            <div class="course-card" v-for="course in courses" :key="course.id" @click="goCourseDetail(course)">
              <div class="course-card-top">
                <div class="course-avatar">{{ course.courseName ? course.courseName.charAt(0) : 'C' }}</div>
                <div class="course-badge" v-if="isStudent">已选</div>
              </div>
              <div class="course-card-body">
                <h4 class="course-name">{{ course.courseName }}</h4>
                <p class="course-teacher">
                  <i class="el-icon-user"></i>
                  {{ course.teacherName || '未指定教师' }}
                </p>
              </div>
              <div class="course-card-footer">
                <span class="course-arrow"><i class="el-icon-arrow-right"></i></span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8" v-if="isTeacher || isAdmin">
        <el-card shadow="never" class="game-card section-card">
          <div slot="header" class="card-header-custom">
            <div class="header-left">
              <span class="header-icon-box"><i class="el-icon-bell"></i></span>
              <span>公告通知</span>
            </div>
          </div>
          <div class="notice-list">
            <div class="notice-item" v-for="(notice, idx) in notices" :key="idx">
              <div class="notice-timeline">
                <div class="timeline-dot" :class="'dot-' + notice.type"></div>
                <div class="timeline-line" v-if="idx < notices.length - 1"></div>
              </div>
              <div class="notice-body">
                <p class="notice-text">{{ notice.text }}</p>
                <span class="notice-time">{{ notice.time }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能介绍 -->
    <el-card shadow="never" class="game-card feature-card">
      <div slot="header" class="card-header-custom">
        <div class="header-left">
          <span class="header-icon-box"><i class="el-icon-info"></i></span>
          <span>平台功能介绍</span>
        </div>
      </div>
      <el-collapse v-model="activeCollapse" class="feature-collapse">
        <el-collapse-item title="教师端功能" name="teacher" v-if="isTeacher">
          <div class="feature-tags">
            <el-tag type="" size="medium" effect="light">📋 课程信息管理：新增、编辑、删除课程</el-tag>
            <el-tag type="success" size="medium" effect="light">📝 作业管理：发布线上作业</el-tag>
            <el-tag type="warning" size="medium" effect="light">✍️ 作业批改：批改学生提交的作业、填写评价</el-tag>
            <el-tag type="danger" size="medium" effect="light">📊 数据查看：学生成绩统计、成绩数据分析展示</el-tag>
          </div>
        </el-collapse-item>
        <el-collapse-item title="学生端功能" name="student" v-if="isStudent">
          <div class="feature-tags">
            <el-tag size="medium" effect="light">👀 查看教师发布的作业</el-tag>
            <el-tag type="success" size="medium" effect="light">📤 在线提交作业（测试用例+缺陷报告+测试总结）</el-tag>
            <el-tag type="warning" size="medium" effect="light">📎 上传Word文档、自动化测试脚本等附件</el-tag>
            <el-tag type="info" size="medium" effect="light">📈 查询个人作业得分</el-tag>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { listCourses, listAllCourses, listStudentCourses } from '@/CourseModule/api'
import { listTasks } from '@/TaskModule/api'
import { getMyScores } from '@/ScoreModule/api'
import { getTeacherSubmissionSummary } from '@/SubmitModule/api'
import { getAdminDashboardStats } from '@/UserModule/api'

export default {
  name: 'Dashboard',
  data() {
    return {
      loading: false,
      activeCollapse: ['teacher', 'student'],
      stats: { taskCount: 0, submitCount: 0, courseCount: 0, userCount: 0 },
      courses: [],
      notices: [
        { type: 'success', text: '期末实验报告提交截止日期为7月15日', time: '2024-07-01' },
        { type: 'warn', text: '实验三：自动化测试脚本已发布', time: '2024-06-28' },
        { type: 'info', text: '请及时查看作业批改结果和教师评语', time: '2024-06-25' }
      ]
    }
  },
  computed: {
    ...mapGetters(['isAdmin', 'isTeacher', 'isStudent']),
    greetingEmoji() {
      const hour = new Date().getHours()
      if (hour < 9) return '🌅'
      if (hour < 12) return '☀️'
      if (hour < 18) return '🌤️'
      return '🌙'
    },
    greetingText() {
      const hour = new Date().getHours()
      if (hour < 9) return '早上好'
      if (hour < 12) return '上午好'
      if (hour < 18) return '下午好'
      return '晚上好'
    },
    roleBadgeClass() {
      if (this.isAdmin) return 'badge-admin'
      if (this.isTeacher) return 'badge-teacher'
      return 'badge-student'
    },
    roleBadgeText() {
      if (this.isAdmin) return '管理员'
      if (this.isTeacher) return '教师端'
      return '学生端'
    },
    ringPercent() {
      if (this.stats.taskCount === 0) return 0
      return Math.round((this.stats.submitCount / this.stats.taskCount) * 100)
    },
    ringDasharray() {
      const circumference = 2 * Math.PI * 54
      const percent = this.ringPercent / 100
      const filled = circumference * percent
      return `${filled} ${circumference - filled}`
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        // 管理员：使用独立的平台统计接口
        if (this.isAdmin) {
          const statsRes = await getAdminDashboardStats()
          const s = statsRes.data || {}
          this.stats.userCount = s.totalUsers || 0
          this.stats.taskCount = s.totalTasks || 0
          this.stats.submitCount = s.totalSubmissions || 0
          this.stats.courseCount = s.totalCourses || 0

          // 管理员也可以看到所有课程列表
          try {
            const courseRes = await listAllCourses()
            const courseData = courseRes.data
            if (Array.isArray(courseData)) {
              this.courses = courseData
            } else if (courseData?.records) {
              this.courses = courseData.records
            }
          } catch (e) {
            this.courses = []
          }
          return
        }

        const promises = []

        if (this.isStudent) {
          promises.push(listStudentCourses())
        } else if (this.isTeacher) {
          promises.push(listCourses())
        } else {
          promises.push(Promise.resolve({ data: [] }))
        }

        if (this.isStudent || this.isTeacher) {
          promises.push(listTasks({ current: 1, size: 1000 }))
        } else {
          promises.push(Promise.resolve({ data: { records: [] } }))
        }

        if (this.isStudent) {
          promises.push(getMyScores().catch(() => ({ data: [] })))
        } else {
          promises.push(Promise.resolve({ data: [] }))
        }

        const results = await Promise.all(promises)
        const courseRes = results[0]
        const taskRes = results[1]
        const scoresRes = results[2]

        const courseData = courseRes.data
        if (Array.isArray(courseData)) {
          this.courses = courseData
          this.stats.courseCount = courseData.length
        } else if (courseData?.records) {
          this.courses = courseData.records
          this.stats.courseCount = courseData.total || courseData.records.length
        }

        const taskData = taskRes.data
        const tasks = taskData?.records || taskData || []
        if (Array.isArray(tasks)) {
          this.stats.taskCount = tasks.length
        }

        if (this.isStudent) {
          const scores = scoresRes.data || []
          this.stats.submitCount = Array.isArray(scores) ? scores.length : 0
        } else if (this.isTeacher) {
          // 教师端：获取所有任务的总已提交人数
          try {
            const summaryRes = await getTeacherSubmissionSummary()
            const summaryList = summaryRes.data || []
            this.stats.submitCount = summaryList.reduce((sum, item) => sum + (item.submittedCount || 0), 0)
          } catch (e) {
            this.stats.submitCount = 0
          }
        }
      } catch (e) {
        console.error('加载仪表盘数据失败:', e)
      } finally {
        this.loading = false
      }
    },
    goCourseDetail(course) {
      if (this.isAdmin) {
        this.$router.push(`/admin/course/${course.id}`)
        return
      }
      if (this.isTeacher) {
        this.$router.push('/teacher/courses')
      } else if (this.isStudent) {
        this.$router.push('/student/courses')
      }
    },
    handleStatClick(type) {
      if (type === 'task') {
        this.$router.push(this.isTeacher ? '/teacher/tasks' : '/student/tasks')
      } else if (type === 'submit') {
        this.$router.push(this.isTeacher ? '/teacher/submissions' : '/student/my-scores')
      } else if (type === 'course') {
        this.$router.push(this.isTeacher ? '/teacher/courses' : '/student/courses')
      }
    }
  }
}
</script>

<style scoped>
.dashboard {
  max-width: 1280px;
  margin: 0 auto;
}

/* ========== 欢迎横幅 ========== */
.welcome-banner {
  background: linear-gradient(135deg, #E8FFF1 0%, #C8F7D5 50%, #B8EFD0 100%);
  border-radius: 24px;
  padding: 0;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(85, 201, 138, 0.15);
}

.welcome-banner.admin-banner {
  background: linear-gradient(135deg, #E8F0FF 0%, #D4E2FC 50%, #C5D8F7 100%);
  border: 1px solid rgba(110, 140, 200, 0.15);
}

.welcome-banner.admin-banner .welcome-title {
  color: #3A5A7A;
}

.welcome-banner.admin-banner .welcome-desc {
  color: #5A7A9A;
}

.welcome-banner.admin-banner .banner-badge.badge-admin {
  background: rgba(110, 140, 200, 0.15);
  color: #3A6AAA;
}

.banner-bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.banner-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.3;
}

.shape-1 {
  width: 160px;
  height: 160px;
  top: -40px;
  right: 60px;
  background: rgba(85, 201, 138, 0.15);
  animation: bannerFloat1 8s ease-in-out infinite;
}

.shape-2 {
  width: 80px;
  height: 80px;
  bottom: -10px;
  right: 200px;
  background: rgba(255, 214, 107, 0.2);
  animation: bannerFloat2 6s ease-in-out infinite;
}

.shape-3 {
  width: 60px;
  height: 60px;
  top: 20px;
  right: 260px;
  background: rgba(110, 198, 255, 0.2);
  animation: bannerFloat3 7s ease-in-out infinite;
}

@keyframes bannerFloat1 {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-10px) scale(1.05); }
}

@keyframes bannerFloat2 {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(8px) scale(0.95); }
}

@keyframes bannerFloat3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-5px, -5px) scale(1.1); }
}

.banner-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 36px;
  position: relative;
  z-index: 1;
}

.banner-left {
  flex: 1;
}

.banner-greeting {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.greeting-emoji {
  font-size: 22px;
  animation: float 3s ease-in-out infinite;
}

.greeting-text {
  font-size: 14px;
  color: rgba(46, 90, 66, 0.6);
  font-weight: 500;
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  color: #2E5A42;
  margin: 0 0 8px;
  letter-spacing: 0.5px;
}

.welcome-desc {
  font-size: 14px;
  color: #5A8A6A;
  margin: 0 0 14px;
  line-height: 1.7;
  max-width: 480px;
}

.banner-badges {
  display: flex;
  gap: 8px;
}

.banner-badge {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.badge-student {
  background: rgba(85, 201, 138, 0.15);
  color: #2E7A4A;
}

.badge-teacher {
  background: rgba(255, 214, 107, 0.2);
  color: #8A6D2B;
}

.badge-admin {
  background: rgba(110, 140, 200, 0.15);
  color: #3A6AAA;
}

.banner-badge-sub {
  background: rgba(110, 198, 255, 0.12);
  color: #3A7AAA;
}

.banner-right {
  flex-shrink: 0;
  margin-left: 24px;
}

.banner-illustration {
  position: relative;
  width: 120px;
  height: 120px;
}

.illustration-circle {
  position: absolute;
  border-radius: 50%;
}

.circle-lg {
  width: 100px;
  height: 100px;
  top: 10px;
  left: 10px;
  background: linear-gradient(135deg, rgba(85, 201, 138, 0.15), rgba(85, 201, 138, 0.05));
}

.circle-md {
  width: 70px;
  height: 70px;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, rgba(255, 214, 107, 0.2), rgba(255, 214, 107, 0.05));
}

.circle-sm {
  width: 45px;
  height: 45px;
  bottom: 0;
  left: 0;
  background: linear-gradient(135deg, rgba(110, 198, 255, 0.2), rgba(110, 198, 255, 0.05));
}

.illustration-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 40px;
  animation: float 3s ease-in-out infinite;
}

/* ========== 统计卡片 ========== */
.stat-row {
  margin-bottom: 24px;
}

.stat-card-item {
  display: flex;
  align-items: center;
  padding: 20px 20px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  height: 100%;
}

.stat-card-item::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 0;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(85, 201, 138, 0.03));
  transition: width 0.3s ease;
}

.stat-card-item:hover::after {
  width: 100%;
}

.stat-icon-box {
  width: 50px;
  height: 50px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.stat-card-item:hover .stat-icon-box {
  transform: scale(1.1) rotate(-5deg);
}

.stat-icon-task {
  background: linear-gradient(135deg, #E8FFF1, #C8F0D8);
  color: #55C98A;
}

.stat-icon-user {
  background: linear-gradient(135deg, #E8F0FF, #CCDDF8);
  color: #5A8AEA;
}

.stat-icon-submit {
  background: linear-gradient(135deg, #FFF8E8, #FFE8B8);
  color: #E8B84B;
}

.stat-icon-course {
  background: linear-gradient(135deg, #E8F0FF, #CCDDF8);
  color: #6EC6FF;
}

.stat-info {
  flex: 1;
  margin-left: 14px;
  min-width: 0;
}

.stat-num {
  font-size: 28px;
  font-weight: 800;
  color: #2D3A2F;
  line-height: 1.1;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.stat-card-item:hover .stat-num {
  transform: scale(1.05);
}

.stat-label {
  font-size: 12px;
  color: #8A9B8F;
  margin-top: 2px;
  font-weight: 500;
}

.stat-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 11px;
  color: #AAB8AD;
  font-weight: 500;
  opacity: 0;
  transform: translateX(8px);
  transition: all 0.3s ease;
}

.stat-card-item:hover .stat-badge {
  opacity: 1;
  transform: translateX(0);
}

/* ========== 环形进度 ========== */
.ring-card {
  padding: 24px;
  text-align: center;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.ring-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
  color: #2D3A2F;
  margin-bottom: 16px;
}

.ring-header i {
  color: #55C98A;
  font-size: 18px;
}

.ring-body {
  position: relative;
  display: flex;
  justify-content: center;
}

.ring-svg {
  width: 140px;
  height: 140px;
}

.ring-track {
  opacity: 0.5;
}

.ring-progress {
  transition: stroke-dasharray 1s cubic-bezier(0.34, 1.56, 0.64, 1);
  filter: drop-shadow(0 2px 8px rgba(85, 201, 138, 0.25));
}

.ring-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.ring-percent {
  font-size: 30px;
  font-weight: 800;
  background: linear-gradient(135deg, #55C98A, #3DCE7B);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: block;
  line-height: 1;
}

.ring-sub {
  font-size: 11px;
  color: #9BADA2;
  margin-top: 4px;
  display: block;
}

.ring-footer {
  margin-top: 12px;
}

.ring-level {
  display: inline-block;
  padding: 4px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: #55C98A;
  background: #E8FFF1;
}

/* ========== 底部卡片 ========== */
.bottom-row {
  margin-bottom: 24px;
}

.section-card {
  border: none !important;
}

.section-card >>> .el-card__header {
  border-bottom: 1px solid #EEF5F0;
  padding: 18px 24px;
}

.section-card >>> .el-card__body {
  padding: 20px 24px;
}

.card-header-custom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  font-size: 16px;
  color: #2D3A2F;
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

.header-link {
  color: #55C98A;
  font-size: 13px;
  font-weight: 500;
  padding: 0;
}

.header-link:hover {
  color: #3DCE7B;
}

/* ========== 课程网格 ========== */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.course-card {
  background: #FAFCF8;
  border: 1px solid #E8EFE8;
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.course-card:hover {
  border-color: rgba(85, 201, 138, 0.3);
  box-shadow: 0 8px 24px rgba(85, 201, 138, 0.1);
  transform: translateY(-3px);
}

.course-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.course-avatar {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: linear-gradient(135deg, #55C98A, #3DCE7B);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
}

.course-badge {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  color: #55C98A;
  background: #E8FFF1;
}

.course-name {
  font-size: 15px;
  font-weight: 600;
  color: #2D3A2F;
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-teacher {
  font-size: 12px;
  color: #8A9B8F;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-card-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid #EEF5F0;
}

.course-arrow {
  color: #B8C8BB;
  font-size: 14px;
  transition: transform 0.3s ease;
}

.course-card:hover .course-arrow {
  transform: translateX(3px);
  color: #55C98A;
}

/* ========== 空状态 ========== */
.empty-state {
  grid-column: 1 / -1;
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
  margin: 0 0 4px;
  font-weight: 500;
}

.empty-hint {
  font-size: 12px;
  color: #B8C8BB;
  margin: 0;
}

/* ========== 公告列表 ========== */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.notice-item {
  display: flex;
  gap: 14px;
  cursor: pointer;
  padding: 10px 0;
  transition: all 0.25s ease;
}

.notice-item:hover {
  transform: translateX(4px);
}

.notice-item:hover .notice-text {
  color: #55C98A;
}

.notice-timeline {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  width: 14px;
}

.timeline-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: 2px solid;
  flex-shrink: 0;
  margin-top: 4px;
}

.dot-success {
  border-color: #55C98A;
  background: #E8FFF1;
}

.dot-warn {
  border-color: #FFD66B;
  background: #FFF8E8;
}

.dot-info {
  border-color: #6EC6FF;
  background: #E8F6FF;
}

.timeline-line {
  width: 2px;
  flex: 1;
  min-height: 20px;
  background: #E8EFE8;
  margin-top: 6px;
}

.notice-body {
  flex: 1;
  padding-bottom: 4px;
}

.notice-text {
  font-size: 13px;
  color: #4A5B4F;
  margin: 0 0 6px;
  line-height: 1.5;
  transition: color 0.25s ease;
}

.notice-time {
  font-size: 11px;
  color: #B8C8BB;
}

/* ========== 功能介绍 ========== */
.feature-card {
  border: none !important;
  margin-bottom: 8px;
}

.feature-card >>> .el-card__header {
  border-bottom: 1px solid #EEF5F0;
  padding: 18px 24px;
}

.feature-card >>> .el-card__body {
  padding: 8px 24px 20px;
}

.feature-collapse >>> .el-collapse-item__header {
  font-size: 14px;
  font-weight: 600;
  color: #3D5A47;
  padding: 12px 0;
  border-bottom: 1px solid #EEF5F0;
}

.feature-collapse >>> .el-collapse-item__wrap {
  border-bottom: none;
}

.feature-collapse >>> .el-collapse-item__content {
  padding: 16px 0;
}

.feature-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.feature-tags .el-tag {
  margin: 0;
  padding: 0 16px;
  height: 36px;
  line-height: 36px;
  font-size: 13px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: none;
}

.feature-tags .el-tag:hover {
  transform: scale(1.04);
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .banner-content {
    flex-direction: column;
    padding: 24px;
    text-align: center;
  }

  .banner-right {
    display: none;
  }

  .banner-badges {
    justify-content: center;
  }

  .welcome-desc {
    max-width: 100%;
  }

  .course-grid {
    grid-template-columns: 1fr;
  }
}
</style>
