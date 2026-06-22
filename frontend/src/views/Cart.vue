<template>
  <div class="cart-container">
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="btn-back">← 返回</button>
        <h2>购物车</h2>
        <button @click="goToOrders" class="btn-ghost">我的订单</button>
      </div>
    </header>

    <div class="content">
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
      <div v-else-if="cartItems.length === 0" class="empty-state">
        <div class="empty-icon">🛒</div>
        <h3>购物车是空的</h3>
        <p>快去选择喜欢的菜品吧</p>
        <button @click="goHome" class="btn-primary">去逛逛</button>
      </div>
      <div v-else>
        <div class="address-section" @click="showAddressModal = true">
          <div class="address-icon">📍</div>
          <div v-if="selectedAddress" class="address-info">
            <div class="address-contact">{{ selectedAddress.name }} {{ selectedAddress.phone }}</div>
            <div class="address-detail">{{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detail }}</div>
          </div>
          <div v-else class="address-info empty-address">
            <span>请选择收货地址</span>
          </div>
          <span class="address-arrow">›</span>
        </div>

        <div class="cart-list">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <img :src="getValidImageUrl(item.image)" :alt="item.foodName" class="item-image" crossorigin="anonymous" @error="handleImageError" />
            <div class="item-info">
              <h3>{{ item.foodName || '菜品' + item.foodId }}</h3>
              <p class="item-price">{{ item.price ? item.price.toFixed(2) : '0.00' }} 元</p>
            </div>
            <div class="item-actions">
              <button @click="decreaseQuantity(item)" class="btn-count">−</button>
              <span class="quantity">{{ item.quantity }}</span>
              <button @click="increaseQuantity(item)" class="btn-count">+</button>
              <button @click="removeItem(item)" class="btn-remove">删除</button>
            </div>
          </div>
        </div>

        <div class="cart-summary">
          <div class="summary-info">
            <p class="summary-count">共 {{ totalQuantity }} 件商品</p>
            <p class="total-price">{{ totalPrice }} 元</p>
          </div>
          <button @click="submitOrder" class="btn-submit">提交订单</button>
        </div>
      </div>
    </div>

    <div v-if="showAddressModal" class="modal-overlay" @click.self="showAddressModal = false">
      <div class="modal-content address-modal">
        <div class="modal-header">
          <h3>选择收货地址</h3>
          <button @click="showAddressModal = false" class="btn-close">×</button>
        </div>
        <div class="address-list-modal">
          <div v-if="addresses.length === 0" class="empty-address-list">
            <p>暂无收货地址</p>
            <button @click="goToAddressManager" class="btn-primary">去添加地址</button>
          </div>
          <div v-else>
            <div
              v-for="address in addresses"
              :key="address.id"
              class="address-item"
              :class="{ 'selected': selectedAddress?.id === address.id }"
              @click="selectAddress(address)"
            >
              <div class="address-item-radio">
                <span v-if="selectedAddress?.id === address.id" class="radio-checked">✓</span>
              </div>
              <div class="address-item-info">
                <div class="address-item-contact">{{ address.name }} {{ address.phone }}</div>
                <div class="address-item-detail">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</div>
              </div>
              <span v-if="address.isDefault" class="default-tag">默认</span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="goToAddressManager" class="btn-secondary">+ 添加新地址</button>
        </div>
      </div>
    </div>

    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { cartApi } from '../api/cart'
import { orderApi } from '../api/order'
import { addressApi } from '../api/address'

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

const handleImageError = (event) => {
  event.target.src = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 80 80"><rect fill="#f0f0f0" width="80" height="80"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="12">菜品</text></svg>')
}

const router = useRouter()
const cartItems = ref([])
const loading = ref(false)
const showToast = ref(false)
const toastMessage = ref('')
const addresses = ref([])
const selectedAddress = ref(null)
const showAddressModal = ref(false)

const totalQuantity = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    const price = parseFloat(item.price) || 0
    return sum + (price * item.quantity)
  }, 0).toFixed(2)
})

const loadCart = async () => {
  loading.value = true
  try {
    const userId = localStorage.getItem('userId')
    const res = await cartApi.getByUser(userId)
    if (res.data.success) {
      cartItems.value = res.data.data
    }
  } catch (error) {
    console.error('加载购物车失败:', error)
  } finally {
    loading.value = false
  }
}

