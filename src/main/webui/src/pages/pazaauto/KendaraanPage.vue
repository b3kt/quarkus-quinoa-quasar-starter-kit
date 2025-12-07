<template>
  <q-page padding>
    <GenericTable :rows="rows" :columns="columns" :loading="loading" :pagination="pagination"
      @update:pagination="pagination = $event" @request="onRequest" @search="onSearch" :on-create="openCreateDialog"
      :on-edit="openEditDialog" :on-delete="confirmDelete" create-label="Create Kendaraan"
      search-placeholder="Search..." />

    <!-- Create/Edit Dialog -->
    <GenericDialog v-model="showDialog" :title="isEditMode ? 'Edit Kendaraan' : 'Create Kendaraan'" min-width="600px">
      <q-form @submit="handleSave" id="kendaraan-form" class="q-gutter-md">
        <div class="row q-col-gutter">
          <div class="col-6">
            <q-select v-model="formData.merk" label="Merk *" outlined dense :options="filteredMerkOptions" use-input
              input-debounce="300" @filter="filterMerk" @new-value="createMerkValue" :loading="loadingMerk"
              new-value-mode="add-unique" :rules="[val => !!val || 'Merk is required']" class="q-mr-md">
              <template v-slot:no-option>
                <q-item>
                  <q-item-section class="text-grey">
                    No results - type to add new
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
          </div>
        </div>

        <div class="row q-col-gutter">
          <div class="col-6">
            <q-input v-model="formData.jenis" label="Jenis *" outlined dense
              :rules="[val => !!val || 'Jenis is required']" class="q-mr-md" />
          </div>
          <div class="col-6">
            <q-input v-model="formData.model" label="Model" outlined dense />
          </div>
        </div>

        <q-input v-model="formData.keterangan" label="Keterangan" outlined dense type="textarea" rows="2" />
      </q-form>
      <template #actions>
        <q-btn flat label="Cancel" color="primary" @click="showDialog = false" />
        <q-btn label="Save" type="submit" form="kendaraan-form" color="primary" :loading="saving" />
      </template>
    </GenericDialog>

    <!-- Delete Confirmation Dialog -->
    <GenericDialog v-model="showDeleteDialog" title="Confirm Delete" min-width="400px">
      Are you sure you want to delete this kendaraan?
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
  // itemToDelete,
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
  baseApiUrl: '/api/pazaauto/kendaraan',
  defaultPagination: {
    sortBy: null,
    descending: false,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
  }
})

// Merk Logic
const loadingMerk = ref(false)
const merkOptions = ref([])
const filteredMerkOptions = ref([])

const fetchDistinctMerks = async () => {
  loadingMerk.value = true
  try {
    const response = await api.get('/api/pazaauto/kendaraan/merk/distinct')
    if (response.data.success) {
      merkOptions.value = response.data.data || []
      filteredMerkOptions.value = merkOptions.value
    }
  } catch (error) {
    console.error('Failed to fetch merk options', error)
  } finally {
    loadingMerk.value = false
  }
}

const filterMerk = (val, update) => {
  update(() => {
    if (val === '') {
      filteredMerkOptions.value = merkOptions.value
    } else {
      const needle = val.toLowerCase()
      filteredMerkOptions.value = merkOptions.value.filter(
        v => v.toLowerCase().indexOf(needle) > -1
      )
    }
  })
}

const createMerkValue = (val, done) => {
  if (val.length > 0) {
    if (!merkOptions.value.includes(val)) {
      merkOptions.value.push(val)
    }
    done(val, 'add-unique')
  }
}

// Form Data
const formData = ref({
  id: null,
  merk: '',
  jenis: '',
  model: '',
  keterangan: ''
})

const resetForm = () => {
  formData.value = {
    id: null,
    merk: '',
    jenis: '',
    model: '',
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
    name: 'merk',
    required: true,
    label: 'Merk',
    align: 'left',
    field: 'merk',
    sortable: true
  },
  {
    name: 'jenis',
    required: true,
    label: 'Jenis',
    align: 'left',
    field: 'jenis',
    sortable: true
  },
  {
    name: 'model',
    label: 'Model',
    align: 'left',
    field: 'model',
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
  fetchDistinctMerks()
})
</script>

<style lang="sass" scoped>
</style>
