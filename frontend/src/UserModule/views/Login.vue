<template>
  <div class="login-container">
    <!-- 浮动装饰元素 -->
    <div class="deco-bubble bubble-1"></div>
    <div class="deco-bubble bubble-2"></div>
    <div class="deco-bubble bubble-3"></div>
    <div class="deco-bubble bubble-4"></div>
    <div class="deco-ring ring-1"></div>
    <div class="deco-ring ring-2"></div>

    <div class="login-wrapper">
      <!-- 左侧品牌区域 -->
      <div class="login-brand">
        <div class="brand-header">
          <div class="brand-logo">
            <i class="el-icon-s-platform"></i>
          </div>
          <div class="brand-text">
            <h2 class="brand-name">ProjectOne</h2>
            <p class="brand-subtitle">软件测试教学平台</p>
          </div>
        </div>
        <h1 class="brand-title">
          让测试学习更有趣<br/>
          <span class="highlight">让质量保障更简单</span>
        </h1>
        <p class="brand-desc">一站式软件测试教学平台，从理论到实践，助力每一位测试工程师成长。</p>

        <div class="brand-features">
          <div class="feature-item">
            <div class="feature-icon">
              <i class="el-icon-document-checked"></i>
            </div>
            <span>在线发布与提交实验作业</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <i class="el-icon-s-data"></i>
            </div>
            <span>自动评分与成绩统计分析</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <i class="el-icon-reading"></i>
            </div>
            <span>课程管理与教学资源整合</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <i class="el-icon-chat-dot-round"></i>
            </div>
            <span>讨论交流与协作学习</span>
          </div>
        </div>

        <div class="brand-badges">
          <div class="badge-item">
            <i class="el-icon-check"></i>
            <span>实战课程</span>
          </div>
          <div class="badge-item">
            <i class="el-icon-check"></i>
            <span>在线实验</span>
          </div>
          <div class="badge-item">
            <i class="el-icon-check"></i>
            <span>缺陷管理</span>
          </div>
          <div class="badge-item">
            <i class="el-icon-check"></i>
            <span>能力评估</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-area">
        <div class="login-form-card">
          <div class="form-welcome">
            <span class="welcome-leaf">🌱</span>
            <h2 class="form-title">欢迎登录</h2>
            <p class="form-subtitle">登录您的账号，继续学习测试之旅</p>
          </div>
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
            <el-form-item class="remember-row">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                @click="handleLogin"
                class="login-btn"
              >
                登录 / 进入平台
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-tips">
            <div class="tips-title">
              <i class="el-icon-info"></i> 测试账号
            </div>
            <div class="tips-list">
              <p><span class="role-tag admin">管理员</span> admin / 123456</p>
              <p><span class="role-tag teacher">教师</span> teacher01 / 123456</p>
              <p><span class="role-tag student">学生</span> stu01 / 123456</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="login-footer">
      © 2024 ProjectOne 软件测试教学平台 · 专注测试教育 · 提升软件质量
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
      loading: false,
      rememberMe: false
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
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #E8FFF1 0%, #F5FBF7 50%, #E8F7FF 100%);
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
}

/* 装饰气泡 */
.deco-bubble {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  animation: float 6s ease-in-out infinite;
}

