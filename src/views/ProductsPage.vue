<template>
  <div class="products">
    <header class="header">
      <h1>중고마켓</h1>
      <nav>
        <router-link to="/">홈</router-link>
        <router-link to="/products">상품목록</router-link>
        <router-link to="/register">상품등록</router-link>
      </nav>
    </header>

    <div class="main-content">
      <h2>전체 상품</h2>
      <div class="product-grid">
        <div v-for="product in products" :key="product.id" class="product-card">
          <img :src="product.image" :alt="product.title">
          <h3>{{ product.title }}</h3>
          <p class="price">{{ product.price }}원</p>
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
          image: 'https://via.placeholder.com/200'
        },
        {
          id: 2,
          title: '맥북 프로',
          price: '1,500,000',
          image: 'https://via.placeholder.com/200'
        },
        {
          id: 3,
          title: '에어팟 프로',
          price: '250,000',
          image: 'https://via.placeholder.com/200'
        },
        {
          id: 4,
          title: '갤럭시 S21',
          price: '900,000',
          image: 'https://via.placeholder.com/200'
        },
        {
          id: 5,
          title: '아이패드 프로',
          price: '1,200,000',
          image: 'https://via.placeholder.com/200'
        },
        {
          id: 6,
          title: '애플워치',
          price: '450,000',
          image: 'https://via.placeholder.com/200'
        }
      ]
    }
  }
}
</script>

<style scoped>
.products {
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

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.product-card h3 {
  margin: 10px 0;
  font-size: 1.1em;
}

.price {
  color: #42b983;
  font-weight: bold;
  font-size: 1.2em;
}
</style>