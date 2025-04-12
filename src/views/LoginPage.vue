<template>
  <div class="login-page" :class="{ 'dark-mode': isDarkMode }">
    <div class="login-container">
      <div class="login-header">
        <img src="@/assets/logo.png" alt="FreeMarket Logo" class="logo" />
        <h1>로그인</h1>
        <p>FreeMarket에 오신 것을 환영합니다</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">아이디</label>
          <div class="input-with-icon">
            <i class="fas fa-user"></i>
            <input 
              type="text" 
              id="username" 
              v-model="username" 
              placeholder="아이디를 입력하세요" 
              required 
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="password">비밀번호</label>
          <div class="input-with-icon">
            <i class="fas fa-lock"></i>
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="password" 
              v-model="password" 
              placeholder="비밀번호를 입력하세요" 
              required 
            />
            <button 
              type="button" 
              class="toggle-password" 
              @click="showPassword = !showPassword"
            >
              <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
            </button>
          </div>
        </div>
        
        <div class="form-options">
          <div class="remember-me">
            <input type="checkbox" id="remember" v-model="rememberMe" />
            <label for="remember">아이디 저장</label>
          </div>
          <router-link to="/forgot-password" class="forgot-password">비밀번호 찾기</router-link>
        </div>
        
        <button type="submit" class="btn btn-primary">로그인</button>
        
        <div class="login-divider">
          <span>또는</span>
        </div>
        
        <div class="social-login">
          <button type="button" class="btn-social btn-kakao">
            <i class="fas fa-comment"></i> 카카오 로그인
          </button>
          <button type="button" class="btn-social btn-naver">
            <i class="fas fa-n"></i> 네이버 로그인
          </button>
        </div>
        
        <div class="signup-link">
          계정이 없으신가요? <router-link to="/signup">회원가입</router-link>
        </div>
      </form>
      
      <button @click="toggleDarkMode" class="theme-toggle">
        <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginPage',
  data() {
    return {
      username: '',
      password: '',
      showPassword: false,
      rememberMe: false,
      isDarkMode: false
    };
  },
  methods: {
    handleLogin() {
      // 로그인 처리 로직 추가
      console.log('아이디:', this.username);
      console.log('비밀번호:', this.password);
      console.log('아이디 저장:', this.rememberMe);
      
      // 실제 로그인 로직을 여기에 추가하세요
      // 예: API 호출, 인증 처리 등
    },
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode;
      localStorage.setItem('darkMode', this.isDarkMode);
    }
  },
  mounted() {
    // 로컬 스토리지에서 다크모드 설정 불러오기
    const savedDarkMode = localStorage.getItem('darkMode');
    if (savedDarkMode !== null) {
      this.isDarkMode = savedDarkMode === 'true';
    }
    
    // 시스템 다크모드 감지
    const prefersDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
    if (savedDarkMode === null && prefersDarkMode) {
      this.isDarkMode = true;
    }
    
    // 저장된 아이디 불러오기
    const savedUsername = localStorage.getItem('rememberedUsername');
    if (savedUsername) {
      this.username = savedUsername;
      this.rememberMe = true;
    }
  },
  watch: {
    rememberMe(newVal) {
      if (newVal && this.username) {
        localStorage.setItem('rememberedUsername', this.username);
      } else if (!newVal) {
        localStorage.removeItem('rememberedUsername');
      }
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap');

.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8f9fa;
  font-family: 'Noto Sans KR', sans-serif;
  transition: all 0.3s ease;
}

.login-container {
  width: 100%;
  max-width: 450px;
  padding: 40px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  transition: all 0.3s ease;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  margin-bottom: 15px;
}

.login-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.login-header p {
  color: #666;
  font-size: 16px;
}

.login-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
  font-size: 15px;
}

.input-with-icon {
  position: relative;
}

.input-with-icon i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
  transition: color 0.3s;
}

