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
      <div v-if="loading" class="loading"><div class="spinner"></div><p>加载中...</p></div>
      <div v-else-if="filteredOrders.length === 0" class="empty-state">
        <div class="empty-icon">📋</div><h3>暂无订单</h3>
      </div>
      <div v-else class="order-list">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-id">#{{ order.orderNumber }}</div>
            <div :class="['order-status', `status-${order.status}`]">{{ getStatusText(order.status) }}</div>
          </div>
          <div class="order-items">
            <div v-for="(item, index) in order.orderItems" :key="index" class="order-item">
              <img :src="getValidImageUrl(item.image)" :alt="item.foodName" class="item-image" />
              <div class="item-info">
                <div class="item-name">{{ item.foodName }}</div>
                <div class="item-price">{{ (item.price || 0).toFixed(2) }} 元 × {{ item.quantity }}</div>
              </div>
            </div>
          </div>
          <div class="order-footer">
            <div class="order-total">合计 {{ order.totalPrice.toFixed(2) }} 元</div>
            <div class="order-time">{{ formatTime(order.createTime) }}</div>
          </div>
          <div class="order-actions">
            <button @click="viewDetail(order)" class="btn-detail">详情</button>
            <template v-if="order.status === 0">
              <button @click="confirmOrder(order)" class="btn-confirm">确认</button>
              <button @click="cancelOrder(order)" class="btn-cancel">拒绝</button>
            </template>
            <template v-else-if="order.status === 5">
              <button @click="approveCancel(order)" class="btn-approve">同意取消</button>
              <button @click="rejectCancel(order)" class="btn-reject">拒绝取消</button>
            </template>
            <template v-else-if="order.status === 1">
              <button @click="startDelivery(order)" class="btn-deliver">开始配送</button>
            </template>
            <template v-else-if="order.status === 2">
              <button @click="confirmDelivery(order)" class="btn-delivery">确认送达</button>
            </template>
            <template v-else-if="order.status === 3 || order.status === 4">
              <button @click="deleteOrder(order)" class="btn-delete">删除</button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showDetailModal" class="modal-overlay" @click.self="closeDetailModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>订单详情</h3>
          <button @click="closeDetailModal" class="close-btn">×</button>
        </div>
        <div class="modal-body" v-if="selectedOrder">
          <div class="detail-section"><h4>订单信息</h4><p><strong>订单号</strong> {{ selectedOrder.orderNumber }}</p><p><strong>状态</strong> {{ getStatusText(selectedOrder.status) }}</p><p><strong>下单时间</strong> {{ formatTime(selectedOrder.createTime) }}</p></div>
          <div class="detail-section"><h4>配送信息</h4><p><strong>地址</strong> {{ selectedOrder.address }}</p><p><strong>联系人</strong> {{ selectedOrder.contact }}</p><p><strong>电话</strong> {{ selectedOrder.phone }}</p><p v-if="selectedOrder.remark"><strong>备注</strong> {{ selectedOrder.remark }}</p></div>
          <div class="detail-section">
            <h4>订单商品</h4>
            <div v-if="selectedOrder.orderItems && selectedOrder.orderItems.length > 0">
              <div v-for="(item, index) in selectedOrder.orderItems" :key="index" class="detail-item">
                <img :src="getValidImageUrl(item.image)" :alt="item.foodName" class="detail-item-image" />
                <div class="detail-item-info"><div class="item-name">{{ item.foodName }}</div><div class="item-price">{{ item.price?.toFixed(2) || '0.00' }} 元 × {{ item.quantity }}</div></div>
                <div class="detail-item-total">{{ ((item.price || 0) * item.quantity).toFixed(2) }} 元</div>
              </div>
            </div>
            <div v-else class="no-items">暂无商品信息</div>
          </div>
          <div class="detail-section total-section"><span>订单金额</span><span class="total-amount">{{ selectedOrder.totalPrice.toFixed(2) }} 元</span></div>
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

const getValidImageUrl = (url) => {
  if (!url || !url.trim()) return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 80 80"><rect fill="#f0f0f0" width="80" height="80"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="12">菜品</text></svg>')
  const validPatterns = /\.(jpg|jpeg|png|gif|webp|bmp)(\?.*)?$/i
  if (validPatterns.test(url)) return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(url)}`
  const mediaUrlMatch = url.match(/mediaurl=([^&]+)/)
  if (mediaUrlMatch && validPatterns.test(mediaUrlMatch[1])) return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(decodeURIComponent(mediaUrlMatch[1]))}`
  return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 80 80"><rect fill="#f0f0f0" width="80" height="80"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="12">菜品</text></svg>')
}

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const statusFilter = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const showDetailModal = ref(false)
const selectedOrder = ref(null)

const filteredOrders = computed(() => { if (!statusFilter.value) return orders.value; return orders.value.filter(o => o.status === parseInt(statusFilter.value)) })

