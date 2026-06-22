<template>
  <div class="home-container">
    <header class="header">
      <div class="header-content">
        <div class="brand">
          <span class="brand-icon"></span>
          <h2>瑞吉外卖</h2>
        </div>
        <div class="header-actions">
          <button @click="goToAddress" class="btn-ghost">收货地址</button>
          <button @click="goToCart" class="btn-ghost">购物车</button>
          <button @click="goToOrders" class="btn-ghost">我的订单</button>
          <button @click="logout" class="btn-outline">退出</button>
        </div>
      </div>
    </header>

    <div class="search-section">
      <div class="search-bar">
        <div class="search-input-wrapper">
          <span class="search-icon">🔍</span>
          <input v-model="searchName" type="text" placeholder="搜索商家、美食..." @keyup.enter="handleSearch" />
        </div>
        <button @click="handleSearch" class="search-btn">搜索</button>
      </div>

      <div class="category-filter">
        <span
          v-for="cat in categories"
          :key="cat.id"
          class="category-chip"
          :class="{ active: selectedCategory === cat.id }"
          @click="selectCategory(cat.id)"
        >{{ cat.name }}</span>
      </div>
    </div>

    <div class="content">
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
      <div v-else-if="businesses.length === 0" class="empty">
        <div class="empty-icon">🏪</div>
        <p>暂无商家</p>
      </div>
      <div v-else class="business-grid">
        <div v-for="business in businesses" :key="business.id" class="business-card" @click="goToBusiness(business.id)">
          <div class="card-image">
            <img :src="business.image || 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=250&fit=crop'" :alt="business.name" />
            <div class="card-badge" v-if="business.rating >= 4.5">🔥 人气商家</div>
          </div>
          <div class="card-body">
            <div class="card-header">
              <h3>{{ business.name }}</h3>
              <span class="rating-badge">★ {{ business.rating || '5.0' }}</span>
            </div>
            <p class="desc">{{ business.description || '美味佳肴，尽在此处' }}</p>
            <div class="card-meta">
              <span class="meta-item">起送 {{ business.startPrice }} 元</span>
              <span class="meta-divider"></span>
              <span class="meta-item">配送 {{ business.deliveryFee }} 元</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { businessApi } from '../api/business'
import { categoryApi } from '../api/category'

const router = useRouter()
const businesses = ref([])
const FALLBACK_CATEGORIES = [
  { id: null, name: '全部' },
  { id: 1, name: '快餐便当' },
  { id: 2, name: '中式美食' },
  { id: 3, name: '西式料理' },
  { id: 4, name: '日韩料理' },
  { id: 5, name: '甜品饮品' },
  { id: 6, name: '烧烤火锅' }
]
const categories = ref([...FALLBACK_CATEGORIES])
const loading = ref(false)
const searchName = ref('')
const selectedCategory = ref(null)

const loadCategories = async () => {
  try {
    const res = await categoryApi.getAll()
    if (res.data.success && res.data.data && res.data.data.length > 0) {
      categories.value = [{ id: null, name: '全部' }, ...res.data.data]
    }
  } catch (error) {
    // API 不可用时使用硬编码分类兜底
    categories.value = [...FALLBACK_CATEGORIES]
  }
}

const loadBusinesses = async () => {
  loading.value = true
  try {
    const res = await businessApi.getAll(searchName.value)
    if (res.data.success) {
      let data = res.data.data
      if (selectedCategory.value !== null) {
        data = data.filter(b => b.categoryId === selectedCategory.value)
      }
      businesses.value = data
    }
  } catch (error) {
    console.error('加载商家失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadBusinesses()
}

const selectCategory = (id) => {
  selectedCategory.value = id
  loadBusinesses()
}

const goToBusiness = (id) => {
  router.push(`/business/${id}`)
}

const goToCart = () => {
  router.push('/cart')
}

const goToOrders = () => {
  router.push('/orders')
}

const goToAddress = () => {
  router.push('/address')
}

const logout = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(() => {
  const user = localStorage.getItem('user')
  if (!user) {
    router.push('/login')
    return
  }
  loadCategories()
  loadBusinesses()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f8f9fb;
}

/* Header */
.header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  color: white;
  padding: 20px 24px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.btn-ghost {
  padding: 10px 18px;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s;
  font-weight: 500;
  font-size: 14px;
  font-family: inherit;
  backdrop-filter: blur(10px);
}

.btn-ghost:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.25);
  transform: translateY(-1px);
}

.btn-outline {
  padding: 10px 18px;
  background: transparent;
  color: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s;
  font-weight: 500;
  font-size: 14px;
  font-family: inherit;
}

.btn-outline:hover {
  background: rgba(255, 107, 53, 0.2);
  border-color: #ff6b35;
  color: #ff6b35;
}

/* Search */
.search-section {
  max-width: 1200px;
  margin: 32px auto 0;
  padding: 0 24px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 18px;
  font-size: 18px;
  z-index: 1;
  opacity: 0.5;
}

.search-input-wrapper input {
  width: 100%;
  padding: 16px 18px 16px 50px;
  border: 2px solid #e5e7eb;
  border-radius: 16px;
  font-size: 15px;
  background: white;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-input-wrapper input:focus {
  outline: none;
  border-color: #ff6b35;
  box-shadow: 0 0 0 4px rgba(255, 107, 53, 0.08), 0 4px 16px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

.search-btn {
  padding: 16px 36px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 16px rgba(255, 107, 53, 0.3);
  font-family: inherit;
  letter-spacing: 0.3px;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(255, 107, 53, 0.4);
}

/* Categories */
.category-filter {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.category-chip {
  padding: 10px 20px;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 100px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
  font-family: inherit;
}

.category-chip:hover {
  border-color: #ff6b35;
  color: #ff6b35;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.08);
}

.category-chip.active {
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 16px rgba(255, 107, 53, 0.3);
}

/* Content */
.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px 60px;
}

.loading, .empty {
  text-align: center;
  padding: 100px 20px;
  color: #9ca3af;
  font-size: 16px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #ff6b35;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.business-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 28px;
}

.business-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.business-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.12);
}

.card-image {
  position: relative;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.business-card:hover .card-image img {
  transform: scale(1.06);
}

.card-badge {
  position: absolute;
  top: 14px;
  left: 14px;
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  padding: 6px 14px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
}

.card-body {
  padding: 22px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1a1a2e;
  font-weight: 700;
  letter-spacing: -0.2px;
}

.rating-badge {
  background: #fffbeb;
  color: #d97706;
  padding: 4px 10px;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
}

.desc {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.5;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f3f4f6;
  font-size: 13px;
  color: #9ca3af;
  font-weight: 500;
}

.meta-divider {
  width: 4px;
  height: 4px;
  background: #d1d5db;
  border-radius: 50%;
}
</style>
