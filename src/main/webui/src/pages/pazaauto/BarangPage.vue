<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar with Create button and Search -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn flat :label="$t('create') + ' Barang'" icon="add" color="white" class="bg-primary"
          @click="openCreateDialog" />
        <q-space />
        <div class="col-2">
          <q-select v-model="filterStatus" multiple :options="statusOptions" label="Availability" dense options-dense
            flat outlined />
        </div>
        <div class="col-6">
          <q-input dense standout="bg-primary" v-model="searchText" input-class="search-field text-left" class="q-ml-md"
            placeholder="Search by code or name...">
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

        <template v-slot:body-cell-hargaJual="props">
          <q-td :props="props">
            {{ formatCurrency(props.row.hargaJual) }}
          </q-td>
        </template>

        <template v-slot:body-cell-hargaBeli="props">
          <q-td :props="props">
            {{ formatCurrency(props.row.hargaBeli) }}
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
          <div class="text-h6">{{ isEditMode ? 'Edit Barang' : 'Create Barang' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveBarang" class="q-gutter-md">
            <q-input v-model="formData.kodeBarang" label="Kode Barang *" outlined dense
              :rules="[val => !!val || 'Kode Barang is required']" />

            <q-input v-model="formData.namaBarang" label="Nama Barang *" outlined dense
              :rules="[val => !!val || 'Nama Barang is required']" />

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input v-model.number="formData.hargaJual" label="Harga Jual" outlined dense type="number" step="0.01"
                  prefix="Rp" class="q-mr-md" />
              </div>
              <div class="col-6">
                <q-input v-model.number="formData.hargaBeli" label="Harga Beli" outlined dense type="number" step="0.01"
                  prefix="Rp" />
              </div>
            </div>

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input v-model.number="formData.stok" label="Stok" outlined dense type="number" class="q-mr-md" />
              </div>
              <div class="col-6">
                <q-input v-model.number="formData.stokMinimal" label="Stok Minimal" outlined dense type="number" />
              </div>
            </div>

            <q-input v-model="formData.satuan" label="Satuan" outlined dense placeholder="e.g., pcs, kg, liter" />

            <q-select v-model="formData.supplierId" label="Supplier" outlined dense :options="filteredSupplierOptions"
              option-label="namaSupplier" option-value="id" emit-value map-options use-input input-debounce="300"
              @filter="filterSupplier" :loading="loadingSupplier" clearable>
              <template v-slot:no-option>
                <q-item>
                  <q-item-section class="text-grey">
                    No results
                  </q-item-section>
                </q-item>
              </template>
            </q-select>

            <q-input v-model="formData.keterangan" label="Keterangan" outlined dense type="textarea" rows="3" />

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
          Are you sure you want to delete <strong>{{ itemToDelete?.namaBarang }}</strong>?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn flat label="Delete" color="negative" @click="deleteBarang" :loading="deleting" />
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

// LocalStorage key for filter persistence
const FILTER_STORAGE_KEY = 'barang_status_filter'

// Load filter from localStorage
const loadFilterFromStorage = () => {
  try {
    const stored = localStorage.getItem(FILTER_STORAGE_KEY)
    return stored ? JSON.parse(stored) : []
  } catch (error) {
    console.error('Failed to load filter from storage:', error)
    return []
  }
}

// Save filter to localStorage
const saveFilterToStorage = (filter) => {
  try {
    localStorage.setItem(FILTER_STORAGE_KEY, JSON.stringify(filter))
  } catch (error) {
    console.error('Failed to save filter to storage:', error)
  }
}

// State
const loading = ref(false)
const loadingSupplier = ref(false)
const saving = ref(false)
const deleting = ref(false)
const searchText = ref('')
const filterStatus = ref(loadFilterFromStorage())
const rows = ref([])
const supplierOptions = ref([])
const filteredSupplierOptions = ref([])
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const isEditMode = ref(false)
const itemToDelete = ref(null)

// Status options for filter
const statusOptions = ref([
  'AVAILABLE',
  'OUT_OF_STOCK'
])

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
  kodeBarang: '',
  namaBarang: '',
  hargaJual: null,
  hargaBeli: null,
  stok: null,
  stokMinimal: null,
  satuan: '',
  supplierId: null,
  keterangan: '',
  active: true
})