const updateQuantity = async (item, quantity) => {
  if (quantity <= 0) {
    await removeItem(item)
    return
  }
  try {
    const userId = localStorage.getItem('userId')
    const res = await cartApi.update({
      userId: parseInt(userId),
      foodId: item.foodId,
      quantity: quantity
    })
    if (res.data.success) {
      item.quantity = quantity
    } else {
      showToastMessage('更新失败')
    }
  } catch (error) {
    console.error('更新数量失败:', error)
    showToastMessage('更新失败')
  }
}

const increaseQuantity = (item) => {
  updateQuantity(item, item.quantity + 1)
}

const decreaseQuantity = (item) => {
  updateQuantity(item, item.quantity - 1)
}

const removeItem = async (item) => {
  try {
    const userId = localStorage.getItem('userId')
    await cartApi.remove(parseInt(userId), item.foodId)
    cartItems.value = cartItems.value.filter(i => i.id !== item.id)
    showToastMessage('已删除')
  } catch (error) {
    showToastMessage('删除失败')
  }
}

const submitOrder = async () => {
  if (cartItems.value.length === 0) {
    showToastMessage('购物车是空的')
    return
  }

  if (!selectedAddress.value) {
    showToastMessage('请选择收货地址')
    return
  }

  try {
    const userId = localStorage.getItem('userId')
    const firstItem = cartItems.value[0]

    const orderItems = cartItems.value.map(item => ({
      foodId: item.foodId,
      foodName: item.foodName || '',
      price: parseFloat(item.price) || 0,
      quantity: item.quantity,
      image: item.image || ''
    }))

    const res = await orderApi.create({
      userId: parseInt(userId),
      businessId: firstItem.businessId,
      totalPrice: parseFloat(totalPrice.value),
      address: selectedAddress.value.province + selectedAddress.value.city + selectedAddress.value.district + selectedAddress.value.detail,
      contact: selectedAddress.value.name,
      phone: selectedAddress.value.phone,
      remark: '',
      orderItems: orderItems
    })

    if (res.data.success) {
      await cartApi.clear(parseInt(userId))
      showToastMessage('下单成功！')
      setTimeout(() => {
        router.push('/orders')
      }, 1500)
    }
  } catch (error) {
    showToastMessage('下单失败')
  }
}

const loadAddresses = async () => {
  const userId = localStorage.getItem('userId')
  const res = await addressApi.getAddresses(userId)
  if (res.data.success) {
    addresses.value = res.data.data
    const defaultAddress = addresses.value.find(a => a.isDefault)
    selectedAddress.value = defaultAddress || addresses.value[0] || null
  }
}

const selectAddress = (address) => {
  selectedAddress.value = address
  showAddressModal.value = false
}

const goToAddressManager = () => {
  showAddressModal.value = false
  router.push('/address')
}

const showToastMessage = (msg) => {
  toastMessage.value = msg
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const goBack = () => {
  router.push('/home')
}

const goToOrders = () => {
  router.push('/orders')
}

const goHome = () => {
  router.push('/home')
}

onMounted(() => {
  const user = localStorage.getItem('user')
  if (!user) {
    router.push('/login')
    return
  }
  loadCart()
  loadAddresses()
})
</script>

<style scoped>
.cart-container {
  min-height: 100vh;
  background: #f8f9fb;
}

/* Header */
.header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: white;
  padding: 18px 24px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
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
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.3px;
}

.btn-back {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.08);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.25s;
  font-family: inherit;
}

.btn-back:hover {
  background: rgba(255, 255, 255, 0.15);
}

.btn-ghost {
  padding: 10px 18px;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.25s;
  font-family: inherit;
}

.btn-ghost:hover {
  background: rgba(255, 255, 255, 0.15);
}

/* Content */
.content {
  max-width: 900px;
  margin: 24px auto;
  padding: 0 24px 60px;
}

