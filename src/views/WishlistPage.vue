<template>
    <div class="wishlist-page">
      <h1>내 찜 목록</h1>
      
      <div v-if="wishlistProducts.length === 0" class="empty-message">
        <p>찜한 상품이 없습니다.</p>
        <router-link to="/" class="browse-btn">상품 둘러보기</router-link>
      </div>
      
      <div v-else class="product-grid">
        <div v-for="product in wishlistProducts" :key="product.id" class="product-card">
          <img :src="product.image" :alt="product.name">
          <h3>{{ product.name }}</h3>
          <p>{{ product.price }}원</p>
          <WishlistButton :product-id="product.id" @click="refreshWishlist" />
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import WishlistButton from '@/components/WishlistButton.vue';
  
  export default {
    components: {
      WishlistButton
    },
    data() {
      return {
        wishlistProducts: []
      }
    },
    mounted() {
      this.loadWishlistProducts();
    },
    methods: {
      loadWishlistProducts() {
        // 로컬 스토리지에서 찜 목록 가져오기
        const wishlistIds = JSON.parse(localStorage.getItem('wishlist') || '[]');
        
        // 실제로는 API에서 상품 정보를 가져와야 합니다
        // 여기서는 예시 데이터를 사용합니다
        const allProducts = [
          { id: 1, name: '상품 1', price: 10000, image: '/img/product1.jpg' },
          { id: 2, name: '상품 2', price: 20000, image: '/img/product2.jpg' },
          { id: 3, name: '상품 3', price: 30000, image: '/img/product3.jpg' }
        ];
        
        // 찜한 상품만 필터링
        this.wishlistProducts = allProducts.filter(product => 
          wishlistIds.includes(product.id)
        );
      },
      refreshWishlist() {
        // 찜 목록 새로고침
        this.loadWishlistProducts();
      }
    }
  }
  </script>
  
  <style scoped>
  .wishlist-page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .empty-message {
    text-align: center;
    margin: 50px 0;
  }
  
  .browse-btn {
    display: inline-block;
    margin-top: 10px;
    padding: 10px 20px;
    background-color: #4CAF50;
    color: white;
    text-decoration: none;
    border-radius: 4px;
  }
  
  .product-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
  }
  
  .product-card {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    text-align: center;
  }
  
  .product-card img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    border-radius: 4px;
  }
  </style>