<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />

        <q-toolbar-title>
          {{ $t('app.constant.app_name') }}
        </q-toolbar-title>

        <div class="q-gutter-sm">
          <q-btn v-if="authStore.isLoggedIn" flat dense icon="logout" label="Logout" @click="handleLogout" />
          <span v-else>Quasar v{{ $q.version }}</span>
        </div>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header>
          Essential Links
        </q-item-label>

        <EssentialLink v-for="link in linksList" :key="link.title" v-bind="link" />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import EssentialLink from 'components/EssentialLink.vue'
import { useAuthStore } from 'stores/auth-store'
import { useI18n } from 'vue-i18n'


const router = useRouter()
const $q = useQuasar()
const { t } = useI18n()
const authStore = useAuthStore()

const linksList = [
  {
    title: t('app.menu.master.title'),
    caption: t('app.menu.master.caption'),
    icon: 'warehouse',
    children: [
      {
        title: t('app.menu.master.product.title'),
        caption: t('app.menu.master.product.caption'),
        icon: 'warehouse',
        link: '/pazaauto/barang'
      },
      {
        title: t('app.menu.master.service.title'),
        caption: t('app.menu.master.service.caption'),
        icon: 'warehouse',
        link: '/pazaauto/jasa'
      },
      {
        title: t('app.menu.master.supplier.title'),
        caption: t('app.menu.master.supplier.caption'),
        icon: 'warehouse',
        link: '/pazaauto/supplier'
      },
      {
        title: t('app.menu.master.sparepart.title'),
        caption: t('app.menu.master.sparepart.caption'),
        icon: 'warehouse',
        link: '/pazaauto/sparepart'
      },

      {
        title: t('app.menu.master.customer.title'),
        caption: t('app.menu.master.customer.caption'),
        icon: 'warehouse',
        link: '/pazaauto/pelanggan'
      },
      {
        title: t('app.menu.master.vehicle.title'),
        caption: t('app.menu.master.vehicle.caption'),
        icon: 'warehouse',
        link: '/pazaauto/kendaraan'
      },
      {
        title: t('app.menu.master.employee.title'),
        caption: t('app.menu.master.employee.caption'),
        icon: 'warehouse',
        link: '/pazaauto/karyawan'
      },
      {
        title: t('app.menu.master.employee_role.title'),
        caption: t('app.menu.master.employee_role.caption'),
        icon: 'warehouse',
        link: '/pazaauto/karyawan-posisi'
      },
    ]
  },
  {
    title: t('app.menu.process.title'),
    caption: t('app.menu.process.caption'),
    icon: 'conveyor_belt',
    link: 'https://quasar.dev',
    children: [
      {
        title: t('app.menu.process.order.title'),
        caption: t('app.menu.process.order.caption'),
        icon: 'warehouse',
        link: '/pazaauto/spk'
      }
    ]
  },
  {
    title: t('app.menu.report.title'),
    caption: t('app.menu.report.caption'),
    icon: 'trolley',
    children: [
      {
        title: t('app.menu.sales.buy.title'),
        caption: t('app.menu.sales.buy.caption'),
        icon: 'warehouse',
        link: '/pazaauto/pembelian-barang'
      },
      {
        title: t('app.menu.sales.sell.title'),
        caption: t('app.menu.sales.sell.caption'),
        icon: 'warehouse',
        link: '/pazaauto/penjualan-barang'
      },
    ]
  },
  {
    title: t('app.menu.admin.title'),
    caption: t('app.menu.admin.caption'),
    icon: 'dashboard',
    children: [
      {
        title: t('app.menu.admin.user.title'),
        caption: t('app.menu.admin.user.caption'),
        icon: 'user',
        link: '/users'
      },
      {
        title: t('app.menu.admin.role.title'),
        caption: t('app.menu.admin.role.caption'),
        icon: 'group',
        link: '/roles'
      },
    ]
  }
]

const leftDrawerOpen = ref(false)

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

async function handleLogout() {
  await authStore.logout()
  $q.notify({
    type: 'info',
    message: 'Logged out successfully',
    position: 'top'
  })
  router.push('/login')
}
</script>
