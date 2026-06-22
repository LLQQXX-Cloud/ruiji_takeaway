import request from './request'

export const categoryApi = {
  getAll() {
    return request.get('/categories')
  }
}
