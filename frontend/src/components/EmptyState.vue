<template>
  <div class="empty-state">
    <div class="empty-icon-wrap">
      <i class="el-icon-document" :style="{ fontSize: iconSize + 'px' }"></i>
    </div>
    <p class="empty-text">{{ text }}</p>
    <p class="empty-subtext" v-if="subtext">{{ subtext }}</p>
    <div class="empty-action" v-if="$slots.default">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    text: { type: String, default: '暂无数据' },
    subtext: { type: String, default: '' },
    iconSize: { type: Number, default: 64 }
  }
}
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon-wrap {
  margin-bottom: 20px;
  color: #dcdfe6;
}

.empty-text {
  font-size: 15px;
  color: #909399;
  margin: 0 0 8px;
  font-weight: 500;
}

.empty-subtext {
  font-size: 13px;
  color: #c0c4cc;
  margin: 0;
}

.empty-action {
  margin-top: 20px;
}
</style>
