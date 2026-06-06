import request from './request'

export const cartApi = {
  getByUser(userId) {
    return request.get(`/cart/user/${userId}`)
  },
  add(data) {
    return request.post('/cart/add', data)
  },
  update(data) {
    return request.post('/cart/update', data)
  },
  remove(userId, foodId) {
    return request.delete(`/cart/remove`, { params: { userId, foodId } })
  },
  clear(userId) {
    return request.delete(`/cart/clear`, { params: { userId } })
  }
}
