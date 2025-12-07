<template>
  <q-page padding>
    <GenericTable :rows="rows" :columns="columns" :loading="loading" :pagination="pagination"
      @update:pagination="pagination = $event" @request="onRequest" @search="onSearch" :on-create="openCreateDialog"
      :on-edit="openEditDialog" :on-delete="confirmDelete" create-label="Create Supplier"
      search-placeholder="Search by name or email..." />

    <!-- Create/Edit Dialog -->
    <GenericDialog v-model="showDialog" :title="isEditMode ? 'Edit Supplier' : 'Create Supplier'" min-width="600px">
      <q-form @submit="handleSave" id="supplier-form" class="q-gutter-md">
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
      </q-form>
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDialog = false" />
        <q-btn label="Save" type="submit" form="supplier-form" color="primary" :loading="saving" />
      </template>
    </GenericDialog>

    <!-- Delete Confirmation Dialog -->
    <GenericDialog v-model="showDeleteDialog" title="Confirm Delete" min-width="400px">
      Are you sure you want to delete <strong>{{ itemToDelete?.namaSupplier }}</strong>?
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
  baseApiUrl: '/api/pazaauto/supplier',
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

const openCreateDialog = () => {
  baseOpenCreateDialog(resetForm)
}

const openEditDialog = (row) => {
  baseOpenEditDialog(row, (r) => {
    formData.value = { ...r }
  })
}

const handleSave = async () => {
  // For create, we might want to exclude ID if it's null, but the backend usually ignores it or handles it.
  // The original code did: const { ...dataToSend } = formData.value
  // Let's just pass formData.value, useCrud handles PUT vs POST based on isEditMode.
  // However, for POST, if ID is null, it's fine.
  await saveData(formData.value)
}

// Table Columns
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

// Lifecycle
onMounted(() => {
  fetchData()
})
</script>

<style lang="sass" scoped>
</style>
