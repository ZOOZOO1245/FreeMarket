<template>
  <div class="home">
    <header class="header">
      <div class="logo-container">
        <img src="@/assets/homepage-image.jpg" alt="로고" class="logo">
      </div>
    </header>
    
    <nav class="main-nav">
      <router-link to="/">홈</router-link>
      <router-link to="/products">상품목록</router-link>
      <router-link to="/register">상품등록</router-link>
      <router-link to="/login" class="nav-link">로그인</router-link>
      <router-link to="/signup" class="nav-link">회원가입</router-link>
    </nav>
    
    <div class="main-content">
      <h2>인기 상품</h2>
      <div class="product-grid">
        <div v-for="product in products" :key="product.id" class="product-card">
          <img :src="product.image" :alt="product.title" class="product-image">
          <div class="product-info">
            <h3 class="product-title">{{ product.title }}</h3>
            <p class="price">{{ product.price }}원</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      products: [
        {
          id: 1,
          title: '아이폰 13',
          price: '800,000',
          image: 'https://source.unsplash.com/300x200/?iphone'
        },
        {
          id: 2,
          title: '맥북 프로',
          price: '1,500,000',
          image: 'https://source.unsplash.com/300x200/?macbook'
        },
        {
          id: 3,
          title: '에어팟 프로',
          price: '250,000',
          image: 'https://source.unsplash.com/300x200/?airpods'
        },
        {
          id: 4,
          title: '갤럭시 S21',
          price: '900,000',
          image: 'https://source.unsplash.com/300x200/?galaxy'
        },
        {
          id: 5,
          title: 'LG 그램',
          price: '1,200,000',
          image: 'https://source.unsplash.com/300x200/?laptop'
        },
        {
          id: 6,
          title: '아이패드 프로',
          price: '1,000,000',
          image: 'https://source.unsplash.com/300x200/?ipad'
        }
      ]
    }
  }
}
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
}

.header {
  width: 100%;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.logo-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  overflow: hidden; /* 추가: 이미지가 컨테이너를 벗어나지 않도록 */
}

.logo {
  width: 100%;
  height: 400px;
  object-fit: cover; /* contain에서 cover로 변경 */
  min-width: 600px; /* 추가: 최소 너비 설정 */
}

.main-nav {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 15px 0;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.main-nav a {
  text-decoration: none;
  color: #333;
  font-weight: bold;
  transition: color 0.3s;
}

.main-nav a:hover {
  color: #42b983;
}

.main-content {
  margin-top: 40px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.product-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-title {
  margin: 10px 0;
  font-size: 1.2em;
  color: #333;
}

.price {
  color: #42b983;
  font-weight: bold;
  font-size: 1.2em;
}
</style>