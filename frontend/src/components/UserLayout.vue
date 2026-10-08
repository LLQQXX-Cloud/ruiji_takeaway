<template>
  <div class="layout">
    <!-- 左侧导航栏 -->
    <aside class="sidebar">
      <div class="sidebar-brand">
        <div class="brand-icon">🍱</div>
        <div class="brand-text">
          <h2>瑞吉外卖</h2>
          <p>在线订餐平台</p>
        </div>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          active-class="active"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <button @click="logout" class="logout-btn">退出登录</button>
      </div>
    </aside>

    <!-- 右侧主区域 -->
    <div class="main">
      <header class="topbar">
        <h2 class="page-title">{{ currentTitle }}</h2>
        <button @click="logout" class="topbar-logout">退出</button>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const navItems = [
  { path: '/home', icon: '🏠', label: '首页' },
  { path: '/orders', icon: '📋', label: '我的订单' },
  { path: '/address', icon: '📍', label: '收货地址' },
  { path: '/cart', icon: '🛒', label: '购物车' }
]

const titleMap = {
  '/home': '首页',
  '/orders': '我的订单',
  '/address': '收货地址',
  '/cart': '购物车'
}

const currentTitle = computed(() => titleMap[route.path] || '瑞吉外卖')

const logout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
  background: #f8f9fb;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  flex-shrink: 0;
  height: 100vh;
  position: sticky;
  top: 0;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 55%, #0f3460 100%);
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.18);
  z-index: 200;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.brand-icon {
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.brand-text h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  letter-spacing: -0.3px;
}

.brand-text p {
  margin: 2px 0 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 500;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 16px;
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.75);
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.25s;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.12);
  color: white;
}

.nav-item.active {
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  box-shadow: 0 4px 16px rgba(255, 107, 53, 0.35);
  font-weight: 700;
}

.nav-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.12);
}

.logout-btn {
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.25s;
}

.logout-btn:hover {
  background: rgba(255, 107, 53, 0.2);
  border-color: #ff6b35;
  color: #ff6b35;
}

/* 主区域 */
.main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: white;
  padding: 16px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eef0f3;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: #1a1a2e;
  letter-spacing: -0.3px;
}

.topbar-logout {
  padding: 9px 20px;
  background: #f3f4f6;
  color: #6b7280;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  font-size: 13px;
  font-family: inherit;
  transition: all 0.2s;
}

.topbar-logout:hover {
  background: #fef2f2;
  color: #dc2626;
}

.content {
  flex: 1;
  padding: 24px;
}

@media (max-width: 768px) {
  .sidebar { width: 72px; }
  .brand-text, .nav-label { display: none; }
  .nav-item { justify-content: center; }
  .logout-btn { font-size: 0; }
  .logout-btn::after { content: '⏻'; font-size: 18px; }
}
</style>
