<template>
  <q-page padding>
    <GenericTable :rows="rows" :columns="columns" :loading="loading" :pagination="pagination"
      @update:pagination="pagination = $event" @request="onRequest" @search="onSearch" :on-create="openCreateDialog"
      :on-edit="openEditDialog" :on-delete="confirmDelete">
      <template v-slot:toolbar-filters>
        <div class="col-2">
          <q-select v-model="filterStatus" multiple :options="statusOptions" :label="$t('availability')" dense
            options-dense flat outlined />
        </div>
      </template>

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
    </GenericTable>

    <!-- Create/Edit Dialog -->
    <GenericDialog v-model="showDialog" :title="isEditMode ? 'Edit Barang' : 'Create Barang'" min-width="500px">
      <q-form @submit="handleSave" id="barang-form" class="q-gutter-md">
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
      </q-form>
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDialog = false" />
        <q-btn label="Save" type="submit" form="barang-form" color="primary" :loading="saving" />
      </template>
    </GenericDialog>

    <!-- Delete Confirmation Dialog -->
    <GenericDialog v-model="showDeleteDialog" title="Confirm Delete" min-width="400px">
      Are you sure you want to delete <strong>{{ itemToDelete?.namaBarang }}</strong>?
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
        <q-btn flat label="Delete" color="negative" @click="deleteItem" :loading="deleting" />
      </template>
    </GenericDialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { api } from 'boot/axios'
// import { useQuasar } from 'quasar'
import GenericTable from 'components/GenericTable.vue'
import GenericDialog from 'components/GenericDialog.vue'
import { useCrud } from 'src/composables/useCrud'

//const $q = useQuasar()

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

// Filter State
const filterStatus = ref(loadFilterFromStorage())
const statusOptions = ref([
  'AVAILABLE',
  'OUT_OF_STOCK'
])

// Use CRUD Composable
const {
  rows,
  loading,
  saving,
  deleting,
  showDialog,
  showDeleteDialog,
  isEditMode,
  itemToDelete,
  pagination,
  fetchData,
  onRequest,
  onSearch,
  saveData,
  confirmDelete,
  deleteItem,
  openCreateDialog: baseOpenCreateDialog,
  openEditDialog: baseOpenEditDialog
} = useCrud({
  baseApiUrl: '/api/pazaauto/barang',
  defaultPagination: {
    sortBy: null,
    descending: false,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
  }
})

// Override fetchData to include custom filters
const originalFetchData = fetchData
const fetchBarang = async () => {
  const customParams = {}
  if (filterStatus.value && filterStatus.value.length > 0) {
    customParams.statusFilter = filterStatus.value.join(',')
  }
  await originalFetchData(customParams)
}

// Re-assign fetch to be used by composable (if it called it internally, but here we call it manually in watchers)
// Actually, useCrud calls fetchData internally on pagination changes via onRequest.
// We need to intercept that or just use the custom params in the hook?
// The hook allows passing custom params to fetchData, but onRequest calls fetchData without arguments.
// A better way is to make the hook accept a "params provider" or just watch the filter here and call fetch.

// Let's patch the onRequest to include our filters if needed, or just rely on the fact that
// we can pass custom params when we call it manually.
// But for pagination, the hook calls `fetchData()`.
// We should modify the hook to accept a `getCustomParams` function or similar.
// OR, we can just override the `fetchData` in the returned object if it was a class, but it's a closure.
// Wait, `fetchData` is returned. If I change `fetchData` here, `onRequest` inside the hook still uses the original one.
// So I should probably update `useCrud` to accept a `params` getter.
// For now, let's just watch the filter and call `fetchBarang`.
// And for pagination, we might need to update `useCrud` to support dynamic params.
// Let's update `useCrud` to accept `extraParams` ref or function.

// Actually, let's keep it simple. The `onRequest` in `useCrud` calls `fetchData()`.
// If I want `fetchData` to include my filters, I should have passed them to `useCrud` or `useCrud` should be flexible.
// Let's modify `useCrud.js` slightly to accept a `beforeFetch` callback or `extraParams`.
// But since I already wrote `useCrud.js`, I'll stick to what I have.
// `fetchData` in `useCrud` takes `customParams`.
// `onRequest` calls `fetchData()`.
// So `onRequest` will miss the filters if I don't patch it.
// I will patch `onRequest` here.

// const handleRequest = (props) => {
//   const { page, rowsPerPage, sortBy, descending } = props.pagination
//   pagination.value.page = page
//   pagination.value.rowsPerPage = rowsPerPage
//   pagination.value.sortBy = sortBy
//   pagination.value.descending = descending
//   fetchBarang()
// }

// Supplier Logic
const loadingSupplier = ref(false)
const supplierOptions = ref([])
const filteredSupplierOptions = ref([])

const fetchSupplier = async () => {
  loadingSupplier.value = true
  try {
    const response = await api.get('/api/pazaauto/supplier')
    if (response.data.success) {
      supplierOptions.value = response.data.data || []
      filteredSupplierOptions.value = supplierOptions.value
    }
  } catch (error) {
    console.error('Failed to fetch suppliers', error)
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

// Form Data
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

const openCreateDialog = () => {
  baseOpenCreateDialog(resetForm)
}

const openEditDialog = (row) => {
  baseOpenEditDialog(row, (r) => {
    formData.value = { ...r }
  })
}

const handleSave = async () => {
  await saveData(formData.value)
}

// Table Columns
const columns = [
  { name: 'kodeBarang', required: true, label: 'Kode Barang', align: 'left', field: 'kodeBarang', sortable: true },
  { name: 'namaBarang', required: true, label: 'Nama Barang', align: 'left', field: 'namaBarang', sortable: true },
  { name: 'hargaJual', label: 'Harga Jual', align: 'right', field: 'hargaJual', sortable: true },
  { name: 'hargaBeli', label: 'Harga Beli', align: 'right', field: 'hargaBeli', sortable: true },
  { name: 'stok', label: 'Stok', align: 'center', field: 'stok', sortable: true },
  { name: 'active', label: 'Status', align: 'center', field: 'active', sortable: true },
  { name: 'actions', label: 'Actions', align: 'center', field: 'actions' }
]

const formatCurrency = (value) => {
  if (!value) return 'Rp 0'
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(value)
}

// Watchers
watch(filterStatus, (newVal) => {
  saveFilterToStorage(newVal)
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
</style>
