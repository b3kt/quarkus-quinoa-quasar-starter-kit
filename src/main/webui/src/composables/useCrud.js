import { ref } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

export function useCrud(config) {
    const {
        baseApiUrl,
        idField = 'id',
        defaultPagination = {
            sortBy: null,
            descending: false,
            page: 1,
            rowsPerPage: 10,
            rowsNumber: 0
        },
        transformPayload = (data) => data,
        onSuccess = () => { },
        onError = () => { }
    } = config

    const $q = useQuasar()

    // State
    const rows = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const deleting = ref(false)
    const showDialog = ref(false)
    const showDeleteDialog = ref(false)
    const isEditMode = ref(false)
    const itemToDelete = ref(null)
    const searchText = ref('')

    const pagination = ref({ ...defaultPagination })

    // Methods
    const fetchData = async (customParams = {}) => {
        loading.value = true
        try {
            const params = {
                page: pagination.value.page,
                rowsPerPage: pagination.value.rowsPerPage,
                ...customParams
            }

            if (pagination.value.sortBy) {
                params.sortBy = pagination.value.sortBy
                params.descending = pagination.value.descending
            }

            if (searchText.value) {
                params.search = searchText.value
            }

            const response = await api.get(`${baseApiUrl}/paginated`, { params })
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
                message: 'Failed to fetch data',
                caption: error.response?.data?.message || error.message
            })
            onError(error)
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
        fetchData()
    }

    const onSearch = (val) => {
        searchText.value = val
        pagination.value.page = 1
        fetchData()
    }

    const saveData = async (formData) => {
        saving.value = true
        try {
            const payload = transformPayload(formData)
            let response

            if (isEditMode.value) {
                response = await api.put(`${baseApiUrl}/${formData[idField]}`, payload)
            } else {
                response = await api.post(baseApiUrl, payload)
            }

            if (response.data.success) {
                $q.notify({
                    type: 'positive',
                    message: isEditMode.value ? 'Item updated successfully' : 'Item created successfully'
                })
                showDialog.value = false
                await fetchData()
                onSuccess('save', response.data.data)
                return true
            }
        } catch (error) {
            $q.notify({
                type: 'negative',
                message: 'Failed to save item',
                caption: error.response?.data?.message || error.message
            })
            onError(error)
            return false
        } finally {
            saving.value = false
        }
    }

    const confirmDelete = (row) => {
        itemToDelete.value = row
        showDeleteDialog.value = true
    }

    const deleteItem = async () => {
        if (!itemToDelete.value) return

        deleting.value = true
        try {
            const response = await api.delete(`${baseApiUrl}/${itemToDelete.value[idField]}`)
            if (response.data.success) {
                $q.notify({
                    type: 'positive',
                    message: 'Item deleted successfully'
                })
                showDeleteDialog.value = false
                itemToDelete.value = null
                await fetchData()
                onSuccess('delete')
            }
        } catch (error) {
            $q.notify({
                type: 'negative',
                message: 'Failed to delete item',
                caption: error.response?.data?.message || error.message
            })
            onError(error)
        } finally {
            deleting.value = false
        }
    }

    const openCreateDialog = (resetFormCallback) => {
        isEditMode.value = false
        if (resetFormCallback) resetFormCallback()
        showDialog.value = true
    }

    const openEditDialog = (row, setFormCallback) => {
        isEditMode.value = true
        if (setFormCallback) setFormCallback(row)
        showDialog.value = true
    }

    return {
        rows,
        loading,
        saving,
        deleting,
        showDialog,
        showDeleteDialog,
        isEditMode,
        itemToDelete,
        searchText,
        pagination,
        fetchData,
        onRequest,
        onSearch,
        saveData,
        confirmDelete,
        deleteItem,
        openCreateDialog,
        openEditDialog
    }
}
