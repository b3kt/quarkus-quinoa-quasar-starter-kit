<template>
    <q-page padding>
        <GenericTable :rows="rows" :columns="columns" :loading="loading" :pagination="pagination"
            @update:pagination="pagination = $event" @request="onRequest" @search="onSearch"
            :on-create="openCreateDialog" :on-edit="openEditDialog" :on-delete="confirmDelete"
            create-label="Create System Parameter" search-placeholder="Search by name or value..." />

        <!-- Create/Edit Dialog -->
        <GenericDialog v-model="showDialog" :title="isEditMode ? 'Edit System Parameter' : 'Create System Parameter'"
            min-width="500px">
            <q-form @submit="handleSave" id="system-parameter-form" class="q-gutter-md">
                <q-input v-model="formData.name" label="Parameter Name *" outlined dense
                    :rules="[val => !!val || 'Name is required']" :readonly="isEditMode" />

                <q-input v-model="formData.value" label="Value *" outlined dense
                    :rules="[val => !!val || 'Value is required']" />
            </q-form>
            <template #actions>
                <q-btn flat label="Cancel" color="primary" @click="showDialog = false" />
                <q-btn label="Save" type="submit" form="system-parameter-form" color="primary" :loading="saving" />
            </template>
        </GenericDialog>

        <!-- Delete Confirmation Dialog -->
        <GenericDialog v-model="showDeleteDialog" title="Confirm Delete" min-width="400px">
            Are you sure you want to delete parameter <strong>{{ itemToDelete?.name }}</strong>?
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
    baseApiUrl: '/api/system-parameters',
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
    name: '',
    value: ''
})

const resetForm = () => {
    formData.value = {
        id: null,
        name: '',
        value: ''
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
        name: 'name',
        required: true,
        label: 'Parameter Name',
        align: 'left',
        field: 'name',
        sortable: true
    },
    {
        name: 'value',
        required: true,
        label: 'Value',
        align: 'left',
        field: 'value'
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
