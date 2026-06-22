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
        <textarea v-model="content" placeholder="分享您的用餐体验..."></textarea>
      </div>
      <div class="actions">
        <button @click="close" class="btn-cancel">取消</button>
        <button @click="submit" class="btn-submit">提交评价</button>
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

  try {
    const res = await reviewApi.addReview(data)
    if (res.data.success) {
      alert('评价成功')
      emit('success')
      close()
    } else {
      alert('评价失败: ' + (res.data.message || '未知错误'))
    }
  } catch (error) {
    alert('评价失败: ' + (error.message || '网络错误'))
  }
}
</script>

<style scoped>
.review-modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.4);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
  z-index: 3000;
}

.review-modal {
  background: white;
  border-radius: 24px;
  padding: 32px;
  width: 90%;
  max-width: 420px;
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.25);
  animation: modalIn 0.3s ease-out;
}

@keyframes modalIn {
  from { opacity: 0; transform: scale(0.95) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.review-modal h3 {
  text-align: center;
  margin: 0 0 28px;
  color: #1a1a2e;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.3px;
}

.rating-section, .content-section { margin-bottom: 24px; }
.rating-section label, .content-section label {
  display: block; margin-bottom: 12px;
  font-weight: 700; color: #374151; font-size: 14px;
}

.stars { display: flex; gap: 8px; justify-content: center; }
.star {
  font-size: 42px; cursor: pointer;
  color: #e5e7eb;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.star.active { color: #fbbf24; text-shadow: 0 2px 8px rgba(251, 191, 36, 0.4); }
.star:hover { transform: scale(1.2); }

.content-section textarea {
  width: 100%; height: 120px; padding: 16px;
  border: 2px solid #e5e7eb; border-radius: 14px;
  resize: none; font-size: 14px; font-family: inherit;
  transition: all 0.3s;
  background: #fafbfc;
}
.content-section textarea:focus {
  outline: none; border-color: #fbbf24;
  box-shadow: 0 0 0 4px rgba(251, 191, 36, 0.1);
  background: white;
}

.actions { display: flex; gap: 12px; }
.actions button { flex: 1; padding: 14px; border: none; border-radius: 14px; cursor: pointer; font-weight: 700; font-size: 15px; font-family: inherit; transition: all 0.3s; }

.btn-cancel { background: #f3f4f6; color: #6b7280; }
.btn-cancel:hover { background: #e5e7eb; }

.btn-submit {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.3);
}
.btn-submit:hover { transform: translateY(-2px); box-shadow: 0 6px 24px rgba(245, 158, 11, 0.4); }
</style>
