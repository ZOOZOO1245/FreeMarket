<template>
  <div class="chat">
    <h2>채팅</h2>
    <div class="messages">
      <div v-for="message in messages" :key="message.id">{{ message.text }}</div>
    </div>
    <input v-model="newMessage" @keyup.enter="sendMessage" placeholder="메시지를 입력하세요" />
  </div>
</template>

<script>
export default {
  data() {
    return {
      messages: [],
      newMessage: ''
    }
  },
  methods: {
    sendMessage() {
      this.messages.push({ id: Date.now(), text: this.newMessage });
      this.newMessage = '';
    }
  }
}
</script>

<style scoped>
/* 스타일 추가 */
</style>