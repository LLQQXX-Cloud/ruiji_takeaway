<template>
  <div class="home-container">
    <header class="header">
      <div class="header-content">
        <h2>瑞吉外卖</h2>
        <div class="header-actions">
          <button @click="goToAddress" class="btn-icon">收货地址</button>
          <button @click="goToCart" class="btn-icon">购物车</button>
          <button @click="goToOrders" class="btn-icon">我的订单</button>
          <button @click="logout" class="btn-outline">退出</button>
        </div>
      </div>
    </header>
    
    <div class="search-section">
      <div class="search-bar">
        <input v-model="searchName" type="text" placeholder="搜索商家..." @keyup.enter="handleSearch" />
        <button @click="handleSearch" class="search-btn">搜索</button>
      </div>
      
      <div class="category-filter">
        <span class="filter-label">分类:</span>
        <div class="category-list">
          <span 
            v-for="cat in categories" 
            :key="cat.id" 
            class="category-item"
            :class="{ active: selectedCategory === cat.id }"
            @click="selectCategory(cat.id)"
          >{{ cat.name }}</span>
        </div>
      </div>
    </div>
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="businesses.length === 0" class="empty">暂无商家</div>
      <div v-else class="business-list">
        <div v-for="business in businesses" :key="business.id" class="business-card" @click="goToBusiness(business.id)">
          <div class="business-image">
            <img :src="business.image || 'https://via.placeholder.com/200x150?text=商家图片'" :alt="business.name" />
          </div>
          <div class="business-info">
            <div class="business-header">
              <h3>{{ business.name }}</h3>
              <span class="rating">★ {{ business.rating || '4.0' }}</span>
            </div>
            <p class="desc">{{ business.description || '暂无描述' }}</p>
            <div class="business-meta">
              <span>起送: ¥{{ business.startPrice }}</span>
              <span>配送: ¥{{ business.deliveryFee }}</span>
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

const router = useRouter()
const businesses = ref([])
const categories = ref([
  { id: null, name: '全部' },
  { id: 1, name: '快餐便当' },
  { id: 2, name: '中式美食' },
  { id: 3, name: '西式料理' },
  { id: 4, name: '日韩料理' },
  { id: 5, name: '甜品饮品' },
  { id: 6, name: '烧烤火锅' }
])
const loading = ref(false)
const searchName = ref('')
const selectedCategory = ref(null)

const loadBusinesses = async () => {
  loading.value = true
  try {
    // 使用原来的 getAll 方法
    const res = await businessApi.getAll(searchName.value)
    if (res.data.success) {
      let data = res.data.data
      // 前端筛选分类
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
  loadBusinesses()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 25px 20px;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: pulse 10s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1) rotate(0deg); }
  50% { transform: scale(1.1) rotate(5deg); }
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-actions {
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

.btn-outline {
  padding: 12px 24px;
  background: transparent;
  color: white;
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
}

.btn-outline:hover {
  background: white;
  color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255,255,255,0.3);
}

.search-section {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-bar input {
  flex: 1;
  padding: 18px 28px;
  border: 2px solid #e9ecef;
  border-radius: 30px;
  font-size: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  background: white;
}

.search-bar input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.search-btn {
  padding: 18px 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.category-filter {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.category-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.category-item {
  padding: 10px 20px;
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  font-weight: 500;
  color: #666;
}

.category-item:hover {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.category-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 20px 40px;
}

.loading, .empty {
  text-align: center;
  padding: 80px 20px;
  color: #999;
  font-size: 18px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.business-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.business-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.business-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.business-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.4s;
}

.business-card:hover .business-image img {
  transform: scale(1.08);
}

.business-info {
  padding: 24px;
}

.business-info h3 {
  margin: 0 0 12px;
  font-size: 20px;
  color: #333;
  font-weight: 700;
}

.rating {
  color: #ff9800;
  font-size: 15px;
  margin: 8px 0;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.desc {
  color: #666;
  font-size: 14px;
  margin: 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.5;
}

.business-meta {
  display: flex;
  gap: 20px;
  margin-top: 18px;
  padding-top: 18px;
  border-top: 2px dashed #f0f0f0;
  font-size: 14px;
  color: #777;
  font-weight: 500;
}

.business-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>
