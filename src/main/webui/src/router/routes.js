const routes = [
  {
    path: '/login',
    component: () => import('layouts/PublicLayout.vue'),
    meta: { requiresAuth: false },
    children: [
      { path: '', component: () => import('pages/LoginPage.vue') }
    ]
  },
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') },
      { path: 'master/users', component: () => import('pages/master/UserPage.vue') },
      { path: 'master/roles', component: () => import('pages/master/RolePage.vue') },
      { path: 'master/products', component: () => import('pages/master/ProductPage.vue') },
      { path: 'master/services', component: () => import('pages/master/ServicePage.vue') }

    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
