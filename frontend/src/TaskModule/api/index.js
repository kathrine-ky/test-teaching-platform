import request from '@/utils/request'

// ========== 实验任务 ==========
export const createTask = (data) => request.post('/task/create', data)
export const updateTask = (data) => request.put('/task/update', data)
export const deleteTask = (id) => request.delete(`/task/${id}`)
export const getTaskDetail = (id) => request.get(`/task/${id}`)
export const listTasks = (data) => request.post('/task/list', data)
export const publishTask = (id) => request.put(`/task/publish/${id}`)
