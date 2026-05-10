<template>
  <q-page class="q-pa-md">
    <div class="text-h5 q-mb-md">Audit Trail</div>

    <div class="row q-mb-md q-gutter-sm">
      <q-btn-toggle
        v-model="actionFilter"
        toggle-color="primary"
        :options="[
          { label: 'All', value: '' },
          { label: 'CREATE', value: 'CREATE' },
          { label: 'UPDATE', value: 'UPDATE' },
          { label: 'DELETE', value: 'DELETE' }
        ]"
        dense
        outlined
      />
      <q-space />
      <q-btn
        flat dense round icon="refresh"
        @click="loadLogs"
        :loading="loading"
      />
    </div>

    <q-table
      :rows="filteredLogs"
      :columns="columns"
      row-key="id"
      :loading="loading"
      :filter="filter"
      flat bordered
      @request="loadLogs"
    >
      <template v-slot:top-right>
        <q-input
          v-model="filter"
          dense
          debounce="300"
          placeholder="Search entity..."
          outlined
        >
          <template v-slot:append><q-icon name="search" /></template>
        </q-input>
      </template>

      <template v-slot:body-cell-action="props">
        <q-td :props="props">
          <q-badge
            :color="actionColor(props.value)"
            class="q-px-sm q-py-xs"
          >
            {{ props.value }}
          </q-badge>
        </q-td>
      </template>

      <template v-slot:body-cell-changedAt="props">
        <q-td :props="props">
          <span class="text-caption">{{ formatDate(props.value) }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-diff="props">
        <q-td :props="props">
          <q-btn
            flat dense round
            icon="visibility"
            color="primary"
            size="sm"
            @click="showDiff(props.row)"
          >
            <q-tooltip>View before/after</q-tooltip>
          </q-btn>
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="diffDialog" maximized>
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">
            <q-badge :color="actionColor(diffRow?.action)" class="q-mr-sm">
              {{ diffRow?.action }}
            </q-badge>
            {{ diffRow?.entityName }} #{{ diffRow?.entityId }}
          </div>
          <q-space />
          <q-btn flat round dense icon="close" v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-md">
          <div class="text-caption text-grey q-mb-sm">
            Changed by: {{ diffRow?.changedBy || 'system' }} &middot;
            {{ formatDate(diffRow?.changedAt) }}
          </div>

          <div class="row q-col-gutter-md">
            <div class="col-6">
              <div class="text-subtitle2 bg-grey-2 q-pa-sm q-mb-sm rounded-borders">
                Before
              </div>
              <div class="diff-json">
                <pre v-if="diffRow?.beforeValues">{{ formatJson(diffRow.beforeValues) }}</pre>
                <div v-else class="text-grey-5 text-center q-py-xl">
                  <q-icon name="remove_circle_outline" size="48px" />
                  <div class="q-mt-sm">No data</div>
                </div>
              </div>
            </div>
            <div class="col-6">
              <div class="text-subtitle2 bg-grey-2 q-pa-sm q-mb-sm rounded-borders">
                After
              </div>
              <div class="diff-json">
                <pre v-if="diffRow?.afterValues">{{ formatJson(diffRow.afterValues) }}</pre>
                <div v-else class="text-grey-5 text-center q-py-xl">
                  <q-icon name="remove_circle_outline" size="48px" />
                  <div class="q-mt-sm">No data</div>
                </div>
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()

const columns = [
  { name: 'entityName', label: 'Entity', field: 'entityName', align: 'left', sortable: true },
  { name: 'entityId', label: 'ID', field: 'entityId', align: 'left', sortable: true },
  { name: 'action', label: 'Action', field: 'action', align: 'center', sortable: true },
  { name: 'changedBy', label: 'Changed By', field: 'changedBy', align: 'left' },
  { name: 'changedAt', label: 'Date', field: 'changedAt', align: 'center', sortable: true },
  { name: 'diff', label: '', field: 'diff', align: 'center' }
]

const logs = ref([])
const loading = ref(false)
const filter = ref('')
const actionFilter = ref('')
const diffDialog = ref(false)
const diffRow = ref(null)

const filteredLogs = computed(() => {
  let result = logs.value
  if (actionFilter.value) {
    result = result.filter(log => log.action === actionFilter.value)
  }
  return result
})

function actionColor(action) {
  switch (action) {
    case 'CREATE': return 'positive'
    case 'UPDATE': return 'orange'
    case 'DELETE': return 'negative'
    default: return 'grey'
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString()
}

function formatJson(data) {
  if (typeof data === 'string') {
    try { return JSON.stringify(JSON.parse(data), null, 2) }
    catch { return data }
  }
  return JSON.stringify(data, null, 2)
}

function showDiff(row) {
  diffRow.value = row
  diffDialog.value = true
}

async function loadLogs() {
  loading.value = true
  try {
    const response = await api.get('/api/admin/audit-logs')
    logs.value = response.data.data || []
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.error || 'Failed to load audit logs',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

onMounted(loadLogs)
</script>

<style scoped>
.diff-json {
  background: #f5f5f5;
  border-radius: 4px;
  max-height: 70vh;
  overflow: auto;
}
.diff-json pre {
  margin: 0;
  padding: 12px;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
