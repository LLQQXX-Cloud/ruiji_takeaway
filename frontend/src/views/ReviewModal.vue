<template>
  <div v-if="visible" class="review-modal-overlay" @click.self="close">
    <div class="review-modal">
      <h3>评价订单</h3>
      <div class="rating-section">
        <label>评分</label>
        <div class="stars">
          <span 
            v-for="i in 5" 
            :key="i" 
            class="star"
            :class="{ active: i <= rating }"
            @click="rating = i"
          >★</span>
        </div>
      </div>
      <div class="content-section">
        <label>评价内容</label>
        <textarea v-model="content" placeholder="请输入您的评价..."></textarea>
      </div>
      <div class="actions">
        <button @click="close">取消</button>
        <button @click="submit">提交评价</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { reviewApi } from '../api/review'

const props = defineProps({
  visible: Boolean,
  orderId: Number,
  userId: Number,
  businessId: Number
})

const emit = defineEmits(['close', 'success'])

const rating = ref(5)
const content = ref('')

const close = () => {
  emit('close')
}

const submit = async () => {
  if (rating.value < 1) {
    alert('请选择评分')
    return
  }
  
  const data = {
    orderId: props.orderId,
    userId: props.userId,
    businessId: props.businessId,
    rating: rating.value,
    content: content.value
  }
  
  const res = await reviewApi.addReview(data)
  if (res.data.success) {
    alert('评价成功')
    emit('success')
    close()
  }
}
</script>

<style scoped>
.review-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.review-modal {
  background: white;
  border-radius: 16px;
  padding: 24px;
  width: 90%;
  max-width: 400px;
}

.review-modal h3 {
  text-align: center;
  margin-top: 0;
  color: #333;
}

.rating-section, .content-section {
  margin-bottom: 20px;
}

.rating-section label, .content-section label {
  display: block;
  margin-bottom: 10px;
  font-weight: 500;
  color: #333;
}

.stars {
  display: flex;
  gap: 10px;
}

.star {
  font-size: 36px;
  cursor: pointer;
  color: #ddd;
  transition: all 0.2s;
}

.star.active {
  color: #ffc107;
}

.star:hover {
  transform: scale(1.1);
}

.content-section textarea {
  width: 100%;
  height: 100px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 10px;
  resize: none;
  box-sizing: border-box;
}

.actions {
  display: flex;
  gap: 12px;
}

.actions button {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 500;
}

.actions button:nth-child(1) {
  background: #f0f0f0;
  color: #666;
}

.actions button:nth-child(2) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
</style>