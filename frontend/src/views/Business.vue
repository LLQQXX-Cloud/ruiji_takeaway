<template>
  <div class="business-container">
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="btn-back">← 返回</button>
        <div class="user-info">
          <button @click="goToCart" class="btn-icon">购物车</button>
          <button @click="goToOrders" class="btn-icon">我的订单</button>
        </div>
      </div>
    </header>
    
    <div v-if="business" class="business-detail">
      <div class="business-header">
        <img :src="business.image || 'https://via.placeholder.com/400x200?text=商家图片'" :alt="business.name" class="business-image" />
        <div class="business-info">
          <h1>{{ business.name }}</h1>
          <p class="rating">评分: {{ business.rating }} ⭐</p>
          <p>{{ business.address }}</p>
          <p>{{ business.phone }}</p>
          <div class="business-meta">
            <span>起送: ¥{{ business.startPrice }}</span>
            <span>配送: ¥{{ business.deliveryFee }}</span>
          </div>
          <p class="desc">{{ business.description }}</p>
        </div>
      </div>
      
      <div class="foods-section">
        <h2>菜单</h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="foods.length === 0" class="empty">暂无菜品</div>
        <div v-else class="food-grid">
          <div v-for="food in foods" :key="food.id" class="food-card">
            <img :src="getValidImageUrl(food.image)" :alt="food.name" class="food-image" crossorigin="anonymous" @error="handleImageError" />
            <div class="food-info">
              <h3>{{ food.name }}</h3>
              <p class="food-desc">{{ food.description || '暂无描述' }}</p>
              <div class="food-footer">
                <span class="price">{{ food.price }}元</span>
                <button @click="addToCart(food)" class="btn-add">加入购物车</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { businessApi } from '../api/business'
import { foodApi } from '../api/food'
import { cartApi } from '../api/cart'

const router = useRouter()
const route = useRoute()
const business = ref(null)
const foods = ref([])
const loading = ref(false)
const showToast = ref(false)
const toastMessage = ref('')

const loadBusiness = async () => {
  try {
    const res = await businessApi.getById(route.params.id)
    if (res.data.success) {
      business.value = res.data.data
    }
  } catch (error) {
    console.error('加载商家失败:', error)
  }
}

const getValidImageUrl = (url) => {
  if (!url || !url.trim()) {
    return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="150" height="150" viewBox="0 0 150 150"><rect fill="#f0f0f0" width="150" height="150"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="14">菜品</text></svg>')
  }
  // 检查URL是否以图片格式扩展名结尾（直接图片链接）
  const validPatterns = /\.(jpg|jpeg|png|gif|webp|bmp)(\?.*)?$/i
  if (validPatterns.test(url)) {
    // 使用后端代理来绑过CORS限制
    return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(url)}`
  }
  // 如果URL包含图片域名但不是直接链接，尝试提取真实图片URL
  const mediaUrlMatch = url.match(/mediaurl=([^&]+)/)
  if (mediaUrlMatch && validPatterns.test(mediaUrlMatch[1])) {
    const realUrl = decodeURIComponent(mediaUrlMatch[1])
    return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(realUrl)}`
  }
  return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="150" height="150" viewBox="0 0 150 150"><rect fill="#f0f0f0" width="150" height="150"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="14">菜品</text></svg>')
}

const handleImageError = (event) => {
  event.target.src = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="150" height="150" viewBox="0 0 150 150"><rect fill="#f0f0f0" width="150" height="150"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="14">菜品</text></svg>')
}

const loadFoods = async () => {
  loading.value = true
  try {
    const res = await foodApi.getByBusiness(route.params.id)
    if (res.data.success) {
      foods.value = res.data.data
    }
  } catch (error) {
    console.error('加载菜品失败:', error)
  } finally {
    loading.value = false
  }
}

const addToCart = async (food) => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      showToastMessage('请先登录')
      router.push('/login')
      return
    }
    const res = await cartApi.add({
      userId: parseInt(userId),
      businessId: parseInt(route.params.id),
      foodId: parseInt(food.id),
      quantity: 1
    })
    if (res.data.success) {
      showToastMessage('已加入购物车')
    }
  } catch (error) {
    console.error('加入购物车错误:', error)
    showToastMessage('加入购物车失败')
  }
}

const showToastMessage = (msg) => {
  toastMessage.value = msg
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const goBack = () => {
  router.push('/home')
}

const goToCart = () => {
  router.push('/cart')
}

const goToOrders = () => {
  router.push('/orders')
}

onMounted(() => {
  const user = localStorage.getItem('user')
  if (!user) {
    router.push('/login')
    return
  }
  loadBusiness()
  loadFoods()
})
</script>

<style scoped>
.business-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 20px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-back {
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
  backdrop-filter: blur(10px);
}

.btn-back:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255,255,255,0.2);
}

.user-info {
  display: flex;
  gap: 12px;
}

.btn-icon {
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
  backdrop-filter: blur(10px);
}

.btn-icon:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255,255,255,0.2);
}

.business-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 25px 20px 40px;
}

.business-header {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.business-image {
  width: 100%;
  height: 280px;
  object-fit: cover;
  transition: transform 0.4s;
}

.business-header:hover .business-image {
  transform: scale(1.02);
}

.business-info {
  padding: 30px;
}

.business-info h1 {
  margin: 0 0 18px;
  font-size: 30px;
  color: #333;
  font-weight: 700;
}

.business-info p {
  margin: 10px 0;
  color: #666;
  font-size: 15px;
}

.rating {
  color: #ff9800 !important;
  font-size: 18px !important;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.business-meta {
  display: flex;
  gap: 30px;
  margin: 20px 0;
  padding: 20px 25px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 16px;
  font-size: 15px;
  color: #555;
  font-weight: 500;
}

.desc {
  color: #666 !important;
  line-height: 1.8;
  margin-top: 20px !important;
  font-size: 15px;
  padding: 15px 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.foods-section h2 {
  margin: 0 0 24px;
  font-size: 26px;
  color: #333;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.foods-section h2::before {
  content: '';
  width: 4px;
  height: 26px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.loading, .empty {
  text-align: center;
  padding: 80px 20px;
  color: #999;
  background: white;
  border-radius: 20px;
  font-size: 18px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.food-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.food-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.food-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.food-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.4s;
}

.food-card:hover .food-image {
  transform: scale(1.08);
}

.food-info {
  padding: 20px;
}

.food-info h3 {
  margin: 0 0 12px;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.food-desc {
  color: #888;
  font-size: 14px;
  margin: 8px 0 18px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.5;
}

.food-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ff5722;
  font-size: 24px;
  font-weight: 700;
}

.btn-add {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-add:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.toast {
  position: fixed;
  top: 100px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #333 0%, #555 100%);
  color: white;
  padding: 16px 32px;
  border-radius: 16px;
  font-size: 15px;
  z-index: 1000;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}
</style>
