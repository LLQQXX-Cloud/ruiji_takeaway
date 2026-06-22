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
      <div v-if="loading" class="loading"><div class="spinner"></div><p>加载中...</p></div>
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
              <textarea v-model="formData.description" placeholder="描述您店铺的特色..."></textarea>
            </div>
          </div>

          <div class="form-section">
            <h3>营业信息</h3>
            <div class="form-row">
              <div class="form-group">
                <label>起送价 (元)</label>
                <input v-model.number="formData.startPrice" type="number" step="0.01" placeholder="0.00" />
              </div>
              <div class="form-group">
                <label>配送费 (元)</label>
                <input v-model.number="formData.deliveryFee" type="number" step="0.01" placeholder="0.00" />
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
              <label>店铺封面图片 URL</label>
              <input v-model="formData.image" type="text" placeholder="https://example.com/image.jpg" />
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
  name: '', phone: '', address: '', description: '',
  startPrice: 0, deliveryFee: 0, status: 1, image: '', categoryId: null
})

const categories = [
  { id: 1, name: '快餐便当' }, { id: 2, name: '中式美食' },
  { id: 3, name: '西式料理' }, { id: 4, name: '日韩料理' },
  { id: 5, name: '甜品饮品' }, { id: 6, name: '烧烤火锅' }
]

const originalData = reactive({})

const loadBusinessInfo = async () => {
  loading.value = true
  const businessId = localStorage.getItem('businessId')
  try {
    const res = await businessApi.getById(businessId)
    if (res.data.success) {
      const data = res.data.data
      formData.name = data.name; formData.phone = data.phone || ''; formData.address = data.address || ''
      formData.description = data.description || ''; formData.startPrice = data.startPrice || 0
      formData.deliveryFee = data.deliveryFee || 0; formData.status = data.status || 1
      formData.image = data.image || ''; formData.categoryId = data.categoryId || null
      Object.assign(originalData, formData)
    }
  } catch (error) { console.error('加载店铺信息失败:', error) }
  finally { loading.value = false }
}

const saveInfo = async () => {
  try {
    const businessId = localStorage.getItem('businessId')
    const payload = {
      name: formData.name, phone: formData.phone, address: formData.address,
      description: formData.description, startPrice: formData.startPrice,
      deliveryFee: formData.deliveryFee, status: formData.status,
      image: formData.image, categoryId: formData.categoryId ? Number(formData.categoryId) : null
    }
    const res = await businessApi.update(businessId, payload)
    if (res.data.success) { showToastMessage('修改成功'); Object.assign(originalData, formData) }
  } catch (error) { showToastMessage('修改失败') }
}

const resetForm = () => { Object.assign(formData, originalData) }

const showToastMessage = (msg) => { toastMessage.value = msg; showToast.value = true; setTimeout(() => showToast.value = false, 2000) }
const goBack = () => router.push('/business-home')

onMounted(() => {
  const role = localStorage.getItem('role'); const business = localStorage.getItem('business')
  if (!business || role !== 'business') { router.push('/business-login'); return }
  loadBusinessInfo()
})
</script>

<style scoped>
.business-info { min-height: 100vh; background: #f8f9fb; }

.header { background: linear-gradient(135deg, #0d9488 0%, #115e59 100%); color: white; padding: 16px 24px; position: sticky; top: 0; z-index: 100; box-shadow: 0 4px 24px rgba(13, 148, 136, 0.2); }
.header-content { max-width: 800px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
.header h2 { margin: 0; font-size: 20px; font-weight: 800; letter-spacing: -0.3px; }
.btn-back { padding: 10px 20px; background: rgba(255, 255, 255, 0.1); color: white; border: 1px solid rgba(255,255,255,0.15); border-radius: 12px; cursor: pointer; font-weight: 600; font-size: 14px; font-family: inherit; transition: all 0.25s; }
.btn-back:hover { background: rgba(255, 255, 255, 0.2); }

.content { max-width: 800px; margin: 32px auto; padding: 0 24px; }
.loading { text-align: center; padding: 80px 20px; color: #9ca3af; }
.spinner { width: 36px; height: 36px; border: 3px solid #e5e7eb; border-top-color: #0d9488; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 16px; }
@keyframes spin { to { transform: rotate(360deg); } }

.form-container { background: white; border-radius: 20px; padding: 36px; box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04); }

.form-section { margin-bottom: 32px; padding-bottom: 28px; border-bottom: 1px solid #f3f4f6; }
.form-section:last-of-type { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
.form-section h3 { margin: 0 0 20px; color: #1a1a2e; font-size: 16px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.5px; }

.form-row { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
.form-group { margin-bottom: 20px; }
.form-group:last-child { margin-bottom: 0; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 600; color: #374151; font-size: 13px; }
.form-group input, .form-group textarea, .form-group select {
  width: 100%; padding: 13px 16px; border: 2px solid #e5e7eb; border-radius: 12px;
  font-size: 14px; font-family: inherit; box-sizing: border-box;
  transition: all 0.3s; background: #fafbfc; color: #1a1a2e;
}
.form-group input:focus, .form-group textarea:focus, .form-group select:focus {
  outline: none; border-color: #0d9488; background: white;
  box-shadow: 0 0 0 4px rgba(13, 148, 136, 0.08);
}
.form-group textarea { min-height: 100px; resize: vertical; }

.preview { margin-top: 14px; }
.preview img { max-width: 280px; border-radius: 12px; box-shadow: 0 4px 16px rgba(0,0,0,0.1); }

.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 32px; }
.btn-cancel { padding: 13px 28px; background: #f3f4f6; color: #6b7280; border: none; border-radius: 14px; cursor: pointer; font-weight: 600; font-size: 14px; font-family: inherit; transition: all 0.25s; }
.btn-cancel:hover { background: #e5e7eb; }
.btn-submit { padding: 13px 28px; background: linear-gradient(135deg, #0d9488, #059669); color: white; border: none; border-radius: 14px; cursor: pointer; font-weight: 700; font-size: 14px; font-family: inherit; box-shadow: 0 4px 16px rgba(13, 148, 136, 0.3); transition: all 0.3s; }
.btn-submit:hover { transform: translateY(-2px); box-shadow: 0 6px 24px rgba(13, 148, 136, 0.4); }

.toast { position: fixed; top: 80px; left: 50%; transform: translateX(-50%); background: #1a1a2e; color: white; padding: 14px 28px; border-radius: 14px; font-size: 14px; font-weight: 600; z-index: 1001; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25); }
</style>
