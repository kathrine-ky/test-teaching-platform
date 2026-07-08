<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="12">
        <div class="game-card profile-card">
          <div class="card-header-custom">
            <span class="header-icon-box"><i class="el-icon-user"></i></span>
            <span>个人信息</span>
            <el-button v-if="!editing" type="text" class="edit-btn" @click="startEdit"><i class="el-icon-edit"></i> 编辑</el-button>
            <template v-else>
              <el-button type="text" class="save-btn" @click="saveProfile" :loading="saving">保存</el-button>
              <el-button type="text" class="cancel-btn" @click="cancelEdit">取消</el-button>
            </template>
          </div>
          <div class="profile-avatar-area">
            <div class="profile-avatar">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="profile-role-tag">
              <span v-if="userInfo.role === 'ADMIN'" class="role-badge role-admin">管理员</span>
              <span v-else-if="userInfo.role === 'TEACHER'" class="role-badge role-teacher">教师</span>
              <span v-else class="role-badge role-student">学生</span>
            </div>
          </div>
          <el-form :model="editForm" label-width="80px" class="profile-form">
            <el-form-item label="用户名">
              <template v-if="editing">
                <el-input v-model="editForm.username" placeholder="请输入用户名" size="small" />
              </template>
              <template v-else>
                <span class="profile-value">{{ userInfo.username }}</span>
              </template>
            </el-form-item>
            <el-form-item label="角色">
              <span class="profile-value">{{ roleText }}</span>
            </el-form-item>
            <el-form-item label="学号">
              <span class="profile-value">{{ userInfo.studentNo || '未设置' }}</span>
            </el-form-item>
            <el-form-item label="班级">
              <span class="profile-value">{{ userInfo.className || '未设置' }}</span>
            </el-form-item>
            <el-form-item label="姓名">
              <span class="profile-value">{{ userInfo.realName || '未设置' }}</span>
            </el-form-item>
            <el-form-item label="性别">
              <template v-if="editing">
                <el-radio-group v-model="editForm.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </template>
              <template v-else>
                <span class="profile-value">{{ userInfo.gender || '未设置' }}</span>
              </template>
            </el-form-item>
            <el-form-item label="邮箱">
              <template v-if="editing">
                <el-input v-model="editForm.email" placeholder="请输入邮箱" size="small" />
              </template>
              <template v-else>
                <span class="profile-value">{{ userInfo.email || '未设置' }}</span>
              </template>
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <!-- 修改密码卡片 -->
      <el-col :span="12">
        <div class="game-card profile-card">
          <div class="card-header-custom">
            <span class="header-icon-box"><i class="el-icon-lock"></i></span>
            <span>修改密码</span>
          </div>
          <el-form ref="pwdForm" :model="pwdForm" :rules="pwdRules" label-width="100px" class="password-form">
            <el-form-item v-if="!isAdmin" label="旧密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入旧密码" prefix-icon="el-icon-lock" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" prefix-icon="el-icon-key" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" prefix-icon="el-icon-key" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="pwdLoading" @click="handleChangePassword" class="submit-btn" round>
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { changePassword, updateProfile } from '@/UserModule/api'

export default {
  name: 'Profile',
  data() {
    const validateConfirm = (rule, value, callback) => { if (value !== this.pwdForm.newPassword) { callback(new Error('两次输入密码不一致')) } else { callback() } }
    return {
      editing: false, saving: false,
      editForm: { username: '', gender: '', email: '' },
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      pwdRules: { oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }], newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少需要6位', trigger: 'blur' }], confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }] },
      pwdLoading: false
    }
  },
  computed: {
    ...mapGetters(['isAdmin', 'isTeacher']),
    userInfo() { return this.$store.state.userInfo || {} },
    roleText() { const map = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }; return map[this.userInfo.role] || this.userInfo.role }
  },
  methods: {
    startEdit() { this.editForm.username = this.userInfo.username || ''; this.editForm.gender = this.userInfo.gender || ''; this.editForm.email = this.userInfo.email || ''; this.editing = true },
    cancelEdit() { this.editing = false },
    async saveProfile() { this.saving = true; try { const res = await updateProfile({ username: this.editForm.username, gender: this.editForm.gender, email: this.editForm.email }); if (res && res.data) { this.$store.commit('SET_USER_INFO', { ...this.userInfo, username: res.data.username, gender: res.data.gender, email: res.data.email }) }; this.$message.success('个人信息更新成功'); this.editing = false } catch (e) { console.error('更新个人信息失败:', e) } finally { this.saving = false } },
    handleChangePassword() { this.$refs.pwdForm.validate(async valid => { if (!valid) return; this.pwdLoading = true; try { const data = { newPassword: this.pwdForm.newPassword }; if (!this.isAdmin) { data.oldPassword = this.pwdForm.oldPassword }; await changePassword(data); this.$message.success('密码修改成功'); this.pwdForm = { oldPassword: '', newPassword: '', confirmPassword: '' }; this.$refs.pwdForm.resetFields() } catch (e) { console.error('修改密码失败:', e) } finally { this.pwdLoading = false } }) }
  }
}
</script>

<style scoped>
.profile-page { max-width: 1000px; margin: 0 auto; }

.profile-card { padding: 24px; height: 100%; }
.card-header-custom { display: flex; align-items: center; gap: 10px; font-weight: 700; font-size: 16px; color: #2D3A2F; margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid #EEF5F0; }
.header-icon-box { width: 34px; height: 34px; border-radius: 10px; background: #E8FFF1; color: #55C98A; display: flex; align-items: center; justify-content: center; font-size: 16px; }

.edit-btn { margin-left: auto; color: #55C98A; font-size: 14px; }
.save-btn { margin-left: auto; color: #55C98A; font-weight: 600; font-size: 14px; }
.cancel-btn { color: #8A9B8F; font-size: 14px; }

.profile-avatar-area { display: flex; flex-direction: column; align-items: center; padding: 12px 0 8px; }
.profile-avatar { width: 80px; height: 80px; border-radius: 50%; background: linear-gradient(135deg, #E8FFF1, #C8F0D8); display: flex; align-items: center; justify-content: center; font-size: 36px; color: #55C98A; margin-bottom: 10px; }
.profile-role-tag { margin-bottom: 4px; }

.role-badge { font-size: 11px; font-weight: 600; padding: 4px 14px; border-radius: 20px; }
.role-admin { background: #E8F0FF; color: #5C8CAD; }
.role-teacher { background: #FFF8E8; color: #8A6D2B; }
.role-student { background: #E8FFF1; color: #2E7A4A; }

.profile-form .el-form-item { margin-bottom: 14px; }
.profile-value { color: #2D3A2F; font-weight: 500; }

.password-form .el-form-item { margin-bottom: 20px; }
.submit-btn { width: 100%; padding: 12px 0; font-size: 15px; font-weight: 600; letter-spacing: 1px; }
</style>
