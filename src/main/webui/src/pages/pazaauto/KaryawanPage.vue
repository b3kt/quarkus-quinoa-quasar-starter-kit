<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn 
          flat 
          label="Create Karyawan" 
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
            placeholder="Search by name or email..."
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
        <template v-slot:body-cell-jenisKelamin="props">
          <q-td :props="props">
            <q-badge :color="props.row.jenisKelamin === 'L' ? 'blue' : 'pink'">
              {{ props.row.jenisKelamin === 'L' ? 'Laki-laki' : 'Perempuan' }}
            </q-badge>
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
          <div class="text-h6">{{ isEditMode ? 'Edit Karyawan' : 'Create Karyawan' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveKaryawan" class="q-gutter-md">
            <q-input
              v-model="formData.namaKaryawan"
              label="Nama Karyawan *"
              outlined
              dense
              :rules="[val => !!val || 'Nama Karyawan is required']"
            />

            <div class="row q-col-gutter-md">
              <div class="col-6">
                <q-input
                  v-model="formData.email"
                  label="Email"
                  outlined
                  dense
                  type="email"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.noTelepon"
                  label="No Telepon"
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

            <div class="row q-col-gutter-md">
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
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model.number="formData.idPosisi"
                  label="ID Posisi"
                  outlined
                  dense
                  type="number"
                />
              </div>
            </div>

            <div class="row q-col-gutter-md">
              <div class="col-6">
                <q-input
                  v-model="formData.tanggalLahir"
                  label="Tanggal Lahir"
                  outlined
                  dense
                  type="date"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.tanggalBergabung"
                  label="Tanggal Bergabung"
                  outlined
                  dense
                  type="date"
                />
              </div>
            </div>

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
          Are you sure you want to delete <strong>{{ itemToDelete?.namaKaryawan }}</strong>?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn 
            flat 
            label="Delete" 
            color="negative" 
            @click="deleteKaryawan"
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
  namaKaryawan: '',
  email: '',
  noTelepon: '',
  alamat: '',
  jenisKelamin: null,
  tanggalLahir: null,
  tanggalBergabung: null,
  idPosisi: null
})

const columns = [
  {
    name: 'namaKaryawan',
    required: true,
    label: 'Nama Karyawan',
    align: 'left',
    field: 'namaKaryawan',
    sortable: true
  },
  {
    name: 'email',
    label: 'Email',
    align: 'left',
    field: 'email',
    sortable: true
  },
  {
    name: 'noTelepon',
    label: 'No Telepon',
    align: 'left',
    field: 'noTelepon'
  },
  {
    name: 'jenisKelamin',
    label: 'Jenis Kelamin',
    align: 'center',
    field: 'jenisKelamin',
    sortable: true
  },
  {
    name: 'tanggalBergabung',
    label: 'Tanggal Bergabung',
    align: 'center',
    field: 'tanggalBergabung',
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
    row.namaKaryawan?.toLowerCase().includes(search) ||
    row.email?.toLowerCase().includes(search)
  )
})

const fetchKaryawan = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/pazaauto/karyawan')
    if (response.data.success) {
      rows.value = response.data.data || []
      pagination.value.rowsNumber = rows.value.length
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to fetch karyawan data',
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
    namaKaryawan: '',
    email: '',
    noTelepon: '',
    alamat: '',
    jenisKelamin: null,
    tanggalLahir: null,
    tanggalBergabung: null,
    idPosisi: null
  }
}

const saveKaryawan = async () => {
  saving.value = true
  try {
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/pazaauto/karyawan/${formData.value.id}`, formData.value)
    } else {
      response = await api.post('/api/pazaauto/karyawan', formData.value)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'Karyawan updated successfully' : 'Karyawan created successfully'
      })
      closeDialog()
      await fetchKaryawan()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save karyawan',
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

const deleteKaryawan = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/pazaauto/karyawan/${itemToDelete.value.id}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'Karyawan deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchKaryawan()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete karyawan',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  fetchKaryawan()
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
