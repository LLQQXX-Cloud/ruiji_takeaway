import request from './request'

export const reviewApi = {
  getReviewsByBusiness(businessId) {
    return request.get(`/review/business/${businessId}`)
  },
  getReviewByOrder(orderId) {
    return request.get(`/review/order/${orderId}`)
  },
  getReviewsByUser(userId) {
    return request.get(`/review/user/${userId}`)
  },
  getBusinessRating(businessId) {
    return request.get(`/review/rating/${businessId}`)
  },
  addReview(data) {
    return request.post('/review', data)
  },
  updateReview(id, data) {
    return request.put(`/review/${id}`, data)
  },
  deleteReview(id) {
    return request.delete(`/review/${id}`)
  }
}