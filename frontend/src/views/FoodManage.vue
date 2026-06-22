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
      <div v-if="loading" class="loading"><div class="spinner"></div><p>加载中...</p></div>
      <div v-else-if="foods.length === 0" class="empty-state">
        <div class="empty-icon">🍽</div>
        <h3>暂无菜品</h3>
        <p>点击上方按钮添加第一个菜品</p>
      </div>
      <div v-else class="food-grid">
        <div v-for="food in foods" :key="food.id" class="food-card">
          <div class="food-image-wrapper">
            <img :src="getValidImageUrl(food.image)" :alt="food.name" class="food-image" crossorigin="anonymous" @error="handleImageError" />
            <div class="food-overlay">
              <span v-if="food.isHot" class="tag hot">🔥 热销</span>
              <span v-if="food.isNew" class="tag new">✨ 新品</span>
            </div>
          </div>
          <div class="food-info">
            <h3>{{ food.name }}</h3>
            <p class="food-desc">{{ food.description || '暂无描述' }}</p>
            <div class="food-price">{{ food.price.toFixed(2) }} 元</div>
            <div class="food-actions">
              <button @click="editFood(food)" class="btn-edit">编辑</button>
              <button @click="deleteFood(food)" class="btn-delete">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>

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
              <input v-model="formData.image" type="text" placeholder="https://example.com/image.jpg" />
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

const formData = reactive({ name: '', price: 0, description: '', image: '', category: '', isHot: false, isNew: false })

const getValidImageUrl = (url) => {
  if (!url || !url.trim()) {
    return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><rect fill="#f0f0f0" width="200" height="200"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="16">暂无图片</text></svg>')
  }
  const validPatterns = /\.(jpg|jpeg|png|gif|webp|bmp)(\?.*)?$/i
  if (validPatterns.test(url)) { return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(url)}` }
  const mediaUrlMatch = url.match(/mediaurl=([^&]+)/)
  if (mediaUrlMatch && validPatterns.test(mediaUrlMatch[1])) {
    const realUrl = decodeURIComponent(mediaUrlMatch[1])
    return `http://localhost:8080/api/image/proxy?url=${encodeURIComponent(realUrl)}`
  }
  return 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><rect fill="#f0f0f0" width="200" height="200"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#999" font-size="16">暂无图片</text></svg>')
}

const handleImageError = (event) => {
  event.target.src = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><rect fill="#f3f4f6" width="200" height="200"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#9ca3af" font-size="14">暂无图片</text></svg>')
}

const loadFoods = async () => {
  loading.value = true
  const businessId = localStorage.getItem('businessId')
  try { const res = await foodApi.getByBusiness(businessId); if (res.data.success) foods.value = res.data.data }
  catch (error) { console.error('加载菜品失败:', error) }
  finally { loading.value = false }
}

const editFood = (food) => {
  editingFood.value = food
  Object.assign(formData, { name: food.name, price: food.price, description: food.description || '', image: food.image || '', category: food.category || '', isHot: food.isHot || false, isNew: food.isNew || false })
  showAddForm.value = true
}

const deleteFood = async (food) => {
  if (!confirm(`确定删除「${food.name}」吗？`)) return
  try { const res = await foodApi.delete(food.id); if (res.data.success) { foods.value = foods.value.filter(f => f.id !== food.id); showToastMessage('删除成功') } }
  catch (error) { showToastMessage('删除失败') }
}

const saveFood = async () => {
  try {
    const businessId = localStorage.getItem('businessId'); formData.businessId = parseInt(businessId)
    if (editingFood.value) { const res = await foodApi.update(editingFood.value.id, formData); if (res.data.success) { showToastMessage('修改成功'); loadFoods() } }
    else { const res = await foodApi.create(formData); if (res.data.success) { showToastMessage('添加成功'); loadFoods() } }
    closeModal()
  } catch (error) { showToastMessage('操作失败') }
}

const closeModal = () => { showAddForm.value = false; editingFood.value = null; Object.assign(formData, { name: '', price: 0, description: '', image: '', category: '', isHot: false, isNew: false }) }
const showToastMessage = (msg) => { toastMessage.value = msg; showToast.value = true; setTimeout(() => showToast.value = false, 2000) }
const goBack = () => router.push('/business-home')

onMounted(() => {
  const role = localStorage.getItem('role'); const business = localStorage.getItem('business')
  if (!business || role !== 'business') { router.push('/business-login'); return }
  loadFoods()
})
</script>

