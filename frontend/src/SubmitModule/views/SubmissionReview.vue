<template>
  <div class="submission-review">
    <div class="page-header">
      <el-button icon="el-icon-back" size="medium" round @click="$router.back()" class="back-btn">返回</el-button>
      <h3>学生提交列表与批改</h3>
    </div>

    <div class="game-card content-card">
      <el-table :data="submissions" stripe v-loading="loading" class="data-table">
        <el-table-column prop="studentName" label="姓名" width="100" align="center" />
        <el-table-column prop="studentNo" label="学号" width="130" align="center" />
        <el-table-column prop="className" label="班级" width="140" align="center" />
        <el-table-column label="提交状态" width="100" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.status === 1" class="status-tag tag-pending">待批改</span>
            <span v-else-if="scope.row.status === 2" class="status-tag tag-reviewed">已批改</span>
            <span v-else class="status-tag tag-none">未提交</span>
          </template>
        </el-table-column>
        <el-table-column label="文本内容" min-width="120" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.testCase || scope.row.defectReport || scope.row.testSummary" class="code-link" @click="showTextDialog(scope.row)">查看文本内容</span>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>
        <el-table-column label="附件" width="120" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.fileUrl || scope.row.fileUrl2 || scope.row.fileUrl3" class="code-link" @click="showFileDialog(scope.row)">查看附件</span>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="80" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.score != null" class="score-num" :class="scope.row.score >= 60 ? 'score-pass' : 'score-fail'">
              {{ scope.row.score }}
            </span>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 1" size="small" type="primary" round @click="showReviewDialog(scope.row)">批改</el-button>
            <el-button v-if="scope.row.status === 2" size="small" type="warning" plain round @click="showReviewDialog(scope.row)">重新批改</el-button>
          </template>
        </el-table-column>
      </el-table>
      <EmptyState v-if="!loading && submissions.length === 0" text="暂无学生提交数据" subtext="等待学生提交作业" />
    </div>

    <!-- 批改弹窗 -->
    <el-dialog title="批改作业" :visible.sync="reviewVisible" width="700px" class="game-dialog">
      <el-form ref="reviewForm" :model="reviewForm" label-width="80px" class="review-form">
        <el-form-item label="学生">
          <span class="student-info">{{ currentSubmission?.studentName }}（{{ currentSubmission?.studentNo }}）</span>
        </el-form-item>

        <el-form-item label="测试用例" v-if="currentSubmission?.testCase">
          <div class="code-block">{{ currentSubmission.testCase }}</div>
        </el-form-item>

        <el-form-item label="缺陷报告" v-if="currentSubmission?.defectReport">
          <div class="code-block">{{ currentSubmission.defectReport }}</div>
        </el-form-item>

        <el-form-item label="测试总结" v-if="currentSubmission?.testSummary">
          <div class="code-block">{{ currentSubmission.testSummary }}</div>
        </el-form-item>

        <el-form-item label="代码文本" v-if="currentSubmission?.codeText && !currentSubmission?.testCase">
          <div class="code-block">{{ currentSubmission.codeText }}</div>
        </el-form-item>

        <el-form-item label="提交附件">
          <div v-if="!hasAnyFile" class="no-data">无附件</div>
          <div v-else class="file-list-inline">
            <el-button v-if="currentSubmission?.fileUrl" type="text" icon="el-icon-download" class="file-dl" @click="handleDownload(currentSubmission.fileUrl)">
              {{ currentSubmission.fileName || '附件1' }}
            </el-button>
            <el-button v-if="currentSubmission?.fileUrl2" type="text" icon="el-icon-download" class="file-dl" @click="handleDownload(currentSubmission.fileUrl2)">
              {{ currentSubmission.fileName2 || '附件2' }}
            </el-button>
            <el-button v-if="currentSubmission?.fileUrl3" type="text" icon="el-icon-download" class="file-dl" @click="handleDownload(currentSubmission.fileUrl3)">
              {{ currentSubmission.fileName3 || '附件3' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="评分" prop="score" :rules="[{ required: true, message: '请输入评分', trigger: 'blur' }]">
          <el-input-number v-model="reviewForm.score" :min="0" :max="100" :step="1" size="medium" />
          <span class="score-unit">分</span>
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="reviewForm.comment" type="textarea" :rows="3" placeholder="请输入评语（选填）" maxlength="300" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reviewVisible = false" size="medium" round>取消</el-button>
        <el-button type="primary" @click="handleReview" size="medium" round>提交批改</el-button>
      </div>
    </el-dialog>

    <!-- 文本内容查看弹窗 -->
    <el-dialog title="学生提交文本内容" :visible.sync="textVisible" width="750px" class="game-dialog">
      <div v-if="currentTextRow.testCase" class="text-section">
        <div class="text-label"><i class="el-icon-document"></i> 测试用例</div>
        <div class="code-viewer">{{ currentTextRow.testCase }}</div>
      </div>
      <div v-if="currentTextRow.defectReport" class="text-section">
        <div class="text-label"><i class="el-icon-warning-outline"></i> 缺陷报告</div>
        <div class="code-viewer">{{ currentTextRow.defectReport }}</div>
      </div>
      <div v-if="currentTextRow.testSummary" class="text-section">
        <div class="text-label"><i class="el-icon-data-analysis"></i> 测试总结</div>
        <div class="code-viewer">{{ currentTextRow.testSummary }}</div>
      </div>
      <div v-if="currentTextRow.codeText && !currentTextRow.testCase" class="text-section">
        <div class="text-label"><i class="el-icon-document"></i> 代码文本</div>
        <div class="code-viewer">{{ currentTextRow.codeText }}</div>
      </div>
      <div slot="footer">
        <el-button @click="textVisible = false" size="medium" round>关闭</el-button>
      </div>
    </el-dialog>

    <!-- 附件查看弹窗 -->
    <el-dialog title="学生提交附件" :visible.sync="fileVisible" width="500px" class="game-dialog">
      <div class="file-list-dialog">
        <div v-if="currentFileRow?.fileUrl" class="file-dialog-item" @click="handleDownload(currentFileRow.fileUrl)">
          <span class="file-icon-box"><i class="el-icon-document"></i></span>
          <span>{{ currentFileRow.fileName || '附件1' }}</span>
          <el-button type="text" icon="el-icon-download" class="file-dl">下载</el-button>
        </div>
        <div v-if="currentFileRow?.fileUrl2" class="file-dialog-item" @click="handleDownload(currentFileRow.fileUrl2)">
          <span class="file-icon-box"><i class="el-icon-document"></i></span>
          <span>{{ currentFileRow.fileName2 || '附件2' }}</span>
          <el-button type="text" icon="el-icon-download" class="file-dl">下载</el-button>
        </div>
        <div v-if="currentFileRow?.fileUrl3" class="file-dialog-item" @click="handleDownload(currentFileRow.fileUrl3)">
          <span class="file-icon-box"><i class="el-icon-document"></i></span>
          <span>{{ currentFileRow.fileName3 || '附件3' }}</span>
          <el-button type="text" icon="el-icon-download" class="file-dl">下载</el-button>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="fileVisible = false" size="medium" round>关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSubmissionsByTask, reviewSubmission, downloadFile } from '@/SubmitModule/api'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'SubmissionReview',
  components: { EmptyState },
  data() {
    return {
      loading: false, submissions: [],
      reviewVisible: false, textVisible: false, fileVisible: false,
      currentTextRow: {}, currentFileRow: {}, currentSubmission: null,
      reviewForm: { score: 0, comment: '' }
    }
  },
  computed: {
    hasAnyFile() { const s = this.currentSubmission; return s && (s.fileUrl || s.fileUrl2 || s.fileUrl3) }
  },
  created() { this.fetchSubmissions() },
  methods: {
    async fetchSubmissions() { this.loading = true; try { const res = await getSubmissionsByTask(this.$route.params.id); this.submissions = res.data || [] } finally { this.loading = false } },
    showReviewDialog(row) { this.currentSubmission = row; this.reviewForm = { score: row.score || 0, comment: row.comment || '' }; this.reviewVisible = true },
    showTextDialog(row) { this.currentTextRow = row; this.textVisible = true },
    showFileDialog(row) { this.currentFileRow = row; this.fileVisible = true },
    async handleDownload(fileUrl) { if (!fileUrl) { this.$message.warning('文件路径为空，无法下载'); return }; try { const res = await downloadFile(fileUrl); const disposition = res.headers['content-disposition']; let filename = 'download'; if (disposition) { const match = disposition.match(/filename\*=UTF-8''(.+)/); if (match) { filename = decodeURIComponent(match[1]) } }; const blob = new Blob([res.data]); const url = window.URL.createObjectURL(blob); const a = document.createElement('a'); a.href = url; a.download = filename; document.body.appendChild(a); a.click(); document.body.removeChild(a); window.URL.revokeObjectURL(url) } catch (e) { console.warn('API下载失败，尝试直接访问:', fileUrl); window.open(fileUrl, '_blank') } },
    async handleReview() { if (this.reviewForm.score == null) { this.$message.warning('请输入评分'); return }; try { await reviewSubmission({ submissionId: this.currentSubmission.id, score: this.reviewForm.score, comment: this.reviewForm.comment }); this.$message.success('批改成功'); this.reviewVisible = false; this.fetchSubmissions() } catch (e) { console.error(e) } }
  }
}
</script>

