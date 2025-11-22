<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar with Create button and Search -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn 
          flat 
          :label="$t('create') + ' SPK'" 
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
            placeholder="Search by SPK number, nopol, or employee name..."
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
        :rows="rows"
        :columns="columns"
        row-key="id"
        :loading="loading"
        v-model:pagination="pagination"
        @request="onRequest"
        binary-state-sort
      >
        <template v-slot:body-cell-statusSpk="props">
          <q-td :props="props">
            <q-badge :color="getStatusColor(props.row.statusSpk)">
              {{ props.row.statusSpk || 'N/A' }}
            </q-badge>
          </q-td>
        </template>

        <template v-slot:body-cell-diskon="props">
          <q-td :props="props">
            {{ formatCurrency(props.row.diskon) }}
          </q-td>
        </template>

        <template v-slot:body-cell-ppn="props">
          <q-td :props="props">
            {{ formatCurrency(props.row.ppn) }}
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
      <q-card class="dialog-spk">
        <q-card-section>
          <div class="text-h6">{{ isEditMode ? 'Edit SPK' : 'Create SPK' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveSpk" class="q-gutter-md">
            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model="formData.noSpk"
                  label="No SPK *"
                  outlined
                  dense
                  :rules="[val => !!val || 'No SPK is required']"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model.number="formData.noAntrian"
                  label="No Antrian"
                  outlined
                  dense
                  type="number"
                />
              </div>
            </div>

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model="formData.tanggalJamSpk"
                  label="Tanggal/Jam SPK"
                  outlined
                  dense
                  placeholder="YYYY-MM-DD HH:mm:ss"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.nopol"
                  label="Nopol"
                  outlined
                  dense
                />
              </div>
            </div>

            <q-input
              v-model="formData.namaKaryawan"
              label="Nama Karyawan"
              outlined
              dense
            />

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model.number="formData.km"
                  label="KM"
                  outlined
                  dense
                  type="number"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model.number="formData.kmSaatIni"
                  label="KM Saat Ini"
                  outlined
                  dense
                  type="number"
                />
              </div>
            </div>

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model="formData.statusSpk"
                  label="Status SPK"
                  outlined
                  dense
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.status"
                  label="Status"
                  outlined
                  dense
                />
              </div>
            </div>

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model.number="formData.diskon"
                  label="Diskon"
                  outlined
                  dense
                  type="number"
                  step="0.01"
                  prefix="Rp"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model.number="formData.ppn"
                  label="PPN"
                  outlined
                  dense
                  type="number"
                  step="0.01"
                  prefix="Rp"
                />
              </div>
            </div>

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model.number="formData.csId"
                  label="CS ID"
                  outlined
                  dense
                  type="number"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model.number="formData.mekanikId"
                  label="Mekanik ID"
                  outlined
                  dense
                  type="number"
                />
              </div>
            </div>

            <q-input
              v-model="formData.keluhan"
              label="Keluhan"
              outlined
              dense
              type="textarea"
              rows="3"
            />

            <q-input
              v-model="formData.keterangan"
              label="Keterangan"
              outlined
              dense
              type="textarea"
              rows="3"
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
          Are you sure you want to delete SPK <strong>{{ itemToDelete?.noSpk }}</strong>?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn 
            flat 
            label="Delete" 
            color="negative" 
            @click="deleteSpk"
            :loading="deleting"
          />
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
  noSpk: '',
  noAntrian: null,
  tanggalJamSpk: '',
  nopol: '',
  namaKaryawan: '',
  km: null,
  statusSpk: '',
  diskon: null,
  keluhan: '',
  keterangan: '',
  kmSaatIni: null,
  ppn: null,
  status: '',
  csId: null,
  mekanikId: null
})

// Table columns
const columns = [
  {
    name: 'noSpk',
    required: true,
    label: 'No SPK',
    align: 'left',
    field: 'noSpk',
    sortable: true
  },
  {
    name: 'noAntrian',
    label: 'No Antrian',
    align: 'center',
    field: 'noAntrian',
    sortable: true
  },
  {
    name: 'tanggalJamSpk',
    label: 'Tanggal/Jam',
    align: 'left',
    field: 'tanggalJamSpk',
    sortable: true
  },
  {
    name: 'nopol',
    label: 'Nopol',
    align: 'left',
    field: 'nopol',
    sortable: true
  },
  {
    name: 'namaKaryawan',
    label: 'Nama Karyawan',
    align: 'left',
    field: 'namaKaryawan',
    sortable: true
  },
  {
    name: 'km',
    label: 'KM',
    align: 'center',
    field: 'km',
    sortable: true
  },
  {
    name: 'statusSpk',
    label: 'Status SPK',
    align: 'center',
    field: 'statusSpk',
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
const fetchSpk = async (paginationData = pagination.value) => {
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
    
    const response = await api.get('/api/pazaauto/spk/paginated', { params })
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
      message: 'Failed to fetch SPK data',
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
  fetchSpk(pagination.value)
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
    noSpk: '',
    noAntrian: null,
    tanggalJamSpk: '',
    nopol: '',
    namaKaryawan: '',
    km: null,
    statusSpk: '',
    diskon: null,
    keluhan: '',
    keterangan: '',
    kmSaatIni: null,
    ppn: null,
    status: '',
    csId: null,
    mekanikId: null
  }
}

const saveSpk = async () => {
  saving.value = true
  try {
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/pazaauto/spk/${formData.value.id}`, formData.value)
    } else {
      response = await api.post('/api/pazaauto/spk', formData.value)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'SPK updated successfully' : 'SPK created successfully'
      })
      closeDialog()
      await fetchSpk()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save SPK',
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

const deleteSpk = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/pazaauto/spk/${itemToDelete.value.id}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'SPK deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchSpk()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete SPK',
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

const getStatusColor = (status) => {
  if (!status) return 'grey'
  const statusLower = status.toLowerCase()
  if (statusLower.includes('selesai') || statusLower.includes('complete')) return 'green'
  if (statusLower.includes('proses') || statusLower.includes('progress')) return 'orange'
  if (statusLower.includes('batal') || statusLower.includes('cancel')) return 'red'
  return 'blue'
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
    fetchSpk()
  }, 500)
})

// Lifecycle
onMounted(() => {
  fetchSpk()
})
</script>

<style lang="sass" scoped>
.dialog-spk
  min-width: 840px
.my-sticky-header-table
  max-height: 70vh

  thead tr th
    position: sticky
    z-index: 1
    background-color: #ffffff

  thead tr:first-child th
    top: 0
</style>
