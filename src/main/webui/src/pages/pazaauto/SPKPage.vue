<template>
  <q-page padding>
    <div class="q-pa-md">
      <!-- Toolbar with Create button and Search -->
      <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
        <q-btn flat :label="$t('create') + ' SPK'" icon="add" color="white" class="bg-primary"
          @click="openCreateDialog" />
        <q-space />
        <div class="col-auto q-mr-md">
          <q-checkbox v-model="filterToday" label="Hanya tampilkan SPK Hari Ini" dense />
        </div>
        <div class="col-1">
          <q-select v-model="filterStatus" multiple :options="statusOptions" label="Status" dense options-dense flat
            outlined />
        </div>
        <div class="col-6">
          <q-input dense standout="bg-secondary" v-model="searchText" input-class="search-field text-left"
            class="q-ml-md" placeholder="Search by SPK number, nopol, or employee name...">
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
        <template v-slot:body-cell-statusSpk="props">
          <q-td :props="props">
            <q-badge :color="getStatusColor(props.row.statusSpk)">
              {{ props.row.statusSpk || 'N/A' }}
            </q-badge>
          </q-td>
        </template>

        <template v-slot:body-cell-diskon="props">
          <q-td :props="props">
            {{ formatCurrency(props.row.diskon) }}
          </q-td>
        </template>

        <template v-slot:body-cell-ppn="props">
          <q-td :props="props">
            {{ formatCurrency(props.row.ppn) }}
          </q-td>
        </template>

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
      <q-card class="dialog-spk">
        <q-card-section class="row">
          <div class="text-h6 col-6">
            {{ isEditMode ? 'Edit SPK' : 'Tambah SPK' }}
          </div>
          <q-space />
          <q-chip square icon="event" :color="getStatusColor(formData.statusSpk)">{{ formData.statusSpk }}</q-chip>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveSpk" class="q-gutter-md">
            <q-card class="row col-12" flat bordered>
              <q-card-section class="col-6 q-pr-none">
                <div class="q-mb-md">
                  <span class="text-caption text-bold">Informasi SPK</span>
                </div>
                <div class="q-mb-md">
                  <q-input v-model="formData.tanggalJamSpk" label="Tanggal" outlined dense
                    placeholder="YYYY-MM-DD HH:mm:ss" disable />
                </div>
                <div class="q-mb-md">
                  <q-input v-model="formData.noSpk" label="No SPK" outlined dense disable />
                </div>
                <div class="q-mb-md">
                  <q-select v-model="formData.nopol" label="No Polisi *" outlined dense
                    :options="filteredPelangganOptions" :option-label="constructNopolOptions" option-value="nopol"
                    emit-value map-options use-input input-debounce="300" @filter="filterPelanggan"
                    @update:model-value="onNopolChange" :loading="loadingPelanggan" :disable="!isEditable"
                    new-value-mode="add-unique" :rules="[val => !!val || 'No Polisi is required']">
                    <template v-slot:no-option>
                      <q-item>
                        <q-item-section class="text-grey">
                          No results
                        </q-item-section>
                      </q-item>
                    </template>
                  </q-select>
                </div>

                <div class="q-mb-md">
                  <q-select v-model="selectedMekaniks" label="Select Mechanics" outlined dense multiple
                    :options="karyawanOptions" option-label="namaKaryawan" option-value="id" use-chips use-input
                    input-debounce="300" @filter="filterKaryawan" :loading="loadingKaryawan" :disable="!isEditable"
                    emit-value map-options>
                    <template v-slot:option="{ itemProps, opt }">
                      <q-item v-bind="itemProps">
                        <q-item-section side>
                          <q-checkbox :model-value="isSelected(opt.id)" @update:model-value="toggleMechanic(opt)" />
                        </q-item-section>
                        <q-item-section @click.stop>
                          <q-item-label>{{ opt.namaKaryawan }}</q-item-label>
                        </q-item-section>
                        <q-item-section @click.stop side v-if="isSelected(opt.id)">
                          <q-select :model-value="getMekanikTugas(opt.id)"
                            @update:model-value="setMekanikTugas(opt.id, $event)" :options="['Utama', 'Pembantu']" dense
                            outlined style="min-width: 120px" />
                        </q-item-section>
                      </q-item>
                    </template>
                  </q-select>
                </div>
                <div>
                  <q-input v-model.number="formData.km" label="KM" outlined dense type="number"
                    :rules="[val => !!val || 'KM is required number']" :disable="!isEditable" />
                </div>
                <!-- <div class="q-mb-md">
                  <q-input v-model.number="formData.noAntrian" label="No Antrian" outlined dense type="number" />
                </div> -->
                <q-input v-model="formData.keterangan" label="Keterangan" outlined dense type="textarea" rows="2"
                  :disable="!isEditable" autogrow />
              </q-card-section>

              <q-card-section class="col-6">
                <div class="q-mb-md">
                  <span class="text-caption text-bold">Informasi Pelanggan</span>
                </div>
                <div class="q-mb-md">
                  <q-input v-model="formData.namaPelanggan" label="Nama *" outlined dense :readonly="!isNewCustomer"
                    :rules="isNewCustomer ? [val => !!val || 'Nama is required'] : []" />
                </div>
                <div class="q-mb-md">
                  <q-input v-model="formData.alamat" label="Alamat" outlined dense :readonly="!isNewCustomer"
                    type="textarea" rows="4" />
                </div>
                <div class="q-mb-md">
                  <q-input v-model="formData.nopol" label="Kendaraan" outlined dense readonly />
                </div>
                <div class="row q-col-gutter-sm">
                  <div class="q-mb-md col-6">
                    <q-input v-model="formData.merk" label="Merk *" outlined dense :readonly="!isNewCustomer"
                      :rules="isNewCustomer ? [val => !!val || 'Merk is required'] : []" />
                  </div>
                  <div class="q-mb-md col-6">
                    <q-input v-model="formData.jenis" label="Jenis" outlined dense :readonly="!isNewCustomer" />
                  </div>
                </div>
              </q-card-section>
            </q-card>


            <!-- Split Layout for Jasa and Barang -->
            <div class="row q-col-gutter-md q-mb-md q-pl-md q-py-md">
              <!-- Left: Jasa -->
              <div class="col-12 col-md-6">
                <q-card flat bordered class="full-height">
                  <q-card-section class="bg-grey-2 q-py-xs">
                    <div class="text-subtitle2">LAYANAN PERBAIKAN / SERVIS</div>
                  </q-card-section>
                  <q-card-section class="q-pa-none">
                    <q-table flat :rows="jasaRows" :columns="jasaColumns" row-key="tempId" dense hide-pagination
                      separator="cell">
                      <template v-slot:body="props">
                        <q-tr :props="props">
                          <q-td key="no" :props="props">{{ props.rowIndex + 1 }}</q-td>
                          <q-td key="namaJasa" :props="props">{{ props.row.namaItem }}</q-td>
                          <q-td key="harga" :props="props" class="text-right">{{ formatCurrency(props.row.harga)
                          }}</q-td>
                          <q-td key="jumlah" :props="props">
                            {{ props.row.jumlah }}
                            <q-popup-edit v-model.number="props.row.jumlah" v-slot="scope">
                              <q-input v-model.number="scope.value" dense autofocus counter @keyup.enter="scope.set" />
                            </q-popup-edit>
                          </q-td>
                          <q-td key="total" :props="props" class="text-right">{{ formatCurrency(props.row.harga *
                            props.row.jumlah) }}</q-td>
                          <q-td key="actions" :props="props" class="text-center"
                            v-if="isEditable || formData.statusSpk !== 'SELESAI'">
                            <q-btn flat dense round icon="delete" color="negative" size="sm"
                              @click="removeDetail(props.row)" />
                          </q-td>
                        </q-tr>
                      </template>
                    </q-table>
                    <!-- Inline Add Jasa -->
                    <div class="row q-pa-sm q-col-gutter-xs items-center bg-grey-1"
                      v-if="isEditable && formData.statusSpk != 'SELESAI'">
                      <div class="col-grow">
                        <q-select v-model="newJasa.item" :options="jasaOptions" option-label="namaJasa" dense outlined
                          label="Pilih Jasa" use-input input-debounce="300" @filter="filterJasa" emit-value map-options>
                          <template v-slot:option="scope">
                            <q-item v-bind="scope.itemProps">
                              <q-item-section>
                                <q-item-label>{{ scope.opt.namaJasa }}</q-item-label>
                                <q-item-label caption>{{ formatCurrency(scope.opt.hargaJasa) }}</q-item-label>
                              </q-item-section>
                            </q-item>
                          </template>
                        </q-select>
                      </div>
                      <!-- <div class="col-2">
                        <q-input v-model.number="newJasa.jumlah" type="number" dense outlined label="Qty" />
                      </div> -->
                      <div class="col-auto">
                        <q-btn icon="add" color="primary" dense round size="sm" @click="addJasa"
                          :disable="!newJasa.item" />
                      </div>
                    </div>
                  </q-card-section>
                  <q-separator />
                  <q-card-section class="q-py-xs text-right bg-grey-2">
                    <span class="text-weight-bold">Sub Total: {{ formatCurrency(subtotalJasa) }}</span>
                  </q-card-section>
                </q-card>
              </div>

              <!-- Right: Barang -->
              <div class="col-12 col-md-6">
                <q-card flat bordered class="full-height">
                  <q-card-section class="bg-grey-2 q-py-xs">
                    <div class="text-subtitle2">BARANG / SPAREPART</div>
                  </q-card-section>
                  <q-card-section class="q-pa-none">
                    <q-table flat :rows="barangRows" :columns="barangColumns" row-key="tempId" dense hide-pagination
                      separator="cell">
                      <template v-slot:body="props">
                        <q-tr :props="props">
                          <q-td key="no" :props="props">{{ props.rowIndex + 1 }}</q-td>
                          <q-td key="namaBarang" :props="props">{{ props.row.namaItem }}</q-td>
                          <q-td key="harga" :props="props" class="text-right">{{ formatCurrency(props.row.harga)
                          }}</q-td>
                          <q-td key="jumlah" :props="props">
                            {{ props.row.jumlah }}
                            <q-popup-edit v-model.number="props.row.jumlah" v-slot="scope">
                              <q-input v-model.number="scope.value" dense autofocus counter @keyup.enter="scope.set" />
                            </q-popup-edit>
                          </q-td>
                          <q-td key="total" :props="props" class="text-right">{{ formatCurrency(props.row.harga *
                            props.row.jumlah) }}</q-td>
                          <q-td key="actions" :props="props" class="text-center"
                            v-if="isEditable || formData.statusSpk !== 'SELESAI'">
                            <q-btn flat dense round icon="delete" color="negative" size="sm"
                              @click="removeDetail(props.row)" />
                          </q-td>
                        </q-tr>
                      </template>
                    </q-table>
                    <!-- Inline Add Barang -->
                    <div class="row q-pa-sm q-col-gutter-xs items-center bg-grey-1"
                      v-if="isEditable || formData.statusSpk !== 'SELESAI'">
                      <div class="col-grow">
                        <q-select v-model="newBarang.item" :options="barangOptions" option-label="namaBarang" dense
                          outlined label="Pilih Barang" use-input input-debounce="300" @filter="filterBarang" emit-value
                          map-options>
                          <template v-slot:option="scope">
                            <q-item v-bind="scope.itemProps">
                              <q-item-section>
                                <q-item-label>{{ scope.opt.namaBarang }}</q-item-label>
                                <q-item-label caption>Stok: {{ scope.opt.stok }} | {{
                                  formatCurrency(scope.opt.hargaJual) }}</q-item-label>
                              </q-item-section>
                            </q-item>
                          </template>
                        </q-select>
                      </div>
                      <div class="col-2">
                        <q-input v-model.number="newBarang.jumlah" type="number" dense outlined label="Qty"
                          :rules="[(val) => val > 0 || 'Qty must be greater than 0']" />
                      </div>
                      <div class="col-auto">
                        <q-btn icon="add" color="primary" dense round size="sm" @click="addBarang"
                          :disable="!newBarang.item" />
                      </div>
                    </div>
                  </q-card-section>
                  <q-separator />
                  <q-card-section class="q-py-xs text-right bg-grey-2">
                    <span class="text-weight-bold">Sub Total: {{ formatCurrency(subtotalBarang) }}</span>
                  </q-card-section>
                </q-card>
              </div>
            </div>

            <!-- Grand Total -->
            <div class="row justify-end q-mb-md">
              <div class="text-h6 bg-grey-3 q-px-md q-py-sm rounded-borders">
                GRAND TOTAL: {{ formatCurrency(grandTotal) }}
              </div>
            </div>

            <div class="row justify-end q-gutter-sm">
              <div v-if="isEditMode && formData.statusSpk == 'OPEN'">
                <q-btn label="Mulai Proses" type="button" color="green" @click="startProcess" :loading="saving" />
              </div>

              <div v-if="formData.statusSpk == 'PROSES'">
                <q-btn label="Bayar" type="button" color="green" @click="finishProcess" :loading="saving" />
              </div>

              <q-space />

              <q-btn flat label="Cancel" color="primary" @click="closeDialog" />

              <q-btn label="Simpan" type="submit" color="primary" :loading="saving"
                v-if="formData.statusSpk != 'SELESAI'" />

            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Add Item Dialog Removed (Inline) -->

    <!-- Delete Confirmation Dialog -->
    <q-dialog v-model="showDeleteDialog" persistent>
      <q-card>
        <q-card-section>
          <div class="text-h6">Confirm Delete</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          Are you sure you want to delete SPK <strong>{{ itemToDelete?.noSpk }}</strong>?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
          <q-btn flat label="Delete" color="negative" @click="deleteSpk" :loading="deleting" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()

