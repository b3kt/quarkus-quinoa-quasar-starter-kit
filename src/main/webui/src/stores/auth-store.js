import { defineStore } from 'pinia'
import { api } from 'boot/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('auth_token') || null,
    user: null,
    isAuthenticated: false
  }),

  getters: {
    isLoggedIn: (state) => !!state.token && state.isAuthenticated,

    userRoles: (state) => (state.user?.roles ? [...state.user.roles] : []),

    hasRole: (state) => (role) => state.user?.roles?.includes(role) ?? false,

    isAdmin: (state) => state.user?.roles?.includes('admin') ?? false
  },

  actions: {
    async register(username, email, password) {
      try {
        await api.post('/api/auth/register', { username, email, password })
        return { success: true }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Registration failed'
        }
      }
    },

    async login(username, password) {
      try {
        const response = await api.post('/api/auth/login', { username, password })
        const loginData = response.data.data

        if (loginData?.token) {
          this.token = loginData.token
          this.user = {
            username: loginData.username,
            email: loginData.email
          }
          this.isAuthenticated = true

          localStorage.setItem('auth_token', this.token)
          api.defaults.headers.common['Authorization'] = `Bearer ${this.token}`

          await this.fetchUserInfo()
          return { success: true }
        } else {
          return {
            success: false,
            error: tokenObject.message || 'Login failed'
          }
        }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.message || error.response?.data?.error || 'Login failed'
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
        const userData = response.data.data
        if (userData) {
          this.user = {
            ...this.user,
            username: userData.username,
            email: userData.email,
            roles: userData.roles
          }
          this.isAuthenticated = true
        }
        return userData
      } catch (error) {
        if (error.response?.status === 401) {
          await this.logout()
        }
        throw error
      }
    },

    initializeAuth() {
      if (this.token) {
        api.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
        this.fetchUserInfo().catch(() => {
          this.logout()
        })
      }
    },

    async loadUsers() {
      try {
        const response = await api.get('/api/admin/users')
        return { success: true, data: response.data.data }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Failed to load users'
        }
      }
    },

    async updateUserRoles(username, roles) {
      try {
        const response = await api.put(`/api/admin/users/${username}/roles`, { roles })
        return { success: true, data: response.data.data }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Failed to update roles'
        }
      }
    },

    async activateUser(username) {
      try {
        const response = await api.put(`/api/admin/users/${username}/activate`)
        return { success: true, data: response.data.data }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Failed to activate user'
        }
      }
    },

    async deactivateUser(username) {
      try {
        const response = await api.put(`/api/admin/users/${username}/deactivate`)
        return { success: true, data: response.data.data }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Failed to deactivate user'
        }
      }
    }
  }
})
