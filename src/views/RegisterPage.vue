<template>
  <div class="register-page" :class="{ 'dark-mode': isDarkMode }">
    <div class="register-container">
      <div class="register-header">
        <h1>상품 등록</h1>
        <p>필요한 정보를 입력하여 상품을 등록해보세요</p>
      </div>
      
      <form @submit.prevent="submitProduct" class="register-form">
        <!-- 이미지 업로드 섹션 -->
        <div class="form-section">
          <h3 class="section-title">상품 이미지</h3>
          <div class="image-upload-container">
            <div 
              v-for="(image, index) in previewImages" 
              :key="index" 
              class="image-preview-item"
            >
              <img :src="image" alt="상품 이미지" class="preview-image" />
              <button 
                type="button" 
                class="remove-image-btn" 
                @click="removeImage(index)"
              >
                <i class="fas fa-times"></i>
              </button>
            </div>
            
            <label 
              v-if="previewImages.length < 5" 
              class="image-upload-label"
            >
              <input 
                type="file" 
                accept="image/*" 
                @change="handleImageUpload" 
                multiple
                ref="fileInput"
              />
              <div class="upload-icon">
                <i class="fas fa-camera"></i>
                <span>{{ previewImages.length }}/5</span>
              </div>
            </label>
          </div>
          <p class="form-hint">
            * 최대 5장까지 등록 가능합니다. 첫 번째 사진이 대표 이미지로 사용됩니다.
          </p>
        </div>
        
        <!-- 기본 정보 섹션 -->
        <div class="form-section">
          <h3 class="section-title">기본 정보</h3>
          
          <div class="form-group">
            <label for="title">제목</label>
            <div class="input-with-counter">
              <input 
                type="text" 
                id="title" 
                v-model="product.title" 
                placeholder="상품 제목을 입력해주세요" 
                maxlength="40"
                required
              />
              <span class="char-counter">{{ product.title.length }}/40</span>
            </div>
          </div>
          
          <div class="form-group">
            <label for="category">카테고리</label>
            <select id="category" v-model="product.category" required>
              <option value="" disabled selected>카테고리를 선택해주세요</option>
              <option v-for="category in categories" :key="category.value" :value="category.value">
                {{ category.name }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label>상품 상태</label>
            <div class="radio-group">
              <div 
                v-for="status in productStatuses" 
                :key="status.value" 
                class="radio-item"
              >
                <input 
                  type="radio" 
                  :id="status.value" 
                  :value="status.value" 
                  v-model="product.status" 
                  required
                />
                <label :for="status.value">{{ status.name }}</label>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label for="price">가격</label>
            <div class="price-input">
              <input 
                type="number" 
                id="price" 
                v-model="product.price" 
                placeholder="숫자만 입력해주세요" 
                min="0"
                required
              />
              <span class="price-unit">원</span>
            </div>
          </div>
          
          <div class="form-group">
            <label for="negotiable">가격 제안</label>
            <div class="toggle-switch">
              <input type="checkbox" id="negotiable" v-model="product.negotiable" />
              <label for="negotiable"></label>
              <span>{{ product.negotiable ? '가격 제안 받기' : '가격 제안 받지 않기' }}</span>
            </div>
          </div>
        </div>
        
        <!-- 상세 정보 섹션 -->
        <div class="form-section">
          <h3 class="section-title">상세 정보</h3>
          
          <div class="form-group">
            <label for="description">상품 설명</label>
            <div class="input-with-counter textarea-container">
              <textarea 
                id="description" 
                v-model="product.description" 
                placeholder="상품에 대한 자세한 정보를 입력해주세요. 구매시기, 사용감, 하자 유무 등 구매자에게 필요한 정보를 포함하면 좋아요." 
                maxlength="1000"
                required
              ></textarea>
              <span class="char-counter">{{ product.description.length }}/1000</span>
            </div>
          </div>
          
          <div class="form-group">
            <label for="location">거래 지역</label>
            <div class="location-input">
              <input 
                type="text" 
                id="location" 
                v-model="product.location" 
                placeholder="거래 희망 지역을 입력해주세요" 
                required
              />
              <button type="button" class="location-btn" @click="useCurrentLocation">
                <i class="fas fa-map-marker-alt"></i> 현재 위치
              </button>
            </div>
          </div>
          
          <div class="form-group">
            <label>배송 방법</label>
            <div class="checkbox-group">
              <div class="checkbox-item">
                <input type="checkbox" id="delivery-direct" v-model="product.deliveryMethods.direct" />
                <label for="delivery-direct">직거래</label>
              </div>
              <div class="checkbox-item">
                <input type="checkbox" id="delivery-parcel" v-model="product.deliveryMethods.parcel" />
                <label for="delivery-parcel">택배거래</label>
              </div>
            </div>
          </div>
          
          <div class="form-group" v-if="product.deliveryMethods.parcel">
            <label for="shipping">배송비</label>
            <div class="radio-group">
              <div class="radio-item">
                <input type="radio" id="shipping-included" value="included" v-model="product.shipping" />
                <label for="shipping-included">배송비 포함</label>
              </div>
              <div class="radio-item">
                <input type="radio" id="shipping-extra" value="extra" v-model="product.shipping" />
                <label for="shipping-extra">배송비 별도</label>
              </div>
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button type="button" class="btn btn-secondary" @click="goBack">취소</button>
          <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
            <span v-if="isSubmitting">
              <i class="fas fa-spinner fa-spin"></i> 등록 중...
            </span>
            <span v-else>상품 등록</span>
          </button>
        </div>
      </form>
      
      <button @click="toggleDarkMode" class="theme-toggle">
        <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isDarkMode: false,
      isSubmitting: false,
      previewImages: [],
      imageFiles: [],
      product: {
        title: '',
        category: '',
        status: 'new',
        price: '',
        negotiable: true,
        description: '',
        location: '',
        deliveryMethods: {
          direct: true,
          parcel: false
        },
        shipping: 'included'
      },
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
      productStatuses: [
        { value: 'new', name: '새상품' },
        { value: 'almost-new', name: '거의 새것' },
        { value: 'used', name: '중고' },
        { value: 'defective', name: '하자있음' }
      ]
    };
  },
  methods: {
    handleImageUpload(event) {
      const files = event.target.files;
      
      if (!files.length) return;
      
      // 최대 5개 이미지로 제한
      const remainingSlots = 5 - this.previewImages.length;
      const filesToAdd = Array.from(files).slice(0, remainingSlots);
      
      filesToAdd.forEach(file => {
        // 이미지 파일만 허용
        if (!file.type.match('image.*')) {
          alert('이미지 파일만 업로드 가능합니다.');
          return;
        }
        
        // 파일 크기 제한 (5MB)
        if (file.size > 5 * 1024 * 1024) {
          alert('이미지 크기는 5MB 이하만 가능합니다.');
          return;
        }
        
        const reader = new FileReader();
        reader.onload = e => {
          this.previewImages.push(e.target.result);
          this.imageFiles.push(file);
        };
        reader.readAsDataURL(file);
      });
      
      // 파일 입력 초기화 (같은 파일 다시 선택 가능하도록)
      this.$refs.fileInput.value = '';
    },
    removeImage(index) {
      this.previewImages.splice(index, 1);
      this.imageFiles.splice(index, 1);
    },
    useCurrentLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          position => {
            // 실제로는 여기서 좌표를 주소로 변환하는 API를 호출해야 합니다
            // 예시로 간단하게 좌표만 표시
            this.product.location = '현재 위치 (좌표: ' + 
              position.coords.latitude.toFixed(2) + ', ' + 
              position.coords.longitude.toFixed(2) + ')';
          },
          error => {
            console.error('위치 정보를 가져오는데 실패했습니다:', error);
            alert('위치 정보를 가져오는데 실패했습니다. 직접 입력해주세요.');
          }
        );
      } else {
        alert('이 브라우저에서는 위치 정보를 지원하지 않습니다. 직접 입력해주세요.');
      }
    },
    goBack() {
      this.$router.go(-1);
    },
    async submitProduct() {
      // 유효성 검사
      if (this.previewImages.length === 0) {
        alert('최소 1개 이상의 상품 이미지를 등록해주세요.');
        return;
      }
      
      if (!this.product.deliveryMethods.direct && !this.product.deliveryMethods.parcel) {
        alert('최소 하나 이상의 배송 방법을 선택해주세요.');
        return;
      }
      
      this.isSubmitting = true;
      
      try {
        // 실제 API 연동 코드
        // 이미지 업로드 후 URL 받아오기
        // const imageUrls = await this.uploadImages(this.imageFiles);
        
        // 상품 데이터 생성
        const productData = {
          ...this.product,
          // images: imageUrls,
          images: this.previewImages, // 실제로는 서버에 업로드된 이미지 URL을 사용해야 함
          createdAt: new Date().toISOString(),
          seller: {
            id: 'user123', // 실제로는 로그인한 사용자 정보를 사용
            name: '판매자'
          }
        };
        
        // API 호출하여 상품 등록
        // await api.registerProduct(productData);
        
        // 임시로 로컬 스토리지에 저장 (실제로는 서버에 저장해야 함)
        const savedProducts = JSON.parse(localStorage.getItem('products') || '[]');
        savedProducts.push(productData);
        localStorage.setItem('products', JSON.stringify(savedProducts));
        
        alert('상품이 성공적으로 등록되었습니다!');
        this.$router.push('/products');
      } catch (error) {
        console.error('상품 등록 중 오류가 발생했습니다:', error);
        alert('상품 등록에 실패했습니다. 다시 시도해주세요.');
      } finally {
        this.isSubmitting = false;
      }
    },
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode;
      localStorage.setItem('darkMode', this.isDarkMode);
    }
  },
  mounted() {
    // 로컬 스토리지에서 다크모드 설정 불러오기
    const savedDarkMode = localStorage.getItem('darkMode');
    if (savedDarkMode !== null) {
      this.isDarkMode = savedDarkMode === 'true';
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap');

.register-page {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  background-color: #f8f9fa;
  font-family: 'Noto Sans KR', sans-serif;
  transition: all 0.3s ease;
  padding: 40px 0;
}

.register-container {
  width: 100%;
  max-width: 800px;
  padding: 40px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  transition: all 0.3s ease;
  margin: 0 20px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.register-header p {
  color: #666;
  font-size: 16px;
}

.form-section {
  margin-bottom: 40px;
  border-bottom: 1px solid #eee;
  padding-bottom: 30px;
}

.form-section:last-child {
  border-bottom: none;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 20px;
  background-color: #4CAF50;
  margin-right: 10px;
  border-radius: 2px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
  font-size: 15px;
}

input[type="text"],
input[type="number"],
select,
textarea {
  width: 100%;
  padding: 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s;
  background-color: #f9f9f9;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #4CAF50;
  outline: none;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

textarea {
  height: 200px;
  resize: vertical;
}

.input-with-counter {
  position: relative;
}

.char-counter {
  position: absolute;
  right: 10px;
  bottom: 10px;
  font-size: 12px;
  color: #999;
}

.textarea-container .char-counter {
  bottom: 10px;
}

.form-hint {
  font-size: 13px;
  color: #888;
  margin-top: 5px;
}

/* 이미지 업로드 스타일 */
.image-upload-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 10px;
}

.image-preview-item {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  border: 1px solid #eee;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.remove-image-btn:hover {
  background-color: rgba(0, 0, 0, 0.7);
}

.image-upload-label {
  width: 120px;
  height: 120px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.image-upload-label:hover {
  border-color: #4CAF50;
}

.image-upload-label input[type="file"] {
  display: none;
}

.upload-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
}

.upload-icon i {
  font-size: 24px;
  margin-bottom: 5px;
}

/* 라디오 버튼 스타일 */
.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.radio-item {
  display: flex;
  align-items: center;
}

.radio-item input[type="radio"] {
  margin-right: 8px;
}

/* 체크박스 스타일 */
.checkbox-group {
  display: flex;
  gap: 20px;
}

.checkbox-item {
  display: flex;
  align-items: center;
}

.checkbox-item input[type="checkbox"] {
  margin-right: 8px;
}

/* 가격 입력 스타일 */
.price-input {
  position: relative;
}

.price-unit {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
}

/* 토글 스위치 스타일 */
.toggle-switch {
  display: flex;
  align-items: center;
}

.toggle-switch input {
  display: none;
}

.toggle-switch label {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
  background-color: #ccc;
  border-radius: 13px;
  margin: 0 10px 0 0;
  cursor: pointer;
  transition: all 0.3s;
}

.toggle-switch label::after {
  content: '';
  position: absolute;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background-color: white;
  top: 2px;
  left: 2px;
  transition: all 0.3s;
}

.toggle-switch input:checked + label {
  background-color: #4CAF50;
}

.toggle-switch input:checked + label::after {
  transform: translateX(24px);
}

/* 위치 입력 스타일 */
.location-input {
  display: flex;
  gap: 10px;
}

.location-input input {
  flex: 1;
}

.location-btn {
  background-color: #f0f8f0;
  color: #4CAF50;
  border: 1px solid #4CAF50;
  border-radius: 8px;
  padding: 0 15px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.location-btn:hover {
  background-color: #4CAF50;
  color: white;
}

/* 버튼 스타일 */
.form-actions {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  margin-top: 40px;
}

.btn {
  flex: 1;
  padding: 14px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 16px;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 4px 10px rgba(76, 175, 80, 0.2);
}

.btn-primary:hover {
  background-color: #45a049;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(76, 175, 80, 0.3);
}

.btn-primary:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-secondary {
  background-color: #f5f5f5;
  color: #666;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.theme-toggle {
  position: absolute;
  top: 20px;
  right: 20px;
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

/* 다크 모드 스타일 */
.dark-mode {
  background-color: #121212;
  color: #e0e0e0;
}

.dark-mode .register-container {
  background-color: #1e1e1e;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.dark-mode .register-header h1 {
  color: #e0e0e0;
}

.dark-mode .register-header p {
  color: #b0b0b0;
}

.dark-mode .section-title {
  color: #e0e0e0;
}

.dark-mode .form-section {
  border-bottom-color: #333;
}

.dark-mode label {
  color: #b0b0b0;
}

.dark-mode input,
.dark-mode select,
.dark-mode textarea {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

.dark-mode .theme-toggle {
  color: #e0e0e0;
}

.dark-mode .theme-toggle:hover {
  background-color: #2a2a2a;
  color: #6abf69;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .register-container {
    padding: 30px 20px;
  }
  
  .image-upload-container {
    justify-content: center;
  }
  
  .location-input {
    flex-direction: column;
  }
  
  .radio-group,
  .checkbox-group {
    flex-direction: column;
    gap: 10px;
  }
}
</style>