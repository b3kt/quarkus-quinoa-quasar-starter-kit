<template>
    <div class="q-pa-sm">
        <!-- Toolbar -->
        <q-toolbar class="shadow-1 rounded-borders q-mb-lg ">

            <div class="col-8 col-md-6 col-sm-7 col-xs-12" v-if="enableSearch">
                <q-input dense standout="bg-primary" v-model="internalSearch" input-class="search-field text-left"
                    :placeholder="!searchPlaceholder ? searchPlaceholder : $t('search')">
                    <template v-slot:append>
                        <q-icon v-if="internalSearch === ''" name="search" />
                        <q-icon v-else name="clear" class="cursor-pointer" @click="internalSearch = ''" />
                    </template>
                </q-input>
            </div>

            <div class="col-4 col-md-2 col-sm-2 gt-xs q-pl-sm">
                <slot name="toolbar-filters"></slot>
            </div>

            <q-space class="gt-md" />
            <div class="gt-xs col-sm-3 text-right">
                <slot name="toolbar-actions">
                    <q-btn v-if="onCreate" flat :label="$t('createLabel')" icon="add" color="white" class="bg-primary"
                        @click="onCreate">
                        <q-tooltip>
                            {{ createLabel }}
                        </q-tooltip>
                    </q-btn>
                </slot>
            </div>

        </q-toolbar>

        <!-- Table -->
        <q-table class="my-sticky-header-table" :class="{ 'cursor-pointer-rows': !!onEdit }" flat bordered :rows="rows"
            :columns="columns" :row-key="rowKey" :loading="loading" v-model:pagination="internalPagination"
            @request="onRequest" @row-click="onRowClick" binary-state-sort>
            <!-- Pass through all slots -->
            <template v-for="(_, slot) in $slots" v-slot:[slot]="scope">
                <slot :name="slot" v-bind="scope" />
            </template>

            <!-- Default Actions Slot if not provided but actions exist -->
            <template v-if="!$slots['body-cell-actions'] && hasActions" v-slot:body-cell-actions="props">
                <q-td :props="props" class="text-right">
                    <q-btn v-if="onDelete" flat dense round icon="delete" color="negative"
                        @click.stop="onDelete(props.row)">
                        <q-tooltip>Delete</q-tooltip>
                    </q-btn>
                </q-td>
            </template>
        </q-table>
    </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

const props = defineProps({
    rows: {
        type: Array,
        required: true
    },
    columns: {
        type: Array,
        required: true
    },
    loading: {
        type: Boolean,
        default: false
    },
    pagination: {
        type: Object,
        required: true
    },
    rowKey: {
        type: String,
        default: 'id'
    },
    createLabel: {
        type: String,
        default: 'Create'
    },
    searchPlaceholder: {
        type: String,
        default: 'Search...'
    },
    enableSearch: {
        type: Boolean,
        default: true
    },
    onCreate: {
        type: Function,
        default: null
    },
    onEdit: {
        type: Function,
        default: null
    },
    onDelete: {
        type: Function,
        default: null
    }
})

const emit = defineEmits(['update:pagination', 'request', 'search'])

// Internal state for two-way binding
const internalPagination = computed({
    get: () => props.pagination,
    set: (val) => emit('update:pagination', val)
})

const internalSearch = ref('')
let searchTimeout = null

// Watch search text for debouncing
watch(internalSearch, (newVal) => {
    if (searchTimeout) {
        clearTimeout(searchTimeout)
    }
    searchTimeout = setTimeout(() => {
        emit('search', newVal)
    }, 500)
})

const onRequest = (requestProp) => {
    emit('request', requestProp)
}

const onRowClick = (evt, row, index) => {
    if (props.onEdit) {
        console.log(index)
        props.onEdit(row)
    }
}

const hasActions = computed(() => {
    return props.columns.some(col => col.name === 'actions')
})
</script>

<style lang="sass">
.my-sticky-header-table
  height: 75vh

  .q-table__top,
  .q-table__bottom,
  thead tr:first-child th
    background-color: #fafafa

  thead tr th
    position: sticky
    z-index: 1
  thead tr:first-child th
    top: 0

  &.q-table--loading thead tr:last-child th
    top: 48px

  tbody
    scroll-margin-top: 48px

.cursor-pointer-rows
  tbody tr
    cursor: pointer
</style>