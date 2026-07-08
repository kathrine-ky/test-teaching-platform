<template>
  <el-container class="main-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '230px'" class="sidebar">
      <div class="logo-area">
        <div class="logo-icon-box">
          <i class="el-icon-s-platform logo-icon"></i>
        </div>
        <span v-if="!isCollapse" class="logo-text">ProjectOne</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="transparent"
        text-color="rgba(255,255,255,0.85)"
        active-text-color="#FFFFFF"
        router
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页概览</span>
        </el-menu-item>

        <template v-if="isAdmin">
          <el-menu-item index="/admin/users">
            <i class="el-icon-s-custom"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
        </template>

        <template v-if="isTeacher">
          <el-menu-item index="/teacher/courses">
            <i class="el-icon-reading"></i>
            <span slot="title">课程信息管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/tasks">
            <i class="el-icon-s-order"></i>
            <span slot="title">作业管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/submissions">
            <i class="el-icon-document-checked"></i>
            <span slot="title">已提交总览</span>
          </el-menu-item>
          <el-menu-item index="/discussion">
            <i class="el-icon-chat-dot-round"></i>
            <span slot="title">讨论区</span>
          </el-menu-item>
        </template>

        <template v-if="isStudent">
          <el-menu-item index="/student/courses">
            <i class="el-icon-reading"></i>
            <span slot="title">我的课程</span>
          </el-menu-item>
          <el-menu-item index="/student/tasks">
            <i class="el-icon-s-order"></i>
            <span slot="title">实验任务</span>
          </el-menu-item>
          <el-menu-item index="/student/my-scores">
            <i class="el-icon-s-data"></i>
            <span slot="title">我的成绩</span>
          </el-menu-item>
          <el-menu-item index="/discussion">
            <i class="el-icon-chat-dot-round"></i>
            <span slot="title">讨论区</span>
          </el-menu-item>
        </template>
      </el-menu>

      <!-- 底部装饰 -->
      <div v-if="!isCollapse" class="sidebar-footer">
        <div class="footer-card">
          <i class="el-icon-magic-stick"></i>
          <span>游戏化学习</span>
        </div>
      </div>
    </el-aside>

    <!-- 右侧主体 -->
    <el-container>
      <!-- 顶栏 -->
      <el-header class="top-header">
        <div class="header-left">
          <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
             @click="isCollapse = !isCollapse"
             class="collapse-btn"></i>
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumb">{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tag v-if="isAdmin" class="role-tag role-admin" size="small">
            <i class="el-icon-s-check"></i> 管理员
          </el-tag>
          <el-tag v-else-if="isTeacher" class="role-tag role-teacher" size="small">
            <i class="el-icon-s-custom"></i> 教师
          </el-tag>
          <el-tag v-else class="role-tag role-student" size="small">
            <i class="el-icon-user-solid"></i> 学生
          </el-tag>

          <div class="user-area">
            <div class="user-avatar">
              <i class="el-icon-user-solid"></i>
            </div>
            <span class="user-name">{{ userName }}</span>
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-dropdown">
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown" class="user-dropdown-menu">
                <el-dropdown-item command="profile" icon="el-icon-user">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" icon="el-icon-switch-button" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'MainLayout',
  data() {
    return {
      isCollapse: false
    }
  },
  computed: {
    ...mapGetters(['isAdmin', 'isTeacher', 'isStudent', 'userName']),
    activeMenu() {
      return this.$route.path
    },
    breadcrumb() {
      const map = {
        '/admin/users': '用户管理',
        '/teacher/courses': '课程信息管理',
        '/teacher/tasks': '作业管理',
        '/teacher/submissions': '已提交总览',
        '/student/courses': '我的课程',
        '/student/tasks': '实验任务',
        '/student/my-scores': '我的成绩',
        '/discussion': '讨论区',
        '/profile': '个人信息'
      }
      if (this.$route.path.includes('/admin/course/')) {
        return '课程详情'
      }
      if (this.$route.path.includes('/teacher/task/')) {
        if (this.$route.path.includes('/submissions')) return '作业批改'
        if (this.$route.path.includes('/statistics')) return '成绩统计'
      }
      if (this.$route.path.includes('/student/task/') && this.$route.path.includes('/submit')) {
        return '提交作业'
      }
      if (this.$route.path.includes('/discussion/') && this.$route.params.id) {
        return '话题详情'
      }
      return map[this.$route.path] || ''
    }
  },
  methods: {
    handleCommand(cmd) {
      if (cmd === 'profile') {
        this.$router.push('/profile')
      } else if (cmd === 'logout') {
        this.$store.commit('LOGOUT')
        this.$router.push('/login')
        this.$message.success('已退出登录')
      }
    }
  }
}
</script>

