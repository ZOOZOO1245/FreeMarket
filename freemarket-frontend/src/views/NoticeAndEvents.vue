<template>
  <div class="notice-events">
    <h2>공지사항 및 이벤트</h2>
    <ul>
      <li v-for="notice in notices" :key="notice.id">{{ notice.text }}</li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      notices: [
        { id: 1, text: '신학기 중고 거래 이벤트!' },
        { id: 2, text: '교내 행사 안내' }
      ]
    }
  }
}
</script>

<style scoped>
/* 스타일 추가 */
</style>