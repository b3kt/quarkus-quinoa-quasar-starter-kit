<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn 
          flat 
          :label="$t('create') + ' Pelanggan'" 
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
            placeholder="Search by name, nopol, or email..."
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
        row-key="id"
        :loading="loading"
        :pagination="pagination"
        @request="onRequest"
      >
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
      <q-card style="min-width: 700px; max-width: 800px">
        <q-card-section>
          <div class="text-h6">{{ isEditMode ? 'Edit Pelanggan' : 'Create Pelanggan' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none" style="max-height: 70vh; overflow-y: auto">
          <q-form @submit="savePelanggan" class="q-gutter-md">
            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model="formData.nopol"
                  label="Nopol *"
                  outlined
                  dense
                  :rules="[val => !!val || 'Nopol is required']"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.namaPelanggan"
                  label="Nama Pelanggan *"
                  outlined
                  dense
                  :rules="[val => !!val || 'Nama Pelanggan is required']"
                />
              </div>
            </div>

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model="formData.email"
                  label="Email"
                  outlined
                  dense
                  type="email"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.noHp"
                  label="No HP"
                  outlined
                  dense
                />
              </div>
            </div>

            <q-input
              v-model="formData.alamat"
              label="Alamat"
              outlined
              dense
              type="textarea"
              rows="2"
            />

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model="formData.kota"
                  label="Kota"
                  outlined
                  dense
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.kodePos"
                  label="Kode Pos"
                  outlined
                  dense
                />
              </div>
            </div>

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-select
                  v-model="formData.jenisKelamin"
                  label="Jenis Kelamin"
                  outlined
                  dense
                  :options="[
                    { label: 'Laki-laki', value: 'L' },
                    { label: 'Perempuan', value: 'P' }
                  ]"
                  option-label="label"
                  option-value="value"
                  emit-value
                  map-options
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.tanggalLahir"
                  label="Tanggal Lahir"
                  outlined
                  dense
                  type="date"
                />
              </div>
            </div>

            <div class="text-subtitle2 q-mt-md">Informasi Kendaraan</div>
            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model="formData.merk"
                  label="Merk *"
                  outlined
                  dense
                  :rules="[val => !!val || 'Merk is required']"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.jenis"
                  label="Jenis"
                  outlined
                  dense
                />
              </div>
            </div>

            <q-input
              v-model="formData.keterangan"
              label="Keterangan"
              outlined
              dense
              type="textarea"
              rows="2"
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
          Are you sure you want to delete <strong>{{ itemToDelete?.namaPelanggan }}</strong>?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn 
            flat 
            label="Delete" 
            color="negative" 
            @click="deletePelanggan"
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
  id: null,
  nopol: '',
  namaPelanggan: '',
  email: '',
  noHp: '',
  alamat: '',
  kota: '',
  kodePos: '',
  jenisKelamin: null,
  tanggalLahir: null,
  merk: '',
  jenis: '',
  keterangan: ''
})

const columns = [
  {
    name: 'nopol',
    required: true,
    label: 'Nopol',
    align: 'left',
    field: 'nopol',
    sortable: true
  },
  {
    name: 'namaPelanggan',
    required: true,
    label: 'Nama Pelanggan',
    align: 'left',
    field: 'namaPelanggan',
    sortable: true
  },
  {
    name: 'email',
    label: 'Email',
    align: 'left',
    field: 'email'
  },
  {
    name: 'noHp',
    label: 'No HP',
    align: 'left',
    field: 'noHp'
  },
  {
    name: 'merk',
    label: 'Merk',
    align: 'left',
    field: 'merk',
    sortable: true
  },
  {
    name: 'jenis',
    label: 'Jenis',
    align: 'left',
    field: 'jenis'
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
    row.namaPelanggan?.toLowerCase().includes(search) ||
    row.nopol?.toLowerCase().includes(search) ||
    row.email?.toLowerCase().includes(search)
  )
})

const fetchPelanggan = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/pazaauto/pelanggan')
    if (response.data.success) {
      rows.value = response.data.data || []
      pagination.value.rowsNumber = rows.value.length
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to fetch pelanggan data',
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
    id: null,
    nopol: '',
    namaPelanggan: '',
    email: '',
    noHp: '',
    alamat: '',
    kota: '',
    kodePos: '',
    jenisKelamin: null,
    tanggalLahir: null,
    merk: '',
    jenis: '',
    keterangan: ''
  }
}

const savePelanggan = async () => {
  saving.value = true
  try {
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/pazaauto/pelanggan/${formData.value.id}`, formData.value)
    } else {
      response = await api.post('/api/pazaauto/pelanggan', formData.value)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'Pelanggan updated successfully' : 'Pelanggan created successfully'
      })
      closeDialog()
      await fetchPelanggan()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save pelanggan',
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

const deletePelanggan = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/pazaauto/pelanggan/${itemToDelete.value.id}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'Pelanggan deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchPelanggan()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete pelanggan',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  fetchPelanggan()
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