.bubble-1 {
  width: 120px;
  height: 120px;
  background: rgba(85, 201, 138, 0.12);
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.bubble-2 {
  width: 80px;
  height: 80px;
  background: rgba(255, 214, 107, 0.15);
  top: 20%;
  right: 8%;
  animation-delay: 1s;
}

.bubble-3 {
  width: 160px;
  height: 160px;
  background: rgba(110, 198, 255, 0.1);
  bottom: 10%;
  left: 10%;
  animation-delay: 2s;
}

.bubble-4 {
  width: 60px;
  height: 60px;
  background: rgba(85, 201, 138, 0.18);
  bottom: 25%;
  right: 12%;
  animation-delay: 3s;
}

.deco-ring {
  position: absolute;
  border-radius: 50%;
  border: 2px solid rgba(85, 201, 138, 0.1);
  pointer-events: none;
  animation: rotate 20s linear infinite;
}

.ring-1 {
  width: 300px;
  height: 300px;
  top: -80px;
  right: -80px;
}

.ring-2 {
  width: 200px;
  height: 200px;
  bottom: -60px;
  left: -60px;
  animation-direction: reverse;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.login-wrapper {
  display: flex;
  width: 1000px;
  min-height: 620px;
  background: #ffffff;
  border-radius: 28px;
  box-shadow: 0 20px 60px rgba(85, 201, 138, 0.15), 0 8px 24px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  position: relative;
  z-index: 1;
  transition: box-shadow 0.3s ease;
}

.login-wrapper:hover {
  box-shadow: 0 24px 72px rgba(85, 201, 138, 0.2), 0 12px 32px rgba(0, 0, 0, 0.06);
}

/* 左侧品牌区域 */
.login-brand {
  flex: 1;
  background: linear-gradient(160deg, #55C98A 0%, #3AAE75 50%, #2D9A68 100%);
  padding: 48px 44px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #ffffff;
  position: relative;
  overflow: hidden;
}

.login-brand::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -30%;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.login-brand::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -20%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}

.brand-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 36px;
  position: relative;
  z-index: 1;
}

.brand-logo {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.brand-name {
  font-size: 22px;
  font-weight: 800;
  margin: 0;
  letter-spacing: 0.5px;
}

.brand-subtitle {
  font-size: 13px;
  opacity: 0.85;
  margin: 0;
  letter-spacing: 1px;
}

.brand-title {
  font-size: 28px;
  font-weight: 800;
  margin: 0 0 14px;
  line-height: 1.4;
  position: relative;
  z-index: 1;
}

.brand-title .highlight {
  color: #FFD66B;
}

.brand-desc {
  font-size: 14px;
  opacity: 0.85;
  margin: 0 0 32px;
  line-height: 1.7;
  max-width: 360px;
  position: relative;
  z-index: 1;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  opacity: 0.92;
  transition: all 0.3s ease;
}

.feature-item:hover {
  opacity: 1;
  transform: translateX(6px);
}

.feature-icon {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.brand-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.badge-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.badge-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.badge-item i {
  color: #FFD66B;
  font-size: 12px;
  font-weight: bold;
}

/* 右侧登录表单 */
.login-form-area {
  flex: 0 0 420px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
  background: #ffffff;
}

.login-form-card {
  width: 100%;
  max-width: 340px;
}

.form-welcome {
  text-align: center;
  margin-bottom: 28px;
}

.welcome-leaf {
  display: inline-block;
  font-size: 36px;
  margin-bottom: 8px;
  animation: float 3s ease-in-out infinite;
}

.form-title {
  font-size: 24px;
  font-weight: 800;
  color: #2D3E36;
  margin: 0 0 8px;
}

.form-title::after {
  content: '';
  display: block;
  width: 40px;
  height: 4px;
  background: linear-gradient(90deg, #55C98A, #6EC6FF);
  border-radius: 2px;
  margin: 10px auto 0;
}

.form-subtitle {
  font-size: 13px;
  color: #8B9A91;
  margin: 0;
}

.login-form {
  margin-top: 0;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.remember-row {
  margin-bottom: 14px !important;
}

.remember-row >>> .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #55C98A;
}

.remember-row >>> .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #55C98A;
  border-color: #55C98A;
}

/* 输入框 */
.login-form >>> .el-input__inner {
  border-radius: 14px;
  border-color: #D8EEE3;
  height: 46px;
  line-height: 46px;
  padding-left: 42px;
  transition: all 0.3s ease;
  background: #FAFDFB;
}

.login-form >>> .el-input__inner:hover {
  border-color: #55C98A;
  background: #fff;
}

.login-form >>> .el-input__inner:focus {
  border-color: #55C98A;
  box-shadow: 0 0 0 4px rgba(85, 201, 138, 0.12);
  background: #fff;
}

.login-form >>> .el-input__prefix {
  left: 14px;
  color: #8B9A91;
  transition: color 0.3s;
  font-size: 16px;
}

.login-form >>> .el-input.is-focus .el-input__prefix {
  color: #55C98A;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  padding: 13px 0;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  border-radius: 14px;
  background: linear-gradient(90deg, #55C98A, #3EB575);
  border: none;
  box-shadow: 0 8px 20px rgba(85, 201, 138, 0.35);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.login-btn:hover,
.login-btn:focus {
  background: linear-gradient(90deg, #3EB575, #2D9A68);
  box-shadow: 0 12px 28px rgba(85, 201, 138, 0.45);
  transform: translateY(-2px);
}

.login-btn:active {
  transform: scale(0.98);
}

/* 测试账号提示 */
.login-tips {
  margin-top: 28px;
  padding: 18px;
  background: #E8FFF1;
  border-radius: 16px;
  border: 1px solid rgba(85, 201, 138, 0.15);
  transition: all 0.3s ease;
}

.login-tips:hover {
  background: #D8F7E8;
  border-color: rgba(85, 201, 138, 0.25);
  transform: translateY(-2px);
}

.tips-title {
  font-size: 13px;
  font-weight: 700;
  color: #2D9A68;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tips-title i {
  color: #55C98A;
  font-size: 14px;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tips-list p {
  color: #5A6B62;
  font-size: 12px;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 700;
}

.role-tag.admin {
  background: #6EC6FF;
  color: #fff;
}

.role-tag.teacher {
  background: #FFD66B;
  color: #5A4A2A;
}

.role-tag.student {
  background: #55C98A;
  color: #fff;
}

.login-footer {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 12px;
  color: #8B9A91;
  z-index: 1;
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
  .login-footer {
    position: relative;
    bottom: auto;
    margin-top: 20px;
  }
}
</style>
