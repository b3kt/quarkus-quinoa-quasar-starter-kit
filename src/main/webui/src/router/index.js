import { defineRouter } from '#q-app/wrappers'
import { createRouter, createMemoryHistory, createWebHistory, createWebHashHistory } from 'vue-router'
import routes from './routes'

export default defineRouter(function () {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory)

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createHistory(process.env.VUE_ROUTER_BASE)
  })

  Router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('auth_token')
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
    if (requiresAuth && !token) {
      next('/login')
    } else if (!requiresAuth && token) {
      next('/')
    } else {
      next()
    }
  })

  return Router
})
