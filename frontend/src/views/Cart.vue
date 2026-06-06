<template>
  <div class="cart-container">
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="btn-back">← 返回</button>
        <h2>购物车</h2>
        <button @click="goToOrders" class="btn-icon">我的订单</button>
      </div>
    </header>
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="cartItems.length === 0" class="empty">
        <p>购物车是空的</p>
        <button @click="goHome" class="btn-primary">去逛逛</button>
      </div>
      <div v-else>
        <div class="cart-list">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <img :src="getValidImageUrl(item.image)" :alt="item.foodName" class="item-image" crossorigin="anonymous" @error="handleImageError" />
            <div class="item-info">
              <h3>{{ item.foodName || '菜品' + item.foodId }}</h3>
              <p class="item-price">{{ item.price ? item.price.toFixed(2) : '0.00' }}元</p>
            </div>
            <div class="item-actions">
              <button @click="decreaseQuantity(item)" class="btn-count">-</button>
              <span class="quantity">{{ item.quantity }}</span>
              <button @click="increaseQuantity(item)" class="btn-count">+</button>
              <button @click="removeItem(item)" class="btn-remove">删除</button>
            </div>
          </div>
        </div>
        
        <div class="cart-summary">
          <div class="summary-info">
            <p>共 {{ totalQuantity }} 件商品</p>
            <p class="total-price">合计: {{ totalPrice }}元</p>
          </div>
          <button @click="submitOrder" class="btn-submit">提交订单</button>
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
  
  try {
    const userId = localStorage.getItem('userId')
    const userData = JSON.parse(localStorage.getItem('user') || '{}')
    const firstItem = cartItems.value[0]
    
    const res = await orderApi.create({
      userId: parseInt(userId),
      businessId: firstItem.businessId,
      totalPrice: parseFloat(totalPrice.value),
      address: userData.address || '未设置地址',
      contact: userData.nickname || userData.username,
      phone: userData.phone || '未填写',
      remark: ''
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
})
</script>

<style scoped>
.cart-container {
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

.cart-list {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.cart-item {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  transition: background 0.3s;
}

.cart-item:hover {
  background: #fafafa;
}

.item-image {
  width: 90px;
  height: 90px;
  object-fit: cover;
  border-radius: 16px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.cart-item:last-child {
  border-bottom: none;
}

.item-info h3 {
  margin: 0 0 10px;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.item-price {
  color: #ff5722;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-count {
  width: 40px;
  height: 40px;
  border: 2px solid #e9ecef;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  font-size: 20px;
  color: #333;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-count:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
  transform: scale(1.1);
}

.quantity {
  font-size: 18px;
  font-weight: 700;
  min-width: 40px;
  text-align: center;
  color: #333;
}

.btn-remove {
  padding: 10px 16px;
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

.btn-remove:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(255,68,68,0.4);
}

.cart-summary {
  margin-top: 25px;
  background: white;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-info p {
  margin: 8px 0;
  color: #666;
  font-size: 16px;
}

.total-price {
  color: #ff5722 !important;
  font-size: 28px !important;
  font-weight: 700 !important;
}

.btn-submit {
  padding: 18px 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 14px;
  cursor: pointer;
  font-size: 18px;
  font-weight: 600;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-submit:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
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
