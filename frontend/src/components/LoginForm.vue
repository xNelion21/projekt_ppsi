<template>
  <div class="login-form">
    <h2>Logowanie</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="email" required />
      </div>
      <div class="form-group">
        <label for="password">Hasło:</label>
        <input type="password" id="password" v_model="password" required />
      </div>
      <button type="submit" :disabled="loading">
        {{ loading ? 'Logowanie...' : 'Zaloguj się' }}
      </button>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script>
import AuthService from '@/services/authService'; // Importuj serwis
// LUB jeśli nie używasz serwisu:
// import axios from 'axios';

export default {
  name: 'LoginForm',
  data() {
    return {
      email: '',
      password: '',
      loading: false,
      errorMessage: ''
    };
  },
  methods: {
    async handleLogin() {
      this.loading = true;
      this.errorMessage = '';

      try {
        const userCredentials = {
          email: this.email,
          password: this.password
        };

        // Użycie serwisu:
        const response = await AuthService.login(userCredentials);

        // LUB bezpośrednio z Axios (jeśli nie używasz authService.js):
        /*
        const response = await axios.post('http://localhost:8080/api/auth/login', {
          email: this.email,
          password: this.password
        });
        */

        console.log('Odpowiedź logowania:', response.data);
        // Tutaj obsłuż sukces:
        // 1. Zapisz dane użytkownika/token (np. w Vuex/Pinia lub localStorage)
        //    localStorage.setItem('user', JSON.stringify(response.data)); // Prosty przykład
        // 2. Przekieruj na inną stronę
        this.$router.push('/dashboard'); // Zakładając, że masz ścieżkę '/dashboard'

      } catch (error) {
        this.loading = false;
        if (error.response && error.response.data && error.response.data.message) {
          this.errorMessage = error.response.data.message;
        } else if (error.message) {
          this.errorMessage = 'Wystąpił błąd: ' + error.message;
        } else {
          this.errorMessage = 'Nie udało się połączyć z serwerem lub wystąpił nieznany błąd.';
        }
        console.error('Błąd logowania:', error.response || error);
      } finally {
        this.loading = false; // Upewnij się, że loading jest resetowany nawet po błędzie
      }
    }
  }
};
</script>

<style scoped>
.login-form {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.form-group {
  margin-bottom: 15px;
}
.form-group label {
  display: block;
  margin-bottom: 5px;
}
.form-group input {
  width: calc(100% - 20px);
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled {
  background-color: #ccc;
}
.error-message {
  color: red;
  margin-top: 10px;
}
</style>