<template>
    <q-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" persistent
        :maximized="$q.screen.lt.sm" :transition-show="$q.screen.lt.sm ? 'slide-up' : 'scale'"
        :transition-hide="$q.screen.lt.sm ? 'slide-down' : 'scale'">
        <q-card :style="$q.screen.lt.sm ? '' : `min-width: ${minWidth}; max-width: ${maxWidth}`"
            :class="{ 'column full-height': $q.screen.lt.sm }">
            <slot name="header">
                <q-card-section class="row items-center q-pb-none">
                    <slot name="title">
                        <div class="text-h6">{{ title }}</div>
                    </slot>
                    <q-space />
                    <q-btn icon="close" flat round dense v-close-popup />
                </q-card-section>
            </slot>

            <q-card-section :class="[contentClass, { 'col scroll': $q.screen.lt.sm }]">
                <slot></slot>
            </q-card-section>

            <q-card-actions align="right" v-if="$slots.actions" class="bg-grey-1">
                <slot name="actions"></slot>
            </q-card-actions>
        </q-card>
    </q-dialog>
</template>

<script setup>
defineProps({
    modelValue: {
        type: Boolean,
        required: true
    },
    title: {
        type: String,
        default: ''
    },
    minWidth: {
        type: String,
        default: '500px'
    },
    maxWidth: {
        type: String,
        default: '80vw'
    },
    contentClass: {
        type: String,
        default: ''
    }
})

defineEmits(['update:modelValue'])
</script>
