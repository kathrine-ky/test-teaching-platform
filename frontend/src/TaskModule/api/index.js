import request from '@/utils/request'

// ========== 实验任务 ==========
export const createTask = (data) => request.post('/task/create', data)
export const updateTask = (data) => request.put('/task/update', data)
export const deleteTask = (id) => request.delete(`/task/${id}`)
export const getTaskDetail = (id) => request.get(`/task/${id}`)
export const listTasks = (data) => request.post('/task/list', data)
export const publishTask = (id) => request.put(`/task/publish/${id}`)
export const withdrawTask = (id) => request.put(`/task/withdraw/${id}`)

// ========== 任务附件 ==========
export const uploadTaskFile = (taskId, formData) => request.post(`/task/${taskId}/file/upload`, formData)
export const getTaskFiles = (taskId) => request.get(`/task/${taskId}/files`)
export const deleteTaskFile = (fileId) => request.delete(`/task/file/${fileId}`)
export const downloadTaskFile = (fileId) => request.get(`/task/file/download/${fileId}`, { responseType: 'blob' })
