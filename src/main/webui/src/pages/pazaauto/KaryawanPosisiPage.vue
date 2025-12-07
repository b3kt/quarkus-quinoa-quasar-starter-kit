<template>
  <q-page padding>
    <GenericTable :rows="rows" :columns="columns" :loading="loading" :pagination="pagination"
      @update:pagination="pagination = $event" @request="onRequest" @search="onSearch" :on-create="openCreateDialog"
      :on-edit="openEditDialog" :on-delete="confirmDelete" create-label="Create Posisi"
      search-placeholder="Search..." />

    <!-- Create/Edit Dialog -->
    <GenericDialog v-model="showDialog" :title="isEditMode ? 'Edit Posisi' : 'Create Posisi'" min-width="500px">
      <q-form @submit="handleSave" id="posisi-form" class="q-gutter-md">
        <q-input v-model="formData.posisi" label="Nama Posisi *" outlined dense
          :rules="[val => !!val || 'Nama Posisi is required']" />

        <q-input v-model="formData.keterangan" label="Keterangan" outlined dense type="textarea" rows="3" />
      </q-form>
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDialog = false" />
        <q-btn label="Save" type="submit" form="posisi-form" color="primary" :loading="saving" />
      </template>
    </GenericDialog>

    <!-- Delete Confirmation Dialog -->
    <GenericDialog v-model="showDeleteDialog" title="Confirm Delete" min-width="400px">
      Are you sure you want to delete <strong>{{ itemToDelete?.posisi }}</strong>?
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
  baseApiUrl: '/api/pazaauto/karyawan-posisi',
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
  posisi: '',
  keterangan: ''
})

const resetForm = () => {
  formData.value = {
    id: null,
    posisi: '',
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
    name: 'posisi',
    required: true,
    label: 'Nama Posisi',
    align: 'left',
    field: 'posisi',
    sortable: true
  },
  {
    name: 'keterangan',
    label: 'Keterangan',
    align: 'left',
    field: 'keterangan'
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
