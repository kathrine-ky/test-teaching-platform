// ========== 模块化 API 统一入口 ==========
// 各模块 API 已拆分到对应模块文件夹：
//   @/UserModule/api      - 认证登录
//   @/CourseModule/api    - 课程管理
//   @/TaskModule/api      - 实验任务
//   @/SubmitModule/api    - 作业提交
//   @/ScoreModule/api     - 成绩统计
//
// 为保持向后兼容，统一重新导出所有接口：

export { login, register, getUserInfo } from '@/UserModule/api'
export { createCourse, updateCourse, deleteCourse, getCourseDetail, listCourses } from '@/CourseModule/api'
export { createTask, updateTask, deleteTask, getTaskDetail, listTasks, publishTask } from '@/TaskModule/api'
export { submitHomework, getMySubmission, getSubmissionsByTask, reviewSubmission, downloadFile } from '@/SubmitModule/api'
export { getMyScores, getScoreStatistics, exportScores } from '@/ScoreModule/api'

