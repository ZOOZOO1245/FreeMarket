import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // 라우터 가져오기
import 'bootstrap/dist/css/bootstrap.min.css';

createApp(App)
  .use(router) // 라우터 사용
  .mount('#app');
