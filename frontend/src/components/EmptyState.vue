<template>
  <div class="empty-state">
    <div class="empty-icon-wrap">
      <span class="empty-emoji">📭</span>
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
  margin-bottom: 16px;
}

.empty-emoji {
  font-size: 52px;
  display: inline-block;
  animation: float 3s ease-in-out infinite;
}

.empty-text {
  font-size: 15px;
  color: #8A9B8F;
  margin: 0 0 6px;
  font-weight: 600;
}

.empty-subtext {
  font-size: 13px;
  color: #B0BDB3;
  margin: 0;
}

.empty-action {
  margin-top: 20px;
}
</style>