.loading {
  text-align: center;
  padding: 80px 20px;
  color: #9ca3af;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #e5e7eb;
  border-top-color: #ff6b35;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin { to { transform: rotate(360deg); } }

.empty-state {
  text-align: center;
  padding: 100px 20px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.empty-icon { font-size: 64px; margin-bottom: 16px; }
.empty-state h3 { color: #1a1a2e; margin: 0 0 8px; font-size: 20px; }
.empty-state p { color: #9ca3af; margin: 0 0 24px; font-size: 15px; }

.btn-primary {
  padding: 14px 40px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  border: none;
  border-radius: 14px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 700;
  font-family: inherit;
  box-shadow: 0 4px 16px rgba(255, 107, 53, 0.3);
  transition: all 0.3s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(255, 107, 53, 0.4);
}

/* Address Section */
.address-section {
  background: white;
  border-radius: 18px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: all 0.25s;
  border: 2px solid transparent;
}

.address-section:hover {
  border-color: #ff6b35;
  box-shadow: 0 4px 20px rgba(255, 107, 53, 0.08);
}

.address-icon { font-size: 24px; }
.address-info { flex: 1; }
.address-contact { font-size: 16px; font-weight: 700; color: #1a1a2e; margin-bottom: 2px; }
.address-detail { font-size: 13px; color: #6b7280; }
.empty-address { color: #d1d5db; }
.address-arrow { font-size: 28px; color: #d1d5db; font-weight: 300; }

/* Cart List */
.cart-list {
  background: white;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.cart-item {
  padding: 22px;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  gap: 18px;
  transition: background 0.2s;
}

.cart-item:hover { background: #fafbfc; }
.cart-item:last-child { border-bottom: none; }

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 14px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.item-info { flex: 1; }
.item-info h3 { margin: 0 0 6px; font-size: 16px; color: #1a1a2e; font-weight: 700; }
.item-price { color: #ff6b35; font-size: 20px; font-weight: 800; margin: 0; }

.item-actions { display: flex; align-items: center; gap: 10px; }
.btn-count {
  width: 36px; height: 36px;
  border: 2px solid #e5e7eb;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  font-size: 18px;
  color: #374151;
  font-weight: 600;
  transition: all 0.2s;
  font-family: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-count:hover { background: #ff6b35; color: white; border-color: #ff6b35; }
.quantity { font-size: 16px; font-weight: 700; min-width: 28px; text-align: center; color: #1a1a2e; }

.btn-remove {
  padding: 8px 14px;
  background: #fef2f2;
  color: #ef4444;
  border: 1px solid #fecaca;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s;
  font-family: inherit;
}
.btn-remove:hover { background: #fbe9e9; }

/* Summary */
.cart-summary {
  margin-top: 20px;
  background: white;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-count { color: #6b7280; font-size: 14px; margin: 0; }
.total-price { color: #ff6b35; font-size: 32px; font-weight: 800; margin: 4px 0 0; }

.btn-submit {
  padding: 16px 40px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  border: none;
  border-radius: 14px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 700;
  font-family: inherit;
  box-shadow: 0 4px 16px rgba(255, 107, 53, 0.3);
  transition: all 0.3s;
  letter-spacing: 0.3px;
}
.btn-submit:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(255, 107, 53, 0.4); }

/* Modal */
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.4);
  backdrop-filter: blur(4px);
  display: flex; align-items: flex-end; justify-content: center;
  z-index: 1000;
}
.modal-content.address-modal {
  background: white;
  width: 100%;
  max-height: 70vh;
  border-radius: 24px 24px 0 0;
  display: flex; flex-direction: column;
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px; border-bottom: 1px solid #f3f4f6;
}
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 800; color: #1a1a2e; }
.btn-close {
  width: 32px; height: 32px; border: none; background: #f3f4f6;
  border-radius: 50%; font-size: 22px; color: #6b7280; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.btn-close:hover { background: #e5e7eb; }
.address-list-modal { flex: 1; overflow-y: auto; padding: 16px; }
.empty-address-list { text-align: center; padding: 40px 20px; color: #9ca3af; }
.address-item {
  display: flex; align-items: center; padding: 16px;
  border-radius: 14px; border: 2px solid #f3f4f6;
  margin-bottom: 12px; cursor: pointer; transition: all 0.25s;
}
.address-item:hover { border-color: #fdba74; }
.address-item.selected { border-color: #ff6b35; background: #fff7ed; }
.address-item-radio {
  width: 24px; height: 24px; border: 2px solid #d1d5db;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  margin-right: 14px; flex-shrink: 0;
}
.address-item.selected .address-item-radio { background: #ff6b35; border-color: #ff6b35; }
.radio-checked { color: white; font-size: 12px; font-weight: bold; }
.address-item-info { flex: 1; }
.address-item-contact { font-size: 16px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; }
.address-item-detail { font-size: 13px; color: #6b7280; }
.default-tag {
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white; padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 700;
}
.modal-footer { padding: 16px 24px; border-top: 1px solid #f3f4f6; }
.btn-secondary {
  width: 100%; padding: 14px; background: white;
  border: 2px solid #ff6b35; color: #ff6b35; border-radius: 14px;
  font-size: 15px; font-weight: 700; cursor: pointer; font-family: inherit;
  transition: all 0.25s;
}
.btn-secondary:hover { background: #fff7ed; }

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
