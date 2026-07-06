import request from '@/utils/request'

// ========== 作业提交 ==========
export const submitHomework = (taskId, data) => {
  const form = new FormData()
  if (data.codeText) form.append('codeText', data.codeText)
  if (data.file) form.append('file', data.file)
  return request.post(`/submission/submit/${taskId}`, form)
}
export const getMySubmission = (taskId) => request.get(`/submission/my/${taskId}`)
export const getSubmissionsByTask = (taskId) => request.get(`/submission/list/${taskId}`)
export const reviewSubmission = (data) => request.post('/submission/review', data)
export const downloadFile = (fileUrl) => {
  // fileUrl 如 /uploads/submissions/1/2/123.docx
  // 转换为后端 ./uploads/submissions/1/2/123.docx 可读取的路径
  // 后端 download 接口: GET /submission/download?path=/uploads/...
  return request.get('/submission/download', {
    params: { path: fileUrl },
    responseType: 'blob'
  })
}
