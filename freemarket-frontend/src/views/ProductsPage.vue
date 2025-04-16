<template>
  <div class="products-page" :class="{ 'dark-mode': isDarkMode }">
    <header class="header">
      <div class="header-left">
        <h1>FreeMarket</h1>
      </div>
      <div class="search-bar">
        <div class="search-input-container">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="상품명, 지역명 검색" 
            @input="filterProducts"
          />
          <button class="search-button">
            <i class="fas fa-search"></i>
          </button>
        </div>
      </div>
      <nav class="main-nav">
        <router-link to="/" class="nav-link">홈</router-link>
        <router-link to="/products" class="nav-link active">상품목록</router-link>
        <router-link to="/register" class="nav-link">상품등록</router-link>
        <router-link to="/login" class="nav-link">로그인</router-link>
      </nav>
      <button @click="toggleDarkMode" class="theme-toggle">
        <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
      </button>
    </header>

    <div class="filter-section">
      <div class="category-filters">
        <button 
          class="category-button" 
          :class="{ active: selectedCategory === '' }"
          @click="selectCategory('')"
        >
          전체
        </button>
        <button 
          v-for="category in categories" 
          :key="category.value"
          class="category-button" 
          :class="{ active: selectedCategory === category.value }"
          @click="selectCategory(category.value)"
        >
          {{ category.name }}
        </button>
      </div>
      <div class="sort-options">
        <select v-model="sortOption" @change="sortProducts">
          <option value="latest">최신순</option>
          <option value="priceAsc">가격 낮은순</option>
          <option value="priceDesc">가격 높은순</option>
        </select>
      </div>
    </div>

    <div class="main-content">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner">
          <i class="fas fa-spinner fa-spin"></i>
        </div>
        <p>상품을 불러오는 중입니다...</p>
      </div>
      
      <div v-else-if="filteredProducts.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="fas fa-box-open"></i>
        </div>
        <h3>상품이 없습니다</h3>
        <p v-if="searchQuery">검색어 "{{ searchQuery }}"에 맞는 상품이 없습니다.</p>
        <p v-else-if="selectedCategory">선택한 카테고리에 상품이 없습니다.</p>
        <p v-else>등록된 상품이 없습니다.</p>
        <router-link to="/register" class="btn-register">
          상품 등록하기
        </router-link>
      </div>
      
      <div v-else>
        <h2 class="section-title">
          {{ selectedCategory ? getCategoryName(selectedCategory) : '전체 상품' }}
          <span class="product-count">({{ filteredProducts.length }})</span>
        </h2>
        
        <div class="product-grid">
          <div 
            v-for="product in filteredProducts" 
            :key="product.id" 
            class="product-card"
            @click="viewProductDetail(product.id)"
          >
            <div class="product-image-container">
              <img 
                :src="product.images && product.images.length > 0 ? product.images[0] : 'https://via.placeholder.com/200'" 
                :alt="product.title"
                class="product-image"
              >
              <div v-if="product.negotiable" class="negotiable-badge">
                가격제안가능
              </div>
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
    </div>
    
    <div class="floating-button" @click="goToRegister">
      <i class="fas fa-plus"></i>
    </div>
  </div>
</template>

