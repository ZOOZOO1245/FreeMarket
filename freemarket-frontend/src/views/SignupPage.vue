<template>
  <div class="signup-page" :class="{ 'dark-mode': isDarkMode }">
    <div class="signup-container">
      <div class="signup-header">
        <h1>회원가입</h1>
        <p>FreeMarket의 회원이 되어 다양한 혜택을 누려보세요</p>
      </div>
      
      <form v-if="!signupComplete" @submit.prevent="handleSignup" class="signup-form">
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
          <div class="input-action">
            <button type="button" class="btn-check" @click="checkUsername">
              중복 확인
            </button>
          </div>
          <span v-if="usernameChecked && !usernameExists" class="success-text">
            <i class="fas fa-check-circle"></i> 사용 가능한 아이디입니다.
          </span>
          <span v-if="usernameExists" class="error-text">
            <i class="fas fa-exclamation-circle"></i> 이미 사용 중인 아이디입니다.
          </span>
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
        
        <div class="form-group">
          <label for="confirmPassword">비밀번호 확인</label>
          <div class="input-with-icon">
            <i class="fas fa-lock"></i>
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="confirmPassword" 
              v-model="confirmPassword" 
              placeholder="비밀번호를 다시 입력하세요" 
              required 
            />
          </div>
          <span v-if="confirmPassword && !passwordMismatch" class="success-text">
            <i class="fas fa-check-circle"></i> 비밀번호가 일치합니다.
          </span>
          <span v-if="passwordMismatch" class="error-text">
            <i class="fas fa-exclamation-circle"></i> 비밀번호가 일치하지 않습니다.
          </span>
        </div>
        
        <div class="form-group">
          <label for="phone">핸드폰 번호</label>
          <div class="input-with-icon">
            <i class="fas fa-mobile-alt"></i>
            <input 
              type="tel" 
              id="phone" 
              v-model="phone" 
              placeholder="'-' 없이 입력하세요" 
              required 
              @input="formatPhoneNumber"
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="email">이메일 (선택)</label>
          <div class="input-with-icon">
            <i class="fas fa-envelope"></i>
            <input 
              type="email" 
              id="email" 
              v-model="email" 
              placeholder="이메일을 입력하세요" 
            />
          </div>
        </div>
        
        <div class="agreement-section">
          <div class="agreement-item">
            <input type="checkbox" id="agreeAll" v-model="agreeAll" @change="toggleAllAgreements" />
            <label for="agreeAll" class="checkbox-label">
              <strong>모든 약관에 동의합니다</strong>
            </label>
          </div>
          
          <div class="agreement-item">
            <input type="checkbox" id="agreeTerms" v-model="agreeTerms" required />
            <label for="agreeTerms" class="checkbox-label">
              <span class="required">[필수]</span> 이용약관 동의
            </label>
            <button type="button" class="btn-view" @click="showTermsModal = true">보기</button>
          </div>
          
          <div class="agreement-item">
            <input type="checkbox" id="agreePrivacy" v-model="agreePrivacy" required />
            <label for="agreePrivacy" class="checkbox-label">
              <span class="required">[필수]</span> 개인정보 수집 및 이용 동의
            </label>
            <button type="button" class="btn-view" @click="showPrivacyModal = true">보기</button>
          </div>
        </div>
        
        <button type="submit" class="btn btn-primary" :disabled="!canSubmit">회원가입</button>
        
        <div class="login-link">
          이미 계정이 있으신가요? <router-link to="/login">로그인</router-link>
        </div>
      </form>
      
      <div v-else class="success-container">
        <div class="success-icon">
          <i class="fas fa-check-circle"></i>
        </div>
        <h2>회원가입이 완료되었습니다!</h2>
        <p>FreeMarket의 회원이 되신 것을 환영합니다.</p>
        <router-link to="/login" class="btn btn-primary">로그인하기</router-link>
      </div>
      
      <button @click="toggleDarkMode" class="theme-toggle">
        <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
      </button>
    </div>
    
    <!-- 이용약관 모달 -->
    <div class="modal" v-if="showTermsModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>이용약관</h3>
          <button class="close-btn" @click="showTermsModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <h4>FreeMarket 이용약관</h4>
          <p>본 약관은 FreeMarket 서비스 이용에 관한 조건 및 절차, 회사와 회원 간의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정합니다.</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="agreeTerms = true; showTermsModal = false">동의하기</button>
        </div>
      </div>
    </div>
    
    <!-- 개인정보 수집 및 이용 동의 모달 -->
    <div class="modal" v-if="showPrivacyModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>개인정보 수집 및 이용 동의</h3>
          <button class="close-btn" @click="showPrivacyModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <h4>개인정보 수집 및 이용 안내</h4>
          <p>FreeMarket은 다음과 같이 개인정보를 수집 및 이용합니다.</p>
          <p>1. 수집항목: 아이디, 비밀번호, 핸드폰 번호, 이메일(선택)</p>
          <p>2. 수집 및 이용목적: 회원제 서비스 제공, 본인 식별, 부정이용 방지</p>
          <p>3. 보유 및 이용기간: 회원 탈퇴 시까지</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="agreePrivacy = true; showPrivacyModal = false">동의하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SignupPage',
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      phone: '',
      email: '',
      usernameExists: false,
      usernameChecked: false,
      passwordMismatch: false,
      signupComplete: false,
      showPassword: false,
      agreeAll: false,
      agreeTerms: false,
      agreePrivacy: false,
      showTermsModal: false,
      showPrivacyModal: false,
      isDarkMode: false
    };
  },
  computed: {
    canSubmit() {
      return (
        this.username && 
        this.password && 
        this.confirmPassword && 
        this.phone && 
        !this.passwordMismatch && 
        this.usernameChecked && 
        !this.usernameExists &&
        this.agreeTerms && 
        this.agreePrivacy
      );
    }
  },
  methods: {
    checkUsername() {
      // 아이디 중복 확인 로직 추가
      const existingUsernames = ['user1', 'user2']; // 예시 데이터
      this.usernameExists = existingUsernames.includes(this.username);
      this.usernameChecked = true;
    },
    formatPhoneNumber() {
      // 숫자만 입력되도록 처리
      this.phone = this.phone.replace(/[^0-9]/g, '');
      
      // 11자리로 제한
      if (this.phone.length > 11) {
        this.phone = this.phone.slice(0, 11);
      }
    },
    toggleAllAgreements() {
      this.agreeTerms = this.agreeAll;
      this.agreePrivacy = this.agreeAll;
    },
    updateAgreeAll() {
      this.agreeAll = this.agreeTerms && this.agreePrivacy;
    },
    handleSignup() {
      // 비밀번호 일치 확인
      this.passwordMismatch = this.password !== this.confirmPassword;
      if (this.passwordMismatch) {
        return;
      }
      
      // 회원가입 처리 로직 추가
      console.log('아이디:', this.username);
      console.log('비밀번호:', this.password);
      console.log('핸드폰 번호:', this.phone);
      console.log('이메일:', this.email);
      
      // 회원가입 완료 상태로 변경
      this.signupComplete = true;
    },
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode;
      localStorage.setItem('darkMode', this.isDarkMode);
    }
  },
  watch: {
    confirmPassword() {
      if (this.confirmPassword) {
        this.passwordMismatch = this.password !== this.confirmPassword;
      } else {
        this.passwordMismatch = false;
      }
    },
    agreeTerms() {
      this.updateAgreeAll();
    },
    agreePrivacy() {
      this.updateAgreeAll();
    }
  },
  mounted() {
    // 로컬 스토리지에서 다크모드 설정 불러오기
    const savedDarkMode = localStorage.getItem('darkMode');
    if (savedDarkMode !== null) {
      this.isDarkMode = savedDarkMode === 'true';
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap');

.signup-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8f9fa;
  font-family: 'Noto Sans KR', sans-serif;
  transition: all 0.3s ease;
  padding: 40px 0;
}

.signup-container {
  width: 100%;
  max-width: 500px;
  padding: 40px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  transition: all 0.3s ease;
}

.signup-header {
  text-align: center;
  margin-bottom: 30px;
}

.signup-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.signup-header p {
  color: #666;
  font-size: 16px;
}

.signup-form {
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

.input-action {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.btn-check {
  background-color: #f0f8f0;
  color: #4CAF50;
  border: 1px solid #4CAF50;
  border-radius: 6px;
  padding: 8px 15px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-check:hover {
  background-color: #4CAF50;
  color: white;
}

.success-text {
  display: block;
  color: #4CAF50;
  font-size: 14px;
  margin-top: 5px;
}

.error-text {
  display: block;
  color: #f44336;
  font-size: 14px;
  margin-top: 5px;
}

.agreement-section {
  margin: 25px 0;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.agreement-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  position: relative;
}

.agreement-item:last-child {
  margin-bottom: 0;
}

.checkbox-label {
  margin-left: 8px;
  cursor: pointer;
  font-size: 14px;
}

.required {
  color: #f44336;
  font-weight: 500;
}

.btn-view {
  position: absolute;
  right: 0;
  background: none;
  border: none;
  color: #4CAF50;
  font-size: 13px;
  cursor: pointer;
  padding: 5px;
}

.btn-view:hover {
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

.btn-primary:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.login-link a {
  color: #4CAF50;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.login-link a:hover {
  color: #45a049;
  text-decoration: underline;
}

.success-container {
  text-align: center;
  padding: 30px 0;
}

.success-icon {
  font-size: 80px;
  color: #4CAF50;
  margin-bottom: 20px;
}

.success-container h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 15px;
}

.success-container p {
  color: #666;
  margin-bottom: 30px;
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

/* 모달 스타일 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #777;
  cursor: pointer;
  transition: color 0.3s;
}

.close-btn:hover {
  color: #f44336;
}

.modal-body {
  padding: 20px;
  max-height: 50vh;
  overflow-y: auto;
}

.modal-body h4 {
  margin-top: 0;
  color: #4CAF50;
}

.modal-body p {
  margin-bottom: 15px;
  line-height: 1.6;
}

.modal-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  text-align: right;
}

/* 다크 모드 스타일 */
.dark-mode {
  background-color: #121212;
  color: #e0e0e0;
}

.dark-mode .signup-container {
  background-color: #1e1e1e;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.dark-mode .signup-header h1 {
  color: #e0e0e0;
}

.dark-mode .signup-header p {
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

.dark-mode .modal-content {
  background-color: #1e1e1e;
}

.dark-mode .modal-header h3 {
  color: #e0e0e0;
}

.dark-mode .modal-body h4 {
  color: #6abf69;
}

/* 반응형 스타일 */
@media (max-width: 576px) {
  .signup-container {
    padding: 30px 20px;
    margin: 0 15px;
  }
  
  .agreement-item {
    flex-wrap: wrap;
  }
  
  .btn-view {
    position: static;
    margin-left: auto;
  }
}
</style>