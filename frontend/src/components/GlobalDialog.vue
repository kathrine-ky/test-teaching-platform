<template>
  <el-dialog
    :title="title"
    :visible.sync="dialogVisible"
    :width="width"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @closed="handleClosed"
    class="global-dialog"
  >
    <slot></slot>
    <div slot="footer" v-if="showFooter" class="dialog-footer">
      <el-button @click="handleCancel" size="medium">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm" size="medium">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'GlobalDialog',
  props: {
    title: { type: String, default: '提示' },
    visible: { type: Boolean, default: false },
    width: { type: String, default: '600px' },
    loading: { type: Boolean, default: false },
    showFooter: { type: Boolean, default: true }
  },
  data() {
    return {
      dialogVisible: this.visible
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    handleConfirm() {
      this.$emit('confirm')
    },
    handleCancel() {
      this.dialogVisible = false
      this.$emit('cancel')
    },
    handleClosed() {
      this.$emit('closed')
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
