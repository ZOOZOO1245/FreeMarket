<template>
  <div class="review-notice-system" :class="{ 'dark-mode': isDarkMode }">
    <div class="container">
      <!-- 탭 메뉴 -->
      <div class="tab-menu">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          class="tab-button" 
          :class="{ active: activeTab === tab.id }" 
          @click="activeTab = tab.id"
        >
          <i :class="tab.icon"></i> {{ tab.name }}
        </button>
      </div>

      <!-- 공지사항 섹션 -->
      <div v-if="activeTab === 'notice'" class="notice-section">
        <h2 class="section-title">공지사항</h2>
        
        <div class="notice-list">
          <div 
            v-for="notice in paginatedNotices" 
            :key="notice.id" 
            class="notice-item"
            :class="{ 'notice-expanded': expandedNoticeId === notice.id }"
          >
            <div class="notice-header" @click="toggleNotice(notice.id)">
              <div class="notice-badge" v-if="notice.important">중요</div>
              <h3 class="notice-title">{{ notice.title }}</h3>
              <div class="notice-date">{{ formatDate(notice.date) }}</div>
              <i class="fas" :class="expandedNoticeId === notice.id ? 'fa-chevron-up' : 'fa-chevron-down'"></i>
            </div>
            <div class="notice-content" v-show="expandedNoticeId === notice.id">
              <p v-html="notice.content"></p>
            </div>
          </div>
        </div>
        
        <Pagination 
          v-if="totalNoticePages > 1"
          :current-page="currentNoticePage"
          :total-pages="totalNoticePages"
          @prev="currentNoticePage--"
          @next="currentNoticePage++"
        />
      </div>

      <!-- 상품 리뷰 섹션 -->
      <div v-if="activeTab === 'reviews'" class="reviews-section">
        <h2 class="section-title">상품 리뷰</h2>
        
        <!-- 상품 선택 -->
        <div class="product-selector">
          <label for="product-select">상품 선택</label>
          <select 
            id="product-select" 
            v-model="selectedProductId" 
            @change="loadProductReviews"
          >
            <option value="">상품을 선택하세요</option>
            <option 
              v-for="product in products" 
              :key="product.id" 
              :value="product.id"
            >
              {{ product.name }}
            </option>
          </select>
        </div>
        
        <!-- 선택된 상품 정보 -->
        <div v-if="selectedProductId" class="selected-product-info">
          <div class="product-image">
            <img :src="selectedProduct.image" :alt="selectedProduct.name">
          </div>
          <div class="product-details">
            <h3>{{ selectedProduct.name }}</h3>
            <p class="product-price">{{ formatPrice(selectedProduct.price) }}</p>
          </div>
        </div>
        
        <!-- 리뷰 통계 -->
        <div v-if="selectedProductId && productReviews.length > 0" class="review-stats">
          <div class="rating-summary">
            <div class="average-rating">
              <div class="rating-number">{{ averageRating.toFixed(1) }}</div>
              <StarRating :rating="Math.round(averageRating)" :editable="false" />
              <div class="total-reviews">{{ productReviews.length }}개 리뷰</div>
            </div>
            <div class="rating-bars">
              <div 
                v-for="score in [5, 4, 3, 2, 1]" 
                :key="score" 
                class="rating-bar-item"
              >
                <div class="rating-label">{{ score }}점</div>
                <div class="rating-bar">
                  <div 
                    class="rating-bar-fill" 
                    :style="{ width: calculateRatingPercentage(score) + '%' }"
                  ></div>
                </div>
                <div class="rating-count">{{ countRatingsByScore(score) }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 리뷰 작성 폼 -->
        <div v-if="selectedProductId && canReview" class="review-form">
          <h3>리뷰 작성</h3>
          <div class="rating-input">
            <span>평점</span>
            <StarRating :rating="newReview.rating" :editable="true" @update:rating="val => newReview.rating = val" />
          </div>
          <div class="form-group">
            <label for="review-title">제목</label>
            <input 
              id="review-title" 
              type="text" 
              v-model="newReview.title" 
              placeholder="리뷰 제목을 입력하세요"
            >
          </div>
          <div class="form-group">
            <label for="review-content">내용</label>
            <textarea 
              id="review-content" 
              v-model="newReview.content" 
              placeholder="리뷰 내용을 입력하세요"
            ></textarea>
            <div class="char-counter">{{ newReview.content.length }}/500</div>
          </div>
          <div class="image-upload">
            <label class="upload-button" for="review-images">
              <i class="fas fa-camera"></i> 사진 첨부 (최대 3장)
            </label>
            <input 
              id="review-images" 
              type="file" 
              accept="image/*" 
              multiple 
              @change="handleImageUpload"
            >
            <div class="image-preview" v-if="newReview.images.length > 0">
              <div 
                v-for="(image, index) in newReview.images" 
                :key="index" 
                class="preview-item"
              >
                <img :src="image" alt="Preview">
                <button type="button" class="remove-image" @click="removeImage(index)">
                  <i class="fas fa-times"></i>
                </button>
              </div>
            </div>
          </div>
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="resetReviewForm">취소</button>
            <button 
              type="button" 
              class="btn-submit" 
              :disabled="!canSubmitReview"
              @click="submitReview"
            >
              등록
            </button>
          </div>
        </div>
        
        <!-- 리뷰 목록 -->
        <div class="review-list">
          <h3 v-if="selectedProductId">리뷰 목록</h3>
          
          <div v-if="selectedProductId && productReviews.length === 0" class="no-reviews">
            <p>아직 작성된 리뷰가 없습니다.</p>
          </div>
          
          <div 
            v-for="review in paginatedReviews" 
            :key="review.id" 
            class="review-item"
          >
            <div class="review-header">
              <div class="reviewer-info">
                <div class="reviewer-avatar">
                  <img :src="review.user.avatar" :alt="review.user.name">
                </div>
                <div class="reviewer-name">{{ review.user.name }}</div>
              </div>
              <StarRating :rating="review.rating" :editable="false" />
            </div>
            <div class="review-body">
              <h4 class="review-title">{{ review.title }}</h4>
              <p class="review-content">{{ review.content }}</p>
              <div class="review-images" v-if="review.images && review.images.length > 0">
                <img 
                  v-for="(image, index) in review.images" 
                  :key="index" 
                  :src="image" 
                  alt="Review image"
                  @click="openImageViewer(review.images, index)"
                >
              </div>
            </div>
            <div class="review-footer">
              <div class="review-date">{{ formatDate(review.date) }}</div>
              <button 
                type="button" 
                class="btn-helpful" 
                :class="{ active: hasMarkedHelpful(review.id) }"
                :disabled="hasMarkedHelpful(review.id)"
                @click="markReviewHelpful(review.id)"
              >
                <i class="fas fa-thumbs-up"></i> 도움됨 {{ review.helpfulCount }}
              </button>
            </div>
          </div>
          
          <Pagination 
            v-if="selectedProductId && totalReviewPages > 1"
            :current-page="currentReviewPage"
            :total-pages="totalReviewPages"
            @prev="currentReviewPage--"
            @next="currentReviewPage++"
          />
        </div>
        
        <!-- 이미지 뷰어 모달 -->
        <ImageViewer 
          v-if="imageViewer.isOpen" 
          :images="imageViewer.images"
          :current-index="imageViewer.currentIndex"
          @close="closeImageViewer"
          @navigate="navigateImage"
        />
      </div>

      <!-- 내 리뷰 섹션 -->
      <div v-if="activeTab === 'my-reviews'" class="my-reviews-section">
        <h2 class="section-title">내 리뷰</h2>
        
        <div v-if="myReviews.length === 0" class="no-reviews">
          <p>작성한 리뷰가 없습니다.</p>
        </div>
        
        <div v-else class="my-review-list">
          <div 
            v-for="review in myReviews" 
            :key="review.id" 
            class="my-review-item"
          >
            <div class="product-info">
              <img :src="getProductById(review.productId).image" :alt="getProductById(review.productId).name">
              <div>
                <h4>{{ getProductById(review.productId).name }}</h4>
                <StarRating :rating="review.rating" :editable="false" />
              </div>
            </div>
            <div class="review-content-wrapper">
              <h4>{{ review.title }}</h4>
              <p>{{ review.content }}</p>
              <div class="review-date">{{ formatDate(review.date) }}</div>
            </div>
            <div class="review-actions">
              <button type="button" class="btn-edit" @click="editReview(review)">
                <i class="fas fa-edit"></i> 수정
              </button>
              <button type="button" class="btn-delete" @click="deleteReview(review.id)">
                <i class="fas fa-trash"></i> 삭제
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <button @click="toggleDarkMode" class="theme-toggle">
      <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
    </button>
  </div>
</template>

<script>
// 컴포넌트 정의
const StarRating = {
  props: {
    rating: {
      type: Number,
      default: 0
    },
    editable: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    setRating(value) {
      if (this.editable) {
        this.$emit('update:rating', value);
      }
    }
  },
  template: `
    <div class="star-rating">
      <i 
        v-for="star in 5" 
        :key="star" 
        class="fas" 
        :class="star <= rating ? 'fa-star' : 'fa-star-o'"
        @click="setRating(star)"
        :style="{ cursor: editable ? 'pointer' : 'default' }"
      ></i>
    </div>
  `
};

const Pagination = {
  props: {
    currentPage: Number,
    totalPages: Number
  },
  template: `
    <div class="pagination">
      <button 
        class="pagination-button" 
        :disabled="currentPage === 1"
        @click="$emit('prev')"
      >
        <i class="fas fa-chevron-left"></i>
      </button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button 
        class="pagination-button" 
        :disabled="currentPage === totalPages"
        @click="$emit('next')"
      >
        <i class="fas fa-chevron-right"></i>
      </button>
    </div>
  `
};

const ImageViewer = {
  props: {
    images: Array,
    currentIndex: Number
  },
  template: `
    <div class="image-viewer" @click="$emit('close')">
      <div class="image-viewer-content" @click.stop>
        <button type="button" class="close-viewer" @click="$emit('close')">
          <i class="fas fa-times"></i>
        </button>
        <div class="image-container">
          <img :src="images[currentIndex]" alt="Enlarged image">
        </div>
        <div class="image-navigation">
          <button 
            type="button" 
            class="nav-button" 
            :disabled="currentIndex === 0"
            @click="$emit('navigate', -1)"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <div class="image-counter">
            {{ currentIndex + 1 }} / {{ images.length }}
          </div>
          <button 
            type="button" 
            class="nav-button" 
            :disabled="currentIndex === images.length - 1"
            @click="$emit('navigate', 1)"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>
  `
};

export default {
  components: {
    StarRating,
    Pagination,
    ImageViewer
  },
  data() {
    return {
      activeTab: 'notice',
      isDarkMode: false,
      tabs: [
        { id: 'notice', name: '공지사항', icon: 'fas fa-bullhorn' },
        { id: 'reviews', name: '상품 리뷰', icon: 'fas fa-star' },
        { id: 'my-reviews', name: '내 리뷰', icon: 'fas fa-pencil-alt' }
      ],
      
      // 공지사항 관련 데이터
      notices: [
        {
          id: 1,
          title: '서비스 이용약관 개정 안내',
          content: '안녕하세요. FreeMarket입니다.<br><br>서비스 이용약관이 2023년 6월 1일부터 개정됩니다. 주요 변경사항은 다음과 같습니다.<br><br>1. 개인정보 처리방침 변경<br>2. 서비스 이용 규정 강화<br>3. 결제 정책 변경<br><br>자세한 내용은 홈페이지에서 확인하실 수 있습니다.',
          date: '2023-05-15',
          important: true
        },
        {
          id: 2,
          title: '5월 휴무 안내',
          content: '안녕하세요. FreeMarket입니다.<br><br>5월 5일(어린이날)과 5월 27일(부처님 오신 날)은 고객센터 휴무입니다. 해당 기간 문의사항은 이메일로 접수해주시기 바랍니다.',
          date: '2023-05-01',
          important: false
        },
        {
          id: 3,
          title: '신규 카테고리 오픈 안내',
          content: '안녕하세요. FreeMarket입니다.<br><br>새로운 카테고리 "홈 가드닝"이 오픈되었습니다. 다양한 식물과 가드닝 용품을 만나보세요!',
          date: '2023-04-20',
          important: false
        },
        {
          id: 4,
          title: '시스템 점검 안내',
          content: '안녕하세요. FreeMarket입니다.<br><br>서비스 안정화를 위한 시스템 점검이 진행됩니다.<br>일시: 2023년 4월 15일 새벽 2시 ~ 5시<br>해당 시간에는 서비스 이용이 제한될 수 있습니다.',
          date: '2023-04-10',
          important: true
        },
        {
          id: 5,
          title: '봄맞이 이벤트 안내',
          content: '안녕하세요. FreeMarket입니다.<br><br>봄맞이 특별 이벤트를 진행합니다. 4월 한 달간 모든 상품 배송비 무료! 많은 이용 바랍니다.',
          date: '2023-04-01',
          important: false
        }
      ],
      expandedNoticeId: null,
      currentNoticePage: 1,
      noticesPerPage: 3,
      
      // 상품 관련 데이터
      products: [
        {
          id: 1,
          name: '아이폰 14 Pro 128GB',
          price: 1350000,
          image: 'https://via.placeholder.com/100x100?text=iPhone'
        },
        {
          id: 2,
          name: '갤럭시 S23 Ultra 256GB',
          price: 1450000,
          image: 'https://via.placeholder.com/100x100?text=Galaxy'
        },
        {
          id: 3,
          name: '맥북 프로 M2 13인치',
          price: 1850000,
          image: 'https://via.placeholder.com/100x100?text=MacBook'
        },
        {
          id: 4,
          name: '에어팟 프로 2세대',
          price: 329000,
          image: 'https://via.placeholder.com/100x100?text=AirPods'
        },
        {
          id: 5,
          name: '갤럭시 워치 5 Pro',
          price: 499000,
          image: 'https://via.placeholder.com/100x100?text=Watch'
        }
      ],
      selectedProductId: '',
      
      // 리뷰 관련 데이터
      productReviews: [],
      currentReviewPage: 1,
      reviewsPerPage: 3,
      
      // 새 리뷰 작성 데이터
      newReview: {
        title: '',
        content: '',
        rating: 0,
        images: []
      },
      
      // 이미지 뷰어 데이터
      imageViewer: {
        isOpen: false,
        images: [],
        currentIndex: 0
      },
      
      // 사용자 데이터
      currentUser: {
        id: 'user123',
        name: '홍길동',
        avatar: 'https://via.placeholder.com/50x50?text=User'
      },
      
      // 도움됨 표시한 리뷰 ID 목록
      helpfulReviews: []
    };
  },
  computed: {
    // 공지사항 페이지네이션
    paginatedNotices() {
      const start = (this.currentNoticePage - 1) * this.noticesPerPage;
      const end = start + this.noticesPerPage;
      return this.notices.slice(start, end);
    },
    totalNoticePages() {
      return Math.ceil(this.notices.length / this.noticesPerPage);
    },
    
    // 선택된 상품 정보
    selectedProduct() {
      return this.products.find(p => p.id === this.selectedProductId) || {};
    },
    
    // 리뷰 페이지네이션
    paginatedReviews() {
      const start = (this.currentReviewPage - 1) * this.reviewsPerPage;
      const end = start + this.reviewsPerPage;
      return this.productReviews.slice(start, end);
    },
    totalReviewPages() {
      return Math.ceil(this.productReviews.length / this.reviewsPerPage);
    },
    
    // 평균 평점
    averageRating() {
      if (this.productReviews.length === 0) return 0;
      const sum = this.productReviews.reduce((total, review) => total + review.rating, 0);
      return sum / this.productReviews.length;
    },
    
    // 내 리뷰 목록
    myReviews() {
      const allReviews = [];
      this.products.forEach(product => {
        const productReviews = JSON.parse(localStorage.getItem(`reviews_${product.id}`) || '[]');
        allReviews.push(...productReviews.filter(review => review.user.id === this.currentUser.id));
      });
      return allReviews;
    },
    
    // 리뷰 작성 가능 여부
    canReview() {
      return !this.productReviews.some(review => review.user.id === this.currentUser.id);
    },
    
    // 리뷰 제출 가능 여부
    canSubmitReview() {
      return this.newReview.title.trim() !== '' && 
             this.newReview.content.trim() !== '' && 
             this.newReview.rating > 0;
    }
  },
  methods: {
    // 공지사항 관련 메소드
    toggleNotice(id) {
      this.expandedNoticeId = this.expandedNoticeId === id ? null : id;
    },
    
    // 날짜 포맷팅
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric', month: 'long', day: 'numeric'
      });
    },
    
    // 가격 포맷팅
    formatPrice(price) {
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') + '원';
    },
    
    // 상품 리뷰 로드
    loadProductReviews() {
      if (!this.selectedProductId) return;
      this.productReviews = JSON.parse(localStorage.getItem(`reviews_${this.selectedProductId}`) || '[]');
      this.currentReviewPage = 1;
      this.resetReviewForm();
    },
    
    // 평점별 리뷰 수 계산
    countRatingsByScore(score) {
      return this.productReviews.filter(review => review.rating === score).length;
    },
    
    // 평점 퍼센트 계산
    calculateRatingPercentage(score) {
      if (this.productReviews.length === 0) return 0;
      const count = this.countRatingsByScore(score);
      return (count / this.productReviews.length) * 100;
    },
    
    // 이미지 업로드 처리
    handleImageUpload(event) {
      const files = event.target.files;
      if (!files.length) return;
      
      if (this.newReview.images.length + files.length > 3) {
        alert('이미지는 최대 3개까지 첨부할 수 있습니다.');
        return;
      }
      
      Array.from(files).forEach(file => {
        const reader = new FileReader();
        reader.onload = e => {
          this.newReview.images.push(e.target.result);
        };
        reader.readAsDataURL(file);
      });
    },
    
    // 이미지 제거
    removeImage(index) {
      this.newReview.images.splice(index, 1);
    },
    
    // 리뷰 폼 초기화
    resetReviewForm() {
      this.newReview = {
        title: '',
        content: '',
        rating: 0,
        images: []
      };
    },
    
    // 리뷰 제출
    submitReview() {
      if (!this.canSubmitReview) return;
      
      const newReview = {
        id: Date.now().toString(),
        productId: this.selectedProductId,
        title: this.newReview.title,
        content: this.newReview.content,
        rating: this.newReview.rating,
        images: this.newReview.images,
        date: new Date().toISOString(),
        user: this.currentUser,
        helpfulCount: 0
      };
      
      this.productReviews.unshift(newReview);
      localStorage.setItem(`reviews_${this.selectedProductId}`, JSON.stringify(this.productReviews));
      this.resetReviewForm();
      alert('리뷰가 등록되었습니다.');
    },
    
    // 리뷰 도움됨 표시
    markReviewHelpful(reviewId) {
      if (this.hasMarkedHelpful(reviewId)) return;
      
      const reviewIndex = this.productReviews.findIndex(r => r.id === reviewId);
      if (reviewIndex === -1) return;
      
      this.productReviews[reviewIndex].helpfulCount++;
      this.helpfulReviews.push(reviewId);
      localStorage.setItem('helpfulReviews', JSON.stringify(this.helpfulReviews));
      localStorage.setItem(`reviews_${this.selectedProductId}`, JSON.stringify(this.productReviews));
    },
    
    // 도움됨 표시 여부 확인
    hasMarkedHelpful(reviewId) {
      return this.helpfulReviews.includes(reviewId);
    },
    
    // 이미지 뷰어 열기
    openImageViewer(images, index) {
      this.imageViewer.images = images;
      this.imageViewer.currentIndex = index;
      this.imageViewer.isOpen = true;
      document.body.style.overflow = 'hidden';
    },
    
    // 이미지 뷰어 닫기
    closeImageViewer() {
      this.imageViewer.isOpen = false;
      document.body.style.overflow = '';
    },
    
    // 이미지 네비게이션
    navigateImage(direction) {
      const newIndex = this.imageViewer.currentIndex + direction;
      if (newIndex >= 0 && newIndex < this.imageViewer.images.length) {
        this.imageViewer.currentIndex = newIndex;
      }
    },
    
    // 상품 ID로 상품 정보 가져오기
    getProductById(productId) {
      return this.products.find(p => p.id === productId) || 
             { name: '삭제된 상품', image: 'https://via.placeholder.com/100x100?text=Deleted' };
    },
    
        // 리뷰 수정
    // eslint-disable-next-line no-unused-vars
    editReview(review) {
      alert('리뷰 수정 기능은 준비 중입니다.');
    },
    
    // 리뷰 삭제
    deleteReview(reviewId) {
      if (!confirm('정말로 이 리뷰를 삭제하시겠습니까?')) return;
      
      const review = this.myReviews.find(r => r.id === reviewId);
      if (!review) return;
      
      const productReviews = JSON.parse(localStorage.getItem(`reviews_${review.productId}`) || '[]');
      const updatedReviews = productReviews.filter(r => r.id !== reviewId);
      
      localStorage.setItem(`reviews_${review.productId}`, JSON.stringify(updatedReviews));
      
      if (review.productId === this.selectedProductId) {
        this.productReviews = updatedReviews;
      }
      
      alert('리뷰가 삭제되었습니다.');
    },
    
    // 다크 모드 토글
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode;
      localStorage.setItem('darkMode', this.isDarkMode);
    }
  },
  mounted() {
    const savedDarkMode = localStorage.getItem('darkMode');
    if (savedDarkMode !== null) {
      this.isDarkMode = savedDarkMode === 'true';
    }
    
    const savedHelpfulReviews = localStorage.getItem('helpfulReviews');
    if (savedHelpfulReviews) {
      this.helpfulReviews = JSON.parse(savedHelpfulReviews);
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap');

.review-notice-system {
  font-family: 'Noto Sans KR', sans-serif;
  background-color: #f8f9fa;
  min-height: 100vh;
  padding: 40px 0;
  transition: all 0.3s ease;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  position: relative;
}

/* 탭 메뉴 스타일 */
.tab-menu {
  display: flex;
  border-bottom: 1px solid #eee;
  margin-bottom: 30px;
}

.tab-button {
  padding: 15px 25px;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.tab-button.active {
  color: #4CAF50;
}

.tab-button.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #4CAF50;
}

.tab-button i {
  margin-right: 8px;
}

/* 섹션 제목 스타일 */
.section-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  color: #333;
}

/* 공지사항 스타일 */
.notice-item {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 15px;
  overflow: hidden;
}

.notice-header {
  padding: 15px 20px;
  background-color: #f9f9f9;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.notice-expanded .notice-header {
  background-color: #f0f8f0;
}

.notice-badge {
  background-color: #ff5252;
  color: white;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 15px;
}

.notice-title {
  flex: 1;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.notice-date {
  color: #888;
  font-size: 14px;
  margin-right: 15px;
}

.notice-content {
  padding: 20px;
  background-color: #fff;
  border-top: 1px solid #eee;
  line-height: 1.6;
}

/* 상품 선택 스타일 */
.product-selector {
  margin-bottom: 25px;
}

.product-selector label {
  display: block;
  margin-bottom: 10px;
  font-weight: 500;
  color: #333;
}

.product-selector select {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  color: #333;
  background-color: #fff;
}

/* 선택된 상품 정보 스타일 */
.selected-product-info {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 25px;
}

.product-image {
  width: 80px;
  height: 80px;
  margin-right: 20px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.product-details h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #333;
}

.product-price {
  font-size: 16px;
  color: #4CAF50;
  font-weight: 500;
  margin: 0;
}

/* 리뷰 통계 스타일 */
.review-stats {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 25px;
}

.rating-summary {
  display: flex;
  align-items: center;
}

.average-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-right: 30px;
  margin-right: 30px;
  border-right: 1px solid #ddd;
}

.rating-number {
  font-size: 48px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.star-rating {
  margin-bottom: 10px;
}

.star-rating i {
  color: #FFD700;
  font-size: 20px;
  margin-right: 2px;
}

.total-reviews {
  font-size: 14px;
  color: #666;
}

.rating-bars {
  flex: 1;
}

.rating-bar-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.rating-label {
  width: 40px;
  font-size: 14px;
  color: #666;
}

.rating-bar {
  flex: 1;
  height: 8px;
  background-color: #eee;
  border-radius: 4px;
  margin: 0 15px;
  overflow: hidden;
}

.rating-bar-fill {
  height: 100%;
  background-color: #4CAF50;
  border-radius: 4px;
}

.rating-count {
  width: 30px;
  font-size: 14px;
  color: #666;
  text-align: right;
}

/* 리뷰 작성 폼 스타일 */
.review-form {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 25px;
}

.review-form h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
}

.rating-input {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.rating-input span {
  margin-right: 15px;
  font-weight: 500;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  color: #333;
}

.form-group textarea {
  height: 150px;
  resize: vertical;
}

.char-counter {
  position: absolute;
  right: 10px;
  bottom: 10px;
  font-size: 12px;
  color: #888;
}

.image-upload {
  margin-bottom: 15px;
}

.upload-button {
  display: inline-block;
  padding: 10px 20px;
  background-color: #f0f8f0;
  color: #4CAF50;
  border: 1px solid #4CAF50;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-button:hover {
  background-color: #e0f0e0;
}

.upload-button i {
  margin-right: 8px;
}

input[type="file"] {
  display: none;
}

.image-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.preview-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.remove-image {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px;
  height: 24px;
  background-color: #ff5252;
  color: white;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 20px;
}

.btn-cancel,
.btn-submit {
  padding: 10px 25px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}

.btn-cancel {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.btn-submit {
  background-color: #4CAF50;
  color: white;
  border: none;
}

.btn-submit:disabled {
  background-color: #a5d6a7;
  cursor: not-allowed;
}

/* 리뷰 목록 스타일 */
.review-list {
  margin-top: 30px;
}

.no-reviews {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 30px;
  text-align: center;
  color: #888;
}

.review-item {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
}

.reviewer-info {
  display: flex;
  align-items: center;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 15px;
}

.reviewer-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.reviewer-name {
  font-weight: 500;
  color: #333;
}

.review-body {
  padding: 20px;
}

.review-title {
  font-size: 16px;
  font-weight: 600;
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.review-content {
  line-height: 1.6;
  margin-bottom: 15px;
  color: #555;
}

.review-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 15px;
}

.review-images img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
}

.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f9f9f9;
  border-top: 1px solid #eee;
}

.review-date {
  color: #888;
  font-size: 14px;
}

.btn-helpful {
  background: none;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 5px 15px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.btn-helpful.active {
  background-color: #f0f8f0;
  color: #4CAF50;
  border-color: #4CAF50;
}

/* 내 리뷰 스타일 */
.my-review-item {
  display: flex;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
}

.product-info {
  width: 200px;
  padding: 20px;
  background-color: #f9f9f9;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.product-info img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  margin-bottom: 10px;
}

.review-content-wrapper {
  flex: 1;
  padding: 20px;
}

.review-actions {
  width: 100px;
  padding: 20px;
  background-color: #f9f9f9;
  border-left: 1px solid #eee;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.btn-edit,
.btn-delete {
  width: 100%;
  padding: 8px 0;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  text-align: center;
}

.btn-edit {
  background-color: #f0f8f0;
  color: #4CAF50;
  border: 1px solid #4CAF50;
}

.btn-delete {
  background-color: #fff0f0;
  color: #ff5252;
  border: 1px solid #ff5252;
}

/* 페이지네이션 스타일 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
}

.pagination-button {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f5f5f5;
  border: none;
  color: #666;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.page-info {
  margin: 0 15px;
  font-size: 14px;
  color: #666;
}

/* 이미지 뷰어 스타일 */
.image-viewer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-viewer-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
}

.close-viewer {
  position: absolute;
  top: -40px;
  right: 0;
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
}

.image-container img {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
}

.image-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.nav-button {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-button:disabled {
  background-color: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.5);
  cursor: not-allowed;
}

.image-counter {
  color: white;
  font-size: 14px;
}

/* 테마 토글 버튼 */
.theme-toggle {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #4CAF50;
  color: white;
  border: none;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  z-index: 100;
}

/* 다크 모드 스타일 */
.dark-mode {
  background-color: #121212;
  color: #e0e0e0;
}

.dark-mode .container {
  background-color: #1e1e1e;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
}

.dark-mode .tab-button {
  color: #b0b0b0;
}

.dark-mode .tab-button.active {
  color: #6abf69;
}

.dark-mode .section-title {
  color: #e0e0e0;
  border-bottom-color: #333;
}

.dark-mode .notice-item,
.dark-mode .review-item,
.dark-mode .my-review-item {
  border-color: #333;
}

.dark-mode .notice-header,
.dark-mode .review-header,
.dark-mode .review-footer,
.dark-mode .product-info,
.dark-mode .review-actions {
  background-color: #2a2a2a;
  border-color: #333;
}

.dark-mode .notice-content {
  background-color: #1e1e1e;
  border-top-color: #333;
}

.dark-mode .product-selector select,
.dark-mode .form-group input,
.dark-mode .form-group textarea {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

.dark-mode .selected-product-info,
.dark-mode .review-stats,
.dark-mode .review-form,
.dark-mode .no-reviews {
  background-color: #2a2a2a;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .container {
    padding: 20px;
    margin: 0 15px;
  }
  
  .tab-button {
    padding: 12px 15px;
    font-size: 14px;
  }
  
  .my-review-item {
    flex-direction: column;
  }
  
  .product-info {
    width: 100%;
    flex-direction: row;
    border-right: none;
    border-bottom: 1px solid #eee;
  }
  
  .review-actions {
    width: 100%;
    flex-direction: row;
    border-left: none;
    border-top: 1px solid #eee;
  }
  
  .btn-edit,
  .btn-delete {
    width: auto;
    flex: 1;
  }
}
</style>