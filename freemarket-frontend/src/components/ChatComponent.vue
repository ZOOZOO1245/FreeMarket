<template>
  <div class="chat-container">
    <div class="chat-header">
      <h3>{{ chatTitle }}</h3>
      <button class="close-btn" @click="closeChat">X</button>
    </div>
    <div class="chat-messages" ref="messageContainer">
      <div v-for="(message, index) in messages" :key="index" 
           :class="['message', message.sender === 'me' ? 'message-sent' : 'message-received']">
        <div class="message-content">{{ message.text }}</div>
        <div class="message-time">{{ message.time }}</div>
      </div>
    </div>
    <div class="chat-input">
      <input 
        type="text" 
        v-model="newMessage" 
        @keyup.enter="sendMessage" 
        placeholder="메시지를 입력하세요..."
      />
      <button @click="sendMessage">전송</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatComponent',
  props: {
    chatTitle: {
      type: String,
      default: '채팅'
    },
    productId: {
      type: String,
      default: ''
    },
    sellerId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      messages: [],
      newMessage: '',
    }
  },
  methods: {
    sendMessage() {
      if (this.newMessage.trim() === '') return;
      
      const now = new Date();
      const timeString = `${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`;
      
      this.messages.push({
        text: this.newMessage,
        sender: 'me',
        time: timeString
      });
      
      this.newMessage = '';
      
      // 서버로 메시지 전송 로직 (실제 구현 시 추가)
      
      // 임시 응답 (실제로는 서버에서 받아야 함)
      setTimeout(() => {
        this.messages.push({
          text: '메시지를 받았습니다. 곧 답변 드리겠습니다.',
          sender: 'other',
          time: timeString
        });
        this.scrollToBottom();
      }, 1000);
    },
    scrollToBottom() {
      this.$nextTick(() => {
        if (this.$refs.messageContainer) {
          this.$refs.messageContainer.scrollTop = this.$refs.messageContainer.scrollHeight;
        }
      });
    },
    closeChat() {
      this.$emit('close');
    }
  },
  mounted() {
    // 채팅 기록 불러오기 (실제 구현 시 추가)
    // 예시 메시지
    const timeString = '12:30';
    this.messages = [
      { text: '안녕하세요! 상품에 관심이 있습니다.', sender: 'me', time: timeString },
      { text: '네, 안녕하세요! 어떤 점이 궁금하신가요?', sender: 'other', time: timeString }
    ];
    
    this.scrollToBottom();
  }
}
</script>

<style scoped>
.chat-container {
  width: 350px;
  height: 450px;
  border: 1px solid #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.chat-header {
  padding: 10px 15px;
  background-color: #4CAF50;
  color: white;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
}

.chat-messages {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background-color: #f9f9f9;
}

.message {
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 18px;
  margin-bottom: 5px;
  word-break: break-word;
}

.message-sent {
  align-self: flex-end;
  background-color: #4CAF50;
  color: white;
  border-bottom-right-radius: 5px;
}

.message-received {
  align-self: flex-start;
  background-color: #f1f1f1;
  color: #333;
  border-bottom-left-radius: 5px;
}

.message-time {
  font-size: 0.7rem;
  margin-top: 3px;
  opacity: 0.7;
  text-align: right;
}

.chat-input {
  display: flex;
  padding: 10px;
  border-top: 1px solid #eee;
  background-color: white;
}

.chat-input input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 20px;
  margin-right: 8px;
}

.chat-input button {
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 15px;
  cursor: pointer;
}

/* 다크 모드 스타일 */
.dark-mode .chat-container {
  background-color: #1e1e1e;
  border-color: #444;
}

.dark-mode .chat-header {
  background-color: #6abf69;
}

.dark-mode .chat-messages {
  background-color: #2a2a2a;
}

.dark-mode .message-received {
  background-color: #3a3a3a;
  color: #e0e0e0;
}

.dark-mode .chat-input {
  background-color: #1e1e1e;
  border-top-color: #444;
}

.dark-mode .chat-input input {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

.dark-mode .chat-input button {
  background-color: #6abf69;
}
</style>