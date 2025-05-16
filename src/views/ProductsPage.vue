<template>
  <div class="products-page" :class="{ 'dark-mode': isDarkMode }">
    <div class="container">
      <div class="header">
        <h1>상품 목록</h1>
        <div class="dark-mode-toggle">
          <button @click="toggleDarkMode" class="dark-mode-btn">
            <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
            {{ isDarkMode ? '라이트 모드' : '다크 모드' }}
          </button>
        </div>
      </div>
      
      <div class="filters">
        <div class="search-bar">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="상품명 검색..." 
            @input="filterProducts"
          />
          <button class="search-btn">
            <i class="fas fa-search"></i>
          </button>
        </div>
        
        <div class="category-filter">
          <select v-model="selectedCategory" @change="filterProducts">
            <option value="">모든 카테고리</option>
            <option v-for="category in categories" :key="category.value" :value="category.value">
              {{ category.name }}
            </option>
          </select>
        </div>
        
        <div class="sort-options">
          <select v-model="sortOption" @change="filterProducts">
            <option value="latest">최신순</option>
            <option value="priceLow">가격 낮은순</option>
            <option value="priceHigh">가격 높은순</option>
          </select>
        </div>
      </div>
      
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>상품을 불러오는 중...</p>
      </div>
      
      <div v-else-if="filteredProducts.length === 0" class="no-products">
        <i class="fas fa-search"></i>
        <p>검색 결과가 없습니다.</p>
      </div>
      
      <div v-else class="products-grid">
        <div 
          v-for="product in filteredProducts" 
          :key="product.id" 
          class="product-card"
          @click="viewProductDetail(product.id)"
        >
          <div class="product-image">
            <img 
              :src="product.images && product.images.length > 0 ? product.images[0] : 'https://via.placeholder.com/300'" 
              :alt="product.title"
            >
            <div class="product-badge" v-if="product.negotiable">가격제안가능</div>
          </div>
          <div class="product-info">
            <h3 class="product-title">{{ product.title }}</h3>
            <p class="product-price">{{ formatPrice(product.price) }}원</p>
            <div class="product-meta">
              <span class="product-location">
                <i class="fas fa-map-marker-alt"></i> {{ product.location || '지역정보 없음' }}
              </span>
              <span class="product-time">
                <i class="far fa-clock"></i> {{ formatTime(product.createdAt) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 상품 상세 모달 추가 -->
    <div v-if="selectedProduct" class="product-detail-modal">
      <div class="modal-overlay" @click="closeProductDetail"></div>
      <div class="modal-content">
        <div class="modal-header">
          <h2>상품 상세</h2>
          <button class="close-btn" @click="closeProductDetail">X</button>
        </div>
        <div class="modal-body">
          <div class="product-detail-container">
            <div class="product-image-section">
              <img 
                :src="selectedProduct.images && selectedProduct.images.length > 0 ? selectedProduct.images[0] : 'https://via.placeholder.com/400'" 
                :alt="selectedProduct.title"
                class="product-main-image"
              >
            </div>
            <div class="product-details-section">
              <h1 class="product-title">{{ selectedProduct.title }}</h1>
              <p class="product-price">{{ formatPrice(selectedProduct.price) }}원</p>
              
              <div class="product-meta">
                <div class="meta-item">
                  <i class="fas fa-map-marker-alt"></i>
                  <span>{{ selectedProduct.location || '지역정보 없음' }}</span>
                </div>
                <div class="meta-item">
                  <i class="far fa-clock"></i>
                  <span>{{ formatTime(selectedProduct.createdAt) }}</span>
                </div>
                <div class="meta-item">
                  <i class="fas fa-tag"></i>
                  <span>{{ getCategoryName(selectedProduct.category) }}</span>
                </div>
              </div>
              
              <div class="product-actions">
                <button class="action-btn chat-btn" @click="openChat">
                  <i class="fas fa-comment"></i> 채팅하기
                </button>
                <button class="action-btn offer-btn" @click="openPriceOffer" v-if="selectedProduct.negotiable">
                  <i class="fas fa-hand-holding-usd"></i> 가격제안
                </button>
              </div>
              
              <div class="product-description">
                <h3>상품 설명</h3>
                <p>{{ selectedProduct.description || '상품 설명이 없습니다.' }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 채팅 모달 -->
    <div v-if="showChat" class="chat-modal">
      <div class="modal-overlay" @click="closeChat"></div>
      <div class="modal-content chat-modal-content">
        <ChatComponent 
          :chatTitle="`${selectedProduct.title} 채팅`"
          :productId="selectedProduct.id.toString()"
          @close="closeChat"
        />
      </div>
    </div>
    
    <!-- 가격제안 모달 -->
    <div v-if="showPriceOffer" class="price-offer-modal">
      <div class="modal-overlay" @click="closePriceOffer"></div>
      <div class="modal-content offer-modal-content">
        <PriceOfferComponent 
          :productId="selectedProduct.id.toString()"
          :productTitle="selectedProduct.title"
          :originalPrice="selectedProduct.price"
          :productImage="selectedProduct.images && selectedProduct.images.length > 0 ? selectedProduct.images[0] : ''"
          @close="closePriceOffer"
        />
      </div>
    </div>
  </div>
</template>

<script>
import ChatComponent from '../components/ChatComponent.vue';
import PriceOfferComponent from '../components/PriceOfferComponent.vue';

export default {
  components: {
    ChatComponent,
    PriceOfferComponent
  },
  data() {
    return {
      loading: true,
      products: [],
      searchQuery: '',
      selectedCategory: '',
      sortOption: 'latest',
      isDarkMode: false,
      categories: [
        { value: 'electronics', name: '전자기기' },
        { value: 'furniture', name: '가구/인테리어' },
        { value: 'clothes', name: '의류' },
        { value: 'books', name: '도서/티켓/음반' },
        { value: 'beauty', name: '뷰티/미용' },
        { value: 'sports', name: '스포츠/레저' },
        { value: 'kids', name: '유아동/출산' },
        { value: 'pets', name: '반려동물용품' },
        { value: 'food', name: '식품' },
        { value: 'plants', name: '식물' },
        { value: 'etc', name: '기타' }
      ],
      filteredProducts: [],
      selectedProduct: null,
      showChat: false,
      showPriceOffer: false
    };
  },
  created() {
    // 로컬 스토리지에서 다크모드 설정 불러오기
    const savedDarkMode = localStorage.getItem('darkMode');
    if (savedDarkMode !== null) {
      this.isDarkMode = savedDarkMode === 'true';
    }
    
    // 상품 데이터 로드
    this.loadProducts();
  },
  methods: {
    async loadProducts() {
      this.loading = true;
      try {
        // 실제 API 호출
        const response = await this.$axios.get('/api/products', {
          params: {
            keyword: this.searchQuery || undefined,
            category: this.selectedCategory || undefined,
            sort: this.sortOption || 'latest'
          }
        });
        
        // 백엔드 응답 구조에 맞게 데이터 추출
        if (response.data && response.data.data) {
          this.products = response.data.data.content.map(product => ({
            ...product,
            negotiable: product.negotiable || false
          }));
          
          this.filteredProducts = [...this.products];
        } else {
          throw new Error('API 응답 구조가 올바르지 않습니다.');
        }
      } catch (error) {
        console.error('상품을 불러오는 중 오류가 발생했습니다:', error);
        // 에러 처리 (사용자에게 알림 표시 등)
        alert('상품을 불러오는 중 오류가 발생했습니다.');
        // 임시 데이터 사용
        this.products = [
          {
            id: 1,
            title: '아이폰 13 Pro 256GB',
            price: 950000,
            description: '구매 후 1년 사용했습니다. 상태 좋습니다. 기스 없음.',
            images: ['https://via.placeholder.com/400?text=iPhone+13+Pro'],
            category: 'electronics',
            location: '서울시 강남구',
            createdAt: new Date('2023-05-15T09:30:00'),
            negotiable: true
          },
          {
            id: 2,
            title: '삼성 갤럭시 S22 울트라',
            price: 850000,
            description: '풀박스 있습니다. 화면 기스 약간 있음.',
            images: ['https://via.placeholder.com/400?text=Galaxy+S22+Ultra'],
            category: 'electronics',
            location: '서울시 송파구',
            createdAt: new Date('2023-05-14T15:20:00'),
            negotiable: false
          },
          {
            id: 3,
            title: '애플 에어팟 프로 2세대',
            price: 200000,
            description: '한 달 사용했습니다. 상태 매우 좋음.',
            images: ['https://via.placeholder.com/400?text=AirPods+Pro'],
            category: 'electronics',
            location: '경기도 성남시',
            createdAt: new Date('2023-05-13T11:45:00'),
            negotiable: true
          },
          {
            id: 4,
            title: '이케아 책상',
            price: 80000,
            description: '2년 사용했습니다. 상태 양호합니다.',
            images: ['https://via.placeholder.com/400?text=IKEA+Desk'],
            category: 'furniture',
            location: '서울시 마포구',
            createdAt: new Date('2023-05-12T14:10:00'),
            negotiable: true
          },
          {
            id: 5,
            title: '나이키 에어맥스 270',
            price: 90000,
            description: '몇 번 신지 않았습니다. 265mm 사이즈.',
            images: ['https://via.placeholder.com/400?text=Nike+Air+Max'],
            category: 'clothes',
            location: '서울시 중구',
            createdAt: new Date('2023-05-11T17:30:00'),
            negotiable: false
          },
          {
            id: 6,
            title: '해리포터 시리즈 전권',
            price: 120000,
            description: '상태 좋습니다. 전권 세트입니다.',
            images: ['https://via.placeholder.com/400?text=Harry+Potter+Books'],
            category: 'books',
            location: '경기도 고양시',
            createdAt: new Date('2023-05-10T09:15:00'),
            negotiable: true
          }
        ];
        
        this.filteredProducts = [...this.products];
      } finally {
        this.loading = false;
      }
    },
    filterProducts() {
      let result = [...this.products];
      
      // 검색어 필터링
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        result = result.filter(product => 
          product.title.toLowerCase().includes(query)
        );
      }
      
      // 카테고리 필터링
      if (this.selectedCategory) {
        result = result.filter(product => 
          product.category === this.selectedCategory
        );
      }
      
      // 정렬
      switch (this.sortOption) {
        case 'latest':
          result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
          break;
        case 'priceLow':
          result.sort((a, b) => a.price - b.price);
          break;
        case 'priceHigh':
          result.sort((a, b) => b.price - a.price);
          break;
      }
      
      this.filteredProducts = result;
    },
    formatPrice(price) {
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },
    formatTime(date) {
      if (!date) return '';
      
      const now = new Date();
      const diff = Math.floor((now - new Date(date)) / 1000 / 60); // 분 단위 차이
      
      if (diff < 1) return '방금 전';
      if (diff < 60) return `${diff}분 전`;
      
      const hours = Math.floor(diff / 60);
      if (hours < 24) return `${hours}시간 전`;
      
      const days = Math.floor(hours / 24);
      if (days < 7) return `${days}일 전`;
      
      const weeks = Math.floor(days / 7);
      if (weeks < 5) return `${weeks}주 전`;
      
      const months = Math.floor(days / 30);
      if (months < 12) return `${months}개월 전`;
      
      return `${Math.floor(months / 12)}년 전`;
    },
    getCategoryName(categoryValue) {
      const category = this.categories.find(c => c.value === categoryValue);
      return category ? category.name : '기타';
    },
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode;
      localStorage.setItem('darkMode', this.isDarkMode);
    },
    async viewProductDetail(productId) {
      try {
        // 상품 ID로 상세 정보 API 호출
        const response = await this.$axios.get(`/api/products/${productId}`);
        
        // 백엔드 응답 구조에 맞게 데이터 추출
        if (response.data && response.data.data) {
          this.selectedProduct = response.data.data;
        } else {
          throw new Error('API 응답 구조가 올바르지 않습니다.');
        }
      } catch (error) {
        console.error('상품 상세 정보를 불러오는 중 오류가 발생했습니다:', error);
        alert('상품 상세 정보를 불러오는 중 오류가 발생했습니다.');
        // 에러 발생 시 기존 방식으로 처리
        const product = this.products.find(p => p.id === productId);
        if (product) {
          this.selectedProduct = product;
        }
      }
    },
    closeProductDetail() {
      this.selectedProduct = null;
    },
    openChat() {
      if (!this.selectedProduct) return;
      this.showChat = true;
    },
    closeChat() {
      this.showChat = false;
    },
    openPriceOffer() {
      if (!this.selectedProduct || !this.selectedProduct.negotiable) return;
      this.showPriceOffer = true;
    },
    closePriceOffer() {
      this.showPriceOffer = false;
    }
  },
  watch: {
    searchQuery() {
      this.filterProducts();
    }
  }
};
</script>

<style scoped>
.products-page {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  font-size: 28px;
  color: #333;
}

.dark-mode-toggle {
  display: flex;
  align-items: center;
}

.dark-mode-btn {
  background: none;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 8px 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.dark-mode-btn:hover {
  background-color: #f0f0f0;
}

.filters {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-bar {
  flex: 1;
  min-width: 200px;
  position: relative;
}

.search-bar input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
}

.category-filter select,
.sort-options select {
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  min-width: 150px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #4CAF50;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.no-products {
  text-align: center;
  padding: 50px 0;
  color: #666;
}

.no-products i {
  font-size: 48px;
  margin-bottom: 15px;
  color: #ddd;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.product-image {
  position: relative;
  height: 200px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: rgba(76, 175, 80, 0.9);
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 16px;
  margin: 0 0 10px;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 40px;
}

.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #4CAF50;
  margin-bottom: 10px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  color: #888;
  font-size: 12px;
}

.product-location,
.product-time {
  display: flex;
  align-items: center;
}

.product-location i,
.product-time i {
  margin-right: 5px;
}

/* 다크 모드 스타일 */
.dark-mode {
  background-color: #121212;
  color: #e0e0e0;
}

.dark-mode .header h1 {
  color: #e0e0e0;
}

.dark-mode .dark-mode-btn {
  border-color: #444;
  color: #e0e0e0;
}

.dark-mode .dark-mode-btn:hover {
  background-color: #2a2a2a;
}

.dark-mode .search-bar input,
.dark-mode .category-filter select,
.dark-mode .sort-options select {
  background-color: #1e1e1e;
  border-color: #444;
  color: #e0e0e0;
}

.dark-mode .search-btn {
  color: #b0b0b0;
}

.dark-mode .product-card {
  background-color: #1e1e1e;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.dark-mode .product-title {
  color: #e0e0e0;
}

.dark-mode .product-price {
  color: #6abf69;
}

.dark-mode .product-meta {
  color: #b0b0b0;
}

.dark-mode .no-products {
  color: #b0b0b0;
}

.dark-mode .no-products i {
  color: #444;
}

.dark-mode .spinner {
  border-color: rgba(255, 255, 255, 0.1);
  border-top-color: #6abf69;
}

/* 상품 상세 모달 스타일 추가 */
.product-detail-modal,
.chat-modal,
.price-offer-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  z-index: 1001;
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  max-width: 90%;
  max-height: 90%;
}

.chat-modal-content,
.offer-modal-content {
  background-color: transparent;
  box-shadow: none;
}

.modal-header {
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

.modal-body {
  padding: 20px;
  overflow-y: auto;
}

.product-detail-container {
  display: flex;
  gap: 30px;
}

.product-image-section {
  flex: 0 0 400px;
}

.product-main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
}

.product-details-section {
  flex: 1;
}

.product-title {
  font-size: 24px;
  margin: 0 0 10px;
}

.product-price {
  font-size: 28px;
  font-weight: bold;
  color: #4CAF50;
  margin-bottom: 20px;
}

.product-meta {
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  color: #666;
}

.meta-item i {
  width: 20px;
  margin-right: 8px;
}

.product-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.action-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.action-btn i {
  margin-right: 8px;
}

.chat-btn {
  background-color: #4CAF50;
  color: white;
}

.offer-btn {
  background-color: white;
  color: #4CAF50;
  border: 1px solid #4CAF50;
}

.product-description {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.product-description h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 18px;
}

.product-description p {
  line-height: 1.6;
  color: #555;
}

/* 다크 모드 스타일 */
.dark-mode .modal-content {
  background-color: #1e1e1e;
  color: #e0e0e0;
}

.dark-mode .modal-header {
  background-color: #6abf69;
}

.dark-mode .product-title,
.dark-mode .product-description h3 {
  color: #e0e0e0;
}

.dark-mode .product-price {
  color: #6abf69;
}

.dark-mode .meta-item,
.dark-mode .product-description p {
  color: #b0b0b0;
}

.dark-mode .offer-btn {
  background-color: #1e1e1e;
  color: #6abf69;
  border-color: #6abf69;
}

.dark-mode .chat-btn {
  background-color: #6abf69;
}

.dark-mode .product-description {
  border-top-color: #444;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .product-detail-container {
    flex-direction: column;
  }
  
  .product-image-section {
    flex: none;
    margin-bottom: 20px;
  }
  
  .product-main-image {
    height: 300px;
  }
}
</style>