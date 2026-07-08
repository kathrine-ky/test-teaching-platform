<template>
  <div class="course-manage">
    <div class="page-header">
      <div class="header-left">
        <span class="header-icon">📚</span>
        <h3>课程信息管理</h3>
      </div>
      <el-button type="primary" icon="el-icon-plus" size="medium" round @click="showCreateDialog">新增课程</el-button>
    </div>

    <div class="game-card content-card">
      <el-table :data="courseList" stripe v-loading="loading" class="data-table">
        <el-table-column prop="id" label="序号" width="70" align="center" />
        <el-table-column prop="courseName" label="课程名称" min-width="180" align="center" />
        <el-table-column prop="description" label="课程描述" min-width="280" show-overflow-tooltip align="center" />
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="400" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="small" type="success" plain round @click="showAssignDialog(scope.row)">分配学生</el-button>
            <el-button size="small" type="warning" plain round @click="showResourceDialog(scope.row)">资源管理</el-button>
            <el-button size="small" type="primary" plain round @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain round @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <EmptyState v-if="!loading && courseList.length === 0" text="暂无课程数据" subtext="点击「新增课程」按钮添加第一个课程" />
    </div>

    <!-- 新增/编辑课程弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="560px" @closed="resetForm" class="game-dialog">
      <el-form ref="courseForm" :model="form" :rules="formRules" label-width="100px" class="course-form">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="5" placeholder="请输入课程描述" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="medium" round>取消</el-button>
        <el-button type="primary" @click="handleSave" size="medium" round>保存</el-button>
      </div>
    </el-dialog>

    <!-- 分配学生弹窗 -->
    <el-dialog :title="'分配学生 - ' + assignCourseName" :visible.sync="assignDialogVisible" width="680px" class="game-dialog" @closed="onAssignDialogClosed">
      <el-tabs v-model="assignTab">
        <el-tab-pane label="选择学生" name="select">
          <div class="assign-section">
            <p class="assign-tip">请选择要分配到本课程的学生（可多选）</p>
            <el-table
              ref="studentTable"
              :data="availableStudents"
              stripe
              max-height="350"
              @selection-change="handleSelectionChange"
              class="data-table"
            >
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column prop="studentNo" label="学号" width="130" align="center" />
              <el-table-column prop="realName" label="姓名" width="100" align="center" />
              <el-table-column prop="className" label="班级" min-width="140" align="center" />
            </el-table>
            <EmptyState v-if="availableStudents.length === 0" text="所有学生均已分配" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="已分配学生" name="assigned">
          <div class="assign-section">
            <el-table :data="assignedStudents" stripe max-height="350" class="data-table">
              <el-table-column prop="studentNo" label="学号" width="130" align="center" />
              <el-table-column prop="realName" label="姓名" width="100" align="center" />
              <el-table-column prop="className" label="班级" min-width="140" align="center" />
              <el-table-column label="操作" width="100" align="center">
                <template slot-scope="scope">
                  <el-button size="mini" type="danger" plain round @click="handleRemoveStudent(scope.row.id)">移除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <EmptyState v-if="assignedStudents.length === 0" text="暂无已分配学生" />
          </div>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button @click="assignDialogVisible = false" size="medium" round>关闭</el-button>
        <el-button v-if="assignTab === 'select'" type="primary" @click="handleAssign" :loading="assignLoading" size="medium" round>确认分配</el-button>
      </div>
    </el-dialog>

    <!-- 课程资源管理弹窗 -->
    <el-dialog :title="'资源管理 - ' + resourceCourseName" :visible.sync="resourceDialogVisible" width="680px" class="game-dialog" @closed="onResourceDialogClosed">
      <div class="resource-section">
        <el-upload
          ref="courseUploadRef"
          :action="''"
          :auto-upload="false"
          :file-list="uploadFileList"
          :on-change="handleResourceFileChange"
          :limit="10"
          multiple
          accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.zip,.rar,.jpg,.png"
          class="course-upload"
        >
          <el-button size="small" type="primary" plain round icon="el-icon-upload2">选择文件</el-button>
          <div slot="tip" class="el-upload__tip">支持 PDF、Word、Excel、PPT、TXT、图片、压缩包等，单文件不超过50MB</div>
        </el-upload>
        <div style="margin-top: 12px; text-align: right;">
          <el-button type="primary" size="small" round @click="handleUploadCourseFiles" :loading="uploadLoading" :disabled="uploadFileList.length === 0">上传所选文件</el-button>
        </div>

        <el-divider content-position="left">已上传资源 ({{ courseFiles.length }})</el-divider>

        <el-table :data="courseFiles" stripe max-height="320" class="data-table" v-loading="filesLoading">
          <el-table-column label="文件名" min-width="280">
            <template slot-scope="scope">
              <i :class="fileIcon(scope.row.fileType)" style="margin-right: 6px; color: #55C98A;"></i>
              <span :title="scope.row.fileName" class="file-name-text">{{ scope.row.fileName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="大小" width="100" align="center">
            <template slot-scope="scope">{{ formatSize(scope.row.fileSize) }}</template>
          </el-table-column>
          <el-table-column prop="createTime" label="上传时间" width="170" align="center" />
          <el-table-column label="操作" width="160" align="center">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" plain round @click="handleDownloadCourseFile(scope.row)">下载</el-button>
              <el-button size="mini" type="danger" plain round @click="handleDeleteCourseFile(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <EmptyState v-if="!filesLoading && courseFiles.length === 0" text="暂无课程资源" subtext="请上传课件、参考资料等学习资源" />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="resourceDialogVisible = false" size="medium" round>关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listCourses, createCourse, updateCourse, deleteCourse,
  assignStudents, getCourseStudents, removeStudent, listAllStudents,
  uploadCourseFile, getCourseFiles, deleteCourseFile
} from '@/CourseModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'CourseManage',
  components: { EmptyState },
  data() {
    return {
      loading: false, courseList: [],
      dialogVisible: false, dialogTitle: '新增课程', editingId: null,
      form: { courseName: '', description: '' },
      formRules: { courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }] },
      assignDialogVisible: false, assignTab: 'select', assignCourseId: null, assignCourseName: '', assignLoading: false,
      allStudents: [], selectedStudents: [], assignedStudents: [],
      resourceDialogVisible: false, resourceCourseId: null, resourceCourseName: '',
      uploadFileList: [], uploadLoading: false, courseFiles: [], filesLoading: false
    }
  },
  computed: {
    availableStudents() {
      const assignedIds = this.assignedStudents.map(s => s.id)
      return this.allStudents.filter(s => !assignedIds.includes(s.id))
    }
  },
  created() { this.fetchCourses() },
  methods: {
    async fetchCourses() { this.loading = true; try { const res = await listCourses(); this.courseList = res.data || [] } finally { this.loading = false } },
    showCreateDialog() { this.dialogTitle = '新增课程'; this.editingId = null; this.form = { courseName: '', description: '' }; this.dialogVisible = true },
    showEditDialog(row) { this.dialogTitle = '编辑课程'; this.editingId = row.id; this.form = { courseName: row.courseName, description: row.description }; this.dialogVisible = true },
    resetForm() { this.$refs.courseForm?.resetFields() },
    async handleSave() { this.$refs.courseForm.validate(async valid => { if (!valid) return; try { if (this.editingId) { await updateCourse(this.editingId, this.form) } else { await createCourse(this.form) }; this.$message.success('保存成功'); this.dialogVisible = false; this.fetchCourses() } catch (e) { console.error(e) } }) },
    async handleDelete(id) { try { await this.$confirm('确认删除该课程？删除后关联的学生分配也将清除。', '提示', { type: 'warning' }); await deleteCourse(id); this.$message.success('删除成功'); this.fetchCourses() } catch (e) {} },
    async showAssignDialog(row) { this.assignCourseId = row.id; this.assignCourseName = row.courseName; this.assignTab = 'select'; this.selectedStudents = []; this.assignDialogVisible = true; try { const [sr, ar] = await Promise.all([listAllStudents(), getCourseStudents(row.id)]); this.allStudents = sr.data || []; this.assignedStudents = ar.data || [] } catch (e) { console.error(e) } },
    handleSelectionChange(sel) { this.selectedStudents = sel },
    onAssignDialogClosed() { this.selectedStudents = []; this.allStudents = []; this.assignedStudents = [] },
    async handleAssign() { if (this.selectedStudents.length === 0) { this.$message.warning('请至少选择一个学生'); return }; this.assignLoading = true; try { const ids = this.selectedStudents.map(s => s.id); await assignStudents(this.assignCourseId, ids); this.$message.success(`成功分配 ${ids.length} 名学生`); const [sr, ar] = await Promise.all([listAllStudents(), getCourseStudents(this.assignCourseId)]); this.allStudents = sr.data || []; this.assignedStudents = ar.data || []; this.selectedStudents = []; this.assignTab = 'assigned'; this.$refs.studentTable?.clearSelection() } catch (e) { console.error(e) } finally { this.assignLoading = false } },
    async handleRemoveStudent(sid) { try { await this.$confirm('确认移除该学生？', '提示', { type: 'warning' }); await removeStudent(this.assignCourseId, sid); this.$message.success('移除成功'); const [sr, ar] = await Promise.all([listAllStudents(), getCourseStudents(this.assignCourseId)]); this.allStudents = sr.data || []; this.assignedStudents = ar.data || []; this.selectedStudents = []; this.$refs.studentTable?.clearSelection() } catch (e) {} },
    fileIcon(type) { const map = { pdf: 'el-icon-document', doc: 'el-icon-document', docx: 'el-icon-document', xls: 'el-icon-s-data', xlsx: 'el-icon-s-data', ppt: 'el-icon-s-marketing', pptx: 'el-icon-s-marketing', zip: 'el-icon-folder', rar: 'el-icon-folder', jpg: 'el-icon-picture', png: 'el-icon-picture' }; return map[(type || '').toLowerCase()] || 'el-icon-document' },
    formatSize(bytes) { if (!bytes) return '0 B'; const u = ['B', 'KB', 'MB', 'GB']; let i = 0, s = bytes; while (s >= 1024 && i < u.length - 1) { s /= 1024; i++ } return s.toFixed(1) + ' ' + u[i] },
    async showResourceDialog(row) { this.resourceCourseId = row.id; this.resourceCourseName = row.courseName; this.uploadFileList = []; this.courseFiles = []; this.resourceDialogVisible = true; this.$nextTick(() => this.$refs.courseUploadRef?.clearFiles()); await this.fetchCourseFiles() },
    onResourceDialogClosed() { this.uploadFileList = []; this.courseFiles = [] },
    async fetchCourseFiles() { this.filesLoading = true; try { const res = await getCourseFiles(this.resourceCourseId); this.courseFiles = res.data || [] } catch (e) { console.error(e) } finally { this.filesLoading = false } },
    handleResourceFileChange(file, fl) { this.uploadFileList = fl },
    async handleUploadCourseFiles() { if (this.uploadFileList.length === 0) { this.$message.warning('请先选择文件'); return }; this.uploadLoading = true; let ok = 0; try { for (const f of this.uploadFileList) { const fd = new FormData(); fd.append('file', f.raw); try { await uploadCourseFile(this.resourceCourseId, fd); ok++ } catch (e) { console.error('上传失败:', f.name, e) } }; this.$message.success(`成功上传 ${ok} 个文件`); this.uploadFileList = []; this.$refs.courseUploadRef?.clearFiles(); await this.fetchCourseFiles() } catch (e) { console.error(e) } finally { this.uploadLoading = false } },
    handleDownloadCourseFile(file) { if (!file.fileUrl) { this.$message.warning('文件路径为空，无法下载'); return }; window.open(file.fileUrl, '_blank') },
    async handleDeleteCourseFile(fid) { try { await this.$confirm('确认删除该资源文件？', '提示', { type: 'warning' }); await deleteCourseFile(fid); this.$message.success('删除成功'); this.courseFiles = this.courseFiles.filter(f => f.id !== fid) } catch (e) {} }
  }
}
</script>

