import axios from 'axios';

// axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// 요청 인터셉터 - 토큰 추가
apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 응답 인터셉터 - 에러 처리
apiClient.interceptors.response.use(
  response => response,
  error => {
    // 401 에러 처리 (인증 실패)
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default {
  // 상품 관련 API
  products: {
    getAll: (params) => apiClient.get('/products', { params }),
    getPopular: () => apiClient.get('/products', { params: { sort: 'viewCount,desc' } }),
    getRecent: () => apiClient.get('/products', { params: { sort: 'createdDate,desc' } }),
    getById: (id) => apiClient.get(`/products/${id}`),
    create: (product, images) => {
      const formData = new FormData();
      formData.append('request', new Blob([JSON.stringify(product)], { type: 'application/json' }));
      if (images) {
        images.forEach(image => formData.append('images', image));
      }
      return apiClient.post('/products', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
    },
    update: (id, product, newImages, deleteImageIds) => {
      const formData = new FormData();
      formData.append('request', new Blob([JSON.stringify(product)], { type: 'application/json' }));
      
      if (newImages) {
        newImages.forEach(image => formData.append('newImages', image));
      }
      
      if (deleteImageIds) {
        deleteImageIds.forEach(imageId => formData.append('deleteImageIds', imageId));
      }
      
      return apiClient.patch(`/products/${id}`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
    },
    delete: (id) => apiClient.delete(`/products/${id}`),
    toggleWishlist: (id) => apiClient.post(`/products/wishlist/${id}`),
    checkWishlist: (id) => apiClient.get(`/products/wishlist/${id}`),
    markAsSold: (productId, buyerId) => apiClient.post(`/products/${productId}/sold?buyerId=${buyerId}`)
  },
  
  // 카테고리 관련 API
  categories: {
    getAll: () => apiClient.get('/categories')
  },
  
  // 사용자 관련 API
  auth: {
    login: (credentials) => apiClient.post('/auth/login', credentials),
    register: (userData) => apiClient.post('/auth/signup', userData),
    getProfile: () => apiClient.get('/users/me'),
    updateProfile: (userData) => apiClient.patch('/users/me', userData),
    changePassword: (passwordData) => apiClient.post('/users/me/password', passwordData),
    refreshToken: (refreshToken) => apiClient.post('/auth/refresh', { refreshToken }),
    logout: () => apiClient.post('/auth/logout')
  },
  
  // 채팅 관련 API
  chat: {
    getChats: () => apiClient.get('/chat'),
    getChatById: (id) => apiClient.get(`/chat/${id}`),
    sendMessage: (data) => apiClient.post('/chat/send', data)
  }
};