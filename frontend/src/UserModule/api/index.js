import request from '@/utils/request'

// ========== 认证 ==========
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
export const getUserInfo = () => request.get('/auth/userInfo')
export const changePassword = (data) => request.post('/auth/changePassword', data)
export const updateProfile = (data) => request.put('/auth/updateProfile', data)

// ========== 管理员 ==========
export const adminRegister = (data) => request.post('/auth/admin/register', data)
export const adminResetPassword = (data) => request.post('/auth/admin/resetPassword', data)
export const getAdminDashboardStats = () => request.get('/auth/admin/dashboard-stats')
export const getAdminUserList = () => request.get('/auth/admin/users')
