<template>
    <div class="product-card">
      <!-- 기존 상품 정보 -->
      <img :src="product.image" :alt="product.name">
      <h3>{{ product.name }}</h3>
      <p>{{ product.price }}원</p>
      
      <!-- 찜하기 버튼 추가 -->
      <WishlistButton :product-id="product.id" />
    </div>
  </template>
  
  <script>
  import WishlistButton from './WishlistButton.vue';
  
  export default {
    components: {
      WishlistButton
    },
    props: {
      product: {
        type: Object,
        required: true
      }
    }
  }
  </script>
  
  <style scoped>
  .product-card {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 20px;
    transition: transform 0.3s;
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
    font-size: 1.2rem;
  }
  
  .product-card p {
    font-weight: bold;
    color: #333;
  }
  </style>