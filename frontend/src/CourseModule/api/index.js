import request from '@/utils/request'

// ========== 课程管理 ==========
export const createCourse = (data) => request.post('/course/create', data)
export const updateCourse = (id, data) => request.put(`/course/update/${id}`, data)
export const deleteCourse = (id) => request.delete(`/course/${id}`)
export const getCourseDetail = (id) => request.get(`/course/${id}`)
export const listCourses = () => request.get('/course/list')
