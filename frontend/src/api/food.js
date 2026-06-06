import request from './request'

export const foodApi = {
  getByBusiness(businessId) {
    return request.get(`/foods/business/${businessId}`)
  },
  getByCategory(categoryId) {
    return request.get(`/foods/category/${categoryId}`)
  },
  getById(id) {
    return request.get(`/foods/${id}`)
  },
  create(data) {
    return request.post('/foods', data)
  },
  update(id, data) {
    return request.put(`/foods/${id}`, data)
  },
  delete(id) {
    return request.delete(`/foods/${id}`)
  }
}
