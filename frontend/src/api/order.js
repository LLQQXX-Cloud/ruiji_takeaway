import request from './request'

export const orderApi = {
  create(data) {
    return request.post('/orders', data)
  },
  getById(id) {
    return request.get(`/orders/${id}`)
  },
  getByUser(userId) {
    return request.get(`/orders/user/${userId}`)
  },
  getByBusiness(businessId) {
    return request.get(`/orders/business/${businessId}`)
  },
  getByOrderNumber(orderNumber) {
    return request.get(`/orders/number/${orderNumber}`)
  },
  updateStatus(id, status) {
    return request.put(`/orders/${id}/status`, { status })
  },
  applyCancel(id) {
    return request.put(`/orders/${id}/cancel/apply`)
  },
  approveCancel(id) {
    return request.put(`/orders/${id}/cancel/approve`)
  },
  rejectCancel(id) {
    return request.put(`/orders/${id}/cancel/reject`)
  },
  cancelByBusiness(id) {
    return request.put(`/orders/${id}/cancel/business`)
  },
  delete(id) {
    return request.delete(`/orders/${id}`)
  },
  deleteByUser(orderId) {
    return request.delete(`/orders/${orderId}/user`)
  },
  deleteByBusiness(orderId) {
    return request.delete(`/orders/${orderId}/business`)
  }
}
