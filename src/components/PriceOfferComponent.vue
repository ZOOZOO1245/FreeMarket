<template>
  <div class="offer-container">
    <div class="offer-header">
      <h3>가격 제안하기</h3>
      <button class="close-btn" @click="closeOffer">X</button>
    </div>
    <div class="offer-content">
      <div class="product-info">
        <div class="product-image" v-if="productImage">
          <img :src="productImage" alt="상품 이미지" />
        </div>
        <div class="product-details">
          <h4>{{ productTitle }}</h4>
          <p class="original-price">판매가: {{ formatPrice(originalPrice) }}원</p>
        </div>
      </div>
      
      <div class="offer-input-section">
        <label for="offer-price">제안 가격</label>
        <div class="price-input-wrapper">
          <input 
            type="number" 
            id="offer-price" 
            v-model="offerPrice" 
            placeholder="가격을 입력하세요" 
          />
          <span class="currency">원</span>
        </div>
        <p class="price-difference" v-if="offerPrice">
          판매가보다 {{ priceDifference }}원 {{ isPriceHigher ? '높음' : '낮음' }}
        </p>
      </div>
      
      <div class="offer-message">
        <label for="offer-message">메시지 (선택사항)</label>
        <textarea 
          id="offer-message" 
          v-model="offerMessage" 
          placeholder="판매자에게 전달할 메시지를 입력하세요"
          rows="3"
        ></textarea>
      </div>
    </div>
    <div class="offer-actions">
      <button class="cancel-btn" @click="closeOffer">취소</button>
      <button class="submit-btn" @click="submitOffer" :disabled="!isValidOffer">제안하기</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PriceOfferComponent',
  props: {
    productId: {
      type: String,
      required: true
    },
    productTitle: {
      type: String,
      default: '상품명'
    },
    productImage: {
      type: String,
      default: ''
    },
    originalPrice: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      offerPrice: '',
      offerMessage: ''
    }
  },
  computed: {
    priceDifference() {
      if (!this.offerPrice) return 0;
      return Math.abs(this.offerPrice - this.originalPrice);
    },
    isPriceHigher() {
      return this.offerPrice > this.originalPrice;
    },
    isValidOffer() {
      return this.offerPrice && this.offerPrice > 0;
    }
  },
  methods: {
    formatPrice(price) {
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },
    submitOffer() {
      if (!this.isValidOffer) return;
      
      const offerData = {
        productId: this.productId,
        price: this.offerPrice,
        message: this.offerMessage
      };
      
      // 여기에 서버로 제안 데이터 전송 로직 추가 (실제 구현 시)
      console.log('가격 제안 데이터:', offerData);
      
      // 성공 메시지 및 창 닫기
      alert('가격 제안이 전송되었습니다.');
      this.closeOffer();
    },
    closeOffer() {
      this.$emit('close');
    }
  }
}
</script>

<style scoped>
.offer-container {
  width: 400px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.offer-header {
  padding: 15px;
  background-color: #4CAF50;
  color: white;
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

.offer-content {
  padding: 20px;
}

.product-info {
  display: flex;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.product-image {
  width: 80px;
  height: 80px;
  margin-right: 15px;
  border-radius: 4px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  flex: 1;
}

.product-details h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.original-price {
  font-weight: bold;
  color: #333;
}

.offer-input-section {
  margin-bottom: 20px;
}

.offer-input-section label,
.offer-message label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #555;
}

.price-input-wrapper {
  position: relative;
}

.price-input-wrapper input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.currency {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
}

.price-difference {
  margin-top: 8px;
  font-size: 14px;
  color: #e74c3c;
}

.offer-message textarea {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: none;
}

.offer-actions {
  display: flex;
  padding: 15px;
  border-top: 1px solid #eee;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn, .submit-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.cancel-btn {
  background-color: #f1f1f1;
  color: #333;
}

.submit-btn {
  background-color: #4CAF50;
  color: white;
}

.submit-btn:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
}

/* 다크 모드 스타일 */
.dark-mode .offer-container {
  background-color: #1e1e1e;
}

.dark-mode .offer-header {
  background-color: #6abf69;
}

.dark-mode .product-info {
  border-bottom-color: #444;
}

.dark-mode .product-details h4,
.dark-mode .offer-input-section label,
.dark-mode .offer-message label {
  color: #e0e0e0;
}

.dark-mode .original-price {
  color: #b0b0b0;
}

.dark-mode .price-input-wrapper input,
.dark-mode .offer-message textarea {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

.dark-mode .currency {
  color: #b0b0b0;
}

.dark-mode .cancel-btn {
  background-color: #2a2a2a;
  color: #e0e0e0;
}

.dark-mode .submit-btn {
  background-color: #6abf69;
}

.dark-mode .offer-actions {
  border-top-color: #444;
}
</style>