// LocalStorage key for filter persistence
const FILTER_STORAGE_KEY = 'spk_status_filter'

// Load filter from localStorage
const loadFilterFromStorage = () => {
  try {
    const stored = localStorage.getItem(FILTER_STORAGE_KEY)
    return stored ? JSON.parse(stored) : []
  } catch (error) {
    console.error('Failed to load filter from storage:', error)
    return []
  }
}

// Save filter to localStorage
const saveFilterToStorage = (filter) => {
  try {
    localStorage.setItem(FILTER_STORAGE_KEY, JSON.stringify(filter))
  } catch (error) {
    console.error('Failed to save filter to storage:', error)
  }
}

// State
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const loadingPelanggan = ref(false)
const loadingKaryawan = ref(false)
const isNewCustomer = ref(false)
const searchText = ref('')
const filterStatus = ref(loadFilterFromStorage())
const filterToday = ref(false)
const rows = ref([])
const pelangganOptions = ref([])
const filteredPelangganOptions = ref([])
const karyawanOptions = ref([])
const selectedMekaniks = ref([])  // Array of { id, namaKaryawan, tugas }
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const isEditMode = ref(false)
const itemToDelete = ref(null)
const isEditable = ref(false)

// Detail SPK State
const jasaOptions = ref([])
const allJasaOptions = ref([])
const barangOptions = ref([])
const allBarangOptions = ref([])

