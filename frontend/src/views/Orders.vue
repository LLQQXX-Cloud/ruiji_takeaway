<template>
  <div class="orders-container">
    <header class="header">
      <div class="header-content">
        <button @click="goHome" class="btn-back">← 返回</button>
        <h2>我的订单</h2>
        <div class="header-actions">
          <button @click="refreshOrders" class="btn-ghost">刷新</button>
          <button @click="goToCart" class="btn-ghost">购物车</button>
        </div>
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
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
      <div v-else-if="filteredOrders.length === 0" class="empty-state">
        <div class="empty-icon">📋</div>
        <h3>暂无订单</h3>
        <p>快去下单吧</p>
        <button @click="goHome" class="btn-primary">去逛逛</button>
      </div>
      <div v-else class="orders-list">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <span class="order-number">#{{ order.orderNumber }}</span>
              <span :class="['status', getStatusClass(order.status)]">{{ getStatusText(order.status, order.cancelledBy) }}</span>
            </div>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
          </div>

          <div class="order-body">
            <div class="order-items-preview">
              <div v-for="(item, idx) in (order.orderItems || []).slice(0, 3)" :key="idx" class="preview-item">
                <img :src="getValidImageUrl(item.image)" :alt="item.foodName" class="preview-img" />
                <span class="preview-name">{{ item.foodName }}</span>
                <span class="preview-qty">×{{ item.quantity }}</span>
              </div>
              <span v-if="order.orderItems && order.orderItems.length > 3" class="more-items">+{{ order.orderItems.length - 3 }} 更多</span>
            </div>
          </div>

          <div class="order-footer">
            <span class="total-price">{{ order.totalPrice }} 元</span>
            <div class="order-actions">
              <button @click="viewDetail(order)" class="btn-detail">详情</button>
              <button v-if="order.status === 0" @click="cancelOrder(order)" class="btn-cancel">取消订单</button>
              <button v-if="order.status === 2" @click="confirmReceipt(order)" class="btn-success">确认收货</button>
              <button v-if="order.status === 3 && !order.reviewed" @click="openReview(order)" class="btn-review">去评价</button>
              <button v-if="order.status === 3 || order.status === 4" @click="deleteOrder(order)" class="btn-delete">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showToast" class="toast">{{ toastMessage }}</div>

    <div v-if="showDetailModal" class="modal-overlay" @click.self="showDetailModal = false">
      <div class="detail-modal">
        <div class="modal-header">
          <h3>订单详情</h3>
          <button @click="showDetailModal = false" class="btn-close">×</button>
        </div>
        <div class="modal-body" v-if="selectedOrder">
          <div class="detail-section">
            <h4>订单信息</h4>
            <p><strong>订单号</strong> {{ selectedOrder.orderNumber }}</p>
            <p><strong>状态</strong> {{ getStatusText(selectedOrder.status, selectedOrder.cancelledBy) }}</p>
            <p><strong>下单时间</strong> {{ formatTime(selectedOrder.createTime) }}</p>
          </div>
          <div class="detail-section">
            <h4>配送信息</h4>
            <p><strong>地址</strong> {{ selectedOrder.address }}</p>
            <p><strong>联系人</strong> {{ selectedOrder.contact }}</p>
            <p><strong>电话</strong> {{ selectedOrder.phone }}</p>
            <p v-if="selectedOrder.remark"><strong>备注</strong> {{ selectedOrder.remark }}</p>
          </div>
          <div class="detail-section">
            <h4>订单商品</h4>
            <div v-if="selectedOrder.orderItems && selectedOrder.orderItems.length > 0">
              <div v-for="(item, index) in selectedOrder.orderItems" :key="index" class="detail-item">
                <img :src="getValidImageUrl(item.image)" :alt="item.foodName" class="detail-item-image" />
                <div class="detail-item-info">
                  <div class="item-name">{{ item.foodName }}</div>
                  <div class="item-price">{{ (item.price || 0).toFixed(2) }} 元 × {{ item.quantity }}</div>
                </div>
                <div class="detail-item-total">{{ ((item.price || 0) * item.quantity).toFixed(2) }} 元</div>
              </div>
            </div>
            <div v-else class="no-items">暂无商品信息</div>
          </div>
          <div class="detail-section total-section">
            <span>订单金额</span>
            <span class="total-amount">{{ selectedOrder.totalPrice }} 元</span>
          </div>
        </div>
      </div>
    </div>

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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api/order'
import ReviewModal from './ReviewModal.vue'

