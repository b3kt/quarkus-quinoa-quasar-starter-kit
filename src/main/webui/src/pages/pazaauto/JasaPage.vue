<template>
  <q-page padding>
    <GenericTable :rows="rows" :columns="columns" :loading="loading" :pagination="pagination"
      @update:pagination="pagination = $event" @request="onRequest" @search="onSearch" :on-create="openCreateDialog"
      :on-edit="openEditDialog" :on-delete="confirmDelete" create-label="Create Jasa"
      search-placeholder="Search by name...">
      <template v-slot:body-cell-hargaJasa="props">
        <q-td :props="props">
          {{ formatCurrency(props.row.hargaJasa) }}
        </q-td>
      </template>
    </GenericTable>

    <!-- Create/Edit Dialog -->
    <GenericDialog v-model="showDialog" :title="isEditMode ? 'Edit Jasa' : 'Create Jasa'" min-width="500px">
      <q-form @submit="handleSave" id="jasa-form" class="q-gutter-md">
        <q-input v-model="formData.namaJasa" label="Nama Jasa *" outlined dense
          :rules="[val => !!val || 'Nama Jasa tidak boleh kosong']" />

        <q-input v-model.number="formData.hargaJasa" label="Harga Jasa" outlined dense type="number" prefix="Rp"
          :rules="[val => val >= 0 || 'Harga jasa tidak valid']" />

        <q-input v-model.number="formData.estimasiWaktu" label="Estimasi Waktu (menit)" outlined dense type="number"
          :rules="[val => val >= 0 || 'Waktu estimasi tidak valid']" />

        <q-input v-model="formData.deskripsi" label="Deskripsi" outlined dense type="textarea" rows="3" />
      </q-form>
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDialog = false" />
        <q-btn label="Save" type="submit" form="jasa-form" color="primary" :loading="saving" />
      </template>
    </GenericDialog>

    <!-- Delete Confirmation Dialog -->
    <GenericDialog v-model="showDeleteDialog" title="Confirm Delete" min-width="400px">
      Are you sure you want to delete <strong>{{ itemToDelete?.namaJasa }}</strong>?
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
  baseApiUrl: '/api/pazaauto/jasa',
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
  namaJasa: '',
  hargaJasa: null,
  estimasiWaktu: null,
  deskripsi: ''
})

const resetForm = () => {
  formData.value = {
    id: null,
    namaJasa: '',
    hargaJasa: null,
    estimasiWaktu: null,
    deskripsi: ''
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
    name: 'namaJasa',
    required: true,
    label: 'Nama Jasa',
    align: 'left',
    field: 'namaJasa',
    sortable: true
  },
  {
    name: 'hargaJasa',
    label: 'Harga Jasa',
    align: 'right',
    field: 'hargaJasa',
    sortable: true
  },
  {
    name: 'estimasiWaktu',
    label: 'Estimasi Waktu (menit)',
    align: 'center',
    field: 'estimasiWaktu',
    sortable: true
  },
  {
    name: 'deskripsi',
    label: 'Deskripsi',
    align: 'left',
    field: 'deskripsi'
  },
  {
    name: 'actions',
    label: 'Actions',
    align: 'center',
    field: 'actions'
  }
]

const formatCurrency = (value) => {
  if (!value) return 'Rp 0'
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(value)
}

// Lifecycle
onMounted(() => {
  fetchData()
})
</script>

<style lang="sass" scoped>
</style>
