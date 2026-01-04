<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />

        <q-toolbar-title class="text-uppercase text-bold">
          {{ $t('app.constant.app_name') }}
        </q-toolbar-title>

        <div class="q-gutter-sm">
          <q-btn v-if="authStore.isLoggedIn" flat dense icon="logout" @click="handleLogout">
            <q-tooltip>
              {{ $t('logout') }}
            </q-tooltip>
          </q-btn>
          <span v-else>Quasar v{{ $q.version }}</span>
        </div>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label>
          <q-card flat class="bg-grey-4">
            <q-item>
              <q-item-section avatar>
                <q-icon name="person" color="white" class="bg-primary rounded-borders" />
              </q-item-section>

              <q-item-section>
                <q-item-label>{{ user.username }}</q-item-label>
                <q-item-label caption>{{ user?.roles?.join(', ') }}</q-item-label>
              </q-item-section>

            </q-item>
          </q-card>
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
import { computed } from 'vue'
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

const user = computed(() => authStore.user)

const hasRole = (role) => {
  return user.value?.roles?.includes(role) || false
}

const linksList = computed(() => [
  {
    title: t('app.menu.master.title'),
    caption: t('app.menu.master.caption'),
    icon: 'warehouse',
    visible: hasRole('Admin') || hasRole('Owner'),
    children: [
      {
        title: t('app.menu.master.product.title'),
        caption: t('app.menu.master.product.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/barang'
      },
      {
        title: t('app.menu.master.service.title'),
        caption: t('app.menu.master.service.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/jasa'
      },
      {
        title: t('app.menu.master.supplier.title'),
        caption: t('app.menu.master.supplier.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/supplier'
      },
      {
        title: t('app.menu.master.sparepart.title'),
        caption: t('app.menu.master.sparepart.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/sparepart'
      },

      {
        title: t('app.menu.master.customer.title'),
        caption: t('app.menu.master.customer.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/pelanggan'
      },
      {
        title: t('app.menu.master.vehicle.title'),
        caption: t('app.menu.master.vehicle.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/kendaraan'
      },
      {
        title: t('app.menu.master.employee.title'),
        caption: t('app.menu.master.employee.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/karyawan'
      },
      {
        title: t('app.menu.master.employee_role.title'),
        caption: t('app.menu.master.employee_role.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/karyawan-posisi'
      },
    ]
  },
  {
    title: t('app.menu.process.title'),
    caption: t('app.menu.process.caption'),
    icon: 'conveyor_belt',
    link: 'https://quasar.dev',
    visible: hasRole('Admin') || hasRole('Karyawan'),
    children: [
      {
        title: t('app.menu.process.order.title'),
        caption: t('app.menu.process.order.caption'),
        icon: 'warehouse',
        link: '/pazaauto/spk',
        visible: hasRole('Admin') || hasRole('Owner'),
      },
      {
        title: 'Absensi',
        caption: 'Employee Attendance',
        icon: 'access_time',
        link: '/pazaauto/absensi',
        visible: hasRole('Karyawan'),
      }
    ]
  },
  {
    title: t('app.menu.report.title'),
    caption: t('app.menu.report.caption'),
    icon: 'trolley',
    visible: hasRole('Admin') || hasRole('Owner'),
    children: [
      {
        title: t('app.menu.sales.buy.title'),
        caption: t('app.menu.sales.buy.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/pembelian-barang'
      },
      {
        title: t('app.menu.sales.sell.title'),
        caption: t('app.menu.sales.sell.caption'),
        icon: 'warehouse',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/pazaauto/penjualan-barang'
      },
    ]
  },
  {
    title: t('app.menu.admin.title'),
    caption: t('app.menu.admin.caption'),
    icon: 'dashboard',
    visible: hasRole('Admin') || hasRole('Owner'),
    children: [
      {
        title: t('app.menu.admin.user.title'),
        caption: t('app.menu.admin.user.caption'),
        icon: 'user',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/users'
      },
      {
        title: t('app.menu.admin.role.title'),
        caption: t('app.menu.admin.role.caption'),
        icon: 'group',
        visible: hasRole('Admin') || hasRole('Owner'),
        link: '/roles'
      },
    ]
  }
])

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
