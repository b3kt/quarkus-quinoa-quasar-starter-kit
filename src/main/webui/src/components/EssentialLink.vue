<template>
  <q-expansion-item expand-separator :icon="props.icon" :label="props.title" :caption="props.caption" :default-opened="false">
    <q-item>
      <q-item-section>
        <q-list>
          <q-item v-for="link in props.children" :key="link.title" v-bind="link"
            :to="link.link">
            <q-item-section>
              <q-item-label>{{ link.title }}</q-item-label>
              <q-item-label caption>{{ link.caption }}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </q-item-section>
    </q-item>
  </q-expansion-item>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    required: true
  },

  caption: {
    type: String,
    default: ''
  },

  link: {
    type: String,
    default: '#'
  },
  icon: {
    type: String,
    default: ''
  },
  children: {
    type: Array,
    default: new Array()
  }
})
</script>
