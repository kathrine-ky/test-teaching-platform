<template>
  <el-container class="main-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo-area">
        <i class="el-icon-s-platform logo-icon"></i>
        <span v-if="!isCollapse" class="logo-text">软件测试教学平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页概览</span>
        </el-menu-item>

        <template v-if="isTeacher">
          <el-menu-item index="/teacher/courses">
            <i class="el-icon-reading"></i>
            <span slot="title">课程信息管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/tasks">
            <i class="el-icon-s-order"></i>
            <span slot="title">作业管理</span>
          </el-menu-item>
        </template>

        <template v-if="isStudent">
          <el-menu-item index="/student/tasks">
            <i class="el-icon-s-order"></i>
            <span slot="title">实验任务</span>
          </el-menu-item>
          <el-menu-item index="/student/my-scores">
            <i class="el-icon-s-data"></i>
            <span slot="title">我的成绩</span>
          </el-menu-item>
        </template>
      </el-menu>
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
          <el-tag :type="isTeacher ? 'warning' : 'success'" size="small" effect="plain">
            {{ isTeacher ? '教师端' : '学生端' }}
          </el-tag>
          <span class="user-name">{{ userName }}</span>
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-dropdown">
              <i class="el-icon-user-solid"></i>
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="logout" icon="el-icon-switch-button">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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
    ...mapGetters(['isTeacher', 'isStudent', 'userName']),
    activeMenu() {
      return this.$route.path
    },
    breadcrumb() {
      const map = {
        '/teacher/courses': '课程信息管理',
        '/teacher/tasks': '作业管理',
        '/student/tasks': '实验任务',
        '/student/my-scores': '我的成绩'
      }
      // 子路由匹配
      if (this.$route.path.includes('/teacher/task/')) {
        if (this.$route.path.includes('/submissions')) return '作业批改'
        if (this.$route.path.includes('/statistics')) return '成绩统计'
      }
      if (this.$route.path.includes('/student/task/') && this.$route.path.includes('/submit')) {
        return '提交作业'
      }
      return map[this.$route.path] || ''
    }
  },
  methods: {
    handleCommand(cmd) {
      if (cmd === 'logout') {
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
  background-color: #304156;
  overflow-y: auto;
  overflow-x: hidden;
  transition: width 0.3s ease;
}

.sidebar::-webkit-scrollbar {
  width: 0;
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background-color: #263445;
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  white-space: nowrap;
  overflow: hidden;
}

.logo-icon {
  font-size: 22px;
  color: #409EFF;
}

.logo-text {
  overflow: hidden;
  text-overflow: ellipsis;
}

.el-menu {
  border-right: none;
}

/* 顶栏 */
.top-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border-bottom: 1px solid #e8eaed;
  padding: 0 24px;
  height: 60px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;
  padding: 4px;
  border-radius: 4px;
}

.collapse-btn:hover {
  color: #409EFF;
  background-color: #ecf5ff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-name {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.user-dropdown {
  cursor: pointer;
  color: #409EFF;
  font-size: 16px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.user-dropdown:hover {
  background-color: #ecf5ff;
}

/* 主内容区 */
.main-content {
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
  padding: 24px;
  overflow-y: auto;
}
</style>
