<template>
  <div class="home" :class="{ 'dark-mode': isDarkMode }">
    <header class="header">
      <div class="logo-container">
        <img src="@/assets/homepage-image.png" alt="로고" class="logo">
        <h1 class="site-title">FreeMarket</h1>
      </div>
      <nav class="main-nav">
        <router-link to="/">홈</router-link>
        <router-link to="/products">상품목록</router-link>
        <router-link to="/register">상품등록</router-link>
        <router-link to="/login" class="nav-link">로그인</router-link>
        <router-link to="/signup" class="nav-link">회원가입</router-link>
        <router-link to="/review" class="nav-link">공지사항</router-link>
        <button @click="toggleDarkMode" class="theme-toggle">
          <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
        </button>
      </nav>
    </header>
    
    <section class="hero-section">
      <div class="hero-content">
        <h1>안전하고 편리한 중고거래</h1>
        <p>지금 바로 FreeMarket에서 시작하세요!</p>
        <div class="hero-buttons">
          <router-link to="/register" class="cta-button primary-btn">판매 시작하기</router-link>
          <router-link to="/products" class="cta-button secondary-btn">둘러보기</router-link>
        </div>
      </div>
    </section>

    <!-- 로딩 오버레이 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>데이터를 불러오는 중입니다...</p>
    </div>

    <!-- 에러 메시지 -->
    <div v-if="error" class="error-message">
      <p>{{ error }}</p>
      <button @click="fetchProducts" class="retry-button">다시 시도</button>
    </div>

    <section class="categories-section">
      <h2 class="section-title">카테고리</h2>
      <div class="categories-grid">
        <div v-for="category in categories" :key="category.id" class="category-item" @click="selectCategory(category.name)">
          <div class="category-icon">
            <i :class="category.icon"></i>
          </div>
          <p>{{ category.name }}</p>
        </div>
      </div>
    </section>
    
    <div class="filter-section">
      <div class="filter-header">
        <h2>필터링</h2>
        <button @click="resetFilters" class="reset-button">초기화</button>
      </div>
      <div class="filter-content">
        <div class="filter-group">
          <label for="category">카테고리:</label>
          <select v-model="selectedCategory" id="category">
            <option value="">모두</option>
            <option v-for="category in categories" :key="category.id" :value="category.name">
              {{ category.name }}
            </option>
          </select>
        </div>
        <div class="filter-group">
          <label for="price">가격대:</label>
          <select v-model="selectedPrice" id="price">
            <option value="">모두</option>
            <option value="0-100000">0 - 100,000원</option>
            <option value="100000-500000">100,000 - 500,000원</option>
            <option value="500000-1000000">500,000 - 1,000,000원</option>
            <option value="1000000-">1,000,000원 이상</option>
          </select>
        </div>
        <div class="filter-group">
          <label for="location">지역:</label>
          <select v-model="selectedLocation" id="location">
            <option value="">모두</option>
            <option value="교내">교내</option>
            <option value="주변">주변</option>
          </select>
        </div>
        <button @click="applyFilters" class="filter-button">필터 적용</button>
      </div>
    </div>

    <div class="main-content">
      <div class="section-header">
        <h2 class="section-title">인기 상품</h2>
        <div class="section-actions">
          <div class="sort-options">
            <span>정렬: </span>
            <button class="sort-btn" :class="{ active: sortBy === 'popular' }" @click="sortProducts('popular')">인기순</button>
            <button class="sort-btn" :class="{ active: sortBy === 'recent' }" @click="sortProducts('recent')">최신순</button>
            <button class="sort-btn" :class="{ active: sortBy === 'price-low' }" @click="sortProducts('price-low')">가격 낮은순</button>
            <button class="sort-btn" :class="{ active: sortBy === 'price-high' }" @click="sortProducts('price-high')">가격 높은순</button>
          </div>
        </div>
      </div>
      <div v-if="filteredProducts.length === 0 && !loading" class="no-products-message">
        <p>표시할 상품이 없습니다.</p>
      </div>
      <div v-else class="product-grid">
        <div v-for="product in filteredProducts" :key="product.id" class="product-card">
          <div class="product-image-container">
            <img :src="product.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'" :alt="product.title" class="product-image">
            <div class="product-badge" v-if="isNewProduct(product.createdAt)">NEW</div>
            <button class="like-button" @click="toggleLike(product)">
              <i :class="product.isLiked ? 'fas fa-heart' : 'far fa-heart'"></i>
            </button>
          </div>
          <div class="product-info">
            <h3 class="product-title">{{ product.title }}</h3>
            <p class="price">{{ formatPrice(product.price) }}원</p>
            <div class="product-meta">
              <span class="location"><i class="fas fa-map-marker-alt"></i> {{ product.location }}</span>
              <span class="time">{{ getTimeAgo(product.createdAt) }}</span>
            </div>
            <div class="product-actions">
              <button class="action-btn chat-btn" @click="openChatModal(product)">
                <i class="fas fa-comment"></i> 채팅하기
              </button>
              <button class="action-btn offer-btn" @click="openOfferModal(product)">
                <i class="fas fa-tag"></i> 가격제안
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="view-more-container">
        <button @click="loadMoreProducts" class="view-more-button">더 보기</button>
      </div>
    </div>

    <div class="main-content">
      <h2 class="section-title">최근 등록된 상품</h2>
      <div v-if="recentProducts.length === 0 && !loading" class="no-products-message">
        <p>최근 등록된 상품이 없습니다.</p>
      </div>
      <div v-else class="product-grid">
        <div v-for="product in recentProducts" :key="product.id" class="product-card">
          <div class="product-image-container">
            <img :src="product.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'" :alt="product.title" class="product-image">
            <div class="product-badge" v-if="isNewProduct(product.createdAt)">NEW</div>
            <button class="like-button" @click="toggleLike(product)">
              <i :class="product.isLiked ? 'fas fa-heart' : 'far fa-heart'"></i>
            </button>
          </div>
          <div class="product-info">
            <h3 class="product-title">{{ product.title }}</h3>
            <p class="price">{{ formatPrice(product.price) }}원</p>
            <div class="product-meta">
              <span class="location"><i class="fas fa-map-marker-alt"></i> {{ product.location }}</span>
              <span class="time">{{ getTimeAgo(product.createdAt) }}</span>
            </div>
            <div class="product-actions">
              <button class="action-btn chat-btn" @click="openChatModal(product)">
                <i class="fas fa-comment"></i> 채팅하기
              </button>
              <button class="action-btn offer-btn" @click="openOfferModal(product)">
                <i class="fas fa-tag"></i> 가격제안
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 채팅 모달 -->
    <div v-if="showChatModal" class="modal-overlay" @click="closeChatModal">
      <div class="modal-content chat-modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedProduct ? selectedProduct.title : '채팅' }}</h3>
          <button class="close-btn" @click="closeChatModal">&times;</button>
        </div>
        <div class="chat-messages">
          <div class="system-message">채팅이 시작되었습니다.</div>
          <!-- 채팅 메시지들이 여기에 표시됩니다 -->
        </div>
        <div class="chat-input">
          <input type="text" v-model="chatMessage" placeholder="메시지를 입력하세요..." @keyup.enter="sendMessage">
          <button @click="sendMessage">전송</button>
        </div>
      </div>
    </div>

    <!-- 가격제안 모달 -->
    <div v-if="showOfferModal" class="modal-overlay" @click="closeOfferModal">
      <div class="modal-content offer-modal-content" @click.stop>
        <div class="modal-header">
          <h3>가격 제안하기</h3>
          <button class="close-btn" @click="closeOfferModal">&times;</button>
        </div>
        <div class="modal-body">
          <div v-if="selectedProduct" class="product-summary">
            <img :src="selectedProduct.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'" :alt="selectedProduct.title" class="product-thumbnail">
            <div>
              <h4>{{ selectedProduct.title }}</h4>
              <p class="original-price">판매가: {{ formatPrice(selectedProduct.price) }}원</p>
            </div>
          </div>
          <form class="offer-form" @submit.prevent="submitOffer">
            <label for="offer-price">제안 가격</label>
            <div class="price-input">
              <input type="number" id="offer-price" v-model="offerPrice" min="0" :max="selectedProduct ? selectedProduct.price : 0" required>
              <span>원</span>
            </div>
            <button type="submit" class="submit-offer">제안하기</button>
          </form>
        </div>
      </div>
    </div>

    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>FreeMarket</h3>
          <p>안전하고 편리한 중고거래 플랫폼</p>
        </div>
        <div class="footer-section">
          <h4>고객센터</h4>
          <p>평일 09:00 - 18:00</p>
          <p>이메일: support@freemarket.com</p>
        </div>
        <div class="footer-section">
          <h4>바로가기</h4>
          <ul>
            <li><router-link to="/about">회사소개</router-link></li>
            <li><router-link to="/terms">이용약관</router-link></li>
            <li><router-link to="/privacy">개인정보처리방침</router-link></li>
          </ul>
        </div>
        <div class="footer-section">
          <h4>팔로우하기</h4>
          <div class="social-links">
            <a href="#" class="social-link"><i class="fab fa-facebook"></i></a>
            <a href="#" class="social-link"><i class="fab fa-instagram"></i></a>
            <a href="#" class="social-link"><i class="fab fa-twitter"></i></a>
          </div>
        </div>
      </div>
      <div class="copyright">
        <p>&copy; 2023 FreeMarket. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      isDarkMode: false,
      sortBy: 'popular',
      selectedCategory: '',
      selectedPrice: '',
      selectedLocation: '',
      categories: [],
      products: [],
      recentProducts: [],
      loading: false,
      error: null,
      apiBaseUrl: 'http://localhost:8080/api', // 백엔드 API 기본 URL
      page: 1,
      limit: 12,
      hasMore: true,
      
      // 모달 관련 데이터
      showChatModal: false,
      showOfferModal: false,
      selectedProduct: null,
      chatMessage: '',
      offerPrice: 0
    }
  },
  computed: {
    filteredProducts() {
      if (!this.products.length) return [];
      
      let result = this.products.filter(product => {
        const matchesCategory = this.selectedCategory ? product.category === this.selectedCategory : true;
        const matchesPrice = this.selectedPrice ? this.isPriceInRange(product.price) : true;
        const matchesLocation = this.selectedLocation ? product.location === this.selectedLocation : true;
        return matchesCategory && matchesPrice && matchesLocation;
      });

      // 정렬 적용
      switch(this.sortBy) {
        case 'recent':
          result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
          break;
        case 'price-low':
          result.sort((a, b) => a.price - b.price);
          break;
        case 'price-high':
          result.sort((a, b) => b.price - a.price);
          break;
        case 'popular':
        default:
          result.sort((a, b) => b.popularity - a.popularity);
          break;
      }
      
      return result;
    }
  },
  methods: {
    // API 호출 메서드들
    async fetchCategories() {
      try {
        this.loading = true;
        const response = await axios.get(`${this.apiBaseUrl}/categories`);
        this.categories = response.data.map(category => ({
          ...category,
          icon: this.getCategoryIcon(category.name)
        }));
      } catch (error) {
        console.error('카테고리를 불러오는 중 오류가 발생했습니다:', error);
        // 임시 카테고리 데이터 설정
        this.categories = [
          { id: 1, name: '디지털/가전', icon: 'fas fa-laptop' },
          { id: 2, name: '가구/인테리어', icon: 'fas fa-couch' },
          { id: 3, name: '의류/패션', icon: 'fas fa-tshirt' },
          { id: 4, name: '도서/티켓/취미', icon: 'fas fa-book' },
          { id: 5, name: '생활/가공식품', icon: 'fas fa-utensils' },
          { id: 6, name: '스포츠/레저', icon: 'fas fa-running' },
          { id: 7, name: '뷰티/미용', icon: 'fas fa-spa' },
          { id: 8, name: '반려동물용품', icon: 'fas fa-paw' }
        ];
      } finally {
        this.loading = false;
      }
    },
    
    async fetchProducts() {
      try {
        this.loading = true;
        this.error = null;
        
        // API 호출 (하드코딩된 부분 대신 실제 API 호출)
        const response = await this.$axios.get('/api/products', {
          params: {
            page: this.page - 1, // 백엔드는 0부터 시작하는 페이지 인덱스 사용
            size: this.limit,
            sort: this.convertSortParam(this.sortBy)
          }
        });
        
        // 백엔드 응답 구조에 맞게 데이터 추출
        if (response.data && response.data.data) {
          const result = response.data.data;
          const fetchedProducts = result.content || [];
          
          // 페이지 초기화가 아닌 경우 기존 상품에 추가
          if (this.page === 1) {
            this.products = fetchedProducts.map(product => ({
              ...product,
              isLiked: product.wishlist || false,
              popularity: product.viewCount || 0
            }));
          } else {
            this.products = [...this.products, ...fetchedProducts.map(product => ({
              ...product,
              isLiked: product.wishlist || false,
              popularity: product.viewCount || 0
            }))];
          }
          
          // 더 불러올 상품이 있는지 확인
          this.hasMore = !result.last; // last 값이 false면 더 불러올 페이지가 있음
        } else {
          throw new Error('API 응답 구조가 올바르지 않습니다.');
        }
      } catch (error) {
        console.error('상품을 불러오는 중 오류가 발생했습니다:', error);
        this.error = '상품을 불러오는 중 오류가 발생했습니다.';
        // 에러 발생 시에도 더 이상 불러올 상품이 없는 것으로 처리
        this.hasMore = false;
        // 에러 발생 시 더미 데이터 사용
        this.useDummyProducts();
      } finally {
        this.loading = false;
      }
    },
    
    async fetchRecentProducts() {
      try {
        this.loading = true;
        // API 호출
        const response = await this.$axios.get('/api/products/recent');
        this.recentProducts = response.data.map(product => ({
          ...product,
          isLiked: product.liked || false,
          popularity: product.likeCount || 0
        }));
      } catch (error) {
        console.error('최근 상품을 불러오는 중 오류가 발생했습니다:', error);
        // 에러 발생 시 더미 데이터 사용
        this.useDummyRecentProducts();
      } finally {
        this.loading = false;
      }
    },
    
    async toggleLike(product) {
      try {
        const endpoint = product.isLiked 
          ? `/api/products/${product.id}/unlike`
          : `/api/products/${product.id}/like`;
        
        // API 호출
        await this.$axios.post(endpoint);
        
        // 좋아요 상태 토글
        product.isLiked = !product.isLiked;
        
        // 인기도(좋아요 수) 업데이트
        if (product.isLiked) {
          product.popularity += 1;
        } else {
          product.popularity -= 1;
        }
      } catch (error) {
        console.error('좋아요 처리 중 오류가 발생했습니다:', error);
        alert('좋아요 처리 중 오류가 발생했습니다.');
      }
    },
    
    async applyFilters() {
      try {
        this.loading = true;
        this.error = null;
        this.page = 1; // 필터 적용 시 페이지 초기화
        
        // 필터 파라미터 구성
        const params = {
          category: this.selectedCategory || undefined,
          priceRange: this.selectedPrice || undefined,
          location: this.selectedLocation || undefined,
          page: this.page,
          limit: this.limit
        };
        
        // API 호출
        const response = await this.$axios.get('/api/products/filter', { params });
        
        this.products = response.data.map(product => ({
          ...product,
          isLiked: product.liked || false,
          popularity: product.likeCount || 0
        }));
        
        this.hasMore = this.products.length === this.limit;
      } catch (error) {
        console.error('필터 적용 중 오류가 발생했습니다:', error);
        this.error = '필터 적용 중 오류가 발생했습니다.';
        // 에러 발생 시 로컬 필터링 사용
        this.useLocalFiltering();
      } finally {
        this.loading = false;
      }
    },
    
    // 임시 데이터 사용 메서드
    useDummyProducts() {
      this.products = [
        {
          id: 1,
          title: '아이폰 13',
          price: 800000,
          category: '디지털/가전',
          location: '교내',
          imageUrl: 'https://source.unsplash.com/300x200/?iphone',
          isLiked: false,
          createdAt: new Date(2023, 4, 15),
          popularity: 24
        },
        {
          id: 2,
          title: '맥북 프로',
          price: 1500000,
          category: '디지털/가전',
          location: '주변',
          imageUrl: 'https://source.unsplash.com/300x200/?macbook',
          isLiked: false,
          createdAt: new Date(2023, 4, 14),
          popularity: 18
        },
        {
          id: 3,
          title: '에어팟 프로',
          price: 250000,
          category: '디지털/가전',
          location: '교내',
          imageUrl: 'https://source.unsplash.com/300x200/?airpods',
          isLiked: false,
          createdAt: new Date(2023, 4, 16),
          popularity: 15
        },
        {
          id: 4,
          title: '갤럭시 S21',
          price: 900000,
          category: '디지털/가전',
          location: '주변',
          imageUrl: 'https://source.unsplash.com/300x200/?galaxy',
          isLiked: false,
          createdAt: new Date(2023, 4, 13),
          popularity: 12
        },
        {
          id: 5,
          title: 'LG 그램',
          price: 1200000,
          category: '디지털/가전',
          location: '교내',
          imageUrl: 'https://source.unsplash.com/300x200/?laptop',
          isLiked: false,
          createdAt: new Date(2023, 4, 12),
          popularity: 10
        },
        {
          id: 6,
          title: '아이패드 프로',
          price: 1000000,
          category: '디지털/가전',
          location: '주변',
          imageUrl: 'https://source.unsplash.com/300x200/?ipad',
          isLiked: false,
          createdAt: new Date(2023, 4, 17),
          popularity: 8
        }
      ];
    },
    
    useDummyRecentProducts() {
      this.recentProducts = [
        {
          id: 7,
          title: '나이키 에어포스 1',
          price: 90000,
          category: '의류/패션',
          location: '교내',
          imageUrl: 'https://source.unsplash.com/300x200/?nike',
          isLiked: false,
          createdAt: new Date(2023, 4, 17),
          popularity: 7
        },
        {
          id: 8,
          title: '해리포터 시리즈 전권',
          price: 80000,
          category: '도서/티켓/취미',
          location: '주변',
          imageUrl: 'https://source.unsplash.com/300x200/?harrypotter',
          isLiked: false,
          createdAt: new Date(2023, 4, 17),
          popularity: 5
        },
        {
          id: 9,
          title: '캠핑 텐트 4인용',
          price: 120000,
          category: '스포츠/레저',
          location: '교내',
          imageUrl: 'https://source.unsplash.com/300x200/?tent',
          isLiked: false,
          createdAt: new Date(2023, 4, 17),
          popularity: 3
        },
        {
          id: 10,
          title: '다이슨 에어랩',
          price: 400000,
          category: '디지털/가전',
          location: '주변',
          imageUrl: 'https://source.unsplash.com/300x200/?dyson',
          isLiked: false,
          createdAt: new Date(2023, 4, 17),
          popularity: 6
        }
      ];
    },
    
    useLocalFiltering() {
      // 이미 로드된 상품 데이터를 사용하여 로컬에서 필터링
      this.fetchProducts();
    },
    
    // 기타 메서드들
    getCategoryIcon(categoryName) {
      // 카테고리 이름에 따라 적절한 아이콘 클래스 반환
      const iconMap = {
        '디지털/가전': 'fas fa-laptop',
        '가구/인테리어': 'fas fa-couch',
        '의류/패션': 'fas fa-tshirt',
        '도서/티켓/취미': 'fas fa-book',
        '생활/가공식품': 'fas fa-utensils',
        '스포츠/레저': 'fas fa-running',
        '뷰티/미용': 'fas fa-spa',
        '반려동물용품': 'fas fa-paw'
      };
      
      return iconMap[categoryName] || 'fas fa-box'; // 기본 아이콘
    },
    
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode;
      localStorage.setItem('darkMode', this.isDarkMode);
    },
    
    isPriceInRange(price) {
      if (!this.selectedPrice) return true;
      
      if (this.selectedPrice === '1000000-') {
        return price >= 1000000;
      }
      
      const [min, max] = this.selectedPrice.split('-').map(Number);
      return price >= min && price <= max;
    },
    
    resetFilters() {
      this.selectedCategory = '';
      this.selectedPrice = '';
      this.selectedLocation = '';
      this.fetchProducts(); // 필터 초기화 후 모든 상품 다시 불러오기
    },
    
    formatPrice(price) {
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },
    
    getTimeAgo(date) {
      if (!date) return '';
      
      const now = new Date();
      const productDate = new Date(date);
      const diffTime = Math.abs(now - productDate);
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays === 0) {
        return '오늘';
      } else if (diffDays === 1) {
        return '어제';
      } else {
        return `${diffDays}일 전`;
      }
    },
    
    isNewProduct(createdAt) {
      if (!createdAt) return false;
      
      const now = new Date();
      const productDate = new Date(createdAt);
      const diffTime = Math.abs(now - productDate);
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
      
      // 3일 이내의 상품은 NEW로 표시
      return diffDays <= 3;
    },
    
    sortProducts(sortType) {
      this.sortBy = sortType;
    },
    
    selectCategory(categoryName) {
      this.selectedCategory = categoryName;
      this.applyFilters();
    },
    
    loadMoreProducts() {
      if (this.hasMore && !this.loading) {
        this.page += 1;
        this.fetchProducts();
      }
    },
    
    // 모달 관련 메서드
    openChatModal(product) {
      this.selectedProduct = product;
      this.showChatModal = true;
    },
    
    closeChatModal() {
      this.showChatModal = false;
      this.selectedProduct = null;
      this.chatMessage = '';
    },
    
    openOfferModal(product) {
      this.selectedProduct = product;
      this.offerPrice = Math.floor(product.price * 0.9); // 기본 제안가
      this.showOfferModal = true;
    },
    
    closeOfferModal() {
      this.showOfferModal = false;
      this.selectedProduct = null;
      this.offerPrice = 0;
    },
    
    async sendMessage() {
      if (!this.chatMessage.trim()) return;
      
      try {
        // 실제 API 연동 시 메시지 전송 로직
        // await axios.post(`${this.apiBaseUrl}/chat/${this.selectedProduct.id}`, {
        //   message: this.chatMessage
        // });
        
        // 메시지 전송 후 입력창 초기화
        console.log(`메시지 전송: ${this.chatMessage}`);
        this.chatMessage = '';
        
        // 실제 구현에서는 웹소켓이나 폴링을 통해 응답 메시지를 받아야 함
      } catch (error) {
        console.error('메시지 전송 중 오류가 발생했습니다:', error);
        alert('메시지 전송 중 오류가 발생했습니다.');
      }
    },
    
    async submitOffer() {
      if (!this.offerPrice || this.offerPrice <= 0) {
        alert('유효한 가격을 입력해주세요.');
        return;
      }
      
      try {
        // 실제 API 연동 시 가격제안 전송 로직
        // await axios.post(`${this.apiBaseUrl}/products/${this.selectedProduct.id}/offer`, {
        //   price: this.offerPrice
        // });
        
        alert(`${this.formatPrice(this.offerPrice)}원으로 가격제안이 전송되었습니다.`);
        this.closeOfferModal();
      } catch (error) {
        console.error('가격제안 중 오류가 발생했습니다:', error);
        alert('가격제안 중 오류가 발생했습니다.');
      }
    },
    
    // 정렬 파라미터 변환 메서드 추가
    convertSortParam(sortBy) {
      switch(sortBy) {
        case 'recent':
          return 'createdDate,desc';
        case 'price-low':
          return 'price,asc';
        case 'price-high':
          return 'price,desc';
        case 'popular':
        default:
          return 'viewCount,desc'; // 인기순은 조회수 기준으로 정렬
      }
    }
  },
  mounted() {
    // 로컬 스토리지에서 다크모드 설정 불러오기
    const savedDarkMode = localStorage.getItem('darkMode');
    if (savedDarkMode !== null) {
      this.isDarkMode = savedDarkMode === 'true';
    }
    
    // 시스템 다크모드 감지
    const prefersDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
    if (savedDarkMode === null && prefersDarkMode) {
      this.isDarkMode = true;
    }
    
    // 데이터 로드
    this.fetchCategories();
    this.fetchProducts();
    this.fetchRecentProducts();
  }
}
</script>

