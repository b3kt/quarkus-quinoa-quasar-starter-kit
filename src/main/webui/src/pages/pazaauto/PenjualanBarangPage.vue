<template>
    <q-page padding>
        <div class="q-pa-md">
            <!-- Toolbar with Create button and Search -->
            <q-toolbar class="shadow-1 rounded-borders q-mb-lg">
                <!-- <q-btn flat :label="$t('create') + ' Penjualan'" icon="add" color="white" class="bg-primary"
                    @click="openCreateDialog" /> -->
                <q-space />
                <div class="col-auto q-mr-md">
                    <q-checkbox v-model="filterToday" label="Filter Penjualan hari ini" dense />
                </div>
                <div class="col-2">
                    <q-select v-model="filterStatus" multiple :options="statusOptions" label="Status Pembayaran" dense
                        options-dense flat outlined />
                </div>
                <div class="col-6">
                    <q-input dense standout="bg-primary" v-model="searchText" input-class="search-field text-left"
                        class="q-ml-md" placeholder="Search by No Penjualan or SPK...">
                        <template v-slot:append>
                            <q-icon v-if="searchText === ''" name="search" />
                            <q-icon v-else name="clear" class="cursor-pointer" @click="searchText = ''" />
                        </template>
                    </q-input>
                </div>
            </q-toolbar>

            <!-- Data Table -->
            <q-table class="my-sticky-header-table" flat bordered :rows="rows" :columns="columns" row-key="noPenjualan"
                :loading="loading" v-model:pagination="pagination" @request="onRequest" binary-state-sort>

                <template v-slot:body-cell-grandTotal="props">
                    <q-td :props="props">
                        {{ formatCurrency(props.row.grandTotal) }}
                    </q-td>
                </template>

                <template v-slot:body-cell-statusPembayaran="props">
                    <q-td :props="props">
                        <q-badge :color="getStatusColor(props.row.statusPembayaran)">
                            {{ props.row.statusPembayaran }}
                        </q-badge>
                    </q-td>
                </template>

                <template v-slot:body-cell-actions="props">
                    <q-td :props="props">
                        <q-btn flat dense round icon="edit" color="primary" @click="openEditDialog(props.row)">
                            <q-tooltip>Edit</q-tooltip>
                        </q-btn>
                        <!-- <q-btn flat dense round icon="delete" color="negative" @click="confirmDelete(props.row)">
                            <q-tooltip>Delete</q-tooltip>
                        </q-btn> -->
                    </q-td>
                </template>
            </q-table>
        </div>

        <!-- Create/Edit Dialog -->
        <q-dialog v-model="showDialog" persistent>
            <q-card style="min-width: 500px">
                <q-card-section>
                    <div class="text-h6">{{ isEditMode ? 'Edit Penjualan' : 'Create Penjualan' }}</div>
                </q-card-section>

                <q-card-section class="q-pt-none">
                    <q-form @submit="savePenjualan" class="q-gutter-md">
                        <q-input v-model="formData.noPenjualan" label="No Penjualan *" outlined dense
                            :rules="[val => !!val || 'No Penjualan is required']" :disable="isEditMode" />

                        <q-input v-model="formData.tanggalJamPenjualan" label="Tanggal Penjualan" outlined dense
                            type="datetime-local" stack-label />

                        <q-input v-model="formData.noSpk" label="No SPK" outlined dense />

                        <q-input v-model.number="formData.grandTotal" label="Grand Total" outlined dense type="number"
                            prefix="Rp" />

                        <q-select v-model="formData.statusPembayaran" label="Status Pembayaran" outlined dense
                            :options="statusOptions" />

                        <q-select v-model="formData.metodePembayaran" label="Metode Pembayaran" outlined dense
                            :options="['CASH', 'CREDIT', 'TRANSFER', 'QRIS']" />

                        <q-input v-model="formData.keterangan" label="Keterangan" outlined dense type="textarea"
                            rows="3" />

                        <div class="row justify-end q-gutter-sm">
                            <q-btn flat label="Cancel" color="primary" @click="closeDialog" />
                            <q-btn label="Save" type="submit" color="primary" :loading="saving" />
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
                    Are you sure you want to delete Penjualan <strong>{{ itemToDelete?.noPenjualan }}</strong>?
                </q-card-section>

                <q-card-actions align="right">
                    <q-btn flat label="Cancel" color="primary" @click="showDeleteDialog = false" />
                    <q-btn flat label="Delete" color="negative" @click="deletePenjualan" :loading="deleting" />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()

// LocalStorage key for filter persistence
const FILTER_STORAGE_KEY = 'penjualan_status_filter'

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
const searchText = ref('')
const filterStatus = ref(loadFilterFromStorage())
const filterToday = ref(false)
const rows = ref([])
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const isEditMode = ref(false)
const itemToDelete = ref(null)

