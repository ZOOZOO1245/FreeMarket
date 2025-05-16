const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081, // 프론트엔드 서버 포트를 8081로 설정
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 백엔드 서버 주소
        changeOrigin: true,
        ws: true // 웹소켓 지원 (필요한 경우)
      }
    }
  }
})