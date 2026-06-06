import request from './request'

export const businessApi = {
  getAll(name) {
    return request.get('/businesses', { params: { name } })
  },
  search(keyword, categoryId, status) {
    return request.get('/businesses/search', { 
      params: { keyword, categoryId, status } 
    })
  },
  getById(id) {
    return request.get(`/businesses/${id}`)
  },
  create(data) {
    return request.post('/businesses', data)
  },
  update(id, data) {
    return request.put(`/businesses/${id}`, data)
  },
  delete(id) {
    return request.delete(`/businesses/${id}`)
  },
  login(data) {
    return request.post('/businesses/login', data)
  },
  register(data) {
    return request.post('/businesses/register', data)
  }
}