// Table columns
const columns = [
  {
    name: 'kodeBarang',
    required: true,
    label: 'Kode Barang',
    align: 'left',
    field: 'kodeBarang',
    sortable: true
  },
  {
    name: 'namaBarang',
    required: true,
    label: 'Nama Barang',
    align: 'left',
    field: 'namaBarang',
    sortable: true
  },
  {
    name: 'hargaJual',
    label: 'Harga Jual',
    align: 'right',
    field: 'hargaJual',
    sortable: true
  },
  {
    name: 'hargaBeli',
    label: 'Harga Beli',
    align: 'right',
    field: 'hargaBeli',
    sortable: true
  },
  {
    name: 'stok',
    label: 'Stok',
    align: 'center',
    field: 'stok',
    sortable: true
  },
  // {
  //   name: 'satuan',
  //   label: 'Satuan',
  //   align: 'center',
  //   field: 'satuan',
  //   sortable: true
  // },
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

// Computed - removed filteredRows as we now use server-side filtering

// Methods
const fetchBarang = async (paginationData = pagination.value) => {
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

    // Add status filter if specified
    if (filterStatus.value && filterStatus.value.length > 0) {
      params.statusFilter = filterStatus.value.join(',')
    }

    const response = await api.get('/api/pazaauto/barang/paginated', { params })
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
      message: 'Failed to fetch barang data',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    loading.value = false
  }
}

const fetchSupplier = async () => {
  loadingSupplier.value = true
  try {
    const response = await api.get('/api/pazaauto/supplier')
    if (response.data.success) {
      supplierOptions.value = response.data.data || []
      filteredSupplierOptions.value = supplierOptions.value
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to fetch supplier data',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    loadingSupplier.value = false
  }
}

const filterSupplier = (val, update) => {
  update(() => {
    if (val === '') {
      filteredSupplierOptions.value = supplierOptions.value
    } else {
      const needle = val.toLowerCase()
      filteredSupplierOptions.value = supplierOptions.value.filter(
        v => v.namaSupplier.toLowerCase().indexOf(needle) > -1
      )
    }
  })
}

const onRequest = (props) => {
  const { page, rowsPerPage, sortBy, descending } = props.pagination
  pagination.value.page = page
  pagination.value.rowsPerPage = rowsPerPage
  pagination.value.sortBy = sortBy
  pagination.value.descending = descending
  fetchBarang(pagination.value)
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
    kodeBarang: '',
    namaBarang: '',
    hargaJual: null,
    hargaBeli: null,
    stok: null,
    stokMinimal: null,
    satuan: '',
    supplierId: null,
    keterangan: '',
    active: true
  }
}

const saveBarang = async () => {
  saving.value = true
  try {
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/pazaauto/barang/${formData.value.id}`, formData.value)
    } else {
      response = await api.post('/api/pazaauto/barang', formData.value)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'Barang updated successfully' : 'Barang created successfully'
      })
      closeDialog()
      await fetchBarang()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save barang',
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

const deleteBarang = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/pazaauto/barang/${itemToDelete.value.id}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'Barang deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchBarang()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete barang',
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
    fetchBarang()
  }, 500)
})

// Watch filter status changes
watch(filterStatus, (newVal) => {
  console.log('filterStatus changed to:', newVal)
  // Save to localStorage
  saveFilterToStorage(newVal)
  // Reset to page 1 when filtering
  pagination.value.page = 1
  fetchBarang()
}, { deep: true })

// Lifecycle
onMounted(() => {
  fetchBarang()
  fetchSupplier()
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
