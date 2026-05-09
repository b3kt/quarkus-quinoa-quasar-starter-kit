<template>
  <q-page class="flex flex-center">
    <q-card class="auth-card">
      <q-card-section>
        <div class="text-h6 text-center q-mb-md">Login</div>
        <q-form @submit="onSubmit" class="q-gutter-md">
          <q-input
            v-model="username"
            label="Username"
            :rules="[val => !!val || 'Username is required']"
            outlined dense
          >
            <template v-slot:prepend>
              <q-icon name="person" />
            </template>
          </q-input>

          <q-input
            v-model="password"
            label="Password"
            type="password"
            :rules="[val => !!val || 'Password is required']"
            outlined dense
          >
            <template v-slot:prepend>
              <q-icon name="lock" />
            </template>
          </q-input>

          <q-banner
            v-if="error"
            class="bg-negative text-white q-mt-md"
            dense
          >
            {{ error }}
          </q-banner>

          <div>
            <q-btn
              label="Login"
              type="submit"
              color="primary"
              class="full-width"
              :loading="loading"
            />
          </div>
        </q-form>

        <div class="q-mt-md text-center">
          <router-link to="/register" class="text-primary text-caption">
            Create an account
          </router-link>
        </div>

        <div class="q-mt-sm text-center">
          <router-link to="/forgot-password" class="text-primary text-caption">
            Forgot password?
          </router-link>
        </div>

        <div class="q-mt-md text-caption text-center text-grey-6">
          Demo credentials: admin / admin123
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
const password = ref('')
const loading = ref(false)
const error = ref('')

const onSubmit = async () => {
  error.value = ''
  loading.value = true

  try {
    const result = await authStore.login(username.value, password.value)
    if (result?.success) {
      $q.notify({
        type: 'positive',
        message: 'Login successful!',
        position: 'top'
      })
      router.push('/')
    } else {
      error.value = result?.error || 'Login failed'
    }
  } catch {
    error.value = 'An error occurred during login'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-card {
  min-width: 350px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