<style scoped>
.submission-review { max-width: 1200px; margin: 0 auto; }

.page-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.back-btn { border-radius: 20px; }
.page-header h3 { color: #2D3A2F; font-size: 22px; font-weight: 700; margin: 0; }

.content-card { padding: 20px 24px; border: none !important; }

.data-table { font-size: 14px; }
.data-table >>> th { background-color: #F5FBF7 !important; font-weight: 700; color: #2D3A2F; }

.status-tag { font-size: 11px; font-weight: 600; padding: 3px 12px; border-radius: 20px; }
.tag-pending { background: #FFF8E8; color: #8A6D2B; }
.tag-reviewed { background: #E8FFF1; color: #2E7A4A; }
.tag-none { background: #F5F5F5; color: #8A8A8A; }

.no-data { color: #B0BDB3; }
.code-link { color: #55C98A; cursor: pointer; font-size: 13px; transition: color 0.25s; }
.code-link:hover { color: #3DCE7B; text-decoration: underline; }

.score-num { font-weight: 700; }
.score-pass { color: #55C98A; }
.score-fail { color: #FF8A80; }

/* Dialog */
.game-dialog >>> .el-dialog__header { background: linear-gradient(135deg, #E8FFF1, #F5FBF7); border-bottom: 1px solid #D8EFE0; padding: 20px 24px; border-radius: 16px 16px 0 0; }
.game-dialog >>> .el-dialog__title { font-weight: 700; color: #2D3A2F; font-size: 16px; }
.game-dialog >>> .el-dialog__body { padding: 24px !important; }

.review-form .el-form-item { margin-bottom: 20px; }
.student-info { font-size: 15px; font-weight: 600; color: #2D3A2F; }

.code-block { background: #FAFCF8; border: 1px solid #E0EFE5; border-radius: 14px; padding: 14px 18px; font-family: 'Consolas', 'Courier New', monospace; font-size: 13px; line-height: 1.6; white-space: pre-wrap; word-break: break-all; max-height: 180px; overflow-y: auto; color: #2D3A2F; width: 100%; }

.score-unit { margin-left: 8px; color: #8A9B8F; font-size: 14px; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
.file-list-inline { display: flex; flex-wrap: wrap; gap: 8px; }
.file-dl { color: #55C98A !important; }

.code-viewer { background: #2D3A2F; color: #D4E8D4; border-radius: 14px; padding: 20px 24px; font-family: 'Consolas', 'Courier New', monospace; font-size: 13px; line-height: 1.7; white-space: pre-wrap; word-break: break-all; max-height: 300px; overflow-y: auto; }

.text-section { margin-bottom: 20px; }
.text-label { font-weight: 700; color: #2D3A2F; margin-bottom: 8px; font-size: 14px; display: flex; align-items: center; gap: 6px; }
.text-label i { color: #55C98A; }

.file-list-dialog { display: flex; flex-direction: column; gap: 10px; }
.file-dialog-item { display: flex; align-items: center; gap: 10px; padding: 14px 16px; background: #FAFCF8; border: 1px solid #E0EFE5; border-radius: 12px; cursor: pointer; transition: all 0.25s ease; }
.file-dialog-item:hover { background: #E8FFF1; border-color: rgba(85, 201, 138, 0.2); }
.file-icon-box { width: 38px; height: 38px; border-radius: 10px; background: #E8FFF1; color: #55C98A; display: flex; align-items: center; justify-content: center; font-size: 16px; flex-shrink: 0; }
.file-dialog-item span { flex: 1; color: #2D3A2F; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
