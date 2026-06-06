<template>
  <div class="food-manage">
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="btn-back">← 返回</button>
        <h2>菜品管理</h2>
        <button @click="showAddForm = true" class="btn-add">+ 添加菜品</button>
      </div>
    </header>
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="foods.length === 0" class="empty">暂无菜品</div>
      <div v-else class="food-grid">
        <div v-for="food in foods" :key="food.id" class="food-card">
          <img :src="getValidImageUrl(food.image)" :alt="food.name" class="food-image" crossorigin="anonymous" @error="handleImageError" />
          <div class="food-info">
            <h3>{{ food.name }}</h3>
            <p class="food-desc">{{ food.description }}</p>
            <div class="food-price">{{ food.price.toFixed(2) }}元</div>
            <div class="food-tags">
              <span v-if="food.isHot" class="tag hot">热销</span>
              <span v-if="food.isNew" class="tag new">新品</span>
            </div>
            <div class="food-actions">
              <button @click="editFood(food)" class="btn-edit">编辑</button>
              <button @click="deleteFood(food)" class="btn-delete">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <div v-if="showAddForm" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingFood ? '编辑菜品' : '添加菜品' }}</h3>
          <button @click="closeModal" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveFood">
            <div class="form-group">
              <label>菜品名称 *</label>
              <input v-model="formData.name" type="text" required placeholder="请输入菜品名称" />
            </div>
            <div class="form-group">
              <label>价格 *</label>
              <input v-model.number="formData.price" type="number" step="0.01" required placeholder="请输入价格" />
            </div>
            <div class="form-group">
              <label>描述</label>
              <textarea v-model="formData.description" placeholder="请输入菜品描述"></textarea>
            </div>
            <div class="form-group">
              <label>图片URL</label>
              <input v-model="formData.image" type="text" placeholder="请输入图片直链地址（如：https://example.com/image.jpg）" />
              <div v-if="formData.image" class="image-preview">
                <img :src="formData.image" @error="$event.target.style.display='none'" />
              </div>
            </div>
            <div class="form-group">
              <label>分类</label>
              <input v-model="formData.category" type="text" placeholder="请输入分类名称" />
            </div>
            <div class="form-group">
              <label>标签</label>
              <div class="checkbox-group">
                <label class="checkbox-item">
                  <input v-model="formData.isHot" type="checkbox" />
                  <span>热销</span>
                </label>
                <label class="checkbox-item">
                  <input v-model="formData.isNew" type="checkbox" />
                  <span>新品</span>
                </label>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" @click="closeModal" class="btn-cancel">取消</button>
              <button type="submit" class="btn-submit">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
    
    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { foodApi } from '../api/food'

const router = useRouter()
const foods = ref([])
const loading = ref(false)
const showAddForm = ref(false)
const editingFood = ref(null)
const showToast = ref(false)
const toastMessage = ref('')

const formData = reactive({
  name: '',
  price: 0,
  description: '',
  image: '',
  category: '',
  isHot: false,
  isNew: false
})

const getValidImageUrl = (url) => {
  if (!url || !url.trim()) {
    return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><rect fill="#f0f0f0" width="200" height="200"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="16">暂无图片</text></svg>')
  }
  // 检查URL是否以图片格式扩展名结尾（直接图片链接）
  const validPatterns = /\.(jpg|jpeg|png|gif|webp|bmp)(\?.*)?$/i
  if (validPatterns.test(url)) {
    // 使用后端代理来绑过CORS限制
    return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(url)}`
  }
  // 如果URL包含图片域名但不是直接链接，尝试提取真实图片URL
  const mediaUrlMatch = url.match(/mediaurl=([^&]+)/)
  if (mediaUrlMatch && validPatterns.test(mediaUrlMatch[1])) {
    const realUrl = decodeURIComponent(mediaUrlMatch[1])
    return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(realUrl)}`
  }
  return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><rect fill="#f0f0f0" width="200" height="200"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="16">暂无图片</text></svg>')
}

const handleImageError = (event) => {
  event.target.src = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><rect fill="#f0f0f0" width="200" height="200"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="16">暂无图片</text></svg>')
}

const loadFoods = async () => {
  loading.value = true
  const businessId = localStorage.getItem('businessId')
  
  try {
    const res = await foodApi.getByBusiness(businessId)
    if (res.data.success) {
      foods.value = res.data.data
      console.log('加载的菜品数据:', foods.value)
    }
  } catch (error) {
    console.error('加载菜品失败:', error)
  } finally {
    loading.value = false
  }
}

const editFood = (food) => {
  editingFood.value = food
  formData.name = food.name
  formData.price = food.price
  formData.description = food.description || ''
  formData.image = food.image || ''
  formData.category = food.category || ''
  formData.isHot = food.isHot || false
  formData.isNew = food.isNew || false
  showAddForm.value = true
}

const deleteFood = async (food) => {
  if (!confirm(`确定删除菜品「${food.name}」吗？`)) return
  
  try {
    const res = await foodApi.delete(food.id)
    if (res.data.success) {
      foods.value = foods.value.filter(f => f.id !== food.id)
      showToastMessage('删除成功')
    }
  } catch (error) {
    showToastMessage('删除失败')
  }
}

const saveFood = async () => {
  try {
    const businessId = localStorage.getItem('businessId')
    formData.businessId = parseInt(businessId)
    
    if (editingFood.value) {
      const res = await foodApi.update(editingFood.value.id, formData)
      if (res.data.success) {
        showToastMessage('修改成功')
        loadFoods()
      }
    } else {
      const res = await foodApi.create(formData)
      if (res.data.success) {
        showToastMessage('添加成功')
        loadFoods()
      }
    }
    closeModal()
  } catch (error) {
    showToastMessage('操作失败')
  }
}

const closeModal = () => {
  showAddForm.value = false
  editingFood.value = null
  formData.name = ''
  formData.price = 0
  formData.description = ''
  formData.image = ''
  formData.category = ''
  formData.isHot = false
  formData.isNew = false
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
  
  loadFoods()
})
</script>

<style scoped>
.food-manage {
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
  max-width: 1200px;
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

.btn-add {
  padding: 8px 20px;
  background: #4caf50;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.content {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.loading, .empty {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  color: #999;
}

.food-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.food-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.food-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.food-info {
  padding: 20px;
}

.food-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.food-desc {
  margin: 0 0 10px 0;
  color: #999;
  font-size: 14px;
  line-height: 1.5;
}

.food-price {
  font-size: 20px;
  font-weight: bold;
  color: #f5576c;
  margin-bottom: 10px;
}

.food-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.tag {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.tag.hot {
  background: #ffeb3b;
  color: #333;
}

.tag.new {
  background: #4caf50;
  color: white;
}

.food-actions {
  display: flex;
  gap: 10px;
}

.btn-edit {
  flex: 1;
  padding: 8px;
  background: #2196f3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-delete {
  flex: 1;
  padding: 8px;
  background: #ff4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 95%;
  max-width: 700px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
}

.btn-close {
  font-size: 24px;
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
}

.form-group {
  padding: 15px 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-group input, .form-group textarea, .form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.checkbox-group {
  display: flex;
  gap: 20px;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.image-preview {
  margin-top: 10px;
  padding: 10px;
  border: 1px dashed #ddd;
  border-radius: 6px;
  text-align: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 150px;
  border-radius: 4px;
}

.form-actions {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel {
  padding: 10px 20px;
  background: #f0f0f0;
  color: #666;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-submit {
  padding: 10px 20px;
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
