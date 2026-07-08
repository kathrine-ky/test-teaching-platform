<template>
  <div class="discussion-list">
    <div class="page-header">
      <div class="header-left">
        <span class="header-icon">💬</span>
        <h3>讨论区</h3>
      </div>
      <el-button type="primary" icon="el-icon-edit" size="medium" round @click="showCreateDialog">发布话题</el-button>
    </div>

    <div class="game-card content-card" v-loading="loading">
      <div v-if="postList.length === 0 && !loading" class="empty-hint">
        <EmptyState text="暂无讨论话题" subtext="点击「发布话题」发起第一个讨论" />
      </div>
      <div v-else class="post-list">
        <div v-for="post in postList" :key="post.id" class="post-item" @click="goDetail(post.id)">
          <div class="post-main">
            <div class="post-title">{{ post.title }}</div>
            <div class="post-meta">
              <span class="meta-author"><i class="el-icon-user"></i> {{ post.userName }}</span>
              <span v-if="post.userRole === 'TEACHER'" class="role-tag tag-teacher">教师</span>
              <span v-else-if="post.userRole === 'STUDENT'" class="role-tag tag-student">学生</span>
              <span class="meta-time">{{ post.createTime }}</span>
            </div>
          </div>
          <div class="post-stats">
            <span class="stat-item"><i class="el-icon-view"></i> {{ post.viewCount || 0 }}</span>
            <span class="stat-item"><i class="el-icon-star-off"></i> {{ post.likeCount || 0 }}</span>
            <span class="stat-item"><i class="el-icon-chat-dot-round"></i> {{ post.commentCount || 0 }}</span>
          </div>
          <el-button
            v-if="isOwner(post.userId)"
            type="text"
            icon="el-icon-delete"
            class="post-delete-btn"
            @click.stop="handleDeletePost(post)"
          ></el-button>
        </div>
      </div>
    </div>

    <!-- 发帖弹窗 -->
    <el-dialog title="发布话题" :visible.sync="dialogVisible" width="600px" class="game-dialog">
      <el-form ref="postForm" :model="postForm" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入话题标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="postForm.content" type="textarea" :rows="6" placeholder="请输入话题内容" maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" round>取消</el-button>
        <el-button type="primary" @click="handleCreatePost" :loading="saving" round>发布</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPosts, createPost, deletePost } from '@/DiscussionModule/api'
import { mapGetters } from 'vuex'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'DiscussionList',
  components: { EmptyState },
  data() {
    return {
      loading: false, saving: false, postList: [],
      dialogVisible: false,
      postForm: { title: '', content: '' },
      rules: { title: [{ required: true, message: '请输入标题', trigger: 'blur' }], content: [{ required: true, message: '请输入内容', trigger: 'blur' }] }
    }
  },
  computed: { ...mapGetters(['userId']) },
  created() { this.fetchPosts() },
  methods: {
    isOwner(postUserId) { return this.userId === postUserId },
    async fetchPosts() { this.loading = true; try { const res = await listPosts(); this.postList = res.data || [] } catch (e) { console.error(e) } finally { this.loading = false } },
    showCreateDialog() { this.postForm = { title: '', content: '' }; this.dialogVisible = true; this.$nextTick(() => this.$refs.postForm?.clearFields()) },
    async handleCreatePost() { try { await this.$refs.postForm.validate() } catch { return }; this.saving = true; try { await createPost(this.postForm); this.$message.success('发布成功'); this.dialogVisible = false; this.fetchPosts() } catch (e) { console.error(e) } finally { this.saving = false } },
    async handleDeletePost(post) { try { await this.$confirm('确认删除该帖子？帖子下的所有评论也会被删除。', '提示', { type: 'warning' }); await deletePost(post.id); this.$message.success('已删除'); this.fetchPosts() } catch (e) {} },
    goDetail(postId) { this.$router.push(`/discussion/${postId}`) }
  }
}
</script>

<style scoped>
.discussion-list { max-width: 1000px; margin: 0 auto; }

.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.header-left { display: flex; align-items: center; gap: 10px; }
.header-icon { font-size: 28px; }
.header-left h3 { color: #2D3A2F; font-size: 22px; font-weight: 700; margin: 0; }

.content-card { padding: 0; border: none !important; overflow: hidden; }
.empty-hint { padding: 60px 0; }

.post-list { display: flex; flex-direction: column; }

.post-item { display: flex; align-items: center; padding: 18px 24px; border-bottom: 1px solid #EEF5F0; cursor: pointer; transition: all 0.25s ease; gap: 16px; }
.post-item:last-child { border-bottom: none; }
.post-item:hover { background-color: #F5FBF7; transform: translateX(4px); }

.post-main { flex: 1; min-width: 0; }
.post-title { font-size: 16px; font-weight: 600; color: #2D3A2F; margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.post-meta { display: flex; align-items: center; gap: 10px; font-size: 13px; color: #8A9B8F; }
.meta-author { display: flex; align-items: center; gap: 4px; }

.role-tag { font-size: 10px; font-weight: 600; padding: 2px 8px; border-radius: 10px; }
.tag-teacher { background: #FFF8E8; color: #8A6D2B; }
.tag-student { background: #E8FFF1; color: #2E7A4A; }

.post-stats { display: flex; gap: 18px; flex-shrink: 0; }
.stat-item { display: flex; align-items: center; gap: 4px; font-size: 13px; color: #B0BDB3; }
.stat-item i { font-size: 15px; }
.post-delete-btn { color: #FF8A80 !important; padding: 4px 8px; flex-shrink: 0; }

/* Dialog */
.game-dialog >>> .el-dialog__header { background: linear-gradient(135deg, #E8FFF1, #F5FBF7); border-bottom: 1px solid #D8EFE0; padding: 20px 24px; border-radius: 16px 16px 0 0; }
.game-dialog >>> .el-dialog__title { font-weight: 700; color: #2D3A2F; font-size: 16px; }
.game-dialog >>> .el-dialog__body { padding: 24px !important; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
</style>