// Status options
const statusOptions = ref([
    'LUNAS',
    'BELUM_LUNAS',
    'DP'
])

const pagination = ref({
    sortBy: 'tanggalJamPenjualan',
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
})

// Form data
const formData = ref({
    noPenjualan: '',
    tanggalJamPenjualan: '',
    noSpk: '',
    grandTotal: 0,
    statusPembayaran: 'BELUM_LUNAS',
    metodePembayaran: 'CASH',
    keterangan: ''
})

// Table columns
const columns = [
    {
        name: 'noPenjualan',
        required: true,
        label: 'No Penjualan',
        align: 'left',
        field: 'noPenjualan',
        sortable: true
    },
    {
        name: 'tanggalJamPenjualan',
        label: 'Tanggal',
        align: 'left',
        field: 'tanggalJamPenjualan',
        sortable: true
    },
    {
        name: 'noSpk',
        label: 'No SPK',
        align: 'left',
        field: 'noSpk',
        sortable: true
    },
    {
        name: 'grandTotal',
        label: 'Grand Total',
        align: 'right',
        field: 'grandTotal',
        sortable: true
    },
    {
        name: 'statusPembayaran',
        label: 'Status',
        align: 'center',
        field: 'statusPembayaran',
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
const fetchPenjualan = async (paginationData = pagination.value) => {
    loading.value = true
    try {
        const params = {
            page: paginationData.page,
            rowsPerPage: paginationData.rowsPerPage
        }

        if (paginationData.sortBy) {
            params.sortBy = paginationData.sortBy
            params.descending = paginationData.descending
        }

        if (searchText.value) {
            params.search = searchText.value
        }

        if (filterStatus.value && filterStatus.value.length > 0) {
            params.statusFilter = filterStatus.value.join(',')
        }

        // Add today filter if checked
        if (filterToday.value) {
            params.filterToday = true
        }

        const response = await api.get('/api/pazaauto/penjualan/paginated', { params })
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
            message: 'Failed to fetch penjualan data',
            caption: error.response?.data?.message || error.message
        })
    } finally {
        loading.value = false
    }
}

const onRequest = (props) => {
    const { page, rowsPerPage, sortBy, descending } = props.pagination
    pagination.value.page = page
    pagination.value.rowsPerPage = rowsPerPage
    pagination.value.sortBy = sortBy
    pagination.value.descending = descending
    fetchPenjualan(pagination.value)
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
        noPenjualan: '',
        tanggalJamPenjualan: new Date().toISOString().slice(0, 16), // Format YYYY-MM-DDTHH:mm
        noSpk: '',
        grandTotal: 0,
        statusPembayaran: 'BELUM_LUNAS',
        metodePembayaran: 'CASH',
        keterangan: ''
    }
}

const savePenjualan = async () => {
    saving.value = true
    try {
        let response
        if (isEditMode.value) {
            response = await api.put(`/api/pazaauto/penjualan/${formData.value.noPenjualan}`, formData.value)
        } else {
            response = await api.post('/api/pazaauto/penjualan', formData.value)
        }

        if (response.data.success) {
            $q.notify({
                type: 'positive',
                message: isEditMode.value ? 'Penjualan updated successfully' : 'Penjualan created successfully'
            })
            closeDialog()
            await fetchPenjualan()
        }
    } catch (error) {
        $q.notify({
            type: 'negative',
            message: 'Failed to save penjualan',
            caption: error.response?.data?.message || error.message
        })
    } finally {
        saving.value = false
    }
}

const deletePenjualan = async () => {
    deleting.value = true
    try {
        const response = await api.delete(`/api/pazaauto/penjualan/${itemToDelete.value.noPenjualan}`)
        if (response.data.success) {
            $q.notify({
                type: 'positive',
                message: 'Penjualan deleted successfully'
            })
            showDeleteDialog.value = false
            itemToDelete.value = null
            await fetchPenjualan()
        }
    } catch (error) {
        $q.notify({
            type: 'negative',
            message: 'Failed to delete penjualan',
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
    switch (status) {
        case 'LUNAS': return 'green'
        case 'BELUM_LUNAS': return 'red'
        case 'DP': return 'orange'
        default: return 'grey'
    }
}

// Watchers
let searchTimeout = null
watch(searchText, () => {
    if (searchTimeout) {
        clearTimeout(searchTimeout)
    }
    searchTimeout = setTimeout(() => {
        pagination.value.page = 1
        fetchPenjualan()
    }, 500)
})

watch(filterStatus, (newVal) => {
    saveFilterToStorage(newVal)
    pagination.value.page = 1
    fetchPenjualan()
}, { deep: true })

watch(filterToday, () => {
    pagination.value.page = 1
    fetchPenjualan()
})

// Lifecycle
onMounted(() => {
    fetchPenjualan()
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