const newJasa = ref({ item: null, jumlah: 1 })
const newBarang = ref({ item: null, jumlah: 1 })

const jasaColumns = [
  { name: 'no', label: 'NO', align: 'left', field: 'no' },
  { name: 'namaJasa', label: 'JASA', align: 'left', field: 'namaItem' },
  { name: 'harga', label: 'HARGA SATUAN', align: 'right', field: 'harga' },
  { name: 'jumlah', label: 'QTY', align: 'center', field: 'jumlah' },
  { name: 'total', label: 'TOTAL', align: 'right', field: row => row.harga * row.jumlah },
  { name: 'actions', label: '', align: 'center' }
]

const barangColumns = [
  { name: 'no', label: 'NO', align: 'left', field: 'no' },
  { name: 'namaBarang', label: 'BARANG', align: 'left', field: 'namaItem' },
  { name: 'harga', label: 'HARGA SATUAN', align: 'right', field: 'harga' },
  { name: 'jumlah', label: 'QTY', align: 'center', field: 'jumlah' },
  { name: 'total', label: 'TOTAL', align: 'right', field: row => row.harga * row.jumlah },
  { name: 'actions', label: '', align: 'center' }
]

// Computed
const jasaRows = computed(() => {
  return formData.value.details
    .filter(d => d.jasaId)
    .map(d => {
      const jasa = allJasaOptions.value.find(j => j.id === d.jasaId)
      const harga = jasa ? jasa.hargaJasa : 0
      return { ...d, harga }
    })
})

