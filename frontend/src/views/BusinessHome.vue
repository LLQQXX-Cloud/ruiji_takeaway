<template>
  <div class="business-home">
    <header class="header">
      <div class="header-content">
        <h2>商家中心</h2>
        <div class="header-actions">
          <button @click="goToOrders" class="btn-icon">我的订单</button>
          <button @click="logout" class="btn-outline">退出</button>
        </div>
      </div>
    </header>
    
    <div class="dashboard">
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon">📦</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.orderCount }}</div>
            <div class="stat-label">今日订单</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💰</div>
          <div class="stat-info">
            <div class="stat-value">¥{{ stats.revenue }}</div>
            <div class="stat-label">今日收入</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🍳</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.foodCount }}</div>
            <div class="stat-label">菜品数量</div>
          </div>
        </div>
      </div>
      
      <div class="nav-grid">
        <div @click="goToBusinessInfo" class="nav-card">
          <div class="nav-icon">🏪</div>
          <div class="nav-title">店铺信息</div>
          <div class="nav-desc">修改店铺资料</div>
        </div>
        <div @click="goToFoodManage" class="nav-card">
          <div class="nav-icon">🍔</div>
          <div class="nav-title">菜品管理</div>
          <div class="nav-desc">添加/编辑菜品</div>
        </div>
        <div @click="goToOrders" class="nav-card">
          <div class="nav-icon">📋</div>
          <div class="nav-title">订单管理</div>
          <div class="nav-desc">查看订单状态</div>
        </div>
      </div>
    </div>
    
    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { businessApi } from '../api/business'

const router = useRouter()
const stats = reactive({
  orderCount: 0,
  revenue: 0,
  foodCount: 0
})
const showToast = ref(false)
const toastMessage = ref('')

const loadStats = async () => {
  const businessId = localStorage.getItem('businessId')
  if (!businessId) return
  
  try {
    const res = await businessApi.getById(businessId)
    if (res.data.success) {
      stats.foodCount = res.data.data.foods?.length || 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const goToBusinessInfo = () => {
  router.push('/business-info')
}

const goToFoodManage = () => {
  router.push('/food-manage')
}

const goToOrders = () => {
  router.push('/business-orders')
}

const logout = () => {
  localStorage.clear()
  router.push('/business-login')
}

const showToastMessage = (msg) => {
  toastMessage.value = msg
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

onMounted(() => {
  const role = localStorage.getItem('role')
  const business = localStorage.getItem('business')
  
  if (!business || role !== 'business') {
    router.push('/business-login')
    return
  }
  
  loadStats()
})
</script>

<style scoped>
.business-home {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 15px 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 15px;
}

.btn-icon {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid white;
  border-radius: 6px;
  cursor: pointer;
}

.btn-outline {
  padding: 8px 16px;
  background: transparent;
  color: white;
  border: 1px solid white;
  border-radius: 6px;
  cursor: pointer;
}

.dashboard {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  font-size: 40px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.nav-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.nav-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.nav-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.nav-desc {
  color: #999;
  font-size: 14px;
}

.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  z-index: 1001;
}
</style>
