<template>
  <div class="business-container">
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="btn-back">← 返回</button>
        <div class="header-actions">
          <button @click="goToCart" class="btn-ghost">购物车</button>
          <button @click="goToOrders" class="btn-ghost">我的订单</button>
        </div>
      </div>
    </header>

    <div v-if="business" class="business-detail">
      <!-- 英雄区：商家大图 + 信息 -->
      <div class="hero-section">
        <div class="hero-banner">
          <img
            :src="getHeroImage(business.image)"
            :alt="business.name"
            class="hero-img"
            @error="handleHeroError"
          />
          <div class="hero-overlay"></div>
          <div class="hero-body">
            <div class="hero-badge">
              <span class="hero-rating">★ {{ business.rating || '5.0' }}</span>
              <span class="hero-divider">|</span>
              <span>起送 {{ business.startPrice }} 元</span>
              <span class="hero-divider">|</span>
              <span>配送 {{ business.deliveryFee }} 元</span>
            </div>
            <h1 class="hero-name">{{ business.name }}</h1>
            <p class="hero-addr">{{ business.address }}</p>
            <p class="hero-desc">{{ business.description }}</p>
          </div>
        </div>
      </div>

      <!-- 菜单区 -->
      <div class="foods-section">
        <div class="section-header">
          <h2>菜单</h2>
          <span class="food-count" v-if="foods.length">{{ foods.length }} 款菜品</span>
        </div>
        <div v-if="loading" class="loading">
          <div class="spinner"></div>
        </div>
        <div v-else-if="foods.length === 0" class="empty">
          <div class="empty-icon">🍽</div>
          <p>暂无菜品</p>
        </div>
        <div v-else class="food-grid">
          <div v-for="food in foods" :key="food.id" class="food-card">
            <div class="food-img-box">
              <img
                :src="getValidImageUrl(food.image)"
                :alt="food.name"
                class="food-img"
                crossorigin="anonymous"
                @error="handleImageError"
                loading="lazy"
              />
              <div v-if="food.isHot" class="food-tag hot">热销</div>
              <div v-if="food.isNew" class="food-tag new">新品</div>
            </div>
            <div class="food-body">
              <h3>{{ food.name }}</h3>
              <p class="food-desc">{{ food.description || '暂无描述' }}</p>
              <div class="food-footer">
                <span class="price">{{ food.price }} 元</span>
                <button @click="addToCart(food)" class="btn-add">＋ 加入购物车</button>
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
const heroImgFailed = ref(false)

const FALLBACK_HERO = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&h=350&fit=crop&crop=center'

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

