<template>
  <div class="address-manager">
    <div class="header">
      <button class="btn-back" @click="goBack">← 返回</button>
      <h2>收货地址管理</h2>
      <button class="btn-add" @click="showAddModal = true">+ 添加地址</button>
    </div>
    
    <div class="address-list">
      <div 
        v-for="address in addresses" 
        :key="address.id" 
        class="address-card"
        :class="{ 'is-default': address.isDefault }"
      >
        <div class="address-header">
          <div class="address-info">
            <span class="name">{{ address.name }}</span>
            <span class="phone">{{ address.phone }}</span>
          </div>
          <span v-if="address.isDefault" class="default-tag">默认</span>
        </div>
        <div class="address-detail">
          {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
        </div>
        <div class="address-actions">
          <button @click="setDefault(address)" v-if="!address.isDefault">设为默认</button>
          <button @click="editAddress(address)">编辑</button>
          <button @click="deleteAddress(address.id)">删除</button>
        </div>
      </div>
    </div>

    <div v-if="showAddModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h3>{{ editingAddress ? '编辑地址' : '添加地址' }}</h3>
        <form @submit.prevent="saveAddress">
          <div class="form-group">
            <label>收货人</label>
            <input v-model="formData.name" type="text" required placeholder="请输入收货人姓名" />
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input v-model="formData.phone" type="tel" required placeholder="请输入手机号" />
          </div>
          <div class="form-group">
            <label>省份</label>
            <select v-model="formData.province" @change="onProvinceChange" required>
              <option value="">请选择省份</option>
              <option v-for="p in provinces" :key="p.code" :value="p.name">{{ p.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>城市</label>
            <select v-model="formData.city" @change="onCityChange" required :disabled="!formData.province">
              <option value="">请选择城市</option>
              <option v-for="c in currentCities" :key="c.code" :value="c.name">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>区县</label>
            <input v-model="formData.district" type="text" required placeholder="请输入区县" />
          </div>
          <div class="form-group">
            <label>详细地址</label>
            <textarea v-model="formData.detail" required placeholder="请输入详细地址"></textarea>
          </div>
          <div class="form-group checkbox-group">
            <label>设为默认地址</label>
            <input v-model="formData.isDefault" type="checkbox" />
          </div>
          <div class="form-actions">
            <button type="button" @click="closeModal">取消</button>
            <button type="submit">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { addressApi } from '../api/address'
import { provinces, cities, districts } from '../data/regions'

const router = useRouter()
const addresses = ref([])
const showAddModal = ref(false)
const editingAddress = ref(null)
const formData = ref({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const userId = parseInt(localStorage.getItem('userId')) || 1

const currentCities = computed(() => {
  if (!formData.value.province) return []
  const province = provinces.find(p => p.name === formData.value.province)
  if (!province) return []
  return cities[province.code] || []
})

const currentDistricts = computed(() => {
  if (!formData.value.city || !formData.value.province) return []
  const province = provinces.find(p => p.name === formData.value.province)
  if (!province) return []
  const city = (cities[province.code] || []).find(c => c.name === formData.value.city)
  if (!city) return []
  return districts[city.code] || []
})

const goBack = () => {
  router.back()
}

const loadAddresses = async () => {
  const res = await addressApi.getAddresses(userId)
  if (res.data.success) {
    addresses.value = res.data.data
  }
}

const closeModal = () => {
  showAddModal.value = false
  editingAddress.value = null
  formData.value = {
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  }
}

const onProvinceChange = () => {
  formData.value.city = ''
  formData.value.district = ''
}

const onCityChange = () => {
  formData.value.district = ''
}

const editAddress = (address) => {
  editingAddress.value = address
  formData.value = {
    name: address.name,
    phone: address.phone,
    province: address.province,
    city: address.city,
    district: address.district,
    detail: address.detail,
    isDefault: address.isDefault
  }
  showAddModal.value = true
}

const saveAddress = async () => {
  formData.value.userId = userId
  
  if (editingAddress.value) {
    const res = await addressApi.updateAddress(editingAddress.value.id, formData.value)
    if (res.data.success) {
      alert('修改成功')
      loadAddresses()
      closeModal()
    }
  } else {
    const res = await addressApi.addAddress(formData.value)
    if (res.data.success) {
      alert('添加成功')
      loadAddresses()
      closeModal()
    }
  }
}

const setDefault = async (address) => {
  address.isDefault = true
  const res = await addressApi.updateAddress(address.id, address)
  if (res.data.success) {
    alert('设置成功')
    loadAddresses()
  }
}

const deleteAddress = async (id) => {
  if (!confirm('确定删除此地址吗？')) return
  const res = await addressApi.deleteAddress(id)
  if (res.data.success) {
    alert('删除成功')
    loadAddresses()
  }
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.address-manager {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  color: #333;
  margin: 0;
}

.btn-back {
  padding: 10px 20px;
  background: #f0f0f0;
  color: #666;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}

.btn-add {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}

.address-list {
  display: grid;
  gap: 16px;
}

.address-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  border: 2px solid transparent;
}

.address-card.is-default {
  border-color: #667eea;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.address-info {
  display: flex;
  gap: 16px;
}

.name {
  font-weight: 600;
  color: #333;
}

.phone {
  color: #666;
}

.default-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.address-detail {
  color: #666;
  margin-bottom: 12px;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 10px;
}

.address-actions button {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.address-actions button:nth-child(1) {
  background: #f0f0f0;
  color: #666;
}

.address-actions button:nth-child(2) {
  background: #667eea;
  color: white;
}

.address-actions button:nth-child(3) {
  background: #ff4757;
  color: white;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 90%;
  max-width: 450px;
}

.modal-content h3 {
  margin-top: 0;
  color: #333;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #333;
  font-weight: 500;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-sizing: border-box;
}

.form-group select:disabled {
  background: #f5f5f5;
  color: #999;
}

.form-group textarea {
  height: 80px;
  resize: vertical;
}

.form-group.checkbox-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-group.checkbox-group label {
  margin-bottom: 0;
}

.form-group.checkbox-group input[type="checkbox"] {
  width: auto;
  margin: 0;
  cursor: pointer;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

.form-actions button {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.form-actions button[type="button"] {
  background: #f0f0f0;
  color: #666;
}

.form-actions button[type="submit"] {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
</style>