const barangRows = computed(() => {
  return formData.value.details
    .filter(d => d.sparepartId)
    .map(d => {
      const barang = allBarangOptions.value.find(b => b.id === d.sparepartId)
      const harga = barang ? barang.hargaJual : 0
      return { ...d, harga }
    })
})

const subtotalJasa = computed(() => {
  return jasaRows.value.reduce((sum, row) => sum + (row.harga * row.jumlah), 0)
})

const subtotalBarang = computed(() => {
  return barangRows.value.reduce((sum, row) => sum + (row.harga * row.jumlah), 0)
})

const grandTotal = computed(() => subtotalJasa.value + subtotalBarang.value)

// Status options for filter
const statusOptions = ref([
  'OPEN',
  'PROSES',
  'SELESAI',
  'BATAL'
])

const pagination = ref({
  sortBy: "noSpk",
  descending: true,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
})

// Form data
const formData = ref({
  id: null,
  noSpk: '',
  noAntrian: null,
  tanggalJamSpk: '',
  nopol: '',
  namaKaryawan: '',
  km: null,
  statusSpk: 'OPEN',
  diskon: null,
  keluhan: '',
  keterangan: '',
  kmSaatIni: null,
  ppn: null,
  status: '',
  csId: null,
  mekanikId: null,
  mekanikList: null,
  // Customer info fields
  namaPelanggan: '',
  alamat: '',
  merk: '',
  jenis: '',
  // Details
  details: [],
  startProcess: false
})

