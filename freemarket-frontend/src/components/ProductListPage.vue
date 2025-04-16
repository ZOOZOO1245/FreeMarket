<template>
  <div class="container">
    <h1 class="text-center">중고마켓</h1>
    <h2>인기 상품</h2>
    <div class="row">
      <div class="col-md-4" v-for="product in products" :key="product.id">
        <div class="card">
          <img :src="product.image" class="card-img-top" alt="상품 이미지">
          <div class="card-body">
            <h5 class="card-title">{{ product.name }}</h5>
            <p class="card-text">{{ product.price }}원</p>
            <a href="#" class="btn btn-primary">자세히 보기</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      products: [
        { id: 1, name: '아이폰 13', price: 800000, image: 'path/to/image1.jpg' },
        { id: 2, name: '맥북 프로', price: 1500000, image: 'path/to/image2.jpg' },
        { id: 3, name: '에어팟 프로', price: 250000, image: 'path/to/image3.jpg' },
      ],
    };
  },
};
</script>

<style scoped>
.container {
  margin-top: 20px;
}

.card {
  margin-bottom: 20px;
}
</style>