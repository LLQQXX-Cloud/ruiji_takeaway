<template>
  <div class="orders-container">
    <header class="header">
      <div class="header-content">
        <button @click="goHome" class="btn-back">← 返回</button>
        <h2>我的订单</h2>
        <button @click="goToCart" class="btn-icon">购物车</button>
      </div>
      <div class="filter-tabs">
        <button :class="{ active: statusFilter === '' }" @click="statusFilter = ''">全部</button>
        <button :class="{ active: statusFilter === '0' }" @click="statusFilter = '0'">待接单</button>
        <button :class="{ active: statusFilter === '1' }" @click="statusFilter = '1'">已接单</button>
        <button :class="{ active: statusFilter === '2' }" @click="statusFilter = '2'">配送中</button>
        <button :class="{ active: statusFilter === '3' }" @click="statusFilter = '3'">已完成</button>
        <button :class="{ active: statusFilter === '4' }" @click="statusFilter = '4'">已取消</button>
      </div>
    </header>
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="filteredOrders.length === 0" class="empty">
        <p>暂无订单</p>
        <button @click="goHome" class="btn-primary">去下单</button>
      </div>
      <div v-else class="orders-list">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <span class="order-number">订单号: {{ order.orderNumber }}</span>
              <span :class="['status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
            </div>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
          </div>
          
          <div class="order-details">
            <p><strong>地址:</strong> {{ order.address }}</p>
            <p><strong>联系人:</strong> {{ order.contact }}</p>
            <p><strong>电话:</strong> {{ order.phone }}</p>
            <p v-if="order.remark"><strong>备注:</strong> {{ order.remark }}</p>
          </div>
          
          <div class="order-footer">
            <span class="total-price">{{ order.totalPrice }}元</span>
            <div class="order-actions">
              <button @click="viewDetail(order)" class="btn-detail">查看详情</button>
              <button v-if="order.status === 0" @click="cancelOrder(order)" class="btn-cancel">取消订单</button>
              <button v-if="order.status === 2" @click="confirmReceipt(order)" class="btn-receipt">确认收货</button>
              <button v-if="order.status === 3 && !order.reviewed" @click="openReview(order)" class="btn-review">去评价</button>
              <button v-if="order.status === 3 || order.status === 4" @click="deleteOrder(order)" class="btn-delete">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
    
    <ReviewModal 
      :visible="showReviewModal" 
      :order-id="reviewOrderId"
      :user-id="currentUserId"
      :business-id="reviewBusinessId"
      @close="showReviewModal = false"
      @success="handleReviewSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api/order'
import ReviewModal from './ReviewModal.vue'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const showToast = ref(false)
const toastMessage = ref('')
const statusFilter = ref('')
const showReviewModal = ref(false)
const reviewOrderId = ref(0)
const reviewBusinessId = ref(0)
const currentUserId = ref(parseInt(localStorage.getItem('userId')) || 0)

const filteredOrders = computed(() => {
  if (!statusFilter.value) return orders.value
  return orders.value.filter(o => o.status === parseInt(statusFilter.value))
})

const statusMap = {
  0: { text: '待接单', class: 'pending' },
  1: { text: '已接单', class: 'accepted' },
  2: { text: '配送中', class: 'delivering' },
  3: { text: '已完成', class: 'completed' },
  4: { text: '已取消', class: 'cancelled' }
}

const getStatusText = (status) => {
  return statusMap[status]?.text || '未知'
}

