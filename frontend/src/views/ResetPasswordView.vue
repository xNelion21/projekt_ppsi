<template>
  <div class="reset-password-container">
    <div class="reset-password-box">
      <h2>Ustaw nowe hasło</h2>
      <form v-if="!successMessage" @submit.prevent="handlePasswordReset">
        <div class="mb-3">
          <label for="password" class="form-label">Nowe hasło:</label>
          <input type="password" id="password" v-model="password" class="form-control" required>
        </div>
        <div class="mb-3">
          <label for="confirmPassword" class="form-label">Potwierdź nowe hasło:</label>
          <input type="password" id="confirmPassword" v-model="confirmPassword" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Zmieniam...' : 'Zmień hasło' }}
        </button>
        <p v-if="errorMessage" class="text-danger mt-3">{{ errorMessage }}</p>
      </form>
      <div v-if="successMessage" class="alert alert-success mt-3">
        <p>{{ successMessage }}</p>
        <a href="http://localhost:8080/loginpage" class="btn btn-secondary mt-2">Przejdź do logowania</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const password = ref('');
const confirmPassword = ref('');
const token = ref(null);
const loading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

// Po zamontowaniu komponentu, pobierz token z adresu URL
onMounted(() => {
  token.value = route.query.token;
  if (!token.value) {
    errorMessage.value = 'Brak tokenu resetującego. Upewnij się, że używasz poprawnego linku.';
  }
});

const handlePasswordReset = async () => {
  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Hasła nie są identyczne.';
    return;
  }

  loading.value = true;

  try {
    const response = await fetch('/api/auth/reset-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        token: token.value,
        newPassword: password.value,
      }),
    });

    const responseText = await response.text();
    if (!response.ok) {
      throw new Error(responseText);
    }

    successMessage.value = responseText;

  } catch (error) {
    errorMessage.value = error.message || 'Wystąpił nieoczekiwany błąd.';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.reset-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8f9fa;
}
.reset-password-box {
  background-color: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px;
  text-align: center;
}
.form-label {
  text-align: left;
  display: block;
  margin-bottom: 5px;
}
</style>