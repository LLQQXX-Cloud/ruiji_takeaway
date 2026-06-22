<template>
  <div class="reviews-container">
    <header class="header">
      <div class="header-content">
        <button @click="goBack" class="btn-back">← 返回</button>
        <h2>评价管理</h2>
      </div>
    </header>

    <div class="content">
      <div v-if="loading" class="loading"><div class="spinner"></div><p>加载中...</p></div>
      <div v-else-if="reviews.length === 0" class="empty-state">
        <div class="empty-icon">⭐</div>
        <h3>暂无评价</h3>
        <p>顾客的评价将会显示在这里</p>
      </div>
      <div v-else class="reviews-list">
        <div v-for="review in reviews" :key="review.id" class="review-card">
          <div class="review-header">
            <div class="review-rating">
              <span v-for="i in 5" :key="i" class="star" :class="{ active: i <= review.rating }">★</span>
              <span class="rating-num">{{ review.rating }}.0</span>
            </div>
            <div class="review-time">{{ formatTime(review.createdAt) }}</div>
          </div>
          <div class="review-content">
            <p>{{ review.content || '暂无评价内容' }}</p>
          </div>
          <div class="review-meta">
            <span class="meta-tag">用户 #{{ review.userId }}</span>
            <span class="meta-tag">订单 #{{ review.orderId }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { reviewApi } from '../api/review'

const router = useRouter()
const reviews = ref([])
const loading = ref(false)

const formatTime = (ts) => {
  if (!ts) return ''
  return new Date(ts).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const goBack = () => router.push('/business-home')

const loadReviews = async () => {
  loading.value = true
  try {
    const businessId = localStorage.getItem('businessId')
    const res = await reviewApi.getReviewsByBusiness(businessId)
    if (res.data.success) reviews.value = res.data.data
  } catch (error) { console.error('加载评价失败:', error) }
  finally { loading.value = false }
}

onMounted(() => {
  const business = localStorage.getItem('business')
  if (!business) { router.push('/businessLogin'); return }
  loadReviews()
})
</script>

<style scoped>
.reviews-container { min-height: 100vh; background: #f8f9fb; }

.header { background: linear-gradient(135deg, #0d9488 0%, #115e59 100%); color: white; padding: 18px 24px; position: sticky; top: 0; z-index: 100; box-shadow: 0 4px 24px rgba(13, 148, 136, 0.2); }
.header-content { max-width: 900px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
.header h2 { margin: 0; font-size: 20px; font-weight: 800; letter-spacing: -0.3px; }

.btn-back { padding: 10px 20px; background: rgba(255,255,255,0.1); color: white; border: 1px solid rgba(255,255,255,0.15); border-radius: 12px; cursor: pointer; font-weight: 600; font-size: 14px; font-family: inherit; transition: all 0.25s; }
.btn-back:hover { background: rgba(255,255,255,0.2); }

.content { max-width: 900px; margin: 24px auto; padding: 0 24px 60px; }
.loading { text-align: center; padding: 80px 20px; color: #9ca3af; }
.spinner { width: 36px; height: 36px; border: 3px solid #e5e7eb; border-top-color: #0d9488; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 16px; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state { text-align: center; padding: 100px 20px; background: white; border-radius: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.empty-icon { font-size: 48px; }
.empty-state h3 { color: #1a1a2e; margin: 12px 0 4px; font-size: 18px; }
.empty-state p { color: #9ca3af; margin: 0; font-size: 14px; }

.reviews-list { display: flex; flex-direction: column; gap: 16px; }

.review-card { background: white; border-radius: 18px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); transition: all 0.3s; }
.review-card:hover { transform: translateY(-2px); box-shadow: 0 8px 28px rgba(0,0,0,0.08); }

.review-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.review-rating { display: flex; align-items: center; gap: 6px; }
.star { font-size: 22px; color: #e5e7eb; transition: color 0.2s; }
.star.active { color: #fbbf24; }
.rating-num { font-size: 14px; font-weight: 700; color: #d97706; margin-left: 6px; }

.review-time { font-size: 13px; color: #9ca3af; font-weight: 500; }

.review-content { margin-bottom: 16px; }
.review-content p { color: #374151; line-height: 1.7; margin: 0; font-size: 15px; }

.review-meta { display: flex; gap: 10px; }
.meta-tag { padding: 4px 12px; background: #f3f4f6; color: #6b7280; border-radius: 100px; font-size: 12px; font-weight: 600; }
</style>
