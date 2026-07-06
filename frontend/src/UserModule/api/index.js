import request from '@/utils/request'

// ========== 认证 ==========
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
export const getUserInfo = () => request.get('/auth/userInfo')
