import request from './request'

export const addressApi = {
  getAddresses(userId) {
    return request.get(`/address?userId=${userId}`)
  },
  getDefaultAddress(userId) {
    return request.get(`/address/default?userId=${userId}`)
  },
  getAddress(id) {
    return request.get(`/address/${id}`)
  },
  addAddress(data) {
    return request.post('/address', data)
  },
  updateAddress(id, data) {
    return request.put(`/address/${id}`, data)
  },
  deleteAddress(id) {
    return request.delete(`/address/${id}`)
  }
}