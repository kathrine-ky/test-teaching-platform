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
      // ========== 个人信息（所有角色通用） ==========
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/UserModule/views/Profile.vue')
      },
      // ========== 管理员端 ==========
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('@/UserModule/views/AdminDashboard.vue'),
        meta: { role: 'ADMIN' }
      },
      {
        path: 'admin/course/:id',
        name: 'AdminCourseDetail',
        component: () => import('@/CourseModule/views/AdminCourseDetail.vue'),
        meta: { role: 'ADMIN' }
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
        path: 'teacher/submissions',
        name: 'TeacherSubmissions',
        component: () => import('@/SubmitModule/views/TeacherSubmissions.vue'),
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
        path: 'student/courses',
        name: 'StudentCourses',
        component: () => import('@/CourseModule/views/StudentCourses.vue'),
        meta: { role: 'STUDENT' }
      },
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
      },
      // ========== 讨论区（教师+学生） ==========
      {
        path: 'discussion',
        name: 'DiscussionList',
        component: () => import('@/DiscussionModule/views/DiscussionList.vue'),
        meta: { role: 'TEACHER_STUDENT' }
      },
      {
        path: 'discussion/:id',
        name: 'DiscussionDetail',
        component: () => import('@/DiscussionModule/views/DiscussionDetail.vue'),
        meta: { role: 'TEACHER_STUDENT' }
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
      const routeRole = to.meta?.role
      if (routeRole) {
        // TEACHER_STUDENT 表示教师和学生都可以访问
        if (routeRole === 'TEACHER_STUDENT') {
          if (userInfo.role !== 'TEACHER' && userInfo.role !== 'STUDENT') {
            next('/dashboard')
          } else {
            next()
          }
        } else if (routeRole !== userInfo.role) {
          next('/dashboard')
        } else {
          next()
        }
      } else {
        next()
      }
    }
  }
})

export default router