// Table columns
const columns = [
  {
    name: 'noSpk',
    required: true,
    label: 'No SPK',
    align: 'left',
    field: 'noSpk',
    sortable: true
  },
  {
    name: 'noAntrian',
    label: 'No Antrian',
    align: 'center',
    field: 'noAntrian',
    sortable: true
  },
  {
    name: 'tanggalJamSpk',
    label: 'Tanggal/Jam',
    align: 'left',
    field: 'tanggalJamSpk',
    sortable: true
  },
  {
    name: 'nopol',
    label: 'Nopol',
    align: 'left',
    field: 'nopol',
    sortable: true
  },
  {
    name: 'namaKaryawan',
    label: 'Nama Karyawan',
    align: 'left',
    field: 'namaKaryawan',
    sortable: true
  },
  {
    name: 'km',
    label: 'KM',
    align: 'center',
    field: 'km',
    sortable: true
  },
  {
    name: 'statusSpk',
    label: 'Status SPK',
    align: 'center',
    field: 'statusSpk',
    sortable: true
  },
  {
    name: 'actions',
    label: 'Actions',
    align: 'center',
    field: 'actions'
  }
]



// Methods
const fetchSpk = async (paginationData = pagination.value) => {
  loading.value = true
  try {
    const params = {
      page: paginationData.page,
      rowsPerPage: paginationData.rowsPerPage
    }

    // Add sorting if specified
    if (paginationData.sortBy) {
      params.sortBy = paginationData.sortBy
      params.descending = paginationData.descending
    }

    // Add search if specified
    if (searchText.value) {
      params.search = searchText.value
    }

    // Add status filter if specified
    if (filterStatus.value && filterStatus.value.length > 0) {
      params.statusFilter = filterStatus.value.join(',')
    }

    // Add today filter if checked
    if (filterToday.value) {
      params.filterToday = true
    }

    const response = await api.get('/api/pazaauto/spk/paginated', { params })
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
      message: 'Failed to fetch SPK data',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    loading.value = false
  }
}

const fetchNextSpkNumber = async () => {
  try {
    const response = await api.get('/api/pazaauto/spk/get-next-spk-number')
    if (response.data.success) {
      formData.value.noSpk = response.data.data
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to fetch SPK data',
      caption: error.response?.data?.message || error.message
    })
  }
}

