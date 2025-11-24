<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar with Create button and Search -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn flat :label="$t('create') + ' Role'" icon="add" color="white" class="bg-primary"
          @click="openCreateDialog" />
        <q-space />
        <div class="col-6">
          <q-input dense standout="bg-secondary" v-model="searchText" input-class="search-field text-left"
            class="q-ml-md" placeholder="Search by name or description...">
            <template v-slot:append>
              <q-icon v-if="searchText === ''" name="search" />
              <q-icon v-else name="clear" class="cursor-pointer" @click="searchText = ''" />
            </template>
          </q-input>
        </div>
      </q-toolbar>

      <!-- Data Table -->
      <q-table class="my-sticky-header-table" flat bordered :rows="rows" :columns="columns" row-key="id"
        :loading="loading" v-model:pagination="pagination" @request="onRequest" binary-state-sort>
        <template v-slot:body-cell-active="props">
          <q-td :props="props">
            <q-badge :color="props.row.active ? 'green' : 'red'">
              {{ props.row.active ? 'Active' : 'Inactive' }}
            </q-badge>
          </q-td>
        </template>

        <template v-slot:body-cell-actions="props">
          <q-td :props="props">
            <q-btn flat dense round icon="preview" color="info" @click="viewRole(props.row)">
              <q-tooltip>View Details</q-tooltip>
            </q-btn>
            <q-btn flat dense round icon="edit" color="primary" @click="openEditDialog(props.row)">
              <q-tooltip>Edit</q-tooltip>
            </q-btn>
            <q-btn flat dense round icon="delete" color="negative" @click="confirmDelete(props.row)">
              <q-tooltip>Delete</q-tooltip>
            </q-btn>
          </q-td>
        </template>
      </q-table>
    </div>

    <!-- Create/Edit Dialog -->
    <q-dialog v-model="showDialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">{{ isEditMode ? 'Edit Role' : 'Create Role' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveRole" class="q-gutter-md">
            <q-input v-model="formData.name" label="Name *" outlined dense
              :rules="[val => !!val || 'Name is required']" />

            <q-input v-model="formData.description" label="Description" outlined dense type="textarea" rows="3" />

            <q-checkbox v-model="formData.active" label="Active" />

            <div class="row justify-end q-gutter-sm">
              <q-btn flat label="Cancel" color="primary" @click="closeDialog" />
              <q-btn label="Save" type="submit" color="primary" :loading="saving" />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Delete Confirmation Dialog -->
    <q-dialog v-model="showDeleteDialog" persistent>
      <q-card>
        <q-card-section>
          <div class="text-h6">Confirm Delete</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          Are you sure you want to delete <strong>{{ itemToDelete?.name }}</strong>?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn flat label="Delete" color="negative" @click="deleteRole" :loading="deleting" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const router = useRouter()
const $q = useQuasar()

// State
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const searchText = ref('')
const rows = ref([])
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const isEditMode = ref(false)
const itemToDelete = ref(null)

const pagination = ref({
  sortBy: null,
  descending: false,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
})

// Form data
const formData = ref({
  id: null,
  name: '',
  description: '',
  active: true
})

// Table columns
const columns = [
  {
    name: 'name',
    required: true,
    label: 'Name',
    align: 'left',
    field: 'name',
    sortable: true
  },
  {
    name: 'description',
    label: 'Description',
    align: 'left',
    field: 'description',
    sortable: true
  },
  {
    name: 'active',
    label: 'Status',
    align: 'center',
    field: 'active',
    sortable: true
  },
  {
    name: 'actions',
    label: 'Actions',
    align: 'center',
    field: 'actions'
  }
]

// Methods
const fetchRoles = async (paginationData = pagination.value) => {
  loading.value = true
  try {
    const params = {
      page: paginationData.page,
      rowsPerPage: paginationData.rowsPerPage
    }

    // Add sorting if specified
    if (paginationData.sortBy) {
      params.sortBy = paginationData.sortBy
      params.descending = paginationData.descending
    }

    // Add search if specified
    if (searchText.value) {
      params.search = searchText.value
    }

    const response = await api.get('/api/roles/paginated', { params })
    if (response.data.success) {
      const pageData = response.data.data
      rows.value = pageData.rows || []
      pagination.value.rowsNumber = pageData.rowsNumber
      pagination.value.page = pageData.page
      pagination.value.rowsPerPage = pageData.rowsPerPage
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to fetch roles',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    loading.value = false
  }
}

const onRequest = (props) => {
  const { page, rowsPerPage, sortBy, descending } = props.pagination
  pagination.value.page = page
  pagination.value.rowsPerPage = rowsPerPage
  pagination.value.sortBy = sortBy
  pagination.value.descending = descending
  fetchRoles(pagination.value)
}

const viewRole = (row) => {
  router.push(`/roles/${row.id}`)
}

const openCreateDialog = () => {
  isEditMode.value = false
  resetForm()
  showDialog.value = true
}

const openEditDialog = (row) => {
  isEditMode.value = true
  formData.value = { ...row }
  showDialog.value = true
}

const closeDialog = () => {
  showDialog.value = false
  resetForm()
}

const resetForm = () => {
  formData.value = {
    id: null,
    name: '',
    description: '',
    active: true
  }
}

const saveRole = async () => {
  saving.value = true
  try {
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/roles/${formData.value.id}`, formData.value)
    } else {
      response = await api.post('/api/roles', formData.value)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'Role updated successfully' : 'Role created successfully'
      })
      closeDialog()
      await fetchRoles()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save role',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    saving.value = false
  }
}

const confirmDelete = (row) => {
  itemToDelete.value = row
  showDeleteDialog.value = true
}

const deleteRole = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/roles/${itemToDelete.value.id}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'Role deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchRoles()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete role',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    deleting.value = false
  }
}

// Watchers
let searchTimeout = null
watch(searchText, (newVal) => {
  console.log('searchText changed to:', newVal)
  // Debounce search to avoid too many API calls
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  searchTimeout = setTimeout(() => {
    // Reset to page 1 when searching
    pagination.value.page = 1
    fetchRoles()
  }, 500)
})

// Lifecycle
onMounted(() => {
  fetchRoles()
})
</script>

<style lang="sass" scoped>
.my-sticky-header-table
  max-height: 70vh

  thead tr th
    position: sticky
    z-index: 1
    background-color: #ffffff

  thead tr:first-child th
    top: 0
</style>
