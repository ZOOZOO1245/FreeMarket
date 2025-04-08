<template>
  <div class="signup-container">
    <h1>회원가입 페이지</h1>
    <form v-if="!signupComplete" @submit.prevent="handleSignup">
      <div class="form-group">
        <label for="phone">핸드폰 번호</label>
        <input type="text" id="phone" v-model="phone" required />
      </div>
      <div class="form-group">
        <label for="username">아이디</label>
        <input type="text" id="username" v-model="username" required />
        <button type="button" @click="checkUsername">아이디 중복 확인</button>
        <span v-if="usernameExists" class="error">이미 사용 중인 아이디입니다.</span>
      </div>
      <div class="form-group">
        <label for="password">비밀번호</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <div class="form-group">
        <label for="confirmPassword">비밀번호 확인</label>
        <input type="password" id="confirmPassword" v-model="confirmPassword" required />
        <span v-if="passwordMismatch" class="error">비밀번호가 일치하지 않습니다.</span>
      </div>
      <button type="submit" class="btn btn-primary">회원가입</button>
    </form>
    <div v-else class="success-message">
      회원가입이 완료되었습니다. 다시 로그인해 주세요.
    </div>
  </div>
</template>

<script>
export default {
  name: 'SignupPage',
  data() {
    return {
      phone: '',
      username: '',
      password: '',
      confirmPassword: '',
      usernameExists: false,
      passwordMismatch: false,
      signupComplete: false, // 회원가입 완료 상태
    };
  },
  methods: {
    checkUsername() {
      // 아이디 중복 확인 로직 추가
      const existingUsernames = ['user1', 'user2']; // 예시 데이터
      this.usernameExists = existingUsernames.includes(this.username);
    },
    handleSignup() {
      this.passwordMismatch = this.password !== this.confirmPassword;
      if (this.passwordMismatch) {
        return; // 비밀번호가 일치하지 않으면 가입하지 않음
      }
      // 회원가입 처리 로직 추가
      console.log('핸드폰 번호:', this.phone);
      console.log('아이디:', this.username);
      console.log('비밀번호:', this.password);
      
      // 회원가입 완료 상태로 변경
      this.signupComplete = true;
    }
  }
};
</script>

<style scoped>
.signup-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #36a572;
}

.error {
  color: red;
  font-size: 0.9em;
}

.success-message {
  color: green;
  font-size: 1.2em;
  margin-top: 20px;
}
</style>