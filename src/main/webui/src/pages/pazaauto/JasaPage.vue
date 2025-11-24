<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn flat :label="$t('create') + ' Jasa'" icon="add" color="white" class="bg-primary"
          @click="openCreateDialog" />
        <q-space />
        <div class="col-6">
          <q-input dense standout="bg-primary" v-model="searchText" input-class="search-field text-left" class="q-ml-md"
            placeholder="Search by name...">
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
        <template v-slot:body-cell-hargaJasa="props">
          <q-td :props="props">
            {{ formatCurrency(props.row.hargaJasa) }}
          </q-td>
        </template>

        <template v-slot:body-cell-actions="props">
          <q-td :props="props">
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
          <div class="text-h6">{{ isEditMode ? 'Edit Jasa' : 'Create Jasa' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveJasa" class="q-gutter-md">
            <q-input v-model="formData.namaJasa" label="Nama Jasa *" outlined dense
              :rules="[val => !!val || 'Nama Jasa tidak boleh kosong']" />

            <q-input v-model.number="formData.hargaJasa" label="Harga Jasa" outlined dense type="number" prefix="Rp"
              :rules="[val => val >= 0 || 'Harga jasa tidak valid']" />

            <q-input v-model.number="formData.estimasiWaktu" label="Estimasi Waktu (menit)" outlined dense type="number"
              :rules="[val => val >= 0 || 'Waktu estimasi tidak valid']" />

            <q-input v-model="formData.deskripsi" label="Deskripsi" outlined dense type="textarea" rows="3" />

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
          Are you sure you want to delete <strong>{{ itemToDelete?.namaJasa }}</strong>?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn flat label="Delete" color="negative" @click="deleteJasa" :loading="deleting" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

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
  namaJasa: '',
  hargaJasa: null,
  estimasiWaktu: null,
  deskripsi: ''
})

// Table columns
const columns = [
  {
    name: 'namaJasa',
    required: true,
    label: 'Nama Jasa',
    align: 'left',
    field: 'namaJasa',
    sortable: true
  },
  {
    name: 'hargaJasa',
    label: 'Harga Jasa',
    align: 'right',
    field: 'hargaJasa',
    sortable: true
  },
  {
    name: 'estimasiWaktu',
    label: 'Estimasi Waktu (menit)',
    align: 'center',
    field: 'estimasiWaktu',
    sortable: true
  },
  {
    name: 'deskripsi',
    label: 'Deskripsi',
    align: 'left',
    field: 'deskripsi'
  },
  {
    name: 'actions',
    label: 'Actions',
    align: 'center',
    field: 'actions'
  }
]

// Methods
const fetchJasa = async (paginationData = pagination.value) => {
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

    const response = await api.get('/api/pazaauto/jasa/paginated', { params })
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
      message: 'Failed to fetch jasa data',
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
  fetchJasa(pagination.value)
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
    namaJasa: '',
    hargaJasa: null,
    estimasiWaktu: null,
    deskripsi: ''
  }
}

const saveJasa = async () => {
  saving.value = true
  try {
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/pazaauto/jasa/${formData.value.id}`, formData.value)
    } else {
      response = await api.post('/api/pazaauto/jasa', formData.value)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'Jasa updated successfully' : 'Jasa created successfully'
      })
      closeDialog()
      await fetchJasa()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save jasa',
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

const deleteJasa = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/pazaauto/jasa/${itemToDelete.value.id}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'Jasa deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchJasa()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete jasa',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    deleting.value = false
  }
}

const formatCurrency = (value) => {
  if (!value) return 'Rp 0'
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(value)
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
    fetchJasa()
  }, 500)
})

// Lifecycle
onMounted(() => {
  fetchJasa()
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