const getStatusText = (status) => { const m = { 0:'待确认',1:'已确认',2:'配送中',3:'已完成',4:'已取消',5:'用户申请取消' }; return m[status] || '未知' }
const formatTime = (time) => { if (!time) return ''; return new Date(time).toLocaleString('zh-CN') }

const loadOrders = async () => { loading.value = true; const businessId = localStorage.getItem('businessId'); try { const res = await orderApi.getByBusiness(businessId); if (res.data.success) orders.value = res.data.data } catch (error) { console.error('加载订单失败:', error) } finally { loading.value = false } }
const confirmOrder = async (order) => { try { const res = await orderApi.updateStatus(order.id, 1); if (res.data.success) { order.status = 1; showToastMessage('已确认') } } catch (e) { showToastMessage('失败') } }
const cancelOrder = async (order) => { if (!confirm('确定拒绝此订单？')) return; try { const res = await orderApi.cancelByBusiness(order.id); if (res.data.success) { order.status = 4; showToastMessage('已拒绝') } } catch (e) { showToastMessage('失败') } }
const approveCancel = async (order) => { if (!confirm('同意取消？')) return; try { const res = await orderApi.approveCancel(order.id); if (res.data.success) { order.status = 4; showToastMessage('已同意取消') } } catch (e) { showToastMessage('失败') } }
const rejectCancel = async (order) => { if (!confirm('拒绝取消？')) return; try { const res = await orderApi.rejectCancel(order.id); if (res.data.success) { order.status = 0; showToastMessage('已拒绝取消申请') } } catch (e) { showToastMessage('失败') } }
const startDelivery = async (order) => { try { const res = await orderApi.updateStatus(order.id, 2); if (res.data.success) { order.status = 2; showToastMessage('已开始配送') } } catch (e) { showToastMessage('失败') } }
const confirmDelivery = async (order) => { try { const res = await orderApi.updateStatus(order.id, 3); if (res.data.success) { order.status = 3; showToastMessage('已确认送达') } } catch (e) { showToastMessage('失败') } }
const deleteOrder = async (order) => { if (!confirm('确定删除？')) return; try { const res = await orderApi.deleteByBusiness(order.id); if (res.data.success) { orders.value = orders.value.filter(o => o.id !== order.id); showToastMessage('已删除') } } catch (e) { showToastMessage('失败') } }
const viewDetail = (order) => { selectedOrder.value = order; showDetailModal.value = true }
const closeDetailModal = () => { showDetailModal.value = false; selectedOrder.value = null }
const showToastMessage = (msg) => { toastMessage.value = msg; showToast.value = true; setTimeout(() => showToast.value = false, 2000) }
const goBack = () => router.push('/business-home')

onMounted(() => { const r = localStorage.getItem('role'); const b = localStorage.getItem('business'); if (!b || r !== 'business') { router.push('/business-login'); return } loadOrders() })
</script>

