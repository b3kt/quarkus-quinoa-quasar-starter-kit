<template>
  <q-page padding>
    <GenericTable :rows="rows" :columns="columns" :loading="loading" :pagination="pagination"
      @update:pagination="pagination = $event" @request="onRequest" @search="onSearch" :on-create="openCreateDialog"
      :on-edit="openEditDialog" :on-delete="confirmDelete" create-label="Create Pelanggan"
      search-placeholder="Search by name, nopol, or email..." />

    <!-- Create/Edit Dialog -->
    <GenericDialog v-model="showDialog" :title="isEditMode ? 'Edit Pelanggan' : 'Create Pelanggan'" min-width="700px"
      max-width="800px">
      <q-form @submit="handleSave" id="pelanggan-form" class="q-gutter-md">
        <div class="row q-col-gutter">
          <div class="col-6">
            <q-input v-model="formData.nopol" label="Nopol *" outlined dense
              :rules="[val => !!val || 'Nopol is required']" class="q-mr-md" />
          </div>
          <div class="col-6">
            <q-input v-model="formData.namaPelanggan" label="Nama Pelanggan *" outlined dense
              :rules="[val => !!val || 'Nama Pelanggan is required']" />
          </div>
        </div>

        <div class="row q-col-gutter">
          <div class="col-6">
            <q-input v-model="formData.email" label="Email" outlined dense type="email" class="q-mr-md" />
          </div>
          <div class="col-6">
            <q-input v-model="formData.noHp" label="No HP" outlined dense />
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
            <q-select v-model="formData.jenisKelamin" label="Jenis Kelamin" outlined dense :options="[
              { label: 'Laki-laki', value: 'L' },
              { label: 'Perempuan', value: 'P' }
            ]" option-label="label" option-value="value" emit-value map-options class="q-mr-md" />
          </div>
          <div class="col-6">
            <q-input v-model="formData.tanggalJoin" label="Tanggal Join" outlined dense type="date" />
          </div>
        </div>

        <div class="text-subtitle2 q-mt-md">Informasi Kendaraan</div>
        <div class="row q-col-gutter">
          <div class="col-6">
            <q-input v-model="formData.merk" label="Merk *" outlined dense :rules="[val => !!val || 'Merk is required']"
              class="q-mr-md" />
          </div>
          <div class="col-6">
            <q-input v-model="formData.jenis" label="Jenis" outlined dense />
          </div>
        </div>

        <q-input v-model="formData.keterangan" label="Keterangan" outlined dense type="textarea" rows="2" />
      </q-form>
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDialog = false" />
        <q-btn label="Save" type="submit" form="pelanggan-form" color="primary" :loading="saving" />
      </template>
    </GenericDialog>

    <!-- Delete Confirmation Dialog -->
    <GenericDialog v-model="showDeleteDialog" title="Confirm Delete" min-width="400px">
      Are you sure you want to delete <strong>{{ itemToDelete?.namaPelanggan }}</strong>?
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
        <q-btn flat label="Delete" color="negative" @click="deleteItem" :loading="deleting" />
      </template>
    </GenericDialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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
  baseApiUrl: '/api/pazaauto/pelanggan',
  defaultPagination: {
    sortBy: null,
    descending: false,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
  }
})

// Form Data
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
  tanggalJoin: null,
  merk: '',
  jenis: '',
  keterangan: ''
})

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
    tanggalJoin: null,
    merk: '',
    jenis: '',
    keterangan: ''
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

// Lifecycle
onMounted(() => {
  fetchData()
})
</script>

<style lang="sass" scoped>
</style>
