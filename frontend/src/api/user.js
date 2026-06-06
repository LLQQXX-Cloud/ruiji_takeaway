import request from './request'

export const userApi = {
  register(data) {
    return request.post('/users/register', data)
  },
  login(data) {
    return request.post('/users/login', data)
  },
  getUser(id) {
    return request.get(`/users/${id}`)
  },
  updateUser(id, data) {
    return request.put(`/users/${id}`, data)
  },
  deleteUser(id) {
    return request.delete(`/users/${id}`)
  }
}