const fetchPelanggan = async () => {
  loadingPelanggan.value = true
  try {
    const response = await api.get('/api/pazaauto/pelanggan')
    if (response.data.success) {
      pelangganOptions.value = response.data.data || []
      filteredPelangganOptions.value = pelangganOptions.value
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to fetch pelanggan data',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    loadingPelanggan.value = false
  }
}

const filterPelanggan = (val, update) => {
  update(() => {
    if (val === '') {
      filteredPelangganOptions.value = pelangganOptions.value
    } else {
      const needle = val.toLowerCase()
      filteredPelangganOptions.value = pelangganOptions.value.filter(
        v => v.nopol.toLowerCase().indexOf(needle) > -1
      )
    }
  })
}

const onNopolChange = async (nopol) => {
  if (!nopol) {
    // Clear customer info fields
    formData.value.namaPelanggan = ''
    formData.value.alamat = ''
    formData.value.merk = ''
    formData.value.jenis = ''
    isNewCustomer.value = false
    return
  }

  // Check if nopol exists in pelangganOptions
  const existingPelanggan = pelangganOptions.value.find(p => p.nopol === nopol)

  if (existingPelanggan) {
    // Existing customer - fetch details and make fields readonly
    isNewCustomer.value = false
    try {
      const response = await api.get(`/api/pazaauto/pelanggan/by-nopol/${nopol}`)
      if (response.data.success && response.data.data) {
        const pelanggan = response.data.data
        formData.value.namaPelanggan = pelanggan.namaPelanggan || ''
        formData.value.alamat = pelanggan.alamat || ''
        formData.value.merk = pelanggan.merk || ''
        formData.value.jenis = pelanggan.jenis || ''
      }
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Failed to fetch pelanggan details',
        caption: error.response?.data?.message || error.message
      })
    }
  } else {
    // New customer - clear fields and make them editable
    isNewCustomer.value = true
    formData.value.namaPelanggan = ''
    formData.value.alamat = ''
    formData.value.merk = ''
    formData.value.jenis = ''
    $q.notify({
      type: 'info',
      message: 'New customer - Please fill in customer details',
      timeout: 2000
    })
  }
}