<script>
export default {
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
      filteredProducts: []
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
    loadProducts() {
      this.loading = true;
      
      // 기존 샘플 데이터
      const sampleProducts = [
        {
          id: 1,
          title: '아이폰 13',
          price: 800000,
          images: ['https://via.placeholder.com/200'],
          category: 'electronics',
          location: '서울시 강남구',
          createdAt: new Date(Date.now() - 3600000).toISOString(), // 1시간 전
          negotiable: true
        },
        {
          id: 2,
          title: '맥북 프로',
          price: 1500000,
          images: ['https://via.placeholder.com/200'],
          category: 'electronics',
          location: '서울시 서초구',
          createdAt: new Date(Date.now() - 86400000).toISOString(), // 1일 전
          negotiable: false
        },
        {
          id: 3,
          title: '에어팟 프로',
          price: 250000,
          images: ['https://via.placeholder.com/200'],
          category: 'electronics',
          location: '서울시 송파구',
          createdAt: new Date(Date.now() - 172800000).toISOString(), // 2일 전
          negotiable: true
        },
        {
          id: 4,
          title: '갤럭시 S21',
          price: 900000,
          images: ['https://via.placeholder.com/200'],
          category: 'electronics',
          location: '경기도 성남시',
          createdAt: new Date(Date.now() - 259200000).toISOString(), // 3일 전
          negotiable: false
        },
        {
          id: 5,
          title: '아이패드 프로',
          price: 1200000,
          images: ['https://via.placeholder.com/200'],
          category: 'electronics',
          location: '인천시 연수구',
          createdAt: new Date(Date.now() - 345600000).toISOString(), // 4일 전
          negotiable: true
        },
        {
          id: 6,
          title: '애플워치',
          price: 450000,
          images: ['https://via.placeholder.com/200'],
          category: 'electronics',
          location: '부산시 해운대구',
          createdAt: new Date(Date.now() - 432000000).toISOString(), // 5일 전
          negotiable: false
        }
      ];
      
      // 로컬 스토리지에서 등록된 상품 가져오기
      const savedProducts = JSON.parse(localStorage.getItem('products') || '[]');
      
      // 샘플 데이터와 로컬 스토리지 데이터 합치기
      this.products = [...sampleProducts, ...savedProducts];
      
      // 최신순으로 정렬
      this.sortProducts();
      
      // 로딩 완료
      setTimeout(() => {
        this.loading = false;
      }, 500);
    },
    filterProducts() {
      // 검색어와 카테고리로 필터링
      let filtered = this.products;
      
      // 카테고리 필터링
      if (this.selectedCategory) {
        filtered = filtered.filter(product => product.category === this.selectedCategory);
      }
      
      // 검색어 필터링
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        filtered = filtered.filter(product => 
          product.title.toLowerCase().includes(query) || 
          (product.location && product.location.toLowerCase().includes(query))
        );
      }
      
      this.filteredProducts = filtered;
    },
    sortProducts() {
      // 정렬 옵션에 따라 정렬
      switch (this.sortOption) {
        case 'latest':
          this.filteredProducts = [...this.products].sort((a, b) => 
            new Date(b.createdAt) - new Date(a.createdAt)
          );
          break;
        case 'priceAsc':
          this.filteredProducts = [...this.products].sort((a, b) => a.price - b.price);
          break;
        case 'priceDesc':
          this.filteredProducts = [...this.products].sort((a, b) => b.price - a.price);
          break;
      }
      
      // 필터링 적용
      this.filterProducts();
    },
    selectCategory(category) {
      this.selectedCategory = category;
      this.filterProducts();
    },
    formatPrice(price) {
      // 가격 포맷팅 (1000 -> 1,000)
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },
    formatTime(timestamp) {
      // 시간 포맷팅 (n분 전, n시간 전, n일 전)
      const now = new Date();
      const time = new Date(timestamp);
      const diff = Math.floor((now - time) / 1000); // 초 단위 차이
      
      if (diff < 60) {
        return '방금 전';
      } else if (diff < 3600) {
        return `${Math.floor(diff / 60)}분 전`;
      } else if (diff < 86400) {
        return `${Math.floor(diff / 3600)}시간 전`;
      } else {
        return `${Math.floor(diff / 86400)}일 전`;
      }
    },
    getCategoryName(categoryValue) {
      const category = this.categories.find(cat => cat.value === categoryValue);
      return category ? category.name : '기타';
    },
    viewProductDetail(productId) {
      // 상품 상세 페이지로 이동
      this.$router.push(`/products/${productId}`);
    },
    goToRegister() {
      this.$router.push('/register');
    },
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode;
      localStorage.setItem('darkMode', this.isDarkMode);
    }
  },
  watch: {
    // 라우터 변경 시 상품 다시 로드
    '$route'() {
      this.loadProducts();
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap');

.products-page {
  font-family: 'Noto Sans KR', sans-serif;
  background-color: #f8f9fa;
  min-height: 100vh;
  transition: all 0.3s ease;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s ease;
}

.header-left {
  display: flex;
  align-items: center;
}

.header h1 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.search-bar {
  flex: 1;
  max-width: 500px;
  margin: 0 20px;
}

.search-input-container {
  position: relative;
  width: 100%;
}

.search-input-container input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.3s;
}

.search-input-container input:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.search-button {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
}

.main-nav {
  display: flex;
  gap: 20px;
}

.nav-link {
  text-decoration: none;
  color: #555;
  font-weight: 500;
  font-size: 15px;
  padding: 5px 0;
  position: relative;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #4CAF50;
}

.nav-link.active {
  color: #4CAF50;
  font-weight: 700;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #4CAF50;
}

.theme-toggle {
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
  margin-left: 15px;
}

.theme-toggle:hover {
  background-color: #f0f8f0;
  color: #4CAF50;
}

.filter-section {
  padding: 15px 20px;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.category-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-button {
  background: none;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 6px 12px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.category-button:hover {
  background-color: #f0f8f0;
  border-color: #4CAF50;
  color: #4CAF50;
}

.category-button.active {
  background-color: #4CAF50;
  border-color: #4CAF50;
  color: white;
}

.sort-options select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  color: #555;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.sort-options select:focus {
  outline: none;
  border-color: #4CAF50;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.section-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.product-count {
  font-size: 16px;
  color: #888;
  margin-left: 10px;
  font-weight: 400;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.product-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.product-image-container {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.negotiable-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background-color: rgba(76, 175, 80, 0.9);
  color: white;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #4CAF50;
  margin: 0 0 10px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #888;
}

.product-location, .product-time {
  display: flex;
  align-items: center;
}

.product-location i, .product-time i {
  margin-right: 5px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
}

.loading-spinner {
  font-size: 30px;
  color: #4CAF50;
  margin-bottom: 15px;
}

.loading-container p {
  color: #666;
  font-size: 16px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  text-align: center;
}

.empty-icon {
  font-size: 50px;
  color: #ccc;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 20px;
  color: #555;
  margin-bottom: 10px;
}

.empty-state p {
  color: #888;
  margin-bottom: 20px;
}

.btn-register {
  display: inline-block;
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-register:hover {
  background-color: #45a049;
  transform: translateY(-2px);
}

.floating-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  background-color: #4CAF50;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: all 0.3s;
  z-index: 90;
}

.floating-button:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
}

/* 다크 모드 스타일 */
.dark-mode {
  background-color: #121212;
  color: #e0e0e0;
}

.dark-mode .header,
.dark-mode .filter-section,
.dark-mode .product-card {
  background-color: #1e1e1e;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.dark-mode .header {
  border-bottom-color: #333;
}

.dark-mode .filter-section {
  border-bottom-color: #333;
}

.dark-mode .header h1,
.dark-mode .section-title {
  color: #e0e0e0;
}

.dark-mode .nav-link {
  color: #b0b0b0;
}

.dark-mode .nav-link:hover,
.dark-mode .nav-link.active {
  color: #6abf69;
}

.dark-mode .nav-link.active::after {
  background-color: #6abf69;
}

.dark-mode .search-input-container input,
.dark-mode .sort-options select,
.dark-mode .category-button {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

.dark-mode .search-input-container input:focus {
  border-color: #6abf69;
  box-shadow: 0 0 0 2px rgba(106, 191, 105, 0.2);
}

.dark-mode .search-button {
  color: #b0b0b0;
}

.dark-mode .category-button:hover {
  background-color: #2a3a2a;
  border-color: #6abf69;
  color: #6abf69;
}

.dark-mode .category-button.active {
  background-color: #6abf69;
  border-color: #6abf69;
  color: #121212;
}

.dark-mode .product-title {
  color: #e0e0e0;
}

.dark-mode .product-price {
  color: #6abf69;
}

.dark-mode .theme-toggle {
  color: #e0e0e0;
}

.dark-mode .theme-toggle:hover {
  background-color: #2a2a2a;
  color: #6abf69;
}

.dark-mode .floating-button {
  background-color: #6abf69;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .header {
    flex-wrap: wrap;
  }
  
  .header-left {
    width: 100%;
    margin-bottom: 10px;
  }
  
  .search-bar {
    order: 3;
    width: 100%;
    max-width: none;
    margin: 10px 0 0;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .category-filters {
    margin-bottom: 15px;
    width: 100%;
    overflow-x: auto;
    padding-bottom: 5px;
    flex-wrap: nowrap;
  }
  
  .sort-options {
    width: 100%;
  }
  
  .sort-options select {
    width: 100%;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
}
</style>