const getValidImageUrl = (url) => {
  if (!url || !url.trim()) {
    return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 80 80"><rect fill="#f0f0f0" width="80" height="80"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="12">菜品</text></svg>')
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
  return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 80 80"><rect fill="#f0f0f0" width="80" height="80"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="12">菜品</text></svg>')
}

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
const showDetailModal = ref(false)
const selectedOrder = ref(null)

let refreshInterval = null

const filteredOrders = computed(() => {
  if (!statusFilter.value) return orders.value
  return orders.value.filter(o => o.status === parseInt(statusFilter.value))
})

const statusMap = {
  0: { text: '待接单', class: 'pending' },
  1: { text: '已接单', class: 'accepted' },
  2: { text: '配送中', class: 'delivering' },
  3: { text: '已完成', class: 'completed' },
  4: { text: '已取消', class: 'cancelled' },
  5: { text: '取消申请中', class: 'cancelling' }
}

const cancelledByMap = { 0: '', 1: ' (用户取消)', 2: ' (商家取消)' }

const getStatusText = (status, cancelledBy) => {
  const baseText = statusMap[status]?.text || '未知'
  if (status === 4) return baseText + (cancelledByMap[cancelledBy] || '')
  return baseText
}

const getStatusClass = (status) => statusMap[status]?.class || ''

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const loadOrders = async () => {
  loading.value = true
  try {
    const userId = parseInt(localStorage.getItem('userId'))
    if (isNaN(userId)) { orders.value = []; return }
    const res = await orderApi.getByUser(userId)
    if (res.data.success) orders.value = res.data.data || []
    else orders.value = []
  } catch (error) {
    orders.value = []
  } finally {
    loading.value = false
  }
}

const viewDetail = (order) => { selectedOrder.value = order; showDetailModal.value = true }
const refreshOrders = async () => { await loadOrders(); showToastMessage('已刷新') }

const cancelOrder = async (order) => {
  if (!confirm('确定要申请取消订单吗？')) return
  try {
    const res = await orderApi.applyCancel(order.id)
    if (res.data.success) { order.status = 5; order.cancelledBy = 1; showToastMessage('已提交取消申请') }
    else showToastMessage(res.data.message || '提交失败')
  } catch (error) { showToastMessage('提交失败') }
}

const confirmReceipt = async (order) => {
  if (!confirm('确认已收到商品吗？')) return
  try {
    const res = await orderApi.updateStatus(order.id, 3)
    if (res.data.success) { order.status = 3; showToastMessage('已确认收货') }
  } catch (error) { showToastMessage('操作失败') }
}

const deleteOrder = async (order) => {
  if (!confirm('确定删除此订单吗？')) return
  try {
    const res = await orderApi.deleteByUser(order.id)
    if (res.data.success) {
      const index = orders.value.findIndex(o => o.id === order.id)
      if (index > -1) orders.value.splice(index, 1)
      showToastMessage('已删除')
    }
  } catch (error) { showToastMessage('删除失败') }
}

const showToastMessage = (msg) => { toastMessage.value = msg; showToast.value = true; setTimeout(() => showToast.value = false, 2000) }
const openReview = (order) => { reviewOrderId.value = order.id; reviewBusinessId.value = order.businessId; showReviewModal.value = true }
const handleReviewSuccess = () => { const order = orders.value.find(o => o.id === reviewOrderId.value); if (order) order.reviewed = true }
const goHome = () => router.push('/home')
const goToCart = () => router.push('/cart')

onMounted(() => {
  const user = localStorage.getItem('user'); const userId = localStorage.getItem('userId')
  if (!user || !userId) { router.push('/login'); return }
  loadOrders()
  refreshInterval = setInterval(() => loadOrders(), 30000)
})
onUnmounted(() => { if (refreshInterval) clearInterval(refreshInterval) })
</script>

<style scoped>
.orders-container { min-height: 100vh; background: #f8f9fb; }

.header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: white; padding: 18px 24px;
  position: sticky; top: 0; z-index: 100;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
}

.filter-tabs { display: flex; gap: 6px; margin-top: 16px; flex-wrap: wrap; max-width: 900px; margin-left: auto; margin-right: auto; }
.filter-tabs button {
  padding: 8px 18px; background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.75); border: none; border-radius: 100px;
  cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit;
  transition: all 0.25s;
}
.filter-tabs button:hover { background: rgba(255, 255, 255, 0.15); }
.filter-tabs button.active { background: white; color: #1a1a2e; font-weight: 700; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }

.header-content { max-width: 900px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
.header-actions { display: flex; gap: 8px; }
.header h2 { margin: 0; font-size: 20px; font-weight: 800; letter-spacing: -0.3px; }

.btn-back { padding: 10px 20px; background: rgba(255, 255, 255, 0.08); color: white; border: 1px solid rgba(255, 255, 255, 0.1); border-radius: 12px; cursor: pointer; font-weight: 600; font-size: 14px; transition: all 0.25s; font-family: inherit; }
.btn-back:hover { background: rgba(255, 255, 255, 0.15); }
.btn-ghost { padding: 10px 18px; background: rgba(255, 255, 255, 0.08); color: rgba(255, 255, 255, 0.9); border: 1px solid rgba(255, 255, 255, 0.1); border-radius: 12px; cursor: pointer; font-weight: 500; font-size: 14px; transition: all 0.25s; font-family: inherit; }
.btn-ghost:hover { background: rgba(255, 255, 255, 0.15); }

.content { max-width: 900px; margin: 24px auto; padding: 0 24px 60px; }
.loading { text-align: center; padding: 80px 20px; color: #9ca3af; }
.spinner { width: 36px; height: 36px; border: 3px solid #e5e7eb; border-top-color: #ff6b35; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 16px; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state { text-align: center; padding: 100px 20px; background: white; border-radius: 24px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04); }
.empty-icon { font-size: 56px; margin-bottom: 12px; }
.empty-state h3 { color: #1a1a2e; margin: 0 0 8px; font-size: 20px; }
.empty-state p { color: #9ca3af; margin: 0 0 24px; font-size: 15px; }
.btn-primary { padding: 14px 40px; background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%); color: white; border: none; border-radius: 14px; cursor: pointer; font-size: 15px; font-weight: 700; font-family: inherit; box-shadow: 0 4px 16px rgba(255, 107, 53, 0.3); transition: all 0.3s; }
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 6px 24px rgba(255, 107, 53, 0.4); }

.orders-list { display: flex; flex-direction: column; gap: 16px; }

.order-card {
  background: white; border-radius: 18px; overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}
.order-card:hover { transform: translateY(-2px); box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08); }

.order-header { padding: 18px 22px; background: #fafbfc; display: flex; justify-content: space-between; align-items: center; }
.order-info { display: flex; gap: 12px; align-items: center; }
.order-number { font-size: 13px; color: #6b7280; font-weight: 600; font-family: 'SF Mono', 'Fira Code', monospace; }
.status { padding: 5px 14px; border-radius: 100px; font-size: 12px; font-weight: 700; }

.status.pending { background: #fffbeb; color: #d97706; }
.status.accepted { background: #eff6ff; color: #2563eb; }
.status.delivering { background: #ecfdf5; color: #059669; }
.status.completed { background: #f0fdf4; color: #16a34a; }
.status.cancelled { background: #fef2f2; color: #dc2626; }
.status.cancelling { background: #fefce8; color: #ca8a04; }

.order-time { font-size: 13px; color: #9ca3af; font-weight: 500; }

.order-body { padding: 16px 22px; }
.order-items-preview { display: flex; flex-wrap: wrap; align-items: center; gap: 10px; }
.preview-item { display: flex; align-items: center; gap: 8px; background: #f9fafb; padding: 6px 12px; border-radius: 10px; }
.preview-img { width: 36px; height: 36px; object-fit: cover; border-radius: 8px; }
.preview-name { font-size: 13px; color: #374151; font-weight: 500; max-width: 100px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.preview-qty { font-size: 12px; color: #9ca3af; }
.more-items { font-size: 13px; color: #ff6b35; font-weight: 600; }

.order-footer { padding: 18px 22px; border-top: 1px solid #f3f4f6; display: flex; justify-content: space-between; align-items: center; }
.total-price { font-size: 24px; font-weight: 800; color: #ff6b35; }
.order-actions { display: flex; gap: 8px; }

.btn-detail { padding: 8px 16px; background: #f3f4f6; color: #374151; border: none; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; transition: all 0.2s; }
.btn-detail:hover { background: #e5e7eb; }
.btn-cancel { padding: 8px 16px; background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; transition: all 0.2s; }
.btn-cancel:hover { background: #fbe9e9; }
.btn-success { padding: 8px 16px; background: #ecfdf5; color: #059669; border: 1px solid #a7f3d0; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; transition: all 0.2s; }
.btn-success:hover { background: #d1fae5; }
.btn-review { padding: 8px 16px; background: linear-gradient(135deg, #f59e0b, #d97706); color: white; border: none; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; transition: all 0.2s; }
.btn-review:hover { transform: scale(1.03); }
.btn-delete { padding: 8px 16px; background: #f9fafb; color: #9ca3af; border: 1px solid #e5e7eb; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; transition: all 0.2s; }
.btn-delete:hover { color: #6b7280; border-color: #d1d5db; }

.toast { position: fixed; top: 100px; left: 50%; transform: translateX(-50%); background: #1a1a2e; color: white; padding: 14px 28px; border-radius: 14px; font-size: 14px; font-weight: 600; z-index: 1000; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25); animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateX(-50%) translateY(-16px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }

.modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.4); backdrop-filter: blur(4px); display: flex; justify-content: center; align-items: center; z-index: 2000; }
.detail-modal { background: white; border-radius: 20px; width: 90%; max-width: 500px; max-height: 80vh; overflow-y: auto; box-shadow: 0 24px 64px rgba(0, 0, 0, 0.25); animation: slideUp 0.3s ease-out; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; background: linear-gradient(135deg, #1a1a2e, #16213e); color: white; border-radius: 20px 20px 0 0; }
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 800; }
.btn-close { width: 32px; height: 32px; background: rgba(255, 255, 255, 0.15); border: none; border-radius: 50%; color: white; font-size: 20px; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.btn-close:hover { background: rgba(255, 255, 255, 0.25); }
.modal-body { padding: 24px; }

.detail-section { margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid #f3f4f6; }
.detail-section:last-child { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
.detail-section h4 { margin: 0 0 12px; font-size: 14px; font-weight: 800; color: #1a1a2e; text-transform: uppercase; letter-spacing: 0.5px; }
.detail-section p { margin: 8px 0; font-size: 14px; color: #6b7280; display: flex; gap: 10px; }
.detail-section p strong { color: #374151; min-width: 72px; font-weight: 600; }

.detail-item { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid #f9fafb; }
.detail-item:last-child { border-bottom: none; }
.detail-item-image { width: 48px; height: 48px; object-fit: cover; border-radius: 10px; }
.detail-item-info { flex: 1; }
.item-name { font-weight: 600; color: #1a1a2e; margin-bottom: 2px; font-size: 14px; }
.item-price { color: #9ca3af; font-size: 13px; }
.detail-item-total { font-weight: 700; color: #1a1a2e; font-size: 15px; }
.no-items { color: #d1d5db; text-align: center; padding: 20px 0; }
.total-section { display: flex; justify-content: space-between; align-items: center; background: #f9fafb; padding: 16px; border-radius: 14px; }
.total-section span { font-size: 15px; font-weight: 600; color: #374151; }
.total-amount { font-size: 24px !important; font-weight: 800 !important; color: #ff6b35 !important; }
</style>