const fetchKaryawan = async () => {
  loadingKaryawan.value = true
  try {
    const response = await api.get('/api/pazaauto/karyawan')
    if (response.data.success) {
      karyawanOptions.value = response.data.data || []
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to fetch karyawan data',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    loadingKaryawan.value = false
  }
}

const filterKaryawan = (val, update) => {
  update(() => {
    if (!val) {
      // Don't filter, let q-select handle it
      return
    }
  })
}

const isSelected = (id) => {
  return selectedMekaniks.value.some(m => m.id === id)
}

const toggleMechanic = (mechanic) => {
  const index = selectedMekaniks.value.findIndex(m => m.id === mechanic.id)
  if (index > -1) {
    // Remove
    selectedMekaniks.value.splice(index, 1)
  } else {
    // Add with default tugas
    selectedMekaniks.value.push({
      id: mechanic.id,
      namaKaryawan: mechanic.namaKaryawan,
      tugas: 'Utama'
    })
  }
}

const getMekanikTugas = (id) => {
  const mechanic = selectedMekaniks.value.find(m => m.id === id)
  return mechanic?.tugas || 'Utama'
}

const setMekanikTugas = (id, tugas) => {
  const mechanic = selectedMekaniks.value.find(m => m.id === id)
  if (mechanic) {
    mechanic.tugas = tugas
  }
}

const onRequest = (props) => {
  const { page, rowsPerPage, sortBy, descending } = props.pagination
  pagination.value.page = page
  pagination.value.rowsPerPage = rowsPerPage
  pagination.value.sortBy = sortBy
  pagination.value.descending = descending
  fetchSpk(pagination.value)
}

const openCreateDialog = async () => {
  isEditMode.value = false
  isEditable.value = true
  resetForm()
  initNoSpk()
  showDialog.value = true
  // Fetch options for inline add
  await Promise.all([fetchJasa(), fetchBarang()])
}

const openEditDialog = async (row) => {
  isEditMode.value = true

  // Fetch options first so we can map prices
  await Promise.all([fetchJasa(), fetchBarang()])

  // Fetch full details including details list
  try {
    const response = await api.get(`/api/pazaauto/spk/${row.id}`)
    if (response.data.success) {
      formData.value = response.data.data
      // Ensure details is an array
      if (!formData.value.details) {
        formData.value.details = []
      }
    } else {
      formData.value = { ...row, details: [] }
    }
  } catch (error) {
    console.error('Failed to fetch SPK details', error)
    formData.value = { ...row, details: [] }
  }

  onNopolChange(formData.value.nopol)

  // Parse mekanikList (now it's an array from backend)
  if (formData.value.mekanikList && Array.isArray(formData.value.mekanikList)) {
    selectedMekaniks.value = formData.value.mekanikList.map(m => {
      const karyawan = karyawanOptions.value.find(k => k.id === m.id)
      return {
        id: m.id,
        namaKaryawan: karyawan?.namaKaryawan || `ID: ${m.id}`,
        tugas: m.tugas
      }
    })
  } else {
    selectedMekaniks.value = []
  }

  isEditable.value = formData.value.statusSpk == 'OPEN'
  showDialog.value = true
}

const closeDialog = () => {
  showDialog.value = false
  isNewCustomer.value = false
  selectedMekaniks.value = []
  resetForm()
}

const resetForm = () => {
  formData.value = {
    id: null,
    noSpk: '',
    noAntrian: null,
    tanggalJamSpk: '',
    nopol: '',
    namaKaryawan: '',
    km: null,
    statusSpk: 'OPEN',
    diskon: null,
    keluhan: '',
    keterangan: '',
    kmSaatIni: null,
    ppn: null,
    status: '',
    csId: null,
    mekanikId: null,
    // Customer info fields
    namaPelanggan: '',
    alamat: '',
    merk: '',
    jenis: '',
    details: []
  }
}

const initNoSpk = () => {
  const offsetMs = 7 * 60 * 60 * 1000;
  const gmt7 = new Date(new Date().getTime() + offsetMs);
  const gmt7Iso = gmt7.toISOString().replace("T", " ").replace("Z", "").substring(0, 16);
  formData.value.tanggalJamSpk = gmt7Iso;
  fetchNextSpkNumber()
}

const startProcess = () => {
  formData.value.statusSpk = 'PROSES'
  saveSpk()
}

const finishProcess = () => {
  formData.value.statusSpk = 'SELESAI'
  saveSpk()
}

const saveSpk = async () => {
  saving.value = true
  try {
    // If new customer, create pelanggan first
    if (isNewCustomer.value && !isEditMode.value) {
      // Validate required customer fields
      if (!formData.value.namaPelanggan || !formData.value.merk) {
        $q.notify({
          type: 'warning',
          message: 'Please fill in required customer fields (Nama, Merk)'
        })
        saving.value = false
        return
      }

      // Create new pelanggan
      const pelangganData = {
        nopol: formData.value.nopol,
        namaPelanggan: formData.value.namaPelanggan,
        alamat: formData.value.alamat,
        merk: formData.value.merk,
        jenis: formData.value.jenis
      }

      try {
        const pelangganResponse = await api.post('/api/pazaauto/pelanggan', pelangganData)
        if (!pelangganResponse.data.success) {
          throw new Error('Failed to create pelanggan')
        }
        $q.notify({
          type: 'positive',
          message: 'New customer created successfully'
        })
        // Refresh pelanggan list
        await fetchPelanggan()
        isNewCustomer.value = false
      } catch (error) {
        $q.notify({
          type: 'negative',
          message: 'Failed to create new customer',
          caption: error.response?.data?.message || error.message
        })
        saving.value = false
        return
      }
    }

    // Convert selectedMekaniks to List (Array)
    if (selectedMekaniks.value.length > 0) {
      formData.value.mekanikList = selectedMekaniks.value.map(m => ({ id: m.id, tugas: m.tugas || 'Utama' }))
    } else {
      formData.value.mekanikList = []
    }

    // Proceed with SPK creation/update
    let response
    if (isEditMode.value) {
      response = await api.put(`/api/pazaauto/spk/${formData.value.id}`, formData.value)
    } else {
      response = await api.post('/api/pazaauto/spk', formData.value)
    }

    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: isEditMode.value ? 'SPK updated successfully' : 'SPK created successfully'
      })
      closeDialog()
      await fetchSpk()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to save SPK',
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

const deleteSpk = async () => {
  deleting.value = true
  try {
    const response = await api.delete(`/api/pazaauto/spk/${itemToDelete.value.id}`)
    if (response.data.success) {
      $q.notify({
        type: 'positive',
        message: 'SPK deleted successfully'
      })
      showDeleteDialog.value = false
      itemToDelete.value = null
      await fetchSpk()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Failed to delete SPK',
      caption: error.response?.data?.message || error.message
    })
  } finally {
    deleting.value = false
  }
}

const formatCurrency = (value) => {
  if (!value) return 'Rp 0'
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(value)
}

const getStatusColor = (status) => {
  if (!status) return 'grey'
  const statusLower = status.toLowerCase()
  if (statusLower.includes('selesai') || statusLower.includes('complete')) return 'green'
  if (statusLower.includes('proses') || statusLower.includes('progress')) return 'orange'
  if (statusLower.includes('batal') || statusLower.includes('cancel')) return 'red'
  return 'blue'
}