input {
  width: 100%;
  padding: 14px 15px 14px 45px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s;
  background-color: #f9f9f9;
}

input:focus {
  border-color: #4CAF50;
  outline: none;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

input:focus + i {
  color: #4CAF50;
}

.toggle-password {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #aaa;
  cursor: pointer;
  transition: color 0.3s;
}

.toggle-password:hover {
  color: #4CAF50;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 14px;
}

.remember-me {
  display: flex;
  align-items: center;
}

.remember-me input {
  width: auto;
  margin-right: 8px;
}

.remember-me label {
  margin: 0;
  cursor: pointer;
}

.forgot-password {
  color: #4CAF50;
  text-decoration: none;
  transition: color 0.3s;
}

.forgot-password:hover {
  color: #45a049;
  text-decoration: underline;
}

.btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 16px;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 4px 10px rgba(76, 175, 80, 0.2);
}

.btn-primary:hover {
  background-color: #45a049;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(76, 175, 80, 0.3);
}

.login-divider {
  position: relative;
  text-align: center;
  margin: 25px 0;
}

.login-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background-color: #eee;
}

.login-divider span {
  position: relative;
  background-color: #fff;
  padding: 0 15px;
  color: #888;
  font-size: 14px;
}

.social-login {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.btn-social {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-social i {
  margin-right: 8px;
}

.btn-kakao {
  background-color: #FEE500;
  color: #000;
}

.btn-kakao:hover {
  background-color: #f2d900;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(254, 229, 0, 0.3);
}

.btn-naver {
  background-color: #03C75A;
  color: white;
}

.btn-naver:hover {
  background-color: #02b350;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(3, 199, 90, 0.3);
}

.signup-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.signup-link a {
  color: #4CAF50;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.signup-link a:hover {
  color: #45a049;
  text-decoration: underline;
}

.theme-toggle {
  position: absolute;
  top: 20px;
  right: 20px;
  background: none;
  border: none;
  color: #333;
  font-size: 18px;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.theme-toggle:hover {
  background-color: #f0f8f0;
  color: #4CAF50;
}

/* 다크 모드 스타일 */
.dark-mode {
  background-color: #121212;
  color: #e0e0e0;
}

.dark-mode .login-container {
  background-color: #1e1e1e;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.dark-mode .login-header h1 {
  color: #e0e0e0;
}

.dark-mode .login-header p {
  color: #b0b0b0;
}

.dark-mode label {
  color: #b0b0b0;
}

.dark-mode input {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

.dark-mode input:focus {
  border-color: #6abf69;
  box-shadow: 0 0 0 2px rgba(106, 191, 105, 0.2);
}

.dark-mode .input-with-icon i,
.dark-mode .toggle-password {
  color: #888;
}

.dark-mode .input-with-icon input:focus + i,
.dark-mode .toggle-password:hover {
  color: #6abf69;
}

.dark-mode .forgot-password {
  color: #6abf69;
}

.dark-mode .forgot-password:hover {
  color: #5ca95c;
}

.dark-mode .btn-primary {
  background-color: #6abf69;
}

.dark-mode .btn-primary:hover {
  background-color: #5ca95c;
}

.dark-mode .login-divider::before {
  background-color: #444;
}

.dark-mode .login-divider span {
  background-color: #1e1e1e;
  color: #b0b0b0;
}

.dark-mode .signup-link {
  color: #b0b0b0;
}

.dark-mode .signup-link a {
  color: #6abf69;
}

.dark-mode .signup-link a:hover {
  color: #5ca95c;
}

.dark-mode .theme-toggle {
  color: #e0e0e0;
}

.dark-mode .theme-toggle:hover {
  background-color: #2a2a2a;
  color: #6abf69;
}

/* 반응형 스타일 */
@media (max-width: 480px) {
  .login-container {
    padding: 30px 20px;
    margin: 0 15px;
  }
  
  .social-login {
    flex-direction: column;
  }
}
</style>