const getHeroImage = (url) => {
  if (heroImgFailed.value) return FALLBACK_HERO
  if (!url || !url.trim()) return FALLBACK_HERO
  const validPatterns = /\.(jpg|jpeg|png|gif|webp|bmp)(\?.*)?$/i
  if (validPatterns.test(url)) {
    return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(url)}`
  }
  return FALLBACK_HERO
}

const handleHeroError = () => {
  heroImgFailed.value = true
}

const getValidImageUrl = (url) => {
  if (!url || !url.trim()) {
    return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="300" height="220" viewBox="0 0 300 220"><rect fill="#f3f4f6" width="300" height="220"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#9ca3af" font-size="16" font-family="sans-serif">暂无图片</text></svg>')
  }
  const validPatterns = /\.(jpg|jpeg|png|gif|webp|bmp)(\?.*)?$/i
  if (validPatterns.test(url)) {
    return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(url)}`
  }
  const mediaUrlMatch = url.match(/mediaurl=([^&]+)/)
  if (mediaUrlMatch && validPatterns.test(mediaUrlMatch[1])) {
    const realUrl = decodeURIComponent(mediaUrlMatch[1])
    return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(realUrl)}`
  }
  return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="300" height="220" viewBox="0 0 300 220"><rect fill="#f3f4f6" width="300" height="220"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#9ca3af" font-size="16" font-family="sans-serif">暂无图片</text></svg>')
}

const handleImageError = (event) => {
  event.target.src = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="300" height="220" viewBox="0 0 300 220"><rect fill="#f3f4f6" width="300" height="220"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#9ca3af" font-size="16" font-family="sans-serif">暂无图片</text></svg>')
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
    const businessId = business.value.id
    const res = await cartApi.add({
      userId: parseInt(userId),
      businessId: parseInt(businessId),
      foodId: food.id,
      quantity: 1
    })
    if (res.data.success) {
      showToastMessage(food.name + ' 已加入购物车')
    } else {
      showToastMessage(res.data.message || '添加失败')
    }
  } catch (error) {
    showToastMessage('添加失败')
  }
}

const showToastMessage = (msg) => {
  toastMessage.value = msg
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const goBack = () => router.push('/home')
const goToCart = () => router.push('/cart')
const goToOrders = () => router.push('/orders')

onMounted(() => {
  loadBusiness()
  loadFoods()
})
</script>

<style scoped>
.business-container {
  min-height: 100vh;
  background: #f5f5f0;
}

/* Header */
.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(26, 26, 46, 0.9);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  padding: 14px 24px;
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.btn-back {
  padding: 8px 18px;
  background: rgba(255,255,255,0.1);
  color: white;
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.2s;
}
.btn-back:hover { background: rgba(255,255,255,0.18); }
.header-actions { display: flex; gap: 8px; }
.btn-ghost {
  padding: 8px 16px;
  background: rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.9);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px;
  cursor: pointer;
  font-weight: 500;
  font-size: 13px;
  font-family: inherit;
  transition: all 0.2s;
}
.btn-ghost:hover { background: rgba(255,255,255,0.15); }

/* Hero */
.hero-section {
  max-width: 1200px;
  margin: 24px auto 0;
  padding: 0 24px;
}
.hero-banner {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  min-height: 280px;
  display: flex;
  align-items: flex-end;
  background: #1a1a2e;
}
.hero-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to top,
    rgba(0,0,0,0.85) 0%,
    rgba(0,0,0,0.4) 40%,
    rgba(0,0,0,0.15) 100%
  );
}
.hero-body {
  position: relative;
  z-index: 1;
  padding: 48px 36px 36px;
  color: white;
  width: 100%;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: rgba(255,255,255,0.12);
  backdrop-filter: blur(8px);
  padding: 8px 18px;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
  color: rgba(255,255,255,0.85);
}
.hero-rating { color: #fbbf24; font-weight: 700; }
.hero-divider { color: rgba(255,255,255,0.2); }
.hero-name {
  margin: 0 0 8px;
  font-size: 36px;
  font-weight: 800;
  letter-spacing: -1px;
  text-shadow: 0 2px 12px rgba(0,0,0,0.5);
}
.hero-addr {
  font-size: 14px;
  color: rgba(255,255,255,0.55);
  margin: 0 0 8px;
}
.hero-desc {
  font-size: 15px;
  color: rgba(255,255,255,0.65);
  margin: 0;
  max-width: 520px;
  line-height: 1.6;
}

/* Foods */
.foods-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 44px 24px 80px;
}
.section-header {
  display: flex;
  align-items: baseline;
  gap: 14px;
  margin-bottom: 28px;
}
.section-header h2 {
  font-size: 24px;
  font-weight: 800;
  color: #1a1a2e;
  margin: 0;
  letter-spacing: -0.3px;
}
.food-count {
  font-size: 14px;
  color: #9ca3af;
  font-weight: 500;
}

.loading, .empty { text-align: center; padding: 80px 20px; color: #9ca3af; }
.spinner {
  width: 36px; height: 36px;
  border: 3px solid #e5e7eb;
  border-top-color: #ff6b35;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto;
}
@keyframes spin { to { transform: rotate(360deg); } }
.empty-icon { font-size: 56px; margin-bottom: 12px; }

/* Food Grid */
.food-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 28px;
}
.food-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04), 0 4px 16px rgba(0,0,0,0.04);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}
.food-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.06), 0 16px 40px rgba(0,0,0,0.1);
}

.food-img-box {
  position: relative;
  overflow: hidden;
  aspect-ratio: 4 / 3;
  background: #f3f4f6;
}
.food-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.food-card:hover .food-img { transform: scale(1.06); }

.food-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 12px;
  border-radius: 100px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.3px;
}
.food-tag.hot { background: linear-gradient(135deg, #ff6b35, #f7931e); color: white; }
.food-tag.new { background: linear-gradient(135deg, #0d9488, #059669); color: white; top: 36px; }

.food-body { padding: 18px 20px 20px; }
.food-body h3 { margin: 0 0 6px; font-size: 17px; font-weight: 700; color: #1a1a2e; letter-spacing: -0.2px; }
.food-desc {
  font-size: 13px; color: #9ca3af; margin: 0 0 18px;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.food-footer { display: flex; justify-content: space-between; align-items: center; }
.price { font-size: 22px; font-weight: 800; color: #ff6b35; letter-spacing: -0.3px; }

.btn-add {
  padding: 10px 20px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255, 107, 53, 0.2);
  letter-spacing: 0.2px;
}
.btn-add:hover { transform: scale(1.04); box-shadow: 0 4px 16px rgba(255, 107, 53, 0.35); }

.toast {
  position: fixed; top: 100px; left: 50%; transform: translateX(-50%);
  background: #1a1a2e; color: white; padding: 14px 28px; border-radius: 14px;
  font-size: 14px; font-weight: 600; z-index: 1000;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
  animation: slideDown 0.3s ease-out;
}
@keyframes slideDown {
  from { opacity: 0; transform: translateX(-50%) translateY(-16px); }
  to { opacity: 1; transform: translateX(-50%) translateY(0); }
}
</style>
