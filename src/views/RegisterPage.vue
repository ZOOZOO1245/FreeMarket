<template>
  <div class="register">
    <header class="header">
      <h1>중고마켓</h1>
      <nav>
        <router-link to="/">홈</router-link>
        <router-link to="/products">상품목록</router-link>
        <router-link to="/register">상품등록</router-link>
      </nav>
    </header>

    <div class="main-content">
      <h2>상품 등록</h2>
      <form @submit.prevent="submitProduct" class="register-form">
        <div class="form-group">
          <label for="title">상품명</label>
          <input type="text" id="title" v-model="product.title" required>
        </div>
        
        <div class="form-group">
          <label for="price">가격</label>
          <input type="number" id="price" v-model="product.price" required>
        </div>
        
        <div class="form-group">
          <label for="description">상품 설명</label>
          <textarea id="description" v-model="product.description" required></textarea>
        </div>
        
        <div class="form-group">
          <label for="image">이미지 URL</label>
          <input type="text" id="image" v-model="product.image" required>
        </div>
        
        <button type="submit" class="submit-btn">상품 등록</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      product: {
        title: '',
        price: '',
        description: '',
        image: ''
      }
    }
  },
  methods: {
    submitProduct() {
      // 여기에 실제 API 연동 코드를 추가할 수 있습니다
      alert('상품이 등록되었습니다!');
      this.$router.push('/products');
    }
  }
}
</script>

<style scoped>
.register {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

nav {
  display: flex;
  gap: 20px;
}

nav a {
  text-decoration: none;
  color: #333;
  font-weight: bold;
}

nav a:hover {
  color: #42b983;
}

.main-content {
  margin-top: 40px;
}

.register-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input, textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

textarea {
  height: 150px;
  resize: vertical;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover {
  background-color: #3aa876;
}
</style>