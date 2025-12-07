<template>
  <q-page padding>
    <GenericTable :rows="rows" :columns="columns" :loading="loading" :pagination="pagination"
      @update:pagination="pagination = $event" @request="onRequest" @search="onSearch" :on-create="openCreateDialog"
      :on-edit="openEditDialog" :on-delete="confirmDelete" create-label="Create Karyawan"
      search-placeholder="Search by name or email...">
      <template v-slot:body-cell-jenisKelamin="props">
        <q-td :props="props">
          <q-badge :color="props.row.jenisKelamin === 'L' ? 'blue' : 'pink'">
            {{ props.row.jenisKelamin === 'L' ? 'Laki-laki' : 'Perempuan' }}
          </q-badge>
        </q-td>
      </template>
    </GenericTable>

    <!-- Create/Edit Dialog -->
    <GenericDialog v-model="showDialog" :title="isEditMode ? 'Edit Karyawan' : 'Create Karyawan'" min-width="600px">
      <q-form @submit="handleSave" id="karyawan-form" class="q-gutter-md">
        <q-input v-model="formData.namaKaryawan" label="Nama Karyawan *" outlined dense
          :rules="[val => !!val || 'Nama Karyawan is required']" />

        <div class="row q-col-gutter">
          <div class="col-6">
            <q-input v-model="formData.email" label="Email" outlined dense type="email" class="q-mr-sm" />
          </div>
          <div class="col-6">
            <q-input v-model="formData.noTelepon" label="No Telepon" outlined dense class="q-ml-sm" />
          </div>
        </div>

        <q-input v-model="formData.alamat" label="Alamat" outlined dense type="textarea" rows="2" />

        <div class="row q-col-gutter">
          <div class="col-6">
            <q-select v-model="formData.jenisKelamin" label="Jenis Kelamin" outlined dense options-dense :options="[
              { label: 'Laki-laki', value: 'L' },
              { label: 'Perempuan', value: 'P' }
            ]" option-label="label" option-value="value" emit-value map-options class="q-mr-sm" />
          </div>
          <div class="col-6">
            <q-select v-model="formData.idPosisi" label="Posisi *" outlined dense :options="filteredPosisiOptions"
              option-label="posisi" option-value="id" emit-value map-options use-input input-debounce="300"
              @filter="filterPosisi" class="q-ml-sm" :rules="[val => !!val || 'Posisi is required']"
              :loading="loadingPosisi">
              <template v-slot:no-option>
                <q-item>
                  <q-item-section class="text-grey">
                    No results
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
          </div>
        </div>

        <div class="row q-col-gutter">
          <div class="col-6">
            <q-input v-model="formData.tanggalLahir" label="Tanggal Lahir" outlined dense type="date" class="q-mr-sm" />
          </div>
          <div class="col-6">
            <q-input v-model="formData.tanggalBergabung" label="Tanggal Bergabung" outlined dense type="date"
              class="q-ml-sm" />
          </div>
        </div>
      </q-form>
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDialog = false" />
        <q-btn label="Save" type="submit" form="karyawan-form" color="primary" :loading="saving" />
      </template>
    </GenericDialog>

    <!-- Delete Confirmation Dialog -->
    <GenericDialog v-model="showDeleteDialog" title="Confirm Delete" min-width="400px">
      Are you sure you want to delete <strong>{{ itemToDelete?.namaKaryawan }}</strong>?
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
        <q-btn flat label="Delete" color="negative" @click="deleteItem" :loading="deleting" />
      </template>
    </GenericDialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from 'boot/axios'
import GenericTable from 'components/GenericTable.vue'
import GenericDialog from 'components/GenericDialog.vue'
import { useCrud } from 'src/composables/useCrud'

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
  baseApiUrl: '/api/pazaauto/karyawan',
  defaultPagination: {
    sortBy: null,
    descending: false,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
  }
})

// Posisi Logic
const loadingPosisi = ref(false)
const posisiOptions = ref([])
const filteredPosisiOptions = ref([])

const fetchPosisi = async () => {
  loadingPosisi.value = true
  try {
    const response = await api.get('/api/pazaauto/karyawan-posisi')
    if (response.data.success) {
      posisiOptions.value = response.data.data || []
      filteredPosisiOptions.value = posisiOptions.value
    }
  } catch (error) {
    console.error('Failed to fetch posisi data', error)
  } finally {
    loadingPosisi.value = false
  }
}

const filterPosisi = (val, update) => {
  update(() => {
    if (val === '') {
      filteredPosisiOptions.value = posisiOptions.value
    } else {
      const needle = val.toLowerCase()
      filteredPosisiOptions.value = posisiOptions.value.filter(
        v => v.posisi.toLowerCase().indexOf(needle) > -1
      )
    }
  })
}

// Form Data
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
    name: 'idPosisi',
    label: 'Posisi',
    align: 'center',
    field: 'idPosisi',
    sortable: true
  },
  {
    name: 'actions',
    label: 'Actions',
    align: 'center',
    field: 'actions'
  }
]

// Lifecycle
onMounted(() => {
  fetchData()
  fetchPosisi()
})
</script>

<style lang="sass" scoped>
</style>
