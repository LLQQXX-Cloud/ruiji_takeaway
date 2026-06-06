import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Business from '../views/Business.vue'
import Cart from '../views/Cart.vue'
import Orders from '../views/Orders.vue'
import AddressManager from '../views/AddressManager.vue'
import BusinessLogin from '../views/BusinessLogin.vue'
import BusinessHome from '../views/BusinessHome.vue'
import BusinessInfo from '../views/BusinessInfo.vue'
import FoodManage from '../views/FoodManage.vue'
import BusinessOrders from '../views/BusinessOrders.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/business/:id',
    name: 'Business',
    component: Business
  },
  {
    path: '/cart',
    name: 'Cart',
    component: Cart
  },
  {
    path: '/orders',
    name: 'Orders',
    component: Orders
  },
  {
    path: '/address',
    name: 'AddressManager',
    component: AddressManager
  },
  {
    path: '/business-login',
    name: 'BusinessLogin',
    component: BusinessLogin
  },
  {
    path: '/business-home',
    name: 'BusinessHome',
    component: BusinessHome
  },
  {
    path: '/business-info',
    name: 'BusinessInfo',
    component: BusinessInfo
  },
  {
    path: '/food-manage',
    name: 'FoodManage',
    component: FoodManage
  },
  {
    path: '/business-orders',
    name: 'BusinessOrders',
    component: BusinessOrders
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