const getStatusClass = (status) => {
  return statusMap[status]?.class || ''
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadOrders = async () => {
  loading.value = true
  try {
    const userId = parseInt(localStorage.getItem('userId'))
    if (isNaN(userId)) {
      console.error('用户ID无效')
      orders.value = []
      return
    }
    console.log('正在加载订单，用户ID:', userId)
    const res = await orderApi.getByUser(userId)
    console.log('订单响应:', res)
    if (res.data.success) {
      orders.value = res.data.data || []
      console.log('订单数据:', orders.value)
    } else {
      orders.value = []
    }
  } catch (error) {
    console.error('加载订单失败:', error.response?.data || error.message)
    orders.value = []
  } finally {
    loading.value = false
  }
}

const viewDetail = (order) => {
  showToastMessage(`订单 ${order.orderNumber} 详情`)
}

const cancelOrder = async (order) => {
  try {
    const res = await orderApi.updateStatus(order.id, 4)
    if (res.data.success) {
      order.status = 4
      showToastMessage('订单已取消')
    }
  } catch (error) {
    showToastMessage('取消失败')
  }
}

const confirmReceipt = async (order) => {
  if (!confirm('确认已收到商品吗？')) return
  
  try {
    const res = await orderApi.updateStatus(order.id, 3)
    if (res.data.success) {
      order.status = 3
      showToastMessage('已确认收货')
    }
  } catch (error) {
    showToastMessage('操作失败')
  }
}

const deleteOrder = async (order) => {
  if (!confirm('确定删除此订单吗？此操作不会影响商家端的订单记录。')) return
  
  try {
    const res = await orderApi.deleteByUser(order.id)
    if (res.data.success) {
      const index = orders.value.findIndex(o => o.id === order.id)
      if (index > -1) {
        orders.value.splice(index, 1)
      }
      showToastMessage('订单已从您的订单列表中删除')
    }
  } catch (error) {
    showToastMessage('删除失败')
  }
}

const showToastMessage = (msg) => {
  toastMessage.value = msg
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const openReview = (order) => {
  reviewOrderId.value = order.id
  reviewBusinessId.value = order.businessId
  showReviewModal.value = true
}

const handleReviewSuccess = () => {
  const order = orders.value.find(o => o.id === reviewOrderId.value)
  if (order) {
    order.reviewed = true
  }
}

const goHome = () => {
  router.push('/home')
}

const goToCart = () => {
  router.push('/cart')
}

onMounted(() => {
  console.log('Orders页面初始化')
  const user = localStorage.getItem('user')
  const userId = localStorage.getItem('userId')
  console.log('用户信息:', user, '用户ID:', userId)
  
  if (!user || !userId) {
    console.log('未登录，跳转到登录页')
    router.push('/login')
    return
  }
  loadOrders()
})
</script>

<style scoped>
.orders-container {
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

.filter-tabs {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.filter-tabs button {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.filter-tabs button:hover {
  background: rgba(255, 255, 255, 0.3);
}

.filter-tabs button.active {
  background: white;
  color: #667eea;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.header-content {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
}

.btn-back {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 10px;
  cursor: pointer;
  font-weight: 500;
  backdrop-filter: blur(10px);
  transition: all 0.3s;
}

.btn-back:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255,255,255,0.2);
}

.btn-icon {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 10px;
  cursor: pointer;
  font-weight: 500;
  backdrop-filter: blur(10px);
  transition: all 0.3s;
}

.btn-icon:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255,255,255,0.2);
}

.content {
  max-width: 900px;
  margin: 25px auto;
  padding: 0 20px 40px;
}

.loading, .empty {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 20px;
  color: #999;
  font-size: 18px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.btn-primary {
  margin-top: 20px;
  padding: 14px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 14px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.btn-primary:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.order-header {
  padding: 20px 24px;
  background: linear-gradient(135deg, #f8f9fa 0%, #f0f2f5 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: none;
}

.order-info {
  display: flex;
  gap: 16px;
  align-items: center;
}

.order-number {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.status {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
}

.status.pending {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeeba 100%);
  color: #856404;
}

.status.accepted {
  background: linear-gradient(135deg, #cce5ff 0%, #b8daff 100%);
  color: #004085;
}

.status.delivering {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
}

.status.completed {
  background: linear-gradient(135deg, #d1e7dd 0%, #a3cfbb 100%);
  color: #0f5132;
}

.status.cancelled {
  background: linear-gradient(135deg, #f8d7da 0%, #f1aeb5 100%);
  color: #721c24;
}

.order-time {
  font-size: 14px;
  color: #888;
  font-weight: 500;
}

.order-details {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.order-details p {
  margin: 10px 0;
  font-size: 15px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-details strong {
  color: #333;
  font-weight: 600;
  min-width: 50px;
}

.order-footer {
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-price {
  font-size: 26px;
  color: #ff5722;
  font-weight: 700;
}

.order-actions {
  display: flex;
  gap: 12px;
}

.btn-detail {
  padding: 10px 18px;
  background: linear-gradient(135deg, #f0f0f0 0%, #e0e0e0 100%);
  color: #666;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-detail:hover {
  transform: scale(1.05);
  background: linear-gradient(135deg, #e0e0e0 0%, #d0d0d0 100%);
}

.btn-cancel {
  padding: 10px 18px;
  background: linear-gradient(135deg, #ff4444 0%, #ff6666 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255,68,68,0.3);
}

.btn-cancel:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(255,68,68,0.4);
}

.btn-receipt {
  padding: 10px 18px;
  background: linear-gradient(135deg, #4caf50 0%, #66bb6a 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(76,175,80,0.3);
}

.btn-receipt:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(76,175,80,0.4);
}

.btn-delete {
  padding: 10px 18px;
  background: linear-gradient(135deg, #999 0%, #777 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(153,153,153,0.3);
}

.btn-delete:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(153,153,153,0.4);
}

.btn-review {
  padding: 10px 18px;
  background: linear-gradient(135deg, #ff9800 0%, #ffc107 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255,152,0,0.3);
}

.btn-review:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(255,152,0,0.4);
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
