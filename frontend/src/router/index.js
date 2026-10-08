import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import BusinessLogin from '../views/BusinessLogin.vue'
import UserLayout from '../components/UserLayout.vue'
import BusinessLayout from '../components/BusinessLayout.vue'
import Home from '../views/Home.vue'
import Business from '../views/Business.vue'
import Cart from '../views/Cart.vue'
import Orders from '../views/Orders.vue'
import AddressManager from '../views/AddressManager.vue'
import BusinessHome from '../views/BusinessHome.vue'
import BusinessInfo from '../views/BusinessInfo.vue'
import FoodManage from '../views/FoodManage.vue'
import BusinessOrders from '../views/BusinessOrders.vue'
import BusinessReviews from '../views/BusinessReviews.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/business-login',
    name: 'BusinessLogin',
    component: BusinessLogin
  },
  {
    path: '/business/:id',
    name: 'Business',
    component: Business
  },
  // 用户端（带左侧导航栏）
  {
    path: '/',
    component: UserLayout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: Home
      },
      {
        path: 'orders',
        name: 'Orders',
        component: Orders
      },
      {
        path: 'address',
        name: 'AddressManager',
        component: AddressManager
      },
      {
        path: 'cart',
        name: 'Cart',
        component: Cart
      }
    ]
  },
  // 商家端（带左侧导航栏）
  {
    path: '/business',
    component: BusinessLayout,
    redirect: '/business/home',
    children: [
      {
        path: 'home',
        name: 'BusinessHome',
        component: BusinessHome
      },
      {
        path: 'info',
        name: 'BusinessInfo',
        component: BusinessInfo
      },
      {
        path: 'food',
        name: 'FoodManage',
        component: FoodManage
      },
      {
        path: 'orders',
        name: 'BusinessOrders',
        component: BusinessOrders
      },
      {
        path: 'reviews',
        name: 'BusinessReviews',
        component: BusinessReviews
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
