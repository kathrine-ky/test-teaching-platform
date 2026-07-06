import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/UserModule/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/common/Dashboard.vue')
      },
      // ========== 教师端 ==========
      {
        path: 'teacher/courses',
        name: 'TeacherCourses',
        component: () => import('@/CourseModule/views/CourseManage.vue'),
        meta: { role: 'TEACHER' }
      },
      {
        path: 'teacher/tasks',
        name: 'TeacherTasks',
        component: () => import('@/TaskModule/views/TaskManage.vue'),
        meta: { role: 'TEACHER' }
      },
      {
        path: 'teacher/task/:id/submissions',
        name: 'TaskSubmissions',
        component: () => import('@/SubmitModule/views/SubmissionReview.vue'),
        meta: { role: 'TEACHER' }
      },
      {
        path: 'teacher/task/:id/statistics',
        name: 'ScoreStatistics',
        component: () => import('@/ScoreModule/views/ScoreStatistics.vue'),
        meta: { role: 'TEACHER' }
      },
      // ========== 学生端 ==========
      {
        path: 'student/tasks',
        name: 'StudentTasks',
        component: () => import('@/TaskModule/views/TaskList.vue'),
        meta: { role: 'STUDENT' }
      },
      {
        path: 'student/task/:id/submit',
        name: 'SubmitHomework',
        component: () => import('@/SubmitModule/views/HomeworkSubmit.vue'),
        meta: { role: 'STUDENT' }
      },
      {
        path: 'student/my-scores',
        name: 'MyScores',
        component: () => import('@/ScoreModule/views/MyScores.vue'),
        meta: { role: 'STUDENT' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (to.meta?.role && to.meta.role !== userInfo.role) {
        next('/dashboard')
      } else {
        next()
      }
    }
  }
})

export default router
