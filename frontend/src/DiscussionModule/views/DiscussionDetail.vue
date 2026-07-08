<template>
  <div class="discussion-detail">
    <div class="page-header">
      <el-button icon="el-icon-back" size="medium" round @click="$router.back()" class="back-btn">返回</el-button>
      <h3>话题详情</h3>
    </div>

    <!-- 帖子内容 -->
    <div class="game-card post-card" v-loading="loading">
      <div class="post-header">
        <h2 class="post-title">{{ post.title }}</h2>
        <div class="post-meta">
          <span class="meta-author"><i class="el-icon-user"></i> {{ post.userName }}</span>
          <span v-if="post.userRole === 'TEACHER'" class="role-tag tag-teacher">教师</span>
          <span v-else-if="post.userRole === 'STUDENT'" class="role-tag tag-student">学生</span>
          <span class="meta-time">{{ post.createTime }}</span>
        </div>
      </div>
      <div class="post-content">{{ post.content }}</div>
      <div class="post-actions">
        <span class="action-item"><i class="el-icon-view"></i> {{ post.viewCount || 0 }} 浏览</span>
        <span class="action-item like-btn" @click="handleLike">
          <i :class="liked ? 'el-icon-star-on' : 'el-icon-star-off'" :style="{ color: liked ? '#FFD66B' : '' }"></i>
          {{ post.likeCount || 0 }} 点赞
        </span>
        <span class="action-item"><i class="el-icon-chat-dot-round"></i> {{ comments.length }} 评论</span>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="game-card comment-card">
      <div class="card-header-custom">
        <span class="header-icon-box"><i class="el-icon-chat-dot-round"></i></span>
        <span>评论区 ({{ comments.length }})</span>
      </div>

      <!-- 评论输入 -->
      <div class="comment-input-area">
        <el-input
          v-model="commentText"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="500"
          show-word-limit
          class="comment-textarea"
        />
        <el-button type="primary" size="medium" round @click="handleAddComment" :loading="submitting" class="submit-comment-btn">
          发表评论
        </el-button>
      </div>

      <!-- 评论列表 -->
      <div v-if="comments.length === 0" class="no-comments">
        <EmptyState text="暂无评论" subtext="快来发表第一条评论吧" />
      </div>
      <div v-else class="comment-list">
        <div v-for="c in comments" :key="c.id" class="comment-item">
          <div class="comment-avatar">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="comment-body">
            <div class="comment-meta">
              <span class="comment-author">{{ c.userName }}</span>
              <span v-if="c.userRole === 'TEACHER'" class="role-tag tag-teacher">教师</span>
              <span v-else-if="c.userRole === 'STUDENT'" class="role-tag tag-student">学生</span>
              <span class="comment-time">{{ c.createTime }}</span>
            </div>
            <div class="comment-content">{{ c.content }}</div>
          </div>
          <el-button
            v-if="canDeleteComment(c)"
            type="text"
            icon="el-icon-delete"
            class="comment-delete-btn"
            @click="handleDeleteComment(c)"
          ></el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getPostDetail, likePost, addComment, deleteComment } from '@/DiscussionModule/api'
import { mapGetters } from 'vuex'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'DiscussionDetail',
  components: { EmptyState },
  data() {
    return { loading: false, submitting: false, post: {}, comments: [], commentText: '', liked: false }
  },
  computed: { ...mapGetters(['userId']) },
  created() { this.fetchDetail() },
  methods: {
    canDeleteComment(comment) { return this.userId === this.post.userId || this.userId === comment.userId },
    async fetchDetail() { this.loading = true; try { const res = await getPostDetail(this.$route.params.id); this.post = res.data.post || {}; this.comments = res.data.comments || [] } catch (e) { console.error(e) } finally { this.loading = false } },
    async handleLike() { try { const res = await likePost(this.post.id); this.post.likeCount = res.data; this.liked = true; this.$message.success('点赞成功') } catch (e) { console.error(e) } },
    async handleAddComment() { const text = this.commentText.trim(); if (!text) { this.$message.warning('请输入评论内容'); return }; this.submitting = true; try { await addComment(this.post.id, text); this.$message.success('评论成功'); this.commentText = ''; this.fetchDetail() } catch (e) { console.error(e) } finally { this.submitting = false } },
    async handleDeleteComment(comment) { try { await this.$confirm('确认删除该评论？', '提示', { type: 'warning' }); await deleteComment(comment.id); this.$message.success('已删除'); this.fetchDetail() } catch (e) {} }
  }
}
</script>

<style scoped>
.discussion-detail { max-width: 900px; margin: 0 auto; }

.page-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.back-btn { border-radius: 20px; }
.page-header h3 { color: #2D3A2F; font-size: 22px; font-weight: 700; margin: 0; }

.post-card { padding: 24px 28px; margin-bottom: 20px; border: none !important; }

.post-header { margin-bottom: 20px; }
.post-title { font-size: 22px; font-weight: 700; color: #2D3A2F; margin: 0 0 12px 0; }
.post-meta { display: flex; align-items: center; gap: 10px; font-size: 13px; color: #8A9B8F; }
.meta-author { display: flex; align-items: center; gap: 4px; }

.role-tag { font-size: 10px; font-weight: 600; padding: 2px 8px; border-radius: 10px; }
.tag-teacher { background: #FFF8E8; color: #8A6D2B; }
.tag-student { background: #E8FFF1; color: #2E7A4A; }

.post-content { font-size: 15px; line-height: 1.8; color: #2D3A2F; white-space: pre-wrap; word-break: break-word; padding: 20px 0; border-top: 1px solid #EEF5F0; border-bottom: 1px solid #EEF5F0; margin-bottom: 16px; }

.post-actions { display: flex; gap: 24px; }
.action-item { display: flex; align-items: center; gap: 6px; font-size: 14px; color: #8A9B8F; }
.action-item i { font-size: 16px; }
.like-btn { cursor: pointer; transition: all 0.2s; }
.like-btn:hover { color: #FFD66B; transform: scale(1.05); }

/* Comments */
.comment-card { padding: 24px 28px; border: none !important; }
.card-header-custom { display: flex; align-items: center; gap: 10px; font-weight: 700; font-size: 16px; color: #2D3A2F; margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid #EEF5F0; }
.header-icon-box { width: 34px; height: 34px; border-radius: 10px; background: #E8FFF1; color: #55C98A; display: flex; align-items: center; justify-content: center; font-size: 16px; }

.comment-input-area { margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #EEF5F0; }
.comment-textarea >>> textarea { border-radius: 14px; background: #FAFCF8; border-color: #E0E8E0; }
.submit-comment-btn { margin-top: 12px; }

.no-comments { padding: 40px 0; }

.comment-list { display: flex; flex-direction: column; }
.comment-item { display: flex; gap: 14px; padding: 16px 0; border-bottom: 1px solid #EEF5F0; align-items: flex-start; }
.comment-item:last-child { border-bottom: none; }

.comment-avatar { width: 36px; height: 36px; border-radius: 50%; background: #E8FFF1; display: flex; align-items: center; justify-content: center; flex-shrink: 0; color: #55C98A; font-size: 16px; }

.comment-body { flex: 1; min-width: 0; }
.comment-meta { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.comment-author { font-size: 14px; font-weight: 600; color: #2D3A2F; }
.comment-time { font-size: 12px; color: #B0BDB3; }
.comment-content { font-size: 14px; line-height: 1.6; color: #2D3A2F; white-space: pre-wrap; word-break: break-word; }
.comment-delete-btn { color: #FF8A80 !important; padding: 4px; flex-shrink: 0; margin-top: 2px; }
</style>
