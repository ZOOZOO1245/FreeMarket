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
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;