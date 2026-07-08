import request from '@/utils/request'

// ========== 讨论区 ==========
export const listPosts = () => request.get('/discussion/posts')
export const getPostDetail = (postId) => request.get(`/discussion/post/${postId}`)
export const createPost = (data) => request.post('/discussion/post', null, { params: data })
export const deletePost = (postId) => request.delete(`/discussion/post/${postId}`)
export const likePost = (postId) => request.post(`/discussion/post/${postId}/like`)
export const addComment = (postId, content) => request.post(`/discussion/post/${postId}/comment`, null, { params: { content } })
export const deleteComment = (commentId) => request.delete(`/discussion/comment/${commentId}`)
