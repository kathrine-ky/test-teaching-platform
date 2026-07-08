import request from '@/utils/request'

// ========== 课程管理 ==========
export const createCourse = (data) => request.post('/course/create', data)
export const updateCourse = (id, data) => request.put(`/course/update/${id}`, data)
export const deleteCourse = (id) => request.delete(`/course/${id}`)
export const getCourseDetail = (id) => request.get(`/course/${id}`)
export const listCourses = () => request.get('/course/list')
export const listAllCourses = () => request.get('/course/listAll')

// ========== 课程资源管理 ==========
export const uploadCourseFile = (courseId, formData) => request.post(`/course/${courseId}/file/upload`, formData)
export const getCourseFiles = (courseId) => request.get(`/course/${courseId}/files`)
export const deleteCourseFile = (fileId) => request.delete(`/course/file/${fileId}`)

// ========== 教师分配学生 ==========
export const assignStudents = (courseId, studentIds) => request.post(`/course/${courseId}/assignStudents`, studentIds)
export const getCourseStudents = (courseId) => request.get(`/course/${courseId}/students`)
export const removeStudent = (courseId, studentId) => request.delete(`/course/${courseId}/student/${studentId}`)
export const listAllStudents = () => request.get('/course/students/all')

// ========== 学生端 ==========
export const listStudentCourses = () => request.get('/course/student/list')
