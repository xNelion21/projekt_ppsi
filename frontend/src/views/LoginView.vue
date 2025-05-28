<template>
  <div class="login-container">
    <div class="login-box">
      <h2>Logowanie</h2>
      <form @submit.prevent="submitLogin">
        <div class="mb-3">
          <label for="email" class="form-label">Email:</label>
          <input type="email" id="email" v-model="email" class="form-control" required>
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">Hasło:</label>
          <input type="password" id="password" v-model="password" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="userStore.loading">
          {{ userStore.loading ? 'Logowanie...' : 'Zaloguj' }}
        </button>
        <p v-if="userStore.error" class="text-danger mt-3">{{ userStore.error }}</p>
      </form>
      <p class="mt-3">Nie masz konta? <router-link to="/register">Zarejestruj się</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';

const email = ref('');
const password = ref('');
const userStore = useUserStore();
const router = useRouter();

const submitLogin = async () => {
  const success = await userStore.login(email.value, password.value);
  if (success) {
    router.push('/dashboard'); // Przekieruj na dashboard po sukcesie
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8f9fa;
}

.login-box {
  background-color: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

.form-label {
  text-align: left;
  display: block;
  margin-bottom: 5px;
}

.form-control {
  width: 100%;
  padding: 10px;
  margin-bottom: 15px;
  border: 1px solid #ced4da;
  border-radius: 4px;
}

.btn-primary {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.text-danger {
  color: #dc3545;
  font-size: 0.9em;
}
</style>