<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn 
          flat 
          :label="$t('create') + ' Kendaraan'" 
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
            placeholder="Search..."
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
      <q-card style="min-width: 600px">
        <q-card-section>
          <div class="text-h6">{{ isEditMode ? 'Edit Kendaraan' : 'Create Kendaraan' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveKendaraan" class="q-gutter-md">
            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model="formData.jenis"
                  label="Jenis *"
                  outlined
                  dense
                  :rules="[val => !!val || 'Jenis is required']"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.model"
                  label="Model"
                  outlined
                  dense
                />
              </div>
            </div>

            <div class="row q-col-gutter">
              <div class="col-6">
                <q-input
                  v-model="formData.nomorMesin"
                  label="Nomor Mesin"
                  outlined
                  dense
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.nomorRangka"
                  label="Nomor Rangka"
                  outlined
                  dense
                />
              </div>
            </div>

            <div class="row q-col-gutter-">
              <div class="col-6">
                <q-input
                  v-model="formData.tahunPembuatan"
                  label="Tahun Pembuatan"
                  outlined
                  dense
                  maxlength="4"
                  class="q-mr-md"
                />
              </div>
              <div class="col-6">
                <q-input
                  v-model="formData.warna"
                  label="Warna"
                  outlined
                  dense
                />
              </div>
            </div>

            <q-input
              v-model.number="formData.pelangganId"
              label="Pelanggan ID"
              outlined
              dense
              type="number"
            />

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
          Are you sure you want to delete this kendaraan?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn 
            flat 
            label="Delete" 
            color="negative" 
            @click="deleteKendaraan"
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
  jenis: '',
  model: '',
  nomorMesin: '',
  nomorRangka: '',
  tahunPembuatan: '',
  warna: '',
  pelangganId: null,
  keterangan: ''
})

const columns = [
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
    name: 'nomorMesin',
    label: 'Nomor Mesin',
    align: 'left',
    field: 'nomorMesin'
  },
  {
    name: 'nomorRangka',
    label: 'Nomor Rangka',
    align: 'left',
    field: 'nomorRangka'
  },
  {
    name: 'tahunPembuatan',
    label: 'Tahun',
    align: 'center',
    field: 'tahunPembuatan',
    sortable: true
  },
  {
    name: 'warna',
    label: 'Warna',
    align: 'center',
    field: 'warna'
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
    row.jenis?.toLowerCase().includes(search) ||
    row.model?.toLowerCase().includes(search) ||
    row.nomorMesin?.toLowerCase().includes(search) ||
    row.nomorRangka?.toLowerCase().includes(search)
  )
})

const fetchKendaraan = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/pazaauto/kendaraan')
    if (response.data.success) {
      rows.value = response.data.data || []
      pagination.value.rowsNumber = rows.value.length
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to fetch kendaraan data',
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
    jenis: '',
    model: '',
    nomorMesin: '',
    nomorRangka: '',
    tahunPembuatan: '',
    warna: '',
    pelangganId: null,
    keterangan: ''
  }
}

const saveKendaraan = async () => {
  saving.value = true
  try {
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/pazaauto/kendaraan/${formData.value.id}`, formData.value)
    } else {
      response = await api.post('/api/pazaauto/kendaraan', formData.value)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'Kendaraan updated successfully' : 'Kendaraan created successfully'
      })
      closeDialog()
      await fetchKendaraan()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save kendaraan',
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

const deleteKendaraan = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/pazaauto/kendaraan/${itemToDelete.value.id}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'Kendaraan deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchKendaraan()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete kendaraan',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  fetchKendaraan()
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