<style scoped>
/* 전체 레이아웃 */
.home {
  font-family: 'Noto Sans KR', sans-serif;
  color: #333;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.dark-mode {
  background-color: #121212;
  color: #f1f1f1;
}

/* 헤더 스타일 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.dark-mode .header {
  background-color: #1e1e1e;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.logo-container {
  display: flex;
  align-items: center;
}

.logo {
  height: 40px;
  margin-right: 1rem;
}

.site-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #4CAF50;
  margin: 0;
}

.main-nav {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.main-nav a {
  text-decoration: none;
  color: #555;
  font-weight: 500;
  transition: color 0.3s;
}

.dark-mode .main-nav a {
  color: #ccc;
}

.main-nav a:hover {
  color: #4CAF50;
}

.theme-toggle {
  background: none;
  border: none;
  color: #555;
  font-size: 1.2rem;
  cursor: pointer;
  transition: color 0.3s;
}

.dark-mode .theme-toggle {
  color: #f1f1f1;
}

.theme-toggle:hover {
  color: #4CAF50;
}

/* 히어로 섹션 스타일 */
.hero-section {
  background-image: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('@/assets/homepage-image.png');
  background-size: cover;
  background-position: center;
  color: white;
  text-align: center;
  padding: 5rem 2rem;
  margin-bottom: 3rem;
}

.hero-content h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.hero-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

.hero-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.cta-button {
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s;
}

.primary-btn {
  background-color: #4CAF50;
  color: white;
}

.primary-btn:hover {
  background-color: #388E3C;
  transform: translateY(-2px);
}

.secondary-btn {
  background-color: transparent;
  color: white;
  border: 2px solid white;
}

.secondary-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

/* 카테고리 섹션 스타일 */
.categories-section {
  padding: 0 2rem;
  margin-bottom: 3rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.section-title {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  color: #333;
}

.dark-mode .section-title {
  color: #f1f1f1;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1.5rem;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1.5rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: pointer;
}

.dark-mode .category-item {
  background-color: #2d2d2d;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.category-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.dark-mode .category-item:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.4);
}

.category-icon {
  font-size: 2rem;
  color: #4CAF50;
  margin-bottom: 1rem;
}

.category-item p {
  font-weight: 500;
  text-align: center;
}

/* 필터 섹션 스타일 */
.filter-section {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  margin: 0 2rem 3rem 2rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.dark-mode .filter-section {
  background-color: #2d2d2d;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.filter-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
}

.reset-button {
  background-color: transparent;
  border: 1px solid #ddd;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  color: #777;
  cursor: pointer;
  transition: all 0.3s;
}

.dark-mode .reset-button {
  border-color: #555;
  color: #ccc;
}

.reset-button:hover {
  background-color: #f5f5f5;
  color: #333;
}

.dark-mode .reset-button:hover {
  background-color: #3d3d3d;
  color: #f1f1f1;
}

.filter-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.filter-group {
  display: flex;
  flex-direction: column;
}

.filter-group label {
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.filter-group select {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
}

.dark-mode .filter-group select {
  background-color: #3d3d3d;
  border-color: #555;
  color: #f1f1f1;
}

.filter-button {
  padding: 0.8rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.3s;
}

.filter-button:hover {
  background-color: #388E3C;
}

/* 메인 콘텐츠 스타일 */
.main-content {
  padding: 0 2rem;
  margin-bottom: 3rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.sort-options span {
  color: #777;
}

.dark-mode .sort-options span {
  color: #ccc;
}

.sort-btn {
  background: none;
  border: none;
  padding: 0.5rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  color: #777;
  transition: all 0.3s;
}

.dark-mode .sort-btn {
  color: #ccc;
}

.sort-btn.active {
  background-color: #4CAF50;
  color: white;
}

.sort-btn:hover:not(.active) {
  background-color: #f5f5f5;
  color: #333;
}

.dark-mode .sort-btn:hover:not(.active) {
  background-color: #3d3d3d;
  color: #f1f1f1;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
}

.product-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
}

.dark-mode .product-card {
  background-color: #2d2d2d;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.dark-mode .product-card:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
}

.product-image-container {
  position: relative;
  height: 200px;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background-color: #FF5722;
  color: white;
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
}

.like-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: rgba(255, 255, 255, 0.8);
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.dark-mode .like-button {
  background-color: rgba(45, 45, 45, 0.8);
}

.like-button:hover {
  background-color: white;
  transform: scale(1.1);
}

.dark-mode .like-button:hover {
  background-color: #3d3d3d;
}

.like-button i {
  color: #FF5722;
  font-size: 1.2rem;
}

.product-info {
  padding: 1.2rem;
}

.product-title {
  font-size: 1.1rem;
  margin-bottom: 0.7rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 600;
}

.dark-mode .product-title {
  color: #f1f1f1;
}

.price {
  font-size: 1.3rem;
  font-weight: 700;
  color: #4CAF50;
  margin-bottom: 0.7rem;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  color: #777;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  padding-bottom: 0.8rem;
  border-bottom: 1px solid #f0f0f0;
}

.dark-mode .product-meta {
  color: #aaa;
  border-bottom-color: #3d3d3d;
}

.product-actions {
  display: flex;
  gap: 0.8rem;
}

.action-btn {
  flex: 1;
  padding: 0.7rem 0;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s;
  font-weight: 600;
  font-size: 0.95rem;
}

.chat-btn {
  background-color: #2196F3;
  color: white;
  box-shadow: 0 2px 5px rgba(33, 150, 243, 0.3);
}

.chat-btn:hover {
  background-color: #1976D2;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(33, 150, 243, 0.4);
}

.offer-btn {
  background-color: #FF9800;
  color: white;
  box-shadow: 0 2px 5px rgba(255, 152, 0, 0.3);
}

.offer-btn:hover {
  background-color: #F57C00;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(255, 152, 0, 0.4);
}

.action-btn i {
  margin-right: 0.5rem;
}

.view-more-container {
  text-align: center;
  margin-top: 2rem;
}

.view-more-button {
  padding: 0.8rem 2rem;
  background-color: transparent;
  border: 2px solid #4CAF50;
  color: #4CAF50;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.view-more-button:hover {
  background-color: #4CAF50;
  color: white;
}

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.modal-content {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  z-index: 1001;
  overflow: hidden;
}

.dark-mode .modal-content {
  background-color: #2d2d2d;
  color: #f1f1f1;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.dark-mode .modal-header {
  border-bottom-color: #444;
}

.modal-header h3 {
  font-size: 1.3rem;
  color: #333;
}

.dark-mode .modal-header h3 {
  color: #f1f1f1;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #777;
  cursor: pointer;
}

.dark-mode .close-btn {
  color: #aaa;
}

.modal-body {
  padding: 1rem;
}

/* 채팅 모달 스타일 */
.chat-modal-content {
  width: 90%;
  max-width: 500px;
  height: 80%;
  max-height: 600px;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.system-message {
  text-align: center;
  color: #777;
  font-size: 0.9rem;
  padding: 0.5rem;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.dark-mode .system-message {
  color: #aaa;
  background-color: #3d3d3d;
}

.chat-input {
  display: flex;
  padding: 1rem;
  border-top: 1px solid #eee;
}

.dark-mode .chat-input {
  border-top-color: #444;
}

.chat-input input {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-right: 0.5rem;
}

.dark-mode .chat-input input {
  background-color: #3d3d3d;
  border-color: #555;
  color: #f1f1f1;
}

.chat-input button {
  padding: 0.8rem 1rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.chat-input button:hover {
  background-color: #388E3C;
}

/* 가격제안 모달 스타일 */
.offer-modal-content {
  width: 90%;
  max-width: 400px;
}

.product-summary {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.product-thumbnail {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.original-price {
  color: #777;
  font-size: 0.9rem;
}

.dark-mode .original-price {
  color: #aaa;
}

.offer-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.offer-form label {
  font-weight: 500;
  color: #333;
}

.dark-mode .offer-form label {
  color: #f1f1f1;
}

.price-input {
  display: flex;
  align-items: center;
}

.price-input input {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.dark-mode .price-input input {
  background-color: #3d3d3d;
  border-color: #555;
  color: #f1f1f1;
}

.price-input span {
  margin-left: 0.5rem;
  font-weight: 500;
}

.submit-offer {
  padding: 0.8rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
}

.submit-offer:hover {
  background-color: #388E3C;
}

/* 로딩 및 오류 메시지 스타일 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dark-mode .loading-overlay {
  background-color: rgba(18, 18, 18, 0.8);
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #4CAF50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 15px;
  border-radius: 8px;
  margin: 0 2rem 2rem 2rem;
  text-align: center;
}

.dark-mode .error-message {
  background-color: #3e2222;
  color: #ff8a80;
}

.retry-button {
  background-color: #c62828;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  margin-top: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.retry-button:hover {
  background-color: #b71c1c;
}

.no-products-message {
  text-align: center;
  padding: 2rem;
  color: #777;
  font-size: 1.1rem;
}

.dark-mode .no-products-message {
  color: #aaa;
}

/* 푸터 스타일 */
.footer {
  background-color: #2c3e50;
  color: #f5f5f5;
  padding: 3rem 2rem 1rem 2rem;
  margin-top: 3rem;
  border-top: 5px solid #4CAF50;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  max-width: 1200px;
  margin: 0 auto 2rem auto;
}

.footer-section {
  flex: 1;
  min-width: 250px;
  padding: 0 1.5rem;
  margin-bottom: 1.5rem;
}

.footer-section h3 {
  font-size: 1.4rem;
  margin-bottom: 1.2rem;
  color: #4CAF50;
  position: relative;
  padding-bottom: 0.8rem;
}

.footer-section h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 3px;
  background-color: #4CAF50;
  border-radius: 3px;
}

.footer-section h4 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: #4CAF50;
}

.footer-section p {
  margin-bottom: 1.2rem;
  line-height: 1.6;
  color: #ccc;
}

.social-links {
  display: flex;
  gap: 1rem;
}

.social-link {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 40px;
  background-color: #4CAF50;
  color: white;
  border-radius: 50%;
  text-decoration: none;
  transition: all 0.3s;
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
}

.social-link:hover {
  background-color: #388E3C;
  transform: translateY(-3px);
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.3);
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 0.8rem;
  transition: transform 0.2s;
}

.footer-section ul li:hover {
  transform: translateX(5px);
}

.footer-section ul li a {
  color: #ccc;
  text-decoration: none;
  transition: color 0.3s;
  display: flex;
  align-items: center;
}

.footer-section ul li a::before {
  content: '›';
  margin-right: 8px;
  color: #4CAF50;
  font-size: 1.2rem;
}

.footer-section ul li a:hover {
  color: #4CAF50;
}

.copyright {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid #3a4a5e;
  color: #aaa;
  font-size: 0.9rem;
  max-width: 1200px;
  margin: 0 auto;
}

.copyright p {
  margin: 0;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .header {
    padding: 1rem;
    flex-direction: column;
    gap: 1rem;
  }
  
  .main-nav {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .hero-content h1 {
    font-size: 2rem;
  }
  
  .filter-content {
    grid-template-columns: 1fr;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
  
  .footer-content {
    flex-direction: column;
  }
  
  .footer-section {
    padding: 0;
    margin-bottom: 2rem;
  }
}
</style>