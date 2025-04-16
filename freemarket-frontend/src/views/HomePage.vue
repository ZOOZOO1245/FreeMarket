<template>
  <div class="home" :class="{ 'dark-mode': isDarkMode }">
    <header class="header">
      <div class="logo-container">
        <img src="@/assets/homepage-image.jpg" alt="로고" class="logo">
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

    <section class="categories-section">
      <h2 class="section-title">카테고리</h2>
      <div class="categories-grid">
        <div v-for="category in categories" :key="category.id" class="category-item">
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
            <option value="책">책</option>
            <option value="가전">가전</option>
            <option value="의류">의류</option>
            <option value="스포츠/레저">스포츠/레저</option>
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
      <div class="product-grid">
        <div v-for="product in filteredProducts" :key="product.id" class="product-card">
          <div class="product-image-container">
            <img :src="product.image" :alt="product.title" class="product-image">
            <div class="product-badge" v-if="product.isNew">NEW</div>
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
              <button class="action-btn chat-btn"><i class="fas fa-comment"></i> 채팅하기</button>
              <button class="action-btn offer-btn"><i class="fas fa-tag"></i> 가격제안</button>
            </div>
          </div>
        </div>
      </div>
      <div class="view-more-container">
        <button class="view-more-button">더 보기</button>
      </div>
    </div>

    <div class="main-content">
      <h2 class="section-title">최근 등록된 상품</h2>
      <div class="product-grid">
        <div v-for="product in recentProducts" :key="product.id" class="product-card">
          <div class="product-image-container">
            <img :src="product.image" :alt="product.title" class="product-image">
            <div class="product-badge" v-if="product.isNew">NEW</div>
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
              <button class="action-btn chat-btn"><i class="fas fa-comment"></i> 채팅하기</button>
              <button class="action-btn offer-btn"><i class="fas fa-tag"></i> 가격제안</button>
            </div>
          </div>
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
export default {
  data() {
    return {
      isDarkMode: false,
      sortBy: 'popular',
      selectedCategory: '',
      selectedPrice: '',
      selectedLocation: '',
      categories: [
        { id: 1, name: '디지털/가전', icon: 'fas fa-laptop' },
        { id: 2, name: '가구/인테리어', icon: 'fas fa-couch' },
        { id: 3, name: '의류/패션', icon: 'fas fa-tshirt' },
        { id: 4, name: '도서/티켓/취미', icon: 'fas fa-book' },
        { id: 5, name: '생활/가공식품', icon: 'fas fa-utensils' },
        { id: 6, name: '스포츠/레저', icon: 'fas fa-running' },
        { id: 7, name: '뷰티/미용', icon: 'fas fa-spa' },
        { id: 8, name: '반려동물용품', icon: 'fas fa-paw' }
      ],
      products: [
        {
          id: 1,
          title: '아이폰 13',
          price: 800000,
          category: '가전',
          location: '교내',
          image: 'https://source.unsplash.com/300x200/?iphone',
          isLiked: false,
          isNew: true,
          createdAt: new Date(2023, 4, 15),
          popularity: 24
        },
        {
          id: 2,
          title: '맥북 프로',
          price: 1500000,
          category: '가전',
          location: '주변',
          image: 'https://source.unsplash.com/300x200/?macbook',
          isLiked: false,
          isNew: false,
          createdAt: new Date(2023, 4, 14),
          popularity: 18
        },
        {
          id: 3,
          title: '에어팟 프로',
          price: 250000,
          category: '가전',
          location: '교내',
          image: 'https://source.unsplash.com/300x200/?airpods',
          isLiked: false,
          isNew: false,
          createdAt: new Date(2023, 4, 16),
          popularity: 15
        },
        {
          id: 4,
          title: '갤럭시 S21',
          price: 900000,
          category: '가전',
          location: '주변',
          image: 'https://source.unsplash.com/300x200/?galaxy',
          isLiked: false,
          isNew: false,
          createdAt: new Date(2023, 4, 13),
          popularity: 12
        },
        {
          id: 5,
          title: 'LG 그램',
          price: 1200000,
          category: '가전',
          location: '교내',
          image: 'https://source.unsplash.com/300x200/?laptop',
          isLiked: false,
          isNew: false,
          createdAt: new Date(2023, 4, 12),
          popularity: 10
        },
        {
          id: 6,
          title: '아이패드 프로',
          price: 1000000,
          category: '가전',
          location: '주변',
          image: 'https://source.unsplash.com/300x200/?ipad',
          isLiked: false,
          isNew: true,
          createdAt: new Date(2023, 4, 17),
          popularity: 8
        }
      ],
      recentProducts: [
        {
          id: 7,
          title: '나이키 에어포스 1',
          price: 90000,
          category: '의류',
          location: '교내',
          image: 'https://source.unsplash.com/300x200/?nike',
          isLiked: false,
          isNew: true,
          createdAt: new Date(2023, 4, 17),
          popularity: 7
        },
        {
          id: 8,
          title: '해리포터 시리즈 전권',
          price: 80000,
          category: '책',
          location: '주변',
          image: 'https://source.unsplash.com/300x200/?harrypotter',
          isLiked: false,
          isNew: true,
          createdAt: new Date(2023, 4, 17),
          popularity: 5
        },
        {
          id: 9,
          title: '캠핑 텐트 4인용',
          price: 120000,
          category: '스포츠/레저',
          location: '교내',
          image: 'https://source.unsplash.com/300x200/?tent',
          isLiked: false,
          isNew: true,
          createdAt: new Date(2023, 4, 17),
          popularity: 3
        },
        {
          id: 10,
          title: '다이슨 에어랩',
          price: 400000,
          category: '가전',
          location: '주변',
          image: 'https://source.unsplash.com/300x200/?dyson',
          isLiked: false,
          isNew: true,
          createdAt: new Date(2023, 4, 17),
          popularity: 6
        }
      ]
    }
  },
  computed: {
    filteredProducts() {
      let result = this.products.filter(product => {
        const matchesCategory = this.selectedCategory ? product.category === this.selectedCategory : true;
        const matchesPrice = this.selectedPrice ? this.isPriceInRange(product.price) : true;
        const matchesLocation = this.selectedLocation ? product.location === this.selectedLocation : true;
        return matchesCategory && matchesPrice && matchesLocation;
      });

      // 정렬 적용
      switch(this.sortBy) {
        case 'recent':
          result.sort((a, b) => b.createdAt - a.createdAt);
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
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode;
      // 로컬 스토리지에 다크모드 설정 저장
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
    applyFilters() {
      console.log('필터 적용됨:', {
        category: this.selectedCategory,
        price: this.selectedPrice,
        location: this.selectedLocation
      });
    },
    resetFilters() {
      this.selectedCategory = '';
      this.selectedPrice = '';
      this.selectedLocation = '';
    },
    formatPrice(price) {
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },
    toggleLike(product) {
      product.isLiked = !product.isLiked;
      if (product.isLiked) {
        product.popularity += 1;
      } else {
        product.popularity -= 1;
      }
    },
    getTimeAgo(date) {
      const now = new Date();
      const diffTime = Math.abs(now - date);
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays === 0) {
        return '오늘';
      } else if (diffDays === 1) {
        return '어제';
      } else {
        return `${diffDays}일 전`;
      }
    },
    sortProducts(sortType) {
      this.sortBy = sortType;
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
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap');

/* 기본 스타일 (라이트 모드) */
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f8f9fa;
  font-family: 'Noto Sans KR', sans-serif;
  min-height: 100vh;
  color: #333;
  transition: all 0.3s ease;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  background-color: #ffffff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.logo-container {
  display: flex;
  align-items: center;
}

.logo {
  width: 40px;
  height: 40px;
  object-fit: contain;
  margin-right: 10px;
}

.site-title {
  font-size: 24px;
  font-weight: 700;
  color: #4CAF50;
  margin: 0;
  transition: color 0.3s ease;
}

.main-nav {
  display: flex;
  gap: 20px;
  align-items: center;
}

.main-nav a {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  transition: all 0.3s;
  padding: 5px 10px;
  border-radius: 4px;
}

.main-nav a:hover {
  color: #4CAF50;
  background-color: #f0f8f0;
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
}

.theme-toggle:hover {
  background-color: #f0f8f0;
  color: #4CAF50;
}

.hero-section {
  background: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)), url('https://source.unsplash.com/1200x400/?marketplace') center/cover no-repeat;
  color: white;
  text-align: center;
  padding: 100px 0;
  margin-bottom: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.hero-content {
  max-width: 700px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-content h1 {
  font-size: 3rem;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.hero-content p {
  font-size: 1.4rem;
  margin-bottom: 40px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.hero-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.cta-button {
  padding: 14px 28px;
  border-radius: 50px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
  display: inline-block;
  font-size: 16px;
}

.primary-btn {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 4px 10px rgba(76, 175, 80, 0.3);
}

.primary-btn:hover {
  background-color: #45a049;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(76, 175, 80, 0.4);
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

.categories-section {
  margin-bottom: 50px;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
  transition: color 0.3s ease;
}

.categories-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 30px;
}

.category-item {
  width: 110px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s;
}

.category-item:hover {
  transform: translateY(-5px);
}

.category-icon {
  background-color: #f0f8f0;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  color: #4CAF50;
  font-size: 28px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.category-item:hover .category-icon {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 6px 15px rgba(76, 175, 80, 0.3);
}

.category-item p {
  font-weight: 500;
  transition: color 0.3s ease;
}

.filter-section {
  margin: 30px 0;
  padding: 25px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
  transition: color 0.3s ease;
}

.reset-button {
  background: none;
  border: none;
  color: #4CAF50;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.3s;
}

.reset-button:hover {
  color: #45a049;
  text-decoration: underline;
}

.filter-content {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: flex-end;
}

.filter-group {
  flex: 1;
  min-width: 200px;
}

.filter-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
  transition: color 0.3s ease;
}

.filter-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  font-family: 'Noto Sans KR', sans-serif;
  transition: all 0.3s ease;
}

.filter-group select:focus {
  border-color: #4CAF50;
  outline: none;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.filter-button {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 10px rgba(76, 175, 80, 0.2);
}

.filter-button:hover {
  background-color: #45a049;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(76, 175, 80, 0.3);
}

.main-content {
  margin-top: 50px;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  flex-wrap: wrap;
  gap: 15px;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.sort-options span {
  color: #777;
  transition: color 0.3s ease;
}

.sort-btn {
  background: none;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #555;
  transition: all 0.3s;
}

.sort-btn:hover {
  background-color: #f0f8f0;
  color: #4CAF50;
}

.sort-btn.active {
  background-color: #4CAF50;
  color: white;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
  margin-top: 20px;
}

.product-card {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s;
  border: 1px solid #eee;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.product-image-container {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card:hover .product-image {
  transform: scale(1.08);
}

.product-badge {
  position: absolute;
  top: 15px;
  left: 15px;
  background-color: #ff6b6b;
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 700;
  z-index: 2;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.like-button {
  position: absolute;
  top: 15px;
  right: 15px;
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 2;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.like-button:hover {
  transform: scale(1.1);
  background-color: #fff;
}

.like-button i {
  color: #ff6b6b;
  font-size: 18px;
  transition: all 0.3s;
}

.like-button:hover i {
  transform: scale(1.2);
}

.product-info {
  padding: 20px;
  position: relative;
}

.product-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 600;
}

.price {
  color: #4CAF50;
  font-weight: 700;
  font-size: 20px;
  margin-bottom: 12px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  color: #777;
  font-size: 14px;
  margin-top: 15px;
}

.location i {
  margin-right: 5px;
}

.view-more-container {
  text-align: center;
  margin-top: 40px;
}

.view-more-button {
  background-color: transparent;
  border: 2px solid #4CAF50;
  color: #4CAF50;
  padding: 12px 30px;
  border-radius: 50px;
  cursor: pointer;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s;
  letter-spacing: 0.5px;
}

.view-more-button:hover {
  background-color: #4CAF50;
  color: white;
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(76, 175, 80, 0.3);
}

/* 다크 모드 스타일 */
.dark-mode {
  background-color: #121212;
  color: #e0e0e0;
}

.dark-mode .header {
  background-color: #1e1e1e;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.dark-mode .site-title {
  color: #6abf69;
}

.dark-mode .main-nav a {
  color: #e0e0e0;
}

.dark-mode .main-nav a:hover {
  color: #6abf69;
  background-color: #2a2a2a;
}

.dark-mode .theme-toggle {
  color: #e0e0e0;
}

.dark-mode .theme-toggle:hover {
  background-color: #2a2a2a;
  color: #6abf69;
}

.dark-mode .hero-section {
  background: linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.7)), url('https://source.unsplash.com/1200x400/?marketplace') center/cover no-repeat;
}

.dark-mode .categories-section,
.dark-mode .filter-section,
.dark-mode .main-content {
  background-color: #1e1e1e;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.dark-mode .section-title {
  color: #e0e0e0;
}

.dark-mode .category-icon {
  background-color: #2a2a2a;
  color: #6abf69;
}

.dark-mode .category-item:hover .category-icon {
  background-color: #6abf69;
  color: #121212;
}

.dark-mode .filter-section h2 {
  color: #e0e0e0;
}

.dark-mode .filter-group label {
  color: #b0b0b0;
}

.dark-mode .filter-group select {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

.dark-mode .product-card {
  background-color: #1e1e1e;
  border: 1px solid #333;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.dark-mode .product-title {
  color: #e0e0e0;
}

.dark-mode .price {
  color: #6abf69;
}

.dark-mode .product-meta {
  color: #b0b0b0;
}

.dark-mode .view-more-button {
  border-color: #6abf69;
  color: #6abf69;
}

.dark-mode .view-more-button:hover {
  background-color: #6abf69;
  color: #121212;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    padding: 15px;
  }
  
  .logo-container {
    margin-bottom: 15px;
  }
  
  .main-nav {
    width: 100%;
    justify-content: center;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .hero-content h1 {
    font-size: 2rem;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
  
  .filter-group {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-group label {
    margin-bottom: 5px;
  }
}

@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
  
  .categories-grid {
    gap: 15px;
  }
  
  .category-item {
    width: 80px;
  }
  
  .hero-content h1 {
    font-size: 1.8rem;
  }
  
  .hero-content p {
    font-size: 1rem;
  }
}

/* 푸터 스타일 */
.footer {
  margin-top: 60px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  padding: 40px 30px;
  transition: all 0.3s ease;
}

.footer-content {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 30px;
}

.footer-section {
  flex: 1;
  min-width: 200px;
}

.footer-section h3 {
  font-size: 22px;
  color: #4CAF50;
  margin-bottom: 15px;
  font-weight: 700;
}

.footer-section h4 {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
  font-weight: 600;
}

.footer-section p {
  margin-bottom: 10px;
  color: #666;
  line-height: 1.5;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  margin-bottom: 8px;
}

.footer-section ul li a {
  color: #555;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-section ul li a:hover {
  color: #4CAF50;
}

.social-links {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: #f0f8f0;
  color: #4CAF50;
  border-radius: 50%;
  transition: all 0.3s;
}

.social-link:hover {
  background-color: #4CAF50;
  color: white;
  transform: translateY(-3px);
}

.copyright {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  text-align: center;
  color: #777;
  font-size: 14px;
}

/* 다크 모드 푸터 스타일 */
.dark-mode .footer {
  background-color: #1e1e1e;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.dark-mode .footer-section h3 {
  color: #6abf69;
}

.dark-mode .footer-section h4 {
  color: #e0e0e0;
}

.dark-mode .footer-section p {
  color: #b0b0b0;
}

.dark-mode .footer-section ul li a {
  color: #b0b0b0;
}

.dark-mode .footer-section ul li a:hover {
  color: #6abf69;
}

.dark-mode .social-link {
  background-color: #2a2a2a;
  color: #6abf69;
}

.dark-mode .social-link:hover {
  background-color: #6abf69;
  color: #121212;
}

.dark-mode .copyright {
  border-top-color: #333;
  color: #999;
}

/* 푸터 반응형 스타일 */
@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    gap: 25px;
  }
  
  .footer-section {
    min-width: 100%;
  }
}
</style>