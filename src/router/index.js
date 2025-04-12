import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue'; // HomePage로 수정
import ReviewSystem from '../views/ReviewSystem.vue'; // ReviewSystem.vue 추가

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage
  },
  {
    path: '/products',
    name: 'ProductsPage',
    component: () => import('../views/ProductsPage.vue')
  },
  {
    path: '/register',
    name: 'RegisterPage',
    component: () => import('../views/RegisterPage.vue')
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: () => import('../views/LoginPage.vue')
  },
  {
    path: '/signup',
    name: 'SignupPage',
    component: () => import('../views/SignupPage.vue')
  },
  {
    path: '/review', // ReviewSystem 경로 추가
    name: 'ReviewSystem', // ReviewSystem 이름 추가
    component: ReviewSystem // ReviewSystem 컴포넌트 추가
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;
