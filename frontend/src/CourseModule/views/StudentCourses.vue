<template>
  <div class="student-courses">
    <div class="page-header">
      <div class="header-left">
        <span class="header-icon">📚</span>
        <h3>我的课程</h3>
      </div>
      <div class="header-meta">
        <span class="meta-count">{{ courseList.length }} 门课程</span>
      </div>
    </div>

    <div class="course-grid" v-loading="loading">
      <div class="game-card course-card" v-for="course in courseList" :key="course.id">
        <div class="course-card-cover">
          <div class="cover-emoji">{{ courseEmoji(course.courseName) }}</div>
          <div class="cover-shapes">
            <span class="cover-shape cs-1"></span>
            <span class="cover-shape cs-2"></span>
          </div>
        </div>
        <div class="course-card-body" @click="showDetailDialog(course)">
          <h4 class="course-name">{{ course.courseName }}</h4>
          <p class="course-desc">{{ course.description || '暂无课程描述' }}</p>
          <div class="course-meta">
            <span class="meta-item">
              <i class="el-icon-date"></i>
              {{ course.createTime || '未知' }}
            </span>
          </div>
        </div>
        <div class="course-card-footer">
          <el-button size="small" type="primary" plain round @click.stop="showFilesDialog(course)">
            <i class="el-icon-folder-opened"></i> 查看资源
          </el-button>
          <el-button size="small" type="text" class="detail-btn" @click.stop="showDetailDialog(course)">
            课程简介 <i class="el-icon-arrow-right"></i>
          </el-button>
        </div>
      </div>
      <EmptyState v-if="!loading && courseList.length === 0" text="暂无已分配课程" subtext="请联系教师为您分配课程" />
    </div>

    <!-- 课程资源弹窗 -->
    <el-dialog :title="'课程资源 - ' + currentCourseName" :visible.sync="filesDialogVisible" width="720px" class="game-dialog" @closed="onFilesDialogClosed">
      <div class="files-section" v-loading="filesLoading">
        <div v-if="!filesLoading && courseFiles.length === 0" class="empty-files">
          <div class="empty-icon">📂</div>
          <p>暂无课程资源</p>
          <span>教师暂未上传学习资料</span>
        </div>
        <div v-else class="files-grid">
          <div class="file-card" v-for="f in courseFiles" :key="f.id" @click="handleDownload(f)">
            <div class="file-card-icon">
              <i :class="fileIcon(f.fileType)"></i>
            </div>
            <div class="file-card-info">
              <span class="file-card-name" :title="f.fileName">{{ f.fileName }}</span>
              <span class="file-card-meta">{{ formatSize(f.fileSize) }} · {{ f.createTime }}</span>
            </div>
            <el-button type="text" class="file-download-btn" icon="el-icon-download" @click.stop="handleDownload(f)"></el-button>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="filesDialogVisible = false" size="medium" round>关闭</el-button>
      </div>
    </el-dialog>

    <!-- 课程简介详情弹窗 -->
    <el-dialog :title="detailCourse.courseName" :visible.sync="detailDialogVisible" width="640px" class="game-dialog">
      <div class="detail-section">
        <div class="detail-label">
          <span class="detail-label-icon">📖</span> 课程简介
        </div>
        <div class="detail-desc">{{ detailCourse.description || '暂无课程描述' }}</div>
        <div class="detail-meta">
          <span class="meta-item"><i class="el-icon-date"></i> 创建时间：{{ detailCourse.createTime || '未知' }}</span>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false" size="medium" round>关闭</el-button>
        <el-button type="primary" size="medium" round @click="detailDialogVisible = false; showFilesDialog(detailCourse)">
          <i class="el-icon-folder-opened"></i> 查看课程资源
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStudentCourses, getCourseFiles } from '@/CourseModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'StudentCourses',
  components: { EmptyState },
  data() {
    return {
      loading: false,
      courseList: [],
      filesDialogVisible: false,
      currentCourseId: null,
      currentCourseName: '',
      courseFiles: [],
      filesLoading: false,
      detailDialogVisible: false,
      detailCourse: {}
    }
  },
  created() { this.fetchCourses() },
  methods: {
    courseEmoji(name) {
      const map = { '测试': '🧪', '自动化': '🤖', '性能': '⚡', '安全': '🔒', '接口': '🔌', '单元': '🧩', '集成': '🔗', '软件': '💻' }
      for (const [key, val] of Object.entries(map)) {
        if ((name || '').includes(key)) return val
      }
      return '📖'
    },
    fileIcon(type) {
      const map = { pdf: 'el-icon-document', doc: 'el-icon-document', docx: 'el-icon-document',
        xls: 'el-icon-s-data', xlsx: 'el-icon-s-data', ppt: 'el-icon-s-marketing', pptx: 'el-icon-s-marketing',
        zip: 'el-icon-folder', rar: 'el-icon-folder', jpg: 'el-icon-picture', png: 'el-icon-picture' }
      return map[(type || '').toLowerCase()] || 'el-icon-document'
    },
    formatSize(bytes) {
      if (!bytes) return '0 B'
      const units = ['B', 'KB', 'MB', 'GB']
      let i = 0; let size = bytes
      while (size >= 1024 && i < units.length - 1) { size /= 1024; i++ }
      return size.toFixed(1) + ' ' + units[i]
    },
    async fetchCourses() {
      this.loading = true
      try {
        const res = await listStudentCourses()
        this.courseList = res.data || []
      } finally { this.loading = false }
    },
    async showFilesDialog(row) {
      this.currentCourseId = row.id
      this.currentCourseName = row.courseName
      this.courseFiles = []
      this.filesDialogVisible = true
      await this.fetchCourseFiles()
    },
    showDetailDialog(course) {
      this.detailCourse = course
      this.detailDialogVisible = true
    },
    onFilesDialogClosed() {
      this.courseFiles = []
    },
    async fetchCourseFiles() {
      this.filesLoading = true
      try {
        const res = await getCourseFiles(this.currentCourseId)
        this.courseFiles = res.data || []
      } catch (e) { console.error(e) }
      finally { this.filesLoading = false }
    },
    handleDownload(file) {
      const baseUrl = process.env.VUE_APP_BASE_API || ''
      window.open(baseUrl + file.fileUrl)
    }
  }
}
</script>

<style scoped>
.student-courses {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  font-size: 28px;
}

.header-left h3 {
  color: #2D3A2F;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.header-meta .meta-count {
  font-size: 13px;
  color: #8A9B8F;
  background: #EEF5F0;
  padding: 5px 14px;
  border-radius: 20px;
  font-weight: 500;
}

/* ========== 课程卡片网格 ========== */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.course-card {
  overflow: hidden;
  padding: 0;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(85, 201, 138, 0.15) !important;
}

.course-card-cover {
  height: 100px;
  background: linear-gradient(135deg, #E8FFF1 0%, #C8F0D8 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.cover-emoji {
  font-size: 42px;
  position: relative;
  z-index: 1;
}

.cover-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.cover-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.25;
}

.cs-1 {
  width: 60px;
  height: 60px;
  top: -10px;
  right: -10px;
  background: rgba(85, 201, 138, 0.3);
}

.cs-2 {
  width: 40px;
  height: 40px;
  bottom: 10px;
  left: 20px;
  background: rgba(255, 214, 107, 0.3);
}

.course-card-body {
  padding: 18px 20px 12px;
  cursor: pointer;
}

.course-name {
  font-size: 16px;
  font-weight: 700;
  color: #2D3A2F;
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-desc {
  font-size: 13px;
  color: #8A9B8F;
  margin: 0 0 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 36px;
}

.course-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  font-size: 12px;
  color: #B0BDB3;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-card-footer {
  padding: 12px 20px 18px;
  border-top: 1px solid #EEF5F0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-btn {
  color: #8A9B8F !important;
  font-size: 13px;
  padding: 6px 8px;
}

.detail-btn:hover {
  color: #55C98A !important;
}

/* ========== 文件弹窗 ========== */
.game-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #E8FFF1, #F5FBF7);
  border-bottom: 1px solid #D8EFE0;
  padding: 20px 24px;
  border-radius: 16px 16px 0 0;
}

.game-dialog >>> .el-dialog__title {
  font-weight: 700;
  color: #2D3A2F;
  font-size: 16px;
}

.game-dialog >>> .el-dialog__body {
  padding: 24px !important;
}

.files-section {
  min-height: 160px;
}

.empty-files {
  text-align: center;
  padding: 48px 20px;
}

.empty-files .empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-files p {
  font-size: 15px;
  color: #6B7A6F;
  margin: 0 0 4px;
}

.empty-files span {
  font-size: 12px;
  color: #B0BDB3;
}

.files-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: #FAFCF8;
  border: 1px solid #E8EFE8;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.file-card:hover {
  background: #E8FFF1;
  border-color: rgba(85, 201, 138, 0.2);
  transform: translateX(4px);
}

.file-card-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: #E8FFF1;
  color: #55C98A;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.file-card-info {
  flex: 1;
  min-width: 0;
}

.file-card-name {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2D3A2F;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-card-meta {
  font-size: 12px;
  color: #B0BDB3;
  margin-top: 2px;
}

.file-download-btn {
  color: #55C98A !important;
  font-size: 18px;
  padding: 6px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* ========== 课程简介详情弹窗 ========== */
.detail-section {
  padding: 4px 0;
}

.detail-label {
  font-size: 15px;
  font-weight: 700;
  color: #2D3A2F;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-label-icon {
  font-size: 20px;
}

.detail-desc {
  font-size: 14px;
  color: #4A5B4F;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
  padding: 20px;
  background: #F5FBF7;
  border-radius: 14px;
  border: 1px solid #E0EFE5;
  margin-bottom: 20px;
  min-height: 80px;
}

.detail-meta {
  padding: 12px 0 0;
  border-top: 1px solid #EEF5F0;
}

.detail-meta .meta-item {
  font-size: 13px;
  color: #8A9B8F;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
