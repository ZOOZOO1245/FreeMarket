import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue'; // HomePage로 수정

const routes = [
  {
    path: '/',
    name: 'HomePage', // HomePage로 수정
    component: HomePage // HomePage로 수정
  },
  {
    path: '/products',
    name: 'ProductsPage', // Products
    component: () => import('../views/ProductsPage.vue') // ProductsPage로 수정
  },
  {
    path: '/register',
    name: 'RegisterPage', // RegisterPage로 수정
    component: () => import('../views/RegisterPage.vue') // RegisterPage로 수정
  },
  {
    path: '/login',
    name: 'LoginPage', // LoginPage 추가
    component: () => import('../views/LoginPage.vue') // LoginPage.vue 파일을 생성해야 합니다.
  },
  {
    path: '/signup',
    name: 'SignupPage', // SignupPage 추가
    component: () => import('../views/SignupPage.vue') // SignupPage.vue 파일을 생성해야 합니다.
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;