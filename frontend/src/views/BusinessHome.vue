<template>
  <div class="business-home">
    <header class="header">
      <div class="header-content">
        <h2>商家中心</h2>
        <div class="header-actions">
          <button @click="goToOrders" class="btn-ghost">订单管理</button>
          <button @click="logout" class="btn-outline">退出</button>
        </div>
      </div>
    </header>

    <div class="dashboard">
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon-box">📦</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.orderCount }}</div>
            <div class="stat-label">今日订单</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-box">💰</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.revenue }} 元</div>
            <div class="stat-label">今日收入</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-box">🍳</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.foodCount }}</div>
            <div class="stat-label">菜品数量</div>
          </div>
        </div>
      </div>

      <div class="nav-grid">
        <div @click="goToBusinessInfo" class="nav-card">
          <div class="nav-icon-box">🏪</div>
          <div class="nav-title">店铺信息</div>
          <div class="nav-desc">修改店铺资料与设置</div>
          <span class="nav-arrow">→</span>
        </div>
        <div @click="goToFoodManage" class="nav-card">
          <div class="nav-icon-box">🍔</div>
          <div class="nav-title">菜品管理</div>
          <div class="nav-desc">添加、编辑菜品信息</div>
          <span class="nav-arrow">→</span>
        </div>
        <div @click="goToOrders" class="nav-card">
          <div class="nav-icon-box">📋</div>
          <div class="nav-title">订单管理</div>
          <div class="nav-desc">处理顾客订单</div>
          <span class="nav-arrow">→</span>
        </div>
        <div @click="goToReviews" class="nav-card">
          <div class="nav-icon-box">⭐</div>
          <div class="nav-title">评价管理</div>
          <div class="nav-desc">查看顾客评价反馈</div>
          <span class="nav-arrow">→</span>
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
import { foodApi } from '../api/food'
import { orderApi } from '../api/order'

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
    // 加载菜品数量
    const foodRes = await foodApi.getByBusiness(businessId)
    if (foodRes.data.success) {
      stats.foodCount = (foodRes.data.data || []).length
    }

    // 加载今日订单数和收入
    const orderRes = await orderApi.getByBusiness(businessId)
    if (orderRes.data.success) {
      const orders = orderRes.data.data || []
      const today = new Date()
      today.setHours(0, 0, 0, 0)

      const todayOrders = orders.filter(o => {
        const orderDate = new Date(o.createTime)
        return orderDate >= today
      })

      stats.orderCount = todayOrders.length
      stats.revenue = todayOrders
        .filter(o => o.status === 3)
        .reduce((sum, o) => sum + (parseFloat(o.totalPrice) || 0), 0)
        .toFixed(2)
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const goToBusinessInfo = () => router.push('/business-info')
const goToFoodManage = () => router.push('/food-manage')
const goToOrders = () => router.push('/business-orders')
const goToReviews = () => router.push('/business-reviews')

const logout = () => {
  localStorage.clear()
  router.push('/business-login')
}

onMounted(() => {
  const role = localStorage.getItem('role')
  const business = localStorage.getItem('business')
  if (!business || role !== 'business') { router.push('/business-login'); return }
  loadStats()
})
</script>

<style scoped>
.business-home { min-height: 100vh; background: #f8f9fb; }

.header {
  background: linear-gradient(135deg, #0d9488 0%, #0f766e 50%, #115e59 100%);
  color: white; padding: 20px 24px; position: sticky; top: 0; z-index: 100;
  box-shadow: 0 4px 24px rgba(13, 148, 136, 0.2);
}
.header-content { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
.header h2 { margin: 0; font-size: 22px; font-weight: 800; letter-spacing: -0.3px; }
.header-actions { display: flex; gap: 8px; }

.btn-ghost { padding: 10px 18px; background: rgba(255, 255, 255, 0.1); color: white; border: 1px solid rgba(255, 255, 255, 0.15); border-radius: 12px; cursor: pointer; font-weight: 500; font-size: 14px; font-family: inherit; transition: all 0.25s; }
.btn-ghost:hover { background: rgba(255, 255, 255, 0.2); }
.btn-outline { padding: 10px 18px; background: transparent; color: rgba(255, 255, 255, 0.8); border: 1px solid rgba(255, 255, 255, 0.2); border-radius: 12px; cursor: pointer; font-weight: 500; font-size: 14px; font-family: inherit; transition: all 0.25s; }
.btn-outline:hover { background: rgba(255, 255, 255, 0.1); border-color: rgba(255, 255, 255, 0.4); }

.dashboard { max-width: 1200px; margin: 32px auto; padding: 0 24px; }

.stats-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 36px; }
.stat-card {
  background: white; padding: 24px; border-radius: 18px;
  display: flex; align-items: center; gap: 18px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}
.stat-card:hover { transform: translateY(-2px); box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08); }

.stat-icon-box { width: 52px; height: 52px; background: #f0fdfa; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 26px; }
.stat-value { font-size: 28px; font-weight: 800; color: #1a1a2e; letter-spacing: -0.5px; }
.stat-label { color: #9ca3af; font-size: 13px; font-weight: 600; margin-top: 2px; }

.nav-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 20px; }
.nav-card {
  background: white; padding: 28px; border-radius: 18px;
  cursor: pointer; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  position: relative;
  display: flex; flex-direction: column;
}
.nav-card:hover { transform: translateY(-4px); box-shadow: 0 12px 32px rgba(13, 148, 136, 0.12); }
.nav-icon-box { font-size: 44px; margin-bottom: 16px; }
.nav-title { font-size: 18px; font-weight: 800; color: #1a1a2e; margin-bottom: 6px; letter-spacing: -0.2px; }
.nav-desc { color: #9ca3af; font-size: 13px; margin-bottom: 16px; }
.nav-arrow {
  position: absolute; bottom: 24px; right: 24px;
  width: 32px; height: 32px; background: #f0fdfa; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px; color: #0d9488; font-weight: 700;
  transition: all 0.3s;
}
.nav-card:hover .nav-arrow { background: #0d9488; color: white; transform: translateX(3px); }

.toast { position: fixed; top: 80px; left: 50%; transform: translateX(-50%); background: #1a1a2e; color: white; padding: 14px 28px; border-radius: 14px; font-size: 14px; font-weight: 600; z-index: 1001; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25); }
</style>
