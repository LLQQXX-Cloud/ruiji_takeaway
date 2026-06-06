<template>
  <div class="business-info">
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="btn-back">← 返回</button>
        <h2>店铺信息</h2>
        <div></div>
      </div>
    </header>
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else class="form-container">
        <form @submit.prevent="saveInfo">
          <div class="form-section">
            <h3>基本信息</h3>
            <div class="form-row">
              <div class="form-group">
                <label>商家名称 *</label>
                <input v-model="formData.name" type="text" required placeholder="请输入商家名称" />
              </div>
              <div class="form-group">
                <label>联系电话</label>
                <input v-model="formData.phone" type="tel" placeholder="请输入联系电话" />
              </div>
            </div>
            <div class="form-group">
              <label>店铺地址</label>
              <input v-model="formData.address" type="text" placeholder="请输入店铺地址" />
            </div>
            <div class="form-group">
              <label>店铺描述</label>
              <textarea v-model="formData.description" placeholder="请输入店铺描述"></textarea>
            </div>
          </div>
          
          <div class="form-section">
            <h3>营业信息</h3>
            <div class="form-row">
              <div class="form-group">
                <label>起送价</label>
                <input v-model.number="formData.startPrice" type="number" step="0.01" placeholder="起送价" />
              </div>
              <div class="form-group">
                <label>配送费</label>
                <input v-model.number="formData.deliveryFee" type="number" step="0.01" placeholder="配送费" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>店铺分类</label>
                <select v-model.number="formData.categoryId">
                  <option :value="null">请选择分类</option>
                  <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
                </select>
              </div>
              <div class="form-group">
                <label>店铺状态</label>
                <select v-model.number="formData.status">
                  <option :value="1">营业中</option>
                  <option :value="0">已关闭</option>
                </select>
              </div>
            </div>
          </div>
          
          <div class="form-section">
            <h3>图片设置</h3>
            <div class="form-group">
              <label>店铺图片URL</label>
              <input v-model="formData.image" type="text" placeholder="请输入图片URL" />
              <div v-if="formData.image" class="preview">
                <img :src="formData.image" alt="店铺图片" />
              </div>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" @click="resetForm" class="btn-cancel">重置</button>
            <button type="submit" class="btn-submit">保存修改</button>
          </div>
        </form>
      </div>
    </div>
    
    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { businessApi } from '../api/business'

const router = useRouter()
const loading = ref(false)
const showToast = ref(false)
const toastMessage = ref('')

const formData = reactive({
  name: '',
  phone: '',
  address: '',
  description: '',
  startPrice: 0,
  deliveryFee: 0,
  status: 1,
  image: '',
  categoryId: null
})

const categories = [
  { id: 1, name: '快餐便当' },
  { id: 2, name: '中式美食' },
  { id: 3, name: '西式料理' },
  { id: 4, name: '日韩料理' },
  { id: 5, name: '甜品饮品' },
  { id: 6, name: '烧烤火锅' }
]

const originalData = reactive({})

const loadBusinessInfo = async () => {
  loading.value = true
  const businessId = localStorage.getItem('businessId')
  
  try {
    const res = await businessApi.getById(businessId)
    if (res.data.success) {
      const data = res.data.data
      formData.name = data.name
      formData.phone = data.phone || ''
      formData.address = data.address || ''
      formData.description = data.description || ''
      formData.startPrice = data.startPrice || 0
      formData.deliveryFee = data.deliveryFee || 0
      formData.status = data.status || 1
      formData.image = data.image || ''
      formData.categoryId = data.categoryId || null
      
      Object.assign(originalData, formData)
    }
  } catch (error) {
    console.error('加载店铺信息失败:', error)
  } finally {
    loading.value = false
  }
}

const saveInfo = async () => {
  try {
    const businessId = localStorage.getItem('businessId')
    const payload = {
      name: formData.name,
      phone: formData.phone,
      address: formData.address,
      description: formData.description,
      startPrice: formData.startPrice,
      deliveryFee: formData.deliveryFee,
      status: formData.status,
      image: formData.image,
      categoryId: formData.categoryId ? Number(formData.categoryId) : null
    }
    console.log('保存的数据:', JSON.stringify(payload))
    console.log('categoryId类型:', typeof payload.categoryId, '值:', payload.categoryId)
    const res = await businessApi.update(businessId, payload)
    if (res.data.success) {
      showToastMessage('修改成功')
      Object.assign(originalData, formData)
    }
  } catch (error) {
    showToastMessage('修改失败')
  }
}

const resetForm = () => {
  Object.assign(formData, originalData)
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
  
  loadBusinessInfo()
})
</script>

<style scoped>
.business-info {
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
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  font-size: 20px;
}

.btn-back {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid white;
  border-radius: 6px;
  cursor: pointer;
}

.content {
  max-width: 800px;
  margin: 30px auto;
  padding: 0 20px;
}

.loading {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  color: #999;
}

.form-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.form-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-of-type {
  border-bottom: none;
}

.form-section h3 {
  margin-bottom: 20px;
  color: #333;
  font-size: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input, .form-group textarea, .form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group textarea {
  min-height: 100px;
}

.preview {
  margin-top: 15px;
}

.preview img {
  max-width: 300px;
  border-radius: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
}

.btn-cancel {
  padding: 12px 24px;
  background: #f0f0f0;
  color: #666;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-submit {
  padding: 12px 24px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
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
