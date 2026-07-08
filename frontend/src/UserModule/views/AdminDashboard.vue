<template>
  <div class="admin-page">
    <!-- 欢迎横幅 -->
    <div class="admin-banner">
      <div class="banner-shapes">
        <span class="shape shape-1"></span>
        <span class="shape shape-2"></span>
      </div>
      <div class="banner-content">
        <div class="banner-text">
          <h3 class="admin-title">⚙️ 管理员控制台</h3>
          <p class="admin-desc">注册教师和学生账号，管理用户密码</p>
        </div>
        <div class="banner-icon-area">
          <div class="shield-icon">🛡️</div>
        </div>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 注册新用户 -->
      <el-col :span="14">
        <div class="game-card admin-card">
          <div class="card-header-custom">
            <span class="header-icon-box"><i class="el-icon-circle-plus-outline"></i></span>
            <span>注册新用户</span>
          </div>
          <el-form ref="regForm" :model="regForm" :rules="regRules" label-width="100px" class="admin-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="regForm.username" placeholder="请输入用户名" prefix-icon="el-icon-user" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="密码" prop="password">
                  <el-input v-model="regForm.password" type="password" placeholder="请输入密码（至少6位）" prefix-icon="el-icon-lock" show-password />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名" prop="realName">
                  <el-input v-model="regForm.realName" placeholder="请输入真实姓名" prefix-icon="el-icon-user-solid" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="角色" prop="role">
                  <el-select v-model="regForm.role" placeholder="请选择角色" style="width: 100%">
                    <el-option label="教师" value="TEACHER" />
                    <el-option label="学生" value="STUDENT" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="学号" prop="studentNo">
                  <el-input v-model="regForm.studentNo" placeholder="请输入学号" prefix-icon="el-icon-tickets" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别">
                  <el-select v-model="regForm.gender" placeholder="请选择性别" style="width: 100%">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱">
                  <el-input v-model="regForm.email" placeholder="请输入邮箱" prefix-icon="el-icon-message" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="班级">
                  <el-input v-model="regForm.className" placeholder="请输入班级（可选）" prefix-icon="el-icon-school" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-button type="primary" :loading="regLoading" @click="handleRegister" class="action-btn" round>
                注册用户
              </el-button>
              <el-button @click="handleResetReg" class="reset-btn" round>重置表单</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <!-- 重置密码 -->
      <el-col :span="10">
        <div class="game-card admin-card">
          <div class="card-header-custom">
            <span class="header-icon-box"><i class="el-icon-refresh-right"></i></span>
            <span>重置用户密码</span>
          </div>
          <el-form ref="resetForm" :model="resetForm" :rules="resetRules" label-width="80px" class="admin-form">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="resetForm.studentNo" placeholder="请输入学号" prefix-icon="el-icon-tickets" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="resetForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" prefix-icon="el-icon-key" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="resetForm.confirmPassword" type="password" placeholder="请再次输入新密码" prefix-icon="el-icon-key" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="warning" :loading="resetLoading" @click="handleResetPassword" class="action-btn" round>
                重置密码
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>

    <!-- 用户列表 -->
    <div class="game-card table-card">
      <div class="card-header-custom">
        <span class="header-icon-box"><i class="el-icon-s-custom"></i></span>
        <span>用户列表</span>
        <el-tag size="small" class="count-tag" type="info">共 {{ userList.length }} 人</el-tag>
      </div>
      <el-table :data="userList" v-loading="userLoading" stripe style="width: 100%">
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column label="角色" width="90">
          <template slot-scope="scope">
            <el-tag :type="scope.row.role === 'TEACHER' ? 'warning' : 'success'" size="small" effect="light">
              {{ scope.row.role === 'TEACHER' ? '教师' : '学生' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
      </el-table>
    </div>

    <!-- 操作说明 -->
    <div class="game-card tip-card">
      <div class="card-header-custom">
        <span class="header-icon-box"><i class="el-icon-info"></i></span>
        <span>操作说明</span>
      </div>
      <div class="tip-content">
        <p><i class="el-icon-success"></i> 管理员可以注册教师和学生账号，不可注册管理员账号</p>
        <p><i class="el-icon-success"></i> 每个教师和学生都必须有唯一的学号</p>
        <p><i class="el-icon-success"></i> 管理员可通过学号直接重置用户密码，无需旧密码</p>
        <p><i class="el-icon-success"></i> 用户名和学号不能与已有用户重复</p>
      </div>
    </div>
  </div>
</template>

<script>
import { adminRegister, adminResetPassword, getAdminUserList } from '@/UserModule/api'

export default {
  name: 'AdminDashboard',
  data() {
    const validateConfirm = (rule, value, callback) => { if (value !== this.resetForm.newPassword) { callback(new Error('两次输入密码不一致')) } else { callback() } }
    return {
      regForm: { username: '', password: '', realName: '', role: '', studentNo: '', gender: '', email: '', className: '' },
      regRules: { username: [{ required: true, message: '请输入用户名', trigger: 'blur' }], password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少需要6位', trigger: 'blur' }], realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }], role: [{ required: true, message: '请选择角色', trigger: 'change' }], studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }] },
      regLoading: false,
      resetForm: { studentNo: '', newPassword: '', confirmPassword: '' },
      resetRules: { studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }], newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少需要6位', trigger: 'blur' }], confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }] },
      resetLoading: false,
      userList: [],
      userLoading: false
    }
  },
  created() {
    this.loadUserList()
  },
  methods: {
    async loadUserList() {
      this.userLoading = true
      try {
        const res = await getAdminUserList()
        this.userList = res.data || []
      } catch (e) {
        console.error('加载用户列表失败:', e)
      } finally {
        this.userLoading = false
      }
    },
    async handleRegister() {
      const valid = await this.$refs.regForm.validate().catch(() => false)
      if (!valid) return
      this.regLoading = true
      try {
        await adminRegister(this.regForm)
        this.$message.success('用户注册成功')
        this.$refs.regForm.resetFields()
        this.loadUserList()
      } catch (e) {
        console.error('注册失败:', e)
      } finally {
        this.regLoading = false
      }
    },
    handleResetReg() { this.$refs.regForm.resetFields() },
    async handleResetPassword() {
      const valid = await this.$refs.resetForm.validate().catch(() => false)
      if (!valid) return
      this.resetLoading = true
      try {
        await adminResetPassword({ studentNo: this.resetForm.studentNo, newPassword: this.resetForm.newPassword })
        this.$message.success('密码重置成功')
        this.$refs.resetForm.resetFields()
      } catch (e) {
        console.error('重置密码失败:', e)
      } finally {
        this.resetLoading = false
      }
    }
  }
}
</script>

<style scoped>
.admin-page { max-width: 1100px; margin: 0 auto; }

/* Banner */
.admin-banner { background: linear-gradient(135deg, #E8F0FF 0%, #D4E2FC 50%, #C5D8F7 100%); border-radius: 24px; padding: 0; margin-bottom: 24px; position: relative; overflow: hidden; border: 1px solid rgba(110, 140, 200, 0.15); }
.banner-shapes { position: absolute; inset: 0; pointer-events: none; }
.shape { position: absolute; border-radius: 50%; opacity: 0.25; }
.shape-1 { width: 120px; height: 120px; top: -20px; right: 40px; background: rgba(110, 140, 200, 0.15); }
.shape-2 { width: 60px; height: 60px; bottom: -10px; right: 160px; background: rgba(110, 198, 255, 0.15); }
.banner-content { display: flex; align-items: center; justify-content: space-between; padding: 24px 32px; position: relative; z-index: 1; }
.admin-title { font-size: 22px; font-weight: 700; color: #3A5A7A; margin: 0 0 6px; }
.admin-desc { font-size: 14px; color: #5A7A9A; margin: 0; line-height: 1.6; }
.banner-icon-area { flex-shrink: 0; margin-left: 24px; }
.shield-icon { font-size: 52px; animation: float 3s ease-in-out infinite; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

/* Cards */
.admin-card { padding: 24px; margin-bottom: 20px; height: 100%; }
.table-card { padding: 24px; margin-bottom: 20px; }
.card-header-custom { display: flex; align-items: center; gap: 10px; font-weight: 700; font-size: 16px; color: #2D3A2F; margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid #EEF5F0; }
.header-icon-box { width: 34px; height: 34px; border-radius: 10px; background: #E8F0FF; color: #5C8CAD; display: flex; align-items: center; justify-content: center; font-size: 16px; }
.count-tag { margin-left: auto; }

.admin-form .el-form-item { margin-bottom: 18px; }
.action-btn { min-width: 130px; padding: 11px 24px; font-size: 14px; font-weight: 600; }
.reset-btn { min-width: 100px; padding: 11px 20px; border-radius: 20px; }

/* Tip card */
.tip-card { padding: 24px; margin-top: 0; }
.tip-content { padding: 4px 0; }
.tip-content p { font-size: 13px; color: #5A6B5F; line-height: 2.2; margin: 0; }
.tip-content i { color: #55C98A; margin-right: 6px; }
</style>
