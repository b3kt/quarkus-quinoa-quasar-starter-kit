<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn flat :label="$t('create') + ' Supplier'" icon="add" color="white" class="bg-primary"
          @click="openCreateDialog" />
        <q-space />
        <div class="col-6">
          <q-input dense standout="bg-primary" v-model="searchText" input-class="search-field text-left" class="q-ml-md"
            placeholder="Search by name or email...">
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
      <q-card style="min-width: 600px">
        <q-card-section>
          <div class="text-h6">{{ isEditMode ? 'Edit Supplier' : 'Create Supplier' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveSupplier" class="q-gutter-md">
            <q-input v-model="formData.namaSupplier" label="Nama Supplier *" outlined dense
              :rules="[val => !!val || 'Nama Supplier is required']" />

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input v-model="formData.email" label="Email" outlined dense type="email" class="q-mr-md" />
              </div>
              <div class="col-6">
                <q-input v-model="formData.noTelepon" label="No Telepon" outlined dense />
              </div>
            </div>

            <q-input v-model="formData.alamat" label="Alamat" outlined dense type="textarea" rows="2" />

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input v-model="formData.kota" label="Kota" outlined dense class="q-mr-md" />
              </div>
              <div class="col-6">
                <q-input v-model="formData.kodePos" label="Kode Pos" outlined dense />
              </div>
            </div>

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input v-model="formData.kontakPerson" label="Kontak Person" outlined dense class="q-mr-md" />
              </div>
              <div class="col-6">
                <q-input v-model="formData.noHpKontak" label="No HP Kontak" outlined dense />
              </div>
            </div>

            <q-input v-model="formData.keterangan" label="Keterangan" outlined dense type="textarea" rows="2" />

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
          Are you sure you want to delete <strong>{{ itemToDelete?.namaSupplier }}</strong>?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn flat label="Delete" color="negative" @click="deleteSupplier" :loading="deleting" />
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

const formData = ref({
  id: null,
  namaSupplier: '',
  email: '',
  noTelepon: '',
  alamat: '',
  kota: '',
  kodePos: '',
  kontakPerson: '',
  noHpKontak: '',
  keterangan: ''
})

const columns = [
  {
    name: 'namaSupplier',
    required: true,
    label: 'Nama Supplier',
    align: 'left',
    field: 'namaSupplier',
    sortable: true
  },
  {
    name: 'email',
    label: 'Email',
    align: 'left',
    field: 'email'
  },
  {
    name: 'noTelepon',
    label: 'No Telepon',
    align: 'left',
    field: 'noTelepon'
  },
  {
    name: 'kota',
    label: 'Kota',
    align: 'left',
    field: 'kota',
    sortable: true
  },
  {
    name: 'kontakPerson',
    label: 'Kontak Person',
    align: 'left',
    field: 'kontakPerson'
  },
  {
    name: 'actions',
    label: 'Actions',
    align: 'center',
    field: 'actions'
  }
]

// Removed filteredRows - using server-side filtering

const fetchSupplier = async (paginationData = pagination.value) => {
  loading.value = true
  try {
    const params = {
      page: paginationData.page,
      rowsPerPage: paginationData.rowsPerPage
    }

    if (paginationData.sortBy) {
      params.sortBy = paginationData.sortBy
      params.descending = paginationData.descending
    }

    if (searchText.value) {
      params.search = searchText.value
    }

    const response = await api.get('/api/pazaauto/supplier/paginated', { params })
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
      message: 'Failed to fetch supplier data',
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
  fetchSupplier(pagination.value)
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
    namaSupplier: '',
    email: '',
    noTelepon: '',
    alamat: '',
    kota: '',
    kodePos: '',
    kontakPerson: '',
    noHpKontak: '',
    keterangan: ''
  }
}

const saveSupplier = async () => {
  saving.value = true
  try {
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/pazaauto/supplier/${formData.value.id}`, formData.value)
    } else {
      // For create, don't send id (UUID will be auto-generated)
      const { ...dataToSend } = formData.value
      response = await api.post('/api/pazaauto/supplier', dataToSend)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'Supplier updated successfully' : 'Supplier created successfully'
      })
      closeDialog()
      await fetchSupplier()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save supplier',
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

const deleteSupplier = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/pazaauto/supplier/${itemToDelete.value.id}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'Supplier deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchSupplier()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete supplier',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    deleting.value = false
  }
}

// Watchers
let searchTimeout = null
watch(searchText, () => {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  searchTimeout = setTimeout(() => {
    pagination.value.page = 1
    fetchSupplier()
  }, 500)
})

onMounted(() => {
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
