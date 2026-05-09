<template>
  <q-page class="q-pa-md">
    <div class="text-h5 q-mb-md">User Management</div>

    <q-table
      :rows="users"
      :columns="columns"
      row-key="username"
      :loading="loading"
      :filter="filter"
      flat bordered
    >
      <template v-slot:top-right>
        <q-input
          v-model="filter"
          dense
          debounce="300"
          placeholder="Search..."
          outlined
        >
          <template v-slot:append><q-icon name="search" /></template>
        </q-input>
      </template>

      <template v-slot:body-cell-roles="props">
        <q-td :props="props">
          <q-chip
            v-for="role in props.value"
            :key="role"
            :color="role === 'admin' ? 'negative' : 'primary'"
            text-color="white"
            dense
            size="sm"
            class="q-mr-xs"
          >
            {{ role }}
          </q-chip>
          <span v-if="!props.value?.length" class="text-grey">—</span>
        </q-td>
      </template>

      <template v-slot:body-cell-active="props">
        <q-td :props="props">
          <q-icon
            :name="props.value ? 'check_circle' : 'cancel'"
            :color="props.value ? 'positive' : 'grey-5'"
            size="sm"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-actions="props">
        <q-td :props="props">
          <q-btn
            flat dense round icon="admin_panel_settings"
            color="warning"
            @click="openRoleDialog(props.row)"
          >
            <q-tooltip>Edit Roles</q-tooltip>
          </q-btn>

          <q-btn
            v-if="props.row.active"
            flat dense round icon="block"
            color="negative"
            @click="handleDeactivate(props.row.username)"
          >
            <q-tooltip>Deactivate</q-tooltip>
          </q-btn>

          <q-btn
            v-else
            flat dense round icon="check_circle"
            color="positive"
            @click="handleActivate(props.row.username)"
          >
            <q-tooltip>Activate</q-tooltip>
          </q-btn>
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="roleDialog" persistent>
      <q-card style="min-width: 350px">
        <q-card-section>
          <div class="text-h6">Edit Roles: {{ selectedUser?.username }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-list>
            <q-item v-for="role in availableRoles" :key="role" tag="label" v-ripple>
              <q-item-section avatar>
                <q-checkbox v-model="selectedRoles" :val="role" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ role }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" v-close-popup />
          <q-btn flat label="Save" color="positive" @click="handleSaveRoles" :loading="saving" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from 'stores/auth-store'
import { useQuasar } from 'quasar'

const authStore = useAuthStore()
const $q = useQuasar()

const columns = [
  { name: 'username', label: 'Username', field: 'username', align: 'left', sortable: true },
  { name: 'email', label: 'Email', field: 'email', align: 'left', sortable: true },
  { name: 'roles', label: 'Roles', field: 'roles', align: 'left' },
  { name: 'active', label: 'Active', field: 'active', align: 'center' },
  { name: 'actions', label: 'Actions', field: 'actions', align: 'center' }
]

const users = ref([])
const loading = ref(false)
const filter = ref('')
const roleDialog = ref(false)
const selectedUser = ref(null)
const selectedRoles = ref([])
const saving = ref(false)
const availableRoles = ['user', 'admin']

async function loadUsers() {
  loading.value = true
  const result = await authStore.loadUsers()
  if (result.success) {
    users.value = result.data || []
  } else {
    $q.notify({ type: 'negative', message: result.error, position: 'top' })
  }
  loading.value = false
}

function openRoleDialog(user) {
  selectedUser.value = user
  selectedRoles.value = [...(user.roles || [])]
  roleDialog.value = true
}

async function handleSaveRoles() {
  saving.value = true
  const result = await authStore.updateUserRoles(selectedUser.value.username, selectedRoles.value)
  if (result.success) {
    $q.notify({ type: 'positive', message: 'Roles updated', position: 'top' })
    roleDialog.value = false
    await loadUsers()
  } else {
    $q.notify({ type: 'negative', message: result.error, position: 'top' })
  }
  saving.value = false
}

async function handleActivate(username) {
  const result = await authStore.activateUser(username)
  if (result.success) {
    $q.notify({ type: 'positive', message: 'User activated', position: 'top' })
    await loadUsers()
  } else {
    $q.notify({ type: 'negative', message: result.error, position: 'top' })
  }
}

async function handleDeactivate(username) {
  $q.dialog({
    title: 'Confirm',
    message: `Deactivate user "${username}"? They will not be able to login.`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    const result = await authStore.deactivateUser(username)
    if (result.success) {
      $q.notify({ type: 'positive', message: 'User deactivated', position: 'top' })
      await loadUsers()
    } else {
      $q.notify({ type: 'negative', message: result.error, position: 'top' })
    }
  })
}

onMounted(loadUsers)
</script>
