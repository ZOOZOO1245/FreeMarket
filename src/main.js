import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import api from './services/api'

const app = createApp(App)

// axios 인스턴스를 전역에서 사용할 수 있도록 설정
app.config.globalProperties.$axios = axios
// API 서비스를 전역에서 사용할 수 있도록 설정
app.config.globalProperties.$api = api

app.use(router).mount('#app')