<style scoped>
.food-manage { min-height: 100vh; background: #f8f9fb; }

.header { background: linear-gradient(135deg, #0d9488 0%, #115e59 100%); color: white; padding: 16px 24px; position: sticky; top: 0; z-index: 100; box-shadow: 0 4px 24px rgba(13, 148, 136, 0.2); }
.header-content { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
.header h2 { margin: 0; font-size: 20px; font-weight: 800; letter-spacing: -0.3px; }
.btn-back { padding: 10px 20px; background: rgba(255, 255, 255, 0.1); color: white; border: 1px solid rgba(255,255,255,0.15); border-radius: 12px; cursor: pointer; font-weight: 600; font-size: 14px; font-family: inherit; transition: all 0.25s; }
.btn-back:hover { background: rgba(255, 255, 255, 0.2); }
.btn-add { padding: 10px 22px; background: white; color: #0d9488; border: none; border-radius: 12px; cursor: pointer; font-size: 14px; font-weight: 700; font-family: inherit; transition: all 0.25s; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.btn-add:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0,0,0,0.15); }

.content { max-width: 1200px; margin: 32px auto; padding: 0 24px 60px; }

.loading { text-align: center; padding: 80px 20px; color: #9ca3af; }
.spinner { width: 36px; height: 36px; border: 3px solid #e5e7eb; border-top-color: #0d9488; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 16px; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state { text-align: center; padding: 100px 20px; background: white; border-radius: 20px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04); }
.empty-icon { font-size: 56px; margin-bottom: 12px; }
.empty-state h3 { color: #1a1a2e; margin: 0 0 8px; font-size: 20px; }
.empty-state p { color: #9ca3af; margin: 0; font-size: 15px; }

.food-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 24px; }

.food-card { background: white; border-radius: 18px; overflow: hidden; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04); transition: all 0.3s; }
.food-card:hover { transform: translateY(-4px); box-shadow: 0 12px 32px rgba(0,0,0,0.1); }
.food-image-wrapper { position: relative; }
.food-image { width: 100%; height: 200px; object-fit: cover; }
.food-overlay { position: absolute; top: 12px; left: 12px; display: flex; gap: 6px; }
.tag { padding: 4px 10px; border-radius: 100px; font-size: 11px; font-weight: 700; }
.tag.hot { background: linear-gradient(135deg, #f59e0b, #d97706); color: white; }
.tag.new { background: linear-gradient(135deg, #0d9488, #059669); color: white; }

.food-info { padding: 20px; }
.food-info h3 { margin: 0 0 6px; color: #1a1a2e; font-size: 17px; font-weight: 700; }
.food-desc { margin: 0 0 12px; color: #9ca3af; font-size: 13px; line-height: 1.5; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.food-price { font-size: 24px; font-weight: 800; color: #0d9488; margin-bottom: 16px; }
.food-actions { display: flex; gap: 10px; }
.btn-edit { flex: 1; padding: 10px; background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; border-radius: 10px; cursor: pointer; font-weight: 600; font-size: 14px; font-family: inherit; transition: all 0.2s; }
.btn-edit:hover { background: #dbeafe; }
.btn-delete { flex: 1; padding: 10px; background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; border-radius: 10px; cursor: pointer; font-weight: 600; font-size: 14px; font-family: inherit; transition: all 0.2s; }
.btn-delete:hover { background: #fbe9e9; }

.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-content { background: white; border-radius: 20px; width: 95%; max-width: 600px; max-height: 85vh; display: flex; flex-direction: column; overflow: hidden; box-shadow: 0 24px 64px rgba(0,0,0,0.25); animation: slideUp 0.3s ease-out; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { padding: 20px 24px; border-bottom: 1px solid #f3f4f6; display: flex; justify-content: space-between; align-items: center; }
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 800; color: #1a1a2e; }
.btn-close { font-size: 24px; background: none; border: none; cursor: pointer; color: #9ca3af; width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; transition: all 0.2s; }
.btn-close:hover { background: #f3f4f6; color: #374151; }
.modal-body { flex: 1; overflow-y: auto; padding: 20px 24px; }

.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 600; color: #374151; font-size: 13px; }
.form-group input, .form-group textarea, .form-group select { width: 100%; padding: 13px 16px; border: 2px solid #e5e7eb; border-radius: 12px; font-size: 14px; font-family: inherit; box-sizing: border-box; transition: all 0.3s; background: #fafbfc; }
.form-group input:focus, .form-group textarea:focus { outline: none; border-color: #0d9488; background: white; box-shadow: 0 0 0 4px rgba(13, 148, 136, 0.08); }
.checkbox-group { display: flex; gap: 24px; }
.checkbox-item { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #374151; }
.image-preview { margin-top: 12px; padding: 12px; border: 2px dashed #e5e7eb; border-radius: 12px; text-align: center; }
.image-preview img { max-width: 100%; max-height: 160px; border-radius: 8px; }

.form-actions { padding: 20px 0; border-top: 1px solid #f3f4f6; display: flex; justify-content: flex-end; gap: 12px; margin-top: 8px; }
.btn-cancel { padding: 12px 24px; background: #f3f4f6; color: #6b7280; border: none; border-radius: 12px; cursor: pointer; font-weight: 600; font-size: 14px; font-family: inherit; transition: all 0.2s; }
.btn-cancel:hover { background: #e5e7eb; }
.btn-submit { padding: 12px 24px; background: linear-gradient(135deg, #0d9488, #059669); color: white; border: none; border-radius: 12px; cursor: pointer; font-weight: 700; font-size: 14px; font-family: inherit; box-shadow: 0 4px 16px rgba(13,148,136,0.3); transition: all 0.3s; }
.btn-submit:hover { transform: translateY(-2px); box-shadow: 0 6px 24px rgba(13,148,136,0.4); }

.toast { position: fixed; top: 80px; left: 50%; transform: translateX(-50%); background: #1a1a2e; color: white; padding: 14px 28px; border-radius: 14px; font-size: 14px; font-weight: 600; z-index: 1001; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25); }
</style>