const constructNopolOptions = (formData) => {
  if (!isNewCustomer.value) {
    if (formData.nopol) {
      return `${formData.nopol} - ${formData.namaPelanggan}`
    }
  }
  return formData
}

// Detail SPK Methods
const fetchJasa = async () => {
  try {
    const response = await api.get('/api/pazaauto/jasa')
    if (response.data.success) {
      allJasaOptions.value = response.data.data || []
      jasaOptions.value = allJasaOptions.value
    }
  } catch (error) {
    console.error('Failed to fetch jasa', error)
  }
}

const fetchBarang = async () => {
  try {
    const response = await api.get('/api/pazaauto/barang')
    if (response.data.success) {
      allBarangOptions.value = response.data.data || []
      barangOptions.value = allBarangOptions.value
    }
  } catch (error) {
    console.error('Failed to fetch barang', error)
  }
}

const filterJasa = (val, update) => {
  update(() => {
    if (val === '') {
      jasaOptions.value = allJasaOptions.value
    } else {
      const needle = val.toLowerCase()
      jasaOptions.value = allJasaOptions.value.filter(v => v.namaJasa.toLowerCase().indexOf(needle) > -1)
    }
  })
}

const filterBarang = (val, update) => {
  update(() => {
    if (val === '') {
      barangOptions.value = allBarangOptions.value
    } else {
      const needle = val.toLowerCase()
      barangOptions.value = allBarangOptions.value.filter(v => v.namaBarang.toLowerCase().indexOf(needle) > -1)
    }
  })
}

const addJasa = () => {
  if (!newJasa.value.item) return
  const item = {
    id: { noSpk: formData.value.noSpk, namaJasa: newJasa.value.item.namaJasa },
    namaItem: newJasa.value.item.namaJasa,
    jasaId: newJasa.value.item.id,
    jumlah: newJasa.value.jumlah,
    keterangan: '',
    tempId: Date.now()
  }
  formData.value.details.push(item)
  newJasa.value = { item: null, jumlah: 1 }
}

const addBarang = () => {
  if (!newBarang.value.item) return
  const item = {
    id: { noSpk: formData.value.noSpk, namaJasa: newBarang.value.item.namaBarang },
    namaItem: newBarang.value.item.namaBarang,
    sparepartId: newBarang.value.item.id,
    jumlah: newBarang.value.jumlah,
    keterangan: '',
    tempId: Date.now()
  }
  formData.value.details.push(item)
  newBarang.value = { item: null, jumlah: 1 }
}

const removeDetail = (row) => {
  // Find index by tempId or ID
  const index = formData.value.details.findIndex(d =>
    (d.tempId && d.tempId === row.tempId) ||
    (d.id && row.id && d.id.namaJasa === row.id.namaJasa && d.id.noSpk === row.id.noSpk)
  )
  if (index > -1) {
    formData.value.details.splice(index, 1)
  }
}

// Watchers
let searchTimeout = null
watch(searchText, (newVal) => {
  console.log('searchText changed to:', newVal)
  // Debounce search to avoid too many API calls
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  searchTimeout = setTimeout(() => {
    // Reset to page 1 when searching
    pagination.value.page = 1
    fetchSpk()
  }, 500)
})

// Watch filter status changes
watch(filterStatus, (newVal) => {
  console.log('filterStatus changed to:', newVal)
  // Save to localStorage
  saveFilterToStorage(newVal)
  // Reset to page 1 when filtering
  pagination.value.page = 1
  fetchSpk()
})

// Watch today filter changes
watch(filterToday, (newVal) => {
  console.log('filterToday changed to:', newVal)
  // Reset to page 1 when filtering
  pagination.value.page = 1
  fetchSpk()
})

// Lifecycle
onMounted(() => {
  fetchSpk()
  fetchPelanggan()
  fetchKaryawan()
})
</script>

<style lang="sass" scoped>
.dialog-spk
  min-width: calc(80vw - 48px);
.my-sticky-header-table
  max-height: 70vh

  thead tr th
    position: sticky
    z-index: 1
    background-color: #ffffff

  thead tr:first-child th
    top: 0
</style>
