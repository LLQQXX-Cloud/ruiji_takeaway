<template>
  <div class="business-orders">
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="btn-back">← 返回</button>
        <h2>订单管理</h2>
        <div class="filter-tabs">
          <button :class="{ active: statusFilter === '' }" @click="statusFilter = ''">全部</button>
          <button :class="{ active: statusFilter === '0' }" @click="statusFilter = '0'">待确认</button>
          <button :class="{ active: statusFilter === '1' }" @click="statusFilter = '1'">已确认</button>
          <button :class="{ active: statusFilter === '2' }" @click="statusFilter = '2'">配送中</button>
          <button :class="{ active: statusFilter === '3' }" @click="statusFilter = '3'">已完成</button>
          <button :class="{ active: statusFilter === '4' }" @click="statusFilter = '4'">已取消</button>
        </div>
      </div>
    </header>
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="filteredOrders.length === 0" class="empty">暂无订单</div>
      <div v-else class="order-list">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-id">订单号: {{ order.orderNo }}</div>
            <div :class="['order-status', `status-${order.status}`]">{{ getStatusText(order.status) }}</div>
          </div>
          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="order-item">
              <img :src="item.food?.image || 'https://via.placeholder.com/80'" :alt="item.food?.name" class="item-image" />
              <div class="item-info">
                <div class="item-name">{{ item.food?.name }}</div>
                <div class="item-price">{{ item.price.toFixed(2) }}元 x {{ item.quantity }}</div>
              </div>
            </div>
          </div>
          <div class="order-footer">
            <div class="order-total">合计: {{ order.totalPrice.toFixed(2) }}元</div>
            <div class="order-time">下单时间: {{ formatTime(order.orderTime) }}</div>
          </div>
          <div v-if="order.status === 0" class="order-actions">
            <button @click="confirmOrder(order)" class="btn-confirm">确认订单</button>
            <button @click="cancelOrder(order)" class="btn-cancel">拒绝订单</button>
          </div>
          <div v-else-if="order.status === 1" class="order-actions">
            <button @click="startDelivery(order)" class="btn-deliver">开始配送</button>
          </div>
          <div v-else-if="order.status === 2" class="order-actions">
            <button @click="confirmDelivery(order)" class="btn-delivery">确认送达</button>
          </div>
          <div v-else-if="order.status === 3 || order.status === 4" class="order-actions">
            <button @click="deleteOrder(order)" class="btn-delete">删除订单</button>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api/order'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const statusFilter = ref('')
const showToast = ref(false)
const toastMessage = ref('')

const filteredOrders = computed(() => {
  if (!statusFilter.value) return orders.value
  return orders.value.filter(o => o.status === parseInt(statusFilter.value))
})

const getStatusText = (status) => {
  const statusMap = {
    0: '待确认',
    1: '已确认',
    2: '配送中',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const loadOrders = async () => {
  loading.value = true
  const businessId = localStorage.getItem('businessId')
  
  try {
    const res = await orderApi.getByBusiness(businessId)
    if (res.data.success) {
      orders.value = res.data.data
    }
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

const confirmOrder = async (order) => {
  try {
    const res = await orderApi.updateStatus(order.id, 1)
    if (res.data.success) {
      order.status = 1
      showToastMessage('订单已确认')
    }
  } catch (error) {
    showToastMessage('操作失败')
  }
}

const cancelOrder = async (order) => {
  if (!confirm('确定拒绝此订单吗？')) return
  
  try {
    const res = await orderApi.updateStatus(order.id, 4)
    if (res.data.success) {
      order.status = 4
      showToastMessage('订单已拒绝')
    }
  } catch (error) {
    showToastMessage('操作失败')
  }
}

const startDelivery = async (order) => {
  try {
    const res = await orderApi.updateStatus(order.id, 2)
    if (res.data.success) {
      order.status = 2
      showToastMessage('已开始配送')
    }
  } catch (error) {
    showToastMessage('操作失败')
  }
}

const confirmDelivery = async (order) => {
  try {
    const res = await orderApi.updateStatus(order.id, 3)
    if (res.data.success) {
      order.status = 3
      showToastMessage('已确认送达')
    }
  } catch (error) {
    showToastMessage('操作失败')
  }
}

const deleteOrder = async (order) => {
  if (!confirm('确定删除此订单吗？此操作不会影响客户端的订单记录。')) return
  
  try {
    const res = await orderApi.deleteByBusiness(order.id)
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

const goBack = () => {
  router.push('/business-home')
}

onMounted(() => {
  const role = localStorage.getItem('role')
  const business = localStorage.getItem('business')
  
  if (!business || role !== 'business') {
    router.push('/business-login')
    return
  }
  
  loadOrders()
})
</script>

<style scoped>
.business-orders {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
}

.header {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 20px 20px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(245, 87, 108, 0.3);
}

.header-content {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
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

.filter-tabs {
  display: flex;
  gap: 10px;
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
  color: #f5576c;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
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

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px dashed #f0f0f0;
}

.order-id {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.order-status {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
}

.order-status.status-0 {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeeba 100%);
  color: #856404;
}

.order-status.status-1 {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
}

.order-status.status-2 {
  background: linear-gradient(135deg, #cce5ff 0%, #b8daff 100%);
  color: #004085;
}

.order-status.status-3 {
  background: linear-gradient(135deg, #d4edda 0%, #a3cfbb 100%);
  color: #0f5132;
}

.order-status.status-4 {
  background: linear-gradient(135deg, #f8d7da 0%, #f1aeb5 100%);
  color: #721c24;
}

.order-items {
  margin-bottom: 20px;
}

.order-item {
  display: flex;
  gap: 16px;
  padding: 12px 0;
  transition: background 0.3s;
  border-radius: 10px;
}

.order-item:hover {
  background: #f8f9fa;
  padding: 12px;
  margin: -12px -12px;
}

.item-image {
  width: 90px;
  height: 90px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.item-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  font-size: 16px;
}

.item-price {
  color: #888;
  font-size: 14px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 2px dashed #f0f0f0;
}

.order-total {
  font-size: 22px;
  font-weight: 700;
  color: #f5576c;
}

.order-time {
  font-size: 14px;
  color: #888;
  font-weight: 500;
}

.order-actions {
  display: flex;
  gap: 12px;
  margin-top: 15px;
}

.btn-confirm {
  flex: 1;
  padding: 12px;
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

.btn-confirm:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(76,175,80,0.4);
}

.btn-deliver {
  flex: 1;
  padding: 12px;
  background: linear-gradient(135deg, #2196f3 0%, #42a5f5 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(33,150,243,0.3);
}

.btn-deliver:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(33,150,243,0.4);
}

.btn-delivery {
  flex: 1;
  padding: 12px;
  background: linear-gradient(135deg, #ff9800 0%, #ffa726 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255,152,0,0.3);
}

.btn-delivery:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(255,152,0,0.4);
}

.btn-cancel {
  flex: 1;
  padding: 12px;
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
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(255,68,68,0.4);
}

.btn-delete {
  flex: 1;
  padding: 12px;
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
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(153,153,153,0.4);
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
  z-index: 1001;
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
