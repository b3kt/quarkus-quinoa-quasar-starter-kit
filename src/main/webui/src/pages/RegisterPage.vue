<template>
  <q-page class="flex flex-center">
    <q-card class="auth-card">
      <q-card-section>
        <div class="text-h6 text-center q-mb-md">Create Account</div>
        <q-form @submit="onSubmit" class="q-gutter-md">
          <q-input
            v-model="username"
            label="Username"
            :rules="[
              val => !!val || 'Username is required',
              val => val.length >= 3 || 'At least 3 characters'
            ]"
            outlined dense
          >
            <template v-slot:prepend><q-icon name="person" /></template>
          </q-input>

          <q-input
            v-model="email"
            label="Email"
            type="email"
            :rules="[
              val => !!val || 'Email is required',
              val => /.+@.+/.test(val) || 'Invalid email'
            ]"
            outlined dense
          >
            <template v-slot:prepend><q-icon name="email" /></template>
          </q-input>

          <q-input
            v-model="password"
            label="Password"
            type="password"
            :rules="[
              val => !!val || 'Password is required',
              val => val.length >= 8 || 'At least 8 characters'
            ]"
            outlined dense
          >
            <template v-slot:prepend><q-icon name="lock" /></template>
          </q-input>

          <q-input
            v-model="confirmPassword"
            label="Confirm Password"
            type="password"
            :rules="[
              val => !!val || 'Please confirm your password',
              val => val === password || 'Passwords do not match'
            ]"
            outlined dense
          >
            <template v-slot:prepend><q-icon name="lock_outline" /></template>
          </q-input>

          <q-banner v-if="error" class="bg-negative text-white q-mt-md" dense>
            {{ error }}
          </q-banner>

          <q-banner v-if="success" class="bg-positive text-white q-mt-md" dense>
            {{ success }}
          </q-banner>

          <div>
            <q-btn
              label="Register"
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
            Already have an account? Login
          </router-link>
        </div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from 'stores/auth-store'
import { useQuasar } from 'quasar'

const router = useRouter()
const authStore = useAuthStore()
const $q = useQuasar()

const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')
const success = ref('')

const onSubmit = async () => {
  error.value = ''
  loading.value = true

  try {
    const result = await authStore.register(
      username.value,
      email.value,
      password.value
    )
    if (result.success) {
      success.value = 'Account created successfully! Redirecting to login...'
      $q.notify({
        type: 'positive',
        message: 'Registration successful!',
        position: 'top'
      })
      setTimeout(() => router.push('/login'), 2000)
    } else {
      error.value = result.error || 'Registration failed'
    }
  } catch {
    error.value = 'An error occurred during registration'
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
