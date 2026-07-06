import request from '@/utils/request'

// ========== 成绩统计 ==========
export const getMyScores = () => request.get('/submission/myScores')
export const getScoreStatistics = (taskId) => request.get(`/submission/statistics/${taskId}`)
export const exportScores = (taskId) => request.get(`/submission/export/${taskId}`, { responseType: 'blob' })
