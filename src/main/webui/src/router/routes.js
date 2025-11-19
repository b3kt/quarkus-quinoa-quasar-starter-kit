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
      { path: 'pazaauto/barang', component: () => import('pages/pazaauto/BarangPage.vue') },
      { path: 'pazaauto/jasa', component: () => import('pages/pazaauto/JasaPage.vue') },
      { path: 'pazaauto/karyawan', component: () => import('pages/pazaauto/KaryawanPage.vue') },
      { path: 'pazaauto/karyawan-posisi', component: () => import('pages/pazaauto/KaryawanPosisiPage.vue') },
      { path: 'pazaauto/kendaraan', component: () => import('pages/pazaauto/KendaraanPage.vue') },
      { path: 'pazaauto/pelanggan', component: () => import('pages/pazaauto/PelangganPage.vue') },
      { path: 'pazaauto/supplier', component: () => import('pages/pazaauto/SupplierPage.vue') },
      { path: 'pazaauto/sparepart', component: () => import('pages/pazaauto/SparepartPage.vue') }

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
