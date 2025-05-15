<template>
    <button @click="toggleWishlist" class="wishlist-btn">
      <span v-if="isWishlisted">❤️</span>
      <span v-else>🤍</span>
    </button>
  </template>
  
  <script>
  export default {
    props: {
      productId: {
        type: Number,
        required: true
      }
    },
    data() {
      return {
        isWishlisted: false
      }
    },
    mounted() {
      // 로컬 스토리지에서 찜 목록 확인
      const wishlist = JSON.parse(localStorage.getItem('wishlist') || '[]');
      this.isWishlisted = wishlist.includes(this.productId);
    },
    methods: {
      toggleWishlist() {
        // 로컬 스토리지에서 찜 목록 가져오기
        let wishlist = JSON.parse(localStorage.getItem('wishlist') || '[]');
        
        if (this.isWishlisted) {
          // 찜 목록에서 제거
          wishlist = wishlist.filter(id => id !== this.productId);
        } else {
          // 찜 목록에 추가
          wishlist.push(this.productId);
        }
        
        // 로컬 스토리지에 저장
        localStorage.setItem('wishlist', JSON.stringify(wishlist));
        
        // 상태 업데이트
        this.isWishlisted = !this.isWishlisted;
      }
    }
  }
  </script>
  
  <style scoped>
  .wishlist-btn {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 1.5rem;
  }
  </style>