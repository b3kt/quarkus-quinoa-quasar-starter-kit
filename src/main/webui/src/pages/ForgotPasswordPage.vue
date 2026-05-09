<template>
  <q-page class="flex flex-center">
    <q-card class="auth-card">
      <q-card-section>
        <div class="text-h6 text-center q-mb-md">Reset Password</div>
        <p class="text-caption text-center text-grey-6 q-mb-md">
          Enter your username to receive a password reset token.
        </p>

        <q-form @submit="onSubmit" class="q-gutter-md">
          <q-input
            v-model="username"
            label="Username"
            :rules="[val => !!val || 'Username is required']"
            outlined dense
          >
            <template v-slot:prepend><q-icon name="person" /></template>
          </q-input>

          <q-banner v-if="message" class="bg-positive text-white" dense>
            {{ message }}
          </q-banner>

          <q-banner v-if="resetToken" class="bg-info text-white" dense>
            <div class="text-center">
              <div class="text-weight-bold">Reset Token (dev mode):</div>
              <code class="text-body2" style="word-break: break-all">{{ resetToken }}</code>
            </div>
          </q-banner>

          <q-banner v-if="error" class="bg-negative text-white" dense>
            {{ error }}
          </q-banner>

          <div>
            <q-btn
              label="Send Reset Token"
              type="submit"
              color="primary"
              class="full-width"
              :loading="loading"
              :disable="!!resetToken"
            />
          </div>
        </q-form>

        <div class="q-mt-md text-center">
          <router-link to="/login" class="text-primary text-caption">
            Back to Login
          </router-link>
        </div>

        <div class="q-mt-sm text-center">
          <router-link to="/reset-password" class="text-primary text-caption">
            Already have a token? Reset password
          </router-link>
        </div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()

const username = ref('')
const loading = ref(false)
const error = ref('')
const message = ref('')
const resetToken = ref('')

const onSubmit = async () => {
  error.value = ''
  message.value = ''
  resetToken.value = ''
  loading.value = true

  try {
    const response = await api.post('/api/auth/forgot-password', {
      username: username.value
    })
    const data = response.data
    message.value = data.message || 'If the account exists, a reset token has been generated'
    if (data.data?.token) {
      resetToken.value = data.data.token
      $q.notify({
        type: 'positive',
        message: 'Reset token generated',
        position: 'top'
      })
    } else {
      $q.notify({
        type: 'info',
        message: 'If the account exists, a reset link has been sent',
        position: 'top'
      })
    }
  } catch (err) {
    error.value = err.response?.data?.error || 'Failed to request password reset'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-card {
  min-width: 380px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
