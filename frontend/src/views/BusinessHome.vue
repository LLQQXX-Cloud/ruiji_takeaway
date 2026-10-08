<template>
  <div class="business-home">
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
    </div>

    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
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

onMounted(() => {
  const role = localStorage.getItem('role')
  const business = localStorage.getItem('business')
  if (!business || role !== 'business') { router.push('/business-login'); return }
  loadStats()
})
</script>

<style scoped>
.business-home { min-height: 100vh; }

.dashboard { max-width: 1200px; margin: 0 auto; }

.stats-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; }
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

.toast { position: fixed; top: 80px; left: 50%; transform: translateX(-50%); background: #1a1a2e; color: white; padding: 14px 28px; border-radius: 14px; font-size: 14px; font-weight: 600; z-index: 1001; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25); }
</style>
