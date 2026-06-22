<template>
  <div class="login-container">
    <div class="login-box">
      <h1>商家中心</h1>
      <p class="subtitle">管理您的店铺，触达更多顾客</p>

      <div class="tabs">
        <button :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">商家登录</button>
        <button :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">商家注册</button>
      </div>

      <form @submit.prevent="handleSubmit">
        <div v-if="activeTab === 'login'" class="form-content">
          <div class="form-group">
            <input v-model="loginForm.username" type="text" placeholder="商家账号" required />
          </div>
          <div class="form-group">
            <input v-model="loginForm.password" type="password" placeholder="密码" required />
          </div>
        </div>

        <div v-else class="form-content">
          <div class="form-group">
            <input v-model="registerForm.username" type="text" placeholder="商家账号" required />
          </div>
          <div class="form-group">
            <input v-model="registerForm.password" type="password" placeholder="密码" required />
          </div>
          <div class="form-group">
            <input v-model="registerForm.phone" type="tel" placeholder="联系电话" required />
          </div>
          <div class="form-group">
            <input v-model="registerForm.name" type="text" placeholder="商家名称" required />
          </div>
          <div class="form-group">
            <input v-model="registerForm.address" type="text" placeholder="店铺地址" />
          </div>
        </div>

        <button type="submit" class="btn-primary">{{ activeTab === 'login' ? '登录' : '注册' }}</button>
      </form>

      <p v-if="message" :class="['message', messageType]">{{ message }}</p>

      <p class="switch-link">
        <a @click="goToUserLogin">返回用户登录</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { businessApi } from '../api/business'

const router = useRouter()
const activeTab = ref('login')
const message = ref('')
const messageType = ref('')

const loginForm = ref({
  username: '',
  password: ''
})

const registerForm = ref({
  username: '',
  password: '',
  phone: '',
  name: '',
  address: ''
})

const handleSubmit = async () => {
  message.value = ''
  try {
    if (activeTab.value === 'login') {
      const res = await businessApi.login(loginForm.value)
      if (res.data.success) {
        localStorage.setItem('business', JSON.stringify(res.data.data))
        localStorage.setItem('businessId', res.data.data.id)
        localStorage.setItem('role', 'business')
        if (res.data.token) localStorage.setItem('token', res.data.token)
        messageType.value = 'success'
        message.value = '登录成功！'
        setTimeout(() => {
          router.push('/business-home')
        }, 1000)
      } else {
        messageType.value = 'error'
        message.value = res.data.message
      }
    } else {
      const res = await businessApi.register(registerForm.value)
      if (res.data.success) {
        messageType.value = 'success'
        message.value = '注册成功！请登录'
        activeTab.value = 'login'
        loginForm.value.username = registerForm.value.username
      } else {
        messageType.value = 'error'
        message.value = res.data.message
      }
    }
  } catch (error) {
    messageType.value = 'error'
    message.value = error.response?.data?.message || '操作失败'
  }
}

const goToUserLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #0a1628 0%, #0f2027 30%, #1a3a3a 60%, #203a43 100%);
}

.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -30%;
  width: 80%;
  height: 100%;
  background: radial-gradient(ellipse, rgba(13, 148, 136, 0.15) 0%, transparent 70%);
  animation: float 22s ease-in-out infinite;
}

.login-container::after {
  content: '';
  position: absolute;
  bottom: -40%;
  right: -20%;
  width: 70%;
  height: 90%;
  background: radial-gradient(ellipse, rgba(5, 150, 105, 0.1) 0%, transparent 70%);
  animation: float 28s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(-25px, 25px) rotate(-2deg); }
  66% { transform: translate(20px, -15px) rotate(1deg); }
}

.login-box {
  background: rgba(255, 255, 255, 0.97);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: 0 32px 64px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 10;
}

h1 {
  text-align: center;
  color: #1a1a2e;
  margin-bottom: 6px;
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.subtitle {
  text-align: center;
  color: #9ca3af;
  margin-bottom: 32px;
  font-size: 14px;
  font-weight: 500;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 36px;
  background: #f3f4f6;
  border-radius: 14px;
  padding: 5px;
}

.tabs button {
  flex: 1;
  padding: 12px;
  border: none;
  background: transparent;
  color: #6b7280;
  border-radius: 11px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.tabs button.active {
  background: linear-gradient(135deg, #0d9488 0%, #059669 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.3);
}

.form-group {
  margin-bottom: 20px;
}

.form-group input {
  width: 100%;
  padding: 15px 18px;
  border: 2px solid #e5e7eb;
  border-radius: 14px;
  font-size: 15px;
  box-sizing: border-box;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: #fafbfc;
  font-family: inherit;
}

.form-group input:focus {
  outline: none;
  border-color: #0d9488;
  background: white;
  box-shadow: 0 0 0 4px rgba(13, 148, 136, 0.1);
}

.btn-primary {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #0d9488 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: 0.5px;
  box-shadow: 0 4px 16px rgba(13, 148, 136, 0.3);
  font-family: inherit;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(13, 148, 136, 0.4);
}

.btn-primary:active {
  transform: translateY(0);
}

.message {
  margin-top: 20px;
  padding: 14px 18px;
  border-radius: 12px;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
}

.message.success {
  background: #ecfdf5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.message.error {
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.switch-link {
  text-align: center;
  margin-top: 24px;
}

.switch-link a {
  color: #0d9488;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s;
}

.switch-link a:hover {
  color: #059669;
}
</style>