<style scoped>
.course-manage { max-width: 1200px; margin: 0 auto; }

.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.header-left { display: flex; align-items: center; gap: 10px; }
.header-icon { font-size: 28px; }
.header-left h3 { color: #2D3A2F; font-size: 22px; font-weight: 700; margin: 0; }

.content-card { padding: 20px 24px; border: none !important; }

.data-table { font-size: 14px; }
.data-table >>> th { background-color: #F5FBF7 !important; font-weight: 700; color: #2D3A2F; }

/* Dialog */
.game-dialog >>> .el-dialog__header { background: linear-gradient(135deg, #E8FFF1, #F5FBF7); border-bottom: 1px solid #D8EFE0; padding: 20px 24px; border-radius: 16px 16px 0 0; }
.game-dialog >>> .el-dialog__title { font-weight: 700; color: #2D3A2F; font-size: 16px; }
.game-dialog >>> .el-dialog__body { padding: 24px !important; }

.course-form .el-form-item { margin-bottom: 22px; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
.assign-section { min-height: 200px; }
.assign-tip { color: #5A6B5F; font-size: 13px; margin: 0 0 16px; }
.resource-section { min-height: 200px; }
.course-upload { width: 100%; }

.file-name-text { max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; display: inline-block; vertical-align: middle; color: #2D3A2F; }
</style>