<style scoped>
.main-container {
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  background: linear-gradient(180deg, #55C98A 0%, #3AAE75 100%);
  overflow-y: auto;
  overflow-x: hidden;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
  position: relative;
}

.sidebar::-webkit-scrollbar {
  width: 0;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.04'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  pointer-events: none;
}

.logo-area {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  position: relative;
  z-index: 1;
  padding: 0 16px;
}

.logo-icon-box {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.logo-icon {
  font-size: 22px;
  color: #FFFFFF;
}

.logo-text {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.5px;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 菜单 */
.el-menu {
  border-right: none;
  flex: 1;
  padding: 12px 0;
  position: relative;
  z-index: 1;
}

.sidebar >>> .el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 6px 12px;
  border-radius: 12px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 600;
}

.sidebar >>> .el-menu-item i {
  font-size: 18px;
  margin-right: 8px;
  transition: transform 0.3s;
}

.sidebar >>> .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.15) !important;
  transform: translateX(4px);
}

.sidebar >>> .el-menu-item:hover i {
  transform: scale(1.15);
}

.sidebar >>> .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.25) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 16px;
  position: relative;
  z-index: 1;
}

.footer-card {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 14px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.footer-card i {
  font-size: 16px;
  color: #FFD66B;
}

/* 顶栏 */
.top-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border-bottom: 1px solid #E8F5EE;
  padding: 0 28px;
  height: 70px;
  box-shadow: 0 2px 16px rgba(85, 201, 138, 0.06);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 22px;
  cursor: pointer;
  color: #5A6B62;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 8px;
  border-radius: 10px;
}

.collapse-btn:hover {
  color: #55C98A;
  background-color: #E8FFF1;
  transform: scale(1.1);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.role-tag {
  border-radius: 20px !important;
  font-weight: 600 !important;
  border: none !important;
  padding: 0 12px !important;
  height: 30px !important;
  line-height: 30px !important;
}

.role-tag i {
  margin-right: 4px;
  font-size: 12px;
}

.role-admin {
  background: #E8F7FF !important;
  color: #2A7A9A !important;
}

.role-teacher {
  background: #FFF8E1 !important;
  color: #8A7000 !important;
}

.role-student {
  background: #E8FFF1 !important;
  color: #2A9A5A !important;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 8px 6px 6px;
  border-radius: 30px;
  background: #F5FBF7;
  transition: all 0.25s ease;
}

.user-area:hover {
  background: #E8FFF1;
  transform: scale(1.03);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #55C98A, #3AAE75);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
}

.user-name {
  color: #2D3E36;
  font-size: 14px;
  font-weight: 600;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-dropdown {
  color: #8B9A91;
  font-size: 12px;
  transition: transform 0.25s;
}

.user-area:hover .user-dropdown {
  transform: rotate(180deg);
  color: #55C98A;
}

/* 主内容区 */
.main-content {
  background: #F5FBF7;
  min-height: calc(100vh - 70px);
  padding: 28px;
  overflow-y: auto;
}

/* 下拉菜单 */
.user-dropdown-menu {
  border-radius: 14px !important;
  box-shadow: 0 8px 24px rgba(85, 201, 138, 0.15) !important;
  border: 1px solid #E8F5EE !important;
}
</style>
