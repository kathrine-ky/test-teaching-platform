import request from '@/utils/request'

// ========== 作业提交 ==========
export const submitHomework = (taskId, data) => {
  const form = new FormData()
  if (data.testCase) form.append('testCase', data.testCase)
  if (data.defectReport) form.append('defectReport', data.defectReport)
  if (data.testSummary) form.append('testSummary', data.testSummary)
  if (data.codeText) form.append('codeText', data.codeText)
  if (data.file) form.append('file', data.file)
  if (data.file2) form.append('file2', data.file2)
  if (data.file3) form.append('file3', data.file3)
  return request.post(`/submission/submit/${taskId}`, form)
}
export const getMySubmission = (taskId) => request.get(`/submission/my/${taskId}`)
export const getSubmissionsByTask = (taskId) => request.get(`/submission/list/${taskId}`)
export const reviewSubmission = (data) => request.post('/submission/review', data)
export const downloadFile = (fileUrl) => {
  return request.get('/submission/download', {
    params: { path: fileUrl },
    responseType: 'blob'
  })
}

/** 教师端：获取所有已发布任务的提交状态汇总 */
export const getTeacherSubmissionSummary = () => request.get('/submission/teacher/submission-summary')