<style scoped>
.business-orders { min-height: 100vh; background: #f8f9fb; }

.header { background: linear-gradient(135deg, #0d9488 0%, #115e59 100%); color: white; padding: 18px 24px; position: sticky; top: 0; z-index: 100; box-shadow: 0 4px 24px rgba(13, 148, 136, 0.2); }

.header-content { max-width: 900px; margin: 0 auto; }
.header-content h2 { margin: 0 0 14px; font-size: 20px; font-weight: 800; letter-spacing: -0.3px; display: flex; justify-content: space-between; align-items: center; }

.btn-back { padding: 10px 20px; background: rgba(255,255,255,0.1); color: white; border: 1px solid rgba(255,255,255,0.15); border-radius: 12px; cursor: pointer; font-weight: 600; font-size: 14px; font-family: inherit; transition: all 0.25s; }

.filter-tabs { display: flex; gap: 6px; flex-wrap: wrap; }
.filter-tabs button { padding: 8px 18px; background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.75); border: none; border-radius: 100px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; transition: all 0.25s; }
.filter-tabs button:hover { background: rgba(255,255,255,0.15); }
.filter-tabs button.active { background: white; color: #0d9488; font-weight: 700; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }

.content { max-width: 900px; margin: 24px auto; padding: 0 24px 60px; }
.loading { text-align: center; padding: 80px 20px; color: #9ca3af; }
.spinner { width: 36px; height: 36px; border: 3px solid #e5e7eb; border-top-color: #0d9488; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 16px; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state { text-align: center; padding: 100px 20px; background: white; border-radius: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.empty-icon { font-size: 48px; }
.empty-state h3 { color: #1a1a2e; margin: 12px 0 0; font-size: 18px; }

.order-list { display: flex; flex-direction: column; gap: 16px; }
.order-card { background: white; border-radius: 18px; padding: 22px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); transition: all 0.3s; }
.order-card:hover { transform: translateY(-2px); box-shadow: 0 8px 28px rgba(0,0,0,0.08); }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; padding-bottom: 14px; border-bottom: 1px dashed #e5e7eb; }
.order-id { font-size: 13px; color: #6b7280; font-weight: 600; font-family: 'SF Mono', 'Fira Code', monospace; }

.order-status { padding: 5px 14px; border-radius: 100px; font-size: 12px; font-weight: 700; }
.order-status.status-0 { background: #fffbeb; color: #d97706; }
.order-status.status-1 { background: #ecfdf5; color: #059669; }
.order-status.status-2 { background: #eff6ff; color: #2563eb; }
.order-status.status-3 { background: #f0fdf4; color: #16a34a; }
.order-status.status-4 { background: #fef2f2; color: #dc2626; }
.order-status.status-5 { background: #fefce8; color: #ca8a04; }

.order-items { margin-bottom: 16px; }
.order-item { display: flex; gap: 14px; padding: 10px 0; border-radius: 10px; transition: background 0.2s; }
.order-item:hover { background: #f9fafb; }
.item-image { width: 72px; height: 72px; object-fit: cover; border-radius: 14px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); flex-shrink: 0; }
.item-info { flex: 1; display: flex; flex-direction: column; justify-content: center; }
.item-name { font-weight: 700; color: #1a1a2e; margin-bottom: 4px; font-size: 15px; }
.item-price { color: #9ca3af; font-size: 13px; }

.order-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 14px; border-top: 1px solid #f3f4f6; }
.order-total { font-size: 20px; font-weight: 800; color: #0d9488; }
.order-time { font-size: 13px; color: #9ca3af; font-weight: 500; }

.order-actions { display: flex; gap: 8px; margin-top: 14px; }
.btn-detail { padding: 8px 16px; background: #f3f4f6; color: #374151; border: none; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; }
.btn-confirm { flex: 1; padding: 10px; background: #ecfdf5; color: #059669; border: 1px solid #a7f3d0; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; }
.btn-cancel { flex: 1; padding: 10px; background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; }
.btn-deliver { flex: 1; padding: 10px; background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; }
.btn-delivery { flex: 1; padding: 10px; background: #fffbeb; color: #d97706; border: 1px solid #fde68a; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; }
.btn-approve { flex: 1; padding: 10px; background: #ecfdf5; color: #059669; border: 1px solid #a7f3d0; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; }
.btn-reject { flex: 1; padding: 10px; background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; }
.btn-delete { flex: 1; padding: 10px; background: #f9fafb; color: #9ca3af; border: 1px solid #e5e7eb; border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; }

.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); backdrop-filter: blur(4px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; border-radius: 20px; width: 100%; max-width: 500px; max-height: 80vh; overflow-y: auto; box-shadow: 0 24px 64px rgba(0,0,0,0.25); animation: slideUp 0.3s ease-out; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; background: linear-gradient(135deg, #0d9488, #115e59); color: white; border-radius: 20px 20px 0 0; }
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 800; }
.close-btn { width: 32px; height: 32px; border: none; background: rgba(255,255,255,0.15); color: white; border-radius: 50%; font-size: 20px; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.modal-body { padding: 24px; }
.detail-section { margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid #f3f4f6; }
.detail-section:last-child { margin-bottom: 0; padding-bottom: 0; border-bottom: none; }
.detail-section h4 { margin: 0 0 12px; font-size: 13px; font-weight: 800; color: #1a1a2e; text-transform: uppercase; letter-spacing: 0.5px; }
.detail-section p { margin: 8px 0; color: #6b7280; font-size: 14px; display: flex; gap: 10px; }
.detail-section p strong { color: #374151; min-width: 72px; font-weight: 600; }
.detail-item { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid #f9fafb; }
.detail-item:last-child { border-bottom: none; }
.detail-item-image { width: 48px; height: 48px; object-fit: cover; border-radius: 10px; }
.detail-item-info { flex: 1; }
.detail-item-info div:first-child { font-weight: 600; color: #1a1a2e; margin-bottom: 2px; }
.detail-item-info div:last-child { color: #9ca3af; font-size: 13px; }
.detail-item-total { font-weight: 700; color: #1a1a2e; font-size: 15px; }
.no-items { color: #d1d5db; text-align: center; padding: 20px 0; }
.total-section { display: flex; justify-content: space-between; align-items: center; background: #f0fdfa; padding: 16px; border-radius: 14px; }
.total-section span { font-size: 15px; font-weight: 600; color: #374151; }
.total-amount { font-size: 24px !important; font-weight: 800 !important; color: #0d9488 !important; }

.toast { position: fixed; top: 100px; left: 50%; transform: translateX(-50%); background: #1a1a2e; color: white; padding: 14px 28px; border-radius: 14px; font-size: 14px; font-weight: 600; z-index: 1001; box-shadow: 0 8px 32px rgba(0,0,0,0.25); animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateX(-50%) translateY(-16px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }
</style>
