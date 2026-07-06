<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧品牌区域 -->
      <div class="login-brand">
        <div class="brand-icon">
          <i class="el-icon-s-platform"></i>
        </div>
        <h1 class="brand-title">软件测试实践教学管理平台</h1>
        <p class="brand-subtitle">Software Testing Practice Teaching Platform</p>
        <div class="brand-features">
          <div class="feature-item">
            <i class="el-icon-document-checked"></i>
            <span>在线发布与提交实验作业</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-s-data"></i>
            <span>自动评分与成绩统计分析</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-reading"></i>
            <span>课程管理与教学资源整合</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-area">
        <div class="login-form-card">
          <h2 class="form-title">用户登录</h2>
          <p class="form-subtitle">请输入您的账号信息</p>
          <el-form ref="loginForm" :model="form" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                prefix-icon="el-icon-user"
                placeholder="请输入用户名"
                size="medium"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                prefix-icon="el-icon-lock"
                placeholder="请输入密码"
                size="medium"
                @keyup.enter.native="handleLogin"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                @click="handleLogin"
                class="login-btn"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-tips">
            <div class="tips-title">
              <i class="el-icon-info"></i> 测试账号
            </div>
            <p>教师：teacher01 / 123456（王老师）</p>
            <p>学生：stu01 / 123456（张三）</p>
            <p class="tips-more">stu02 / 123456（李四）| stu03 / 123456（王五）</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/UserModule/api'

export default {
  name: 'Login',
  data() {
    return {
      form: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await login(this.form)
          if (res && res.data) {
            this.$store.commit('SET_TOKEN', res.data.token)
            this.$store.commit('SET_USER_INFO', res.data)
            this.$message.success('登录成功')
            this.$router.push('/dashboard')
          }
        } catch (e) {
          console.error('登录失败:', e)
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8edf5 0%, #dce3ed 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -30%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.08) 0%, transparent 70%);
  border-radius: 50%;
}

.login-container::after {
  content: '';
  position: absolute;
  bottom: -40%;
  left: -20%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.06) 0%, transparent 70%);
  border-radius: 50%;
}

.login-wrapper {
  display: flex;
  width: 900px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* 左侧品牌区域 */
.login-brand {
  flex: 1;
  background: linear-gradient(160deg, #409EFF 0%, #337ecc 100%);
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #ffffff;
}

.brand-icon {
  font-size: 48px;
  margin-bottom: 24px;
  opacity: 0.9;
}

.brand-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: 1px;
}

.brand-subtitle {
  font-size: 13px;
  opacity: 0.75;
  margin: 0 0 36px;
  letter-spacing: 0.5px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.feature-item i {
  font-size: 18px;
  width: 20px;
  text-align: center;
}

/* 右侧登录表单 */
.login-form-area {
  flex: 0 0 440px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
}

.login-form-card {
  width: 100%;
  max-width: 340px;
}

.form-title {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 4px;
  text-align: center;
}

.form-subtitle {
  font-size: 13px;
  color: #909399;
  margin: 0 0 32px;
  text-align: center;
}

.login-form {
  margin-top: 0;
}

.login-form .el-form-item {
  margin-bottom: 22px;
}

.login-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border-radius: 6px;
}

.login-tips {
  margin-top: 24px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.tips-title {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
}

.tips-title i {
  color: #409EFF;
}

.login-tips p {
  color: #909399;
  font-size: 12px;
  line-height: 2;
  margin: 0;
}

.tips-more {
  color: #c0c4cc !important;
}

/* 响应式 */
@media screen and (max-width: 960px) {
  .login-wrapper {
    width: 420px;
    flex-direction: column;
  }
  .login-brand {
    display: none;
  }
  .login-form-area {
    flex: none;
    padding: 40px 32px;
  }
}
</style>
