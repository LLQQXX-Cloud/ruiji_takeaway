<template>
  <div class="manage-container">
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="btn-back">← 返回</button>
        <h2>商家管理</h2>
        <button @click="showAddForm = true" class="btn-add">+ 添加商家</button>
      </div>
    </header>
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="businesses.length === 0" class="empty">暂无商家</div>
      <div v-else class="business-table">
        <table>
          <thead>
            <tr>
              <th>商家名称</th>
              <th>地址</th>
              <th>评分</th>
              <th>起送价</th>
              <th>配送费</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="business in businesses" :key="business.id">
              <td>{{ business.name }}</td>
              <td>{{ business.address }}</td>
              <td>{{ business.rating }} ⭐</td>
              <td>¥{{ business.startPrice }}</td>
              <td>¥{{ business.deliveryFee }}</td>
              <td>
                <span :class="['status', business.status === 1 ? 'active' : 'disabled']">
                  {{ business.status === 1 ? '营业中' : '已关闭' }}
                </span>
              </td>
              <td class="actions">
                <button @click="editBusiness(business)" class="btn-edit">编辑</button>
                <button @click="deleteBusiness(business)" class="btn-delete">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <div v-if="showAddForm" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingBusiness ? '编辑商家' : '添加商家' }}</h3>
          <button @click="closeModal" class="btn-close">×</button>
        </div>
        <form @submit.prevent="saveBusiness">
          <div class="form-group">
            <label>商家名称 *</label>
            <input v-model="formData.name" type="text" required placeholder="请输入商家名称" />
          </div>
          <div class="form-group">
            <label>地址</label>
            <input v-model="formData.address" type="text" placeholder="请输入地址" />
          </div>
          <div class="form-group">
            <label>电话</label>
            <input v-model="formData.phone" type="tel" placeholder="请输入电话" />
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea v-model="formData.description" placeholder="请输入商家描述"></textarea>
          </div>
          <div class="form-group">
            <label>图片URL</label>
            <input v-model="formData.image" type="text" placeholder="请输入图片URL" />
          </div>
          <div class="form-group">
            <label>起送价</label>
            <input v-model.number="formData.startPrice" type="number" step="0.01" placeholder="起送价" />
          </div>
          <div class="form-group">
            <label>配送费</label>
            <input v-model.number="formData.deliveryFee" type="number" step="0.01" placeholder="配送费" />
          </div>
          <div class="form-group">
            <label>状态</label>
            <select v-model.number="formData.status">
              <option :value="1">营业中</option>
              <option :value="0">已关闭</option>
            </select>
          </div>
          <div class="form-actions">
            <button type="button" @click="closeModal" class="btn-cancel">取消</button>
            <button type="submit" class="btn-submit">保存</button>
          </div>
        </form>
      </div>
    </div>
    
    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { businessApi } from '../api/business'

const router = useRouter()
const businesses = ref([])
const loading = ref(false)
const showAddForm = ref(false)
const editingBusiness = ref(null)
const showToast = ref(false)
const toastMessage = ref('')

const formData = reactive({
  name: '',
  address: '',
  phone: '',
  description: '',
  image: '',
  startPrice: 0,
  deliveryFee: 0,
  status: 1
})

const loadBusinesses = async () => {
  loading.value = true
  try {
    const res = await businessApi.getAll()
    if (res.data.success) {
      businesses.value = res.data.data
    }
  } catch (error) {
    console.error('加载商家失败:', error)
  } finally {
    loading.value = false
  }
}

const editBusiness = (business) => {
  editingBusiness.value = business
  formData.name = business.name
  formData.address = business.address || ''
  formData.phone = business.phone || ''
  formData.description = business.description || ''
  formData.image = business.image || ''
  formData.startPrice = business.startPrice || 0
  formData.deliveryFee = business.deliveryFee || 0
  formData.status = business.status || 1
  showAddForm.value = true
}

const deleteBusiness = async (business) => {
  if (!confirm(`确定删除商家「${business.name}」吗？`)) return
  
  try {
    const res = await businessApi.delete(business.id)
    if (res.data.success) {
      businesses.value = businesses.value.filter(b => b.id !== business.id)
      showToastMessage('删除成功')
    }
  } catch (error) {
    showToastMessage('删除失败')
  }
}

const saveBusiness = async () => {
  try {
    if (editingBusiness.value) {
      const res = await businessApi.update(editingBusiness.value.id, formData)
      if (res.data.success) {
        showToastMessage('修改成功')
        loadBusinesses()
      }
    } else {
      const res = await businessApi.create(formData)
      if (res.data.success) {
        showToastMessage('添加成功')
        loadBusinesses()
      }
    }
    closeModal()
  } catch (error) {
    showToastMessage('操作失败')
  }
}

const closeModal = () => {
  showAddForm.value = false
  editingBusiness.value = null
  formData.name = ''
  formData.address = ''
  formData.phone = ''
  formData.description = ''
  formData.image = ''
  formData.startPrice = 0
  formData.deliveryFee = 0
  formData.status = 1
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

onMounted(() => {
  const user = localStorage.getItem('user')
  if (!user) {
    router.push('/login')
    return
  }
  loadBusinesses()
})
</script>

<style scoped>
.manage-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  margin: 20px auto;
  padding: 0 20px;
}

.loading, .empty {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  color: #999;
}

.business-table {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

th {
  background: #f9f9f9;
  font-weight: 600;
  color: #666;
}

.status {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
}

.status.active {
  background: #d4edda;
  color: #155724;
}

.status.disabled {
  background: #f8d7da;
  color: #721c24;
}

.actions {
  display: flex;
  gap: 10px;
}

.btn-edit {
  padding: 6px 12px;
  background: #2196f3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}

.btn-delete {
  padding: 6px 12px;
  background: #ff4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
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
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
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
  font-size: 18px;
}

.btn-close {
  font-size: 24px;
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
}

.form-group {
  padding: 15px 20px;
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
  min-height: 80px;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
