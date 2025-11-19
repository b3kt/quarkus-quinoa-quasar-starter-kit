<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn 
          flat 
          label="Create Sparepart" 
          icon="add" 
          color="primary"
          @click="openCreateDialog"
        />
        <q-space />
        <div class="col-6">
          <q-input 
            dense 
            standout 
            v-model="searchText" 
            input-class="search-field text-left" 
            class="q-ml-md"
            placeholder="Search by code or name..."
          >
            <template v-slot:append>
              <q-icon v-if="searchText === ''" name="search" />
              <q-icon v-else name="clear" class="cursor-pointer" @click="searchText = ''" />
            </template>
          </q-input>
        </div>
      </q-toolbar>

      <!-- Data Table -->
      <q-table
        class="my-sticky-header-table"
        flat
        bordered
        :rows="filteredRows"
        :columns="columns"
        row-key="kodeBarang"
        :loading="loading"
        :pagination="pagination"
        @request="onRequest"
      >
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
            <q-btn 
              flat 
              dense 
              round 
              icon="edit" 
              color="primary"
              @click="openEditDialog(props.row)"
            >
              <q-tooltip>Edit</q-tooltip>
            </q-btn>
            <q-btn 
              flat 
              dense 
              round 
              icon="delete" 
              color="negative"
              @click="confirmDelete(props.row)"
            >
              <q-tooltip>Delete</q-tooltip>
            </q-btn>
          </q-td>
        </template>
      </q-table>
    </div>

    <!-- Create/Edit Dialog -->
    <q-dialog v-model="showDialog" persistent>
      <q-card style="min-width: 600px">
        <q-card-section>
          <div class="text-h6">{{ isEditMode ? 'Edit Sparepart' : 'Create Sparepart' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveSparepart" class="q-gutter-md">
            <q-input
              v-model="formData.kodeSparepart"
              label="Kode Sparepart *"
              outlined
              dense
              :rules="[val => !!val || 'Kode Sparepart is required']"
            />

            <q-input
              v-model="formData.namaSparepart"
              label="Nama Sparepart *"
              outlined
              dense
              :rules="[val => !!val || 'Nama Sparepart is required']"
            />

            <div class="row q-col-gutter-md">
              <div class="col-6">
                <q-input
                  v-model.number="formData.hargaJual"
                  label="Harga Jual"
                  outlined
                  dense
                  type="number"
                  step="0.01"
                  prefix="Rp"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model.number="formData.hargaBeli"
                  label="Harga Beli"
                  outlined
                  dense
                  type="number"
                  step="0.01"
                  prefix="Rp"
                />
              </div>
            </div>

            <div class="row q-col-gutter-md">
              <div class="col-4">
                <q-input
                  v-model.number="formData.stok"
                  label="Stok"
                  outlined
                  dense
                  type="number"
                />
              </div>
              <div class="col-4">
                <q-input
                  v-model.number="formData.stokMinimal"
                  label="Stok Minimal"
                  outlined
                  dense
                  type="number"
                />
              </div>
              <div class="col-4">
                <q-input
                  v-model="formData.satuan"
                  label="Satuan"
                  outlined
                  dense
                />
              </div>
            </div>

            <div class="row q-col-gutter-md">
              <div class="col-6">
                <q-input
                  v-model="formData.merek"
                  label="Merek"
                  outlined
                  dense
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.tipeKendaraan"
                  label="Tipe Kendaraan"
                  outlined
                  dense
                />
              </div>
            </div>

            <q-input
              v-model.number="formData.supplierId"
              label="Supplier ID"
              outlined
              dense
              type="number"
            />

            <q-input
              v-model="formData.keterangan"
              label="Keterangan"
              outlined
              dense
              type="textarea"
              rows="2"
            />

            <q-checkbox
              v-model="formData.active"
              label="Active"
            />

            <div class="row justify-end q-gutter-sm">
              <q-btn 
                flat 
                label="Cancel" 
                color="primary" 
                @click="closeDialog"
              />
              <q-btn 
                label="Save" 
                type="submit" 
                color="primary"
                :loading="saving"
              />
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
          Are you sure you want to delete <strong>{{ itemToDelete?.namaSparepart }}</strong>?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn 
            flat 
            label="Delete" 
            color="negative" 
            @click="deleteSparepart"
            :loading="deleting"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()

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
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
})

const formData = ref({
  kodeBarang: '',
  kodeSparepart: '',
  namaSparepart: '',
  hargaJual: null,
  hargaBeli: null,
  stok: null,
  stokMinimal: null,
  satuan: '',
  merek: '',
  tipeKendaraan: '',
  supplierId: null,
  keterangan: '',
  active: true
})

const columns = [
  {
    name: 'kodeSparepart',
    required: true,
    label: 'Kode Sparepart',
    align: 'left',
    field: 'kodeSparepart',
    sortable: true
  },
  {
    name: 'namaSparepart',
    required: true,
    label: 'Nama Sparepart',
    align: 'left',
    field: 'namaSparepart',
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
  {
    name: 'merek',
    label: 'Merek',
    align: 'left',
    field: 'merek'
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

const filteredRows = computed(() => {
  if (!searchText.value) {
    return rows.value
  }
  const search = searchText.value.toLowerCase()
  return rows.value.filter(row => 
    row.kodeSparepart?.toLowerCase().includes(search) ||
    row.namaSparepart?.toLowerCase().includes(search)
  )
})

const fetchSparepart = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/pazaauto/sparepart')
    if (response.data.success) {
      rows.value = response.data.data || []
      pagination.value.rowsNumber = rows.value.length
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to fetch sparepart data',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    loading.value = false
  }
}

const onRequest = (props) => {
  pagination.value = props.pagination
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
    kodeBarang: '',
    kodeSparepart: '',
    namaSparepart: '',
    hargaJual: null,
    hargaBeli: null,
    stok: null,
    stokMinimal: null,
    satuan: '',
    merek: '',
    tipeKendaraan: '',
    supplierId: null,
    keterangan: '',
    active: true
  }
}

const saveSparepart = async () => {
  saving.value = true
  try {
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/pazaauto/sparepart/${formData.value.kodeBarang}`, formData.value)
    } else {
      response = await api.post('/api/pazaauto/sparepart', formData.value)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'Sparepart updated successfully' : 'Sparepart created successfully'
      })
      closeDialog()
      await fetchSparepart()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save sparepart',
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

const deleteSparepart = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/pazaauto/sparepart/${itemToDelete.value.kodeBarang}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'Sparepart deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchSparepart()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete sparepart',
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

onMounted(() => {
  fetchSparepart()
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
