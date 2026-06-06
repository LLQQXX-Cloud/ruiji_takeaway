<template>
  <div class="login-container">
    <div class="login-box">
      <h1>瑞吉外卖</h1>
      <div class="tabs">
        <button :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">登录</button>
        <button :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">注册</button>
      </div>
      
      <form @submit.prevent="handleSubmit">
        <div v-if="activeTab === 'login'" class="form-content">
          <div class="form-group">
            <input v-model="loginForm.username" type="text" placeholder="用户名" required />
          </div>
          <div class="form-group">
            <input v-model="loginForm.password" type="password" placeholder="密码" required />
          </div>
        </div>
        
        <div v-else class="form-content">
          <div class="form-group">
            <input v-model="registerForm.username" type="text" placeholder="用户名" required />
          </div>
          <div class="form-group">
            <input v-model="registerForm.password" type="password" placeholder="密码" required />
          </div>
          <div class="form-group">
            <input v-model="registerForm.phone" type="tel" placeholder="手机号" required />
          </div>
          <div class="form-group">
            <input v-model="registerForm.nickname" type="text" placeholder="昵称" />
          </div>
          <div class="form-group">
            <input v-model="registerForm.address" type="text" placeholder="地址" />
          </div>
        </div>
        
        <button type="submit" class="btn-primary">{{ activeTab === 'login' ? '登录' : '注册' }}</button>
      </form>
      
      <p v-if="message" :class="['message', messageType]">{{ message }}</p>
      
      <p class="switch-link">
        <a @click="goToBusinessLogin">商家登录入口</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api/user'

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
  nickname: '',
  address: ''
})

const handleSubmit = async () => {
  message.value = ''
  try {
    if (activeTab.value === 'login') {
      const res = await userApi.login(loginForm.value)
      if (res.data.success) {
        localStorage.setItem('user', JSON.stringify(res.data.data))
        localStorage.setItem('userId', res.data.data.id)
        localStorage.setItem('role', 'user')
        messageType.value = 'success'
        message.value = '登录成功！'
        setTimeout(() => {
          router.push('/home')
        }, 1000)
      } else {
        messageType.value = 'error'
        message.value = res.data.message
      }
    } else {
      const res = await userApi.register(registerForm.value)
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

const goToBusinessLogin = () => {
  router.push('/business-login')
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
  background-image: url('https://ts1.tc.mm.bing.net/th/id/OIP-C.e6d5gn9zGiH356c_pbj_tAAAAA?w=193&h=136&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.login-container::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.1);
}

.login-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 400px;
  position: relative;
  z-index: 10;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.tabs button {
  flex: 1;
  padding: 12px;
  border: none;
  background: #f0f0f0;
  color: #666;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 16px;
}

.tabs button.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.form-group {
  margin-bottom: 20px;
}

.form-group input {
  width: 100%;
  padding: 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.btn-primary {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.2s;
}

.btn-primary:hover {
  transform: translateY(-2px);
}

.message {
  margin-top: 20px;
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  font-size: 14px;
}

.message.success {
  background: #d4edda;
  color: #155724;
}

.message.error {
  background: #f8d7da;
  color: #721c24;
}

.switch-link {
  text-align: center;
  margin-top: 20px;
}

.switch-link a {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
}

.switch-link a:hover {
  text-decoration: underline;
}
</style>
