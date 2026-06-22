<template>
  <div class="address-manager">
    <header class="header">
      <button @click="goBack" class="btn-back">← 返回</button>
      <h2>收货地址</h2>
      <button @click="showAddModal = true" class="btn-add">+ 添加</button>
    </header>

    <div class="address-list">
      <div v-for="address in addresses" :key="address.id" class="address-card" :class="{ 'is-default': address.isDefault }">
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
        <h3>{{ editingAddress ? '编辑地址' : '添加新地址' }}</h3>
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
            <textarea v-model="formData.detail" required placeholder="楼号、门牌号等"></textarea>
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
const formData = ref({ name: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: false })
const isSubmitting = ref(false)

const userId = parseInt(localStorage.getItem('userId')) || 1

const currentCities = computed(() => { if (!formData.value.province) return []; const province = provinces.find(p => p.name === formData.value.province); if (!province) return []; return cities[province.code] || [] })
const currentDistricts = computed(() => { if (!formData.value.city || !formData.value.province) return []; const province = provinces.find(p => p.name === formData.value.province); if (!province) return []; const city = (cities[province.code] || []).find(c => c.name === formData.value.city); if (!city) return []; return districts[city.code] || [] })

const goBack = () => router.back()

const loadAddresses = async () => { const res = await addressApi.getAddresses(userId); if (res.data.success) addresses.value = res.data.data }

const closeModal = () => { showAddModal.value = false; editingAddress.value = null; formData.value = { name: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: false } }
const onProvinceChange = () => { formData.value.city = ''; formData.value.district = '' }
const onCityChange = () => { formData.value.district = '' }

const editAddress = (address) => { editingAddress.value = address; formData.value = { name: address.name, phone: address.phone, province: address.province, city: address.city, district: address.district, detail: address.detail, isDefault: address.isDefault }; showAddModal.value = true }

const saveAddress = async () => {
  if (isSubmitting.value) return; isSubmitting.value = true
  formData.value.userId = userId
  try {
    if (editingAddress.value) { const res = await addressApi.updateAddress(editingAddress.value.id, formData.value); if (res.data.success) { alert('修改成功'); loadAddresses(); closeModal() } else alert(res.data.message || '修改失败') }
    else { const res = await addressApi.addAddress(formData.value); if (res.data.success) { alert('添加成功'); loadAddresses(); closeModal() } else alert(res.data.message || '添加失败') }
  } catch (error) { alert('操作失败') }
  finally { isSubmitting.value = false }
}

const setDefault = async (address) => { address.isDefault = true; const res = await addressApi.updateAddress(address.id, address); if (res.data.success) { alert('设置成功'); loadAddresses() } }
const deleteAddress = async (id) => { if (!confirm('确定删除？')) return; const res = await addressApi.deleteAddress(id); if (res.data.success) { alert('删除成功'); loadAddresses() } }

onMounted(() => loadAddresses())
</script>

<style scoped>
.address-manager { max-width: 800px; margin: 0 auto; padding: 24px; min-height: 100vh; background: #f8f9fb; }

.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
.header h2 { color: #1a1a2e; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: -0.5px; }
.btn-back { padding: 10px 20px; background: #f3f4f6; color: #374151; border: none; border-radius: 12px; cursor: pointer; font-weight: 600; font-size: 14px; font-family: inherit; transition: all 0.25s; }
.btn-back:hover { background: #e5e7eb; }
.btn-add { padding: 10px 22px; background: linear-gradient(135deg, #ff6b35, #f7931e); color: white; border: none; border-radius: 12px; cursor: pointer; font-weight: 700; font-size: 14px; font-family: inherit; box-shadow: 0 2px 8px rgba(255,107,53,0.25); transition: all 0.25s; }
.btn-add:hover { transform: translateY(-1px); box-shadow: 0 4px 16px rgba(255,107,53,0.35); }

.address-list { display: flex; flex-direction: column; gap: 14px; }

.address-card { background: white; border-radius: 16px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.03); border: 2px solid transparent; transition: all 0.3s; }
.address-card.is-default { border-color: #ff6b35; background: #fffaf7; }
.address-card:hover { box-shadow: 0 4px 20px rgba(0,0,0,0.06); }

.address-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.address-info { display: flex; gap: 16px; }
.name { font-weight: 700; color: #1a1a2e; font-size: 16px; }
.phone { color: #6b7280; font-size: 15px; }
.default-tag { background: linear-gradient(135deg, #ff6b35, #f7931e); color: white; padding: 3px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; }

.address-detail { color: #6b7280; margin-bottom: 16px; line-height: 1.5; font-size: 14px; }

.address-actions { display: flex; gap: 8px; }
.address-actions button { padding: 7px 14px; border: none; border-radius: 8px; cursor: pointer; font-size: 13px; font-weight: 600; font-family: inherit; transition: all 0.2s; }
.address-actions button:nth-child(1) { background: #f3f4f6; color: #374151; }
.address-actions button:nth-child(1):hover { background: #e5e7eb; }
.address-actions button:nth-child(2) { background: #eff6ff; color: #2563eb; }
.address-actions button:nth-child(2):hover { background: #dbeafe; }
.address-actions button:nth-child(3) { background: #fef2f2; color: #dc2626; }
.address-actions button:nth-child(3):hover { background: #fbe9e9; }

.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-content { background: white; border-radius: 20px; padding: 32px; width: 90%; max-width: 480px; box-shadow: 0 24px 64px rgba(0,0,0,0.25); animation: modalIn 0.3s ease-out; max-height: 85vh; overflow-y: auto; }
@keyframes modalIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }

.modal-content h3 { margin: 0 0 24px; color: #1a1a2e; font-size: 20px; font-weight: 800; }

.form-group { margin-bottom: 18px; }
.form-group label { display: block; margin-bottom: 7px; color: #374151; font-weight: 600; font-size: 13px; }
.form-group input, .form-group textarea, .form-group select { width: 100%; padding: 13px 16px; border: 2px solid #e5e7eb; border-radius: 12px; font-family: inherit; font-size: 14px; box-sizing: border-box; transition: all 0.3s; background: #fafbfc; }
.form-group input:focus, .form-group textarea:focus, .form-group select:focus { outline: none; border-color: #ff6b35; background: white; box-shadow: 0 0 0 4px rgba(255,107,53,0.08); }
.form-group select:disabled { background: #f3f4f6; color: #9ca3af; }
.form-group textarea { height: 80px; resize: vertical; }
.checkbox-group { display: flex; align-items: center; gap: 10px; }
.checkbox-group label { margin-bottom: 0; }
.checkbox-group input[type="checkbox"] { width: auto; margin: 0; cursor: pointer; accent-color: #ff6b35; }

.form-actions { display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px; }
.form-actions button { padding: 12px 28px; border: none; border-radius: 12px; cursor: pointer; font-weight: 700; font-size: 14px; font-family: inherit; transition: all 0.25s; }
.form-actions button[type="button"] { background: #f3f4f6; color: #6b7280; }
.form-actions button[type="button"]:hover { background: #e5e7eb; }
.form-actions button[type="submit"] { background: linear-gradient(135deg, #ff6b35, #f7931e); color: white; box-shadow: 0 4px 16px rgba(255,107,53,0.3); }
.form-actions button[type="submit"]:hover { transform: translateY(-1px); box-shadow: 0 6px 24px rgba(255,107,53,0.4); }
</style>
