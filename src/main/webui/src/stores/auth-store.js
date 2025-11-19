import { defineStore } from 'pinia'
import { api } from 'boot/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('auth_token') || null,
    user: null,
    isAuthenticated: false
  }),

  getters: {
    isLoggedIn: (state) => !!state.token && state.isAuthenticated
  },

  actions: {
    async login(username, password) {
      try {
        const response = await api.post('/api/auth/login', {
          username,
          password
        })

        console.log(response)

        if (response.data.token) {

          this.token = response.data.token
          this.user = {
            username: response.data.username,
            email: response.data.email
          }
          this.isAuthenticated = true

          // Store token in localStorage
          localStorage.setItem('auth_token', this.token)

          // Set default authorization header for all requests
          api.defaults.headers.common['Authorization'] = `Bearer ${this.token}`

          return { success: true }
        }
      } catch (error) {
        console.error('Login error:', error)
        return {
          success: false,
          error: error.response?.data?.error || 'Login failed'
        }
      }
    },

    async logout() {
      try {
        await api.post('/api/auth/logout')
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        this.token = null
        this.user = null
        this.isAuthenticated = false
        localStorage.removeItem('auth_token')
        delete api.defaults.headers.common['Authorization']
      }
    },

    async fetchUserInfo() {
      try {
        const response = await api.get('/api/auth/me')
        this.user = response.data
        this.isAuthenticated = true
        return response.data
      } catch (error) {
        console.error('Fetch user info error:', error)
        // If token is invalid, logout
        if (error.response?.status === 401) {
          this.logout()
        }
        throw error
      }
    },

    initializeAuth() {
      // Restore token from localStorage on app initialization
      if (this.token) {
        api.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
        // Optionally fetch user info to verify token is still valid
        this.fetchUserInfo().catch(() => {
          // Token is invalid, clear it
          this.logout()
        })
      }
    }
  }
})

