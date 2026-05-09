<template>
  <q-page class="flex flex-center">
    <q-card class="auth-card">
      <q-card-section>
        <div class="text-h6 text-center q-mb-md">Set New Password</div>
        <p class="text-caption text-center text-grey-6 q-mb-md">
          Enter the reset token from the forgot-password step and your new password.
        </p>

        <q-form @submit="onSubmit" class="q-gutter-md">
          <q-input
            v-model="token"
            label="Reset Token"
            :rules="[val => !!val || 'Reset token is required']"
            outlined dense
          >
            <template v-slot:prepend><q-icon name="vpn_key" /></template>
          </q-input>

          <q-input
            v-model="newPassword"
            label="New Password"
            type="password"
            :rules="[
              val => !!val || 'New password is required',
              val => val.length >= 8 || 'At least 8 characters'
            ]"
            outlined dense
          >
            <template v-slot:prepend><q-icon name="lock" /></template>
          </q-input>

          <q-input
            v-model="confirmPassword"
            label="Confirm New Password"
            type="password"
            :rules="[
              val => !!val || 'Please confirm your password',
              val => val === newPassword || 'Passwords do not match'
            ]"
            outlined dense
          >
            <template v-slot:prepend><q-icon name="lock_outline" /></template>
          </q-input>

          <q-banner v-if="error" class="bg-negative text-white" dense>
            {{ error }}
          </q-banner>

          <q-banner v-if="success" class="bg-positive text-white" dense>
            {{ success }}
          </q-banner>

          <div>
            <q-btn
              label="Reset Password"
              type="submit"
              color="primary"
              class="full-width"
              :loading="loading"
              :disable="!!success"
            />
          </div>
        </q-form>

        <div class="q-mt-md text-center">
          <router-link to="/login" class="text-primary text-caption">
            Back to Login
          </router-link>
        </div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const router = useRouter()
const $q = useQuasar()

const token = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')
const success = ref('')

const onSubmit = async () => {
  error.value = ''
  loading.value = true

  try {
    const response = await api.post('/api/auth/reset-password', {
      token: token.value,
      newPassword: newPassword.value
    })
    success.value = response.data.message || 'Password reset successfully!'
    $q.notify({
      type: 'positive',
      message: 'Password reset successfully!',
      position: 'top'
    })
    setTimeout(() => router.push('/login'), 2000)
  } catch (err) {
    error.value = err.response?.data?.error || 'Failed to reset password'
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
