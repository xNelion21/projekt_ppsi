<template>
  <div class="container mt-4">
    <h1>Ustawienia</h1>
    <p v-if="userStore.loading">Ładowanie ustawień...</p>
    <p v-if="userStore.error" class="text-danger">{{ userStore.error }}</p>

    <form @submit.prevent="saveSettings" class="mt-4">
      <div class="mb-3">
        <label for="nickname" class="form-label">Pseudonim:</label>
        <input type="text" id="nickname" v-model="settings.nickname" class="form-control" />
      </div>

      <div class="mb-3">
        <label for="languagePreference" class="form-label">Język:</label>
        <select id="languagePreference" v-model="settings.languagePreference" class="form-select">
          <option value="pl">Polski</option>
          <option value="en">English</option>
        </select>
      </div>

      <div class="mb-3">
        <label for="themePreference" class="form-label">Wygląd:</label>
        <select id="themePreference" v-model="settings.themePreference" class="form-select">
          <option value="light">Jasny</option>
          <option value="dark">Ciemny</option>
        </select>
      </div>

      <button type="submit" class="btn btn-primary" :disabled="userStore.loading">
        {{ userStore.loading ? 'Zapisywanie...' : 'Zapisz ustawienia' }}
      </button>
      <div v-if="successMessage" class="alert alert-success mt-3">{{ successMessage }}</div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();
const successMessage = ref(null);

const settings = ref({
  nickname: userStore.user.nickname,
  languagePreference: userStore.user.languagePreference,
  themePreference: userStore.user.themePreference
});

// Wczytaj dane użytkownika, jeśli nie są jeszcze wczytane (np. po odświeżeniu strony)
onMounted(async () => {
  if (!userStore.user.email) {
    await userStore.fetchCurrentUser();
    settings.value.nickname = userStore.user.nickname;
    settings.value.languagePreference = userStore.user.languagePreference;
    settings.value.themePreference = userStore.user.themePreference;
  }
});

const saveSettings = async () => {
  const success = await userStore.updateSettings(settings.value);
  if (success) {
    successMessage.value = 'Ustawienia zapisane pomyślnie!';
    setTimeout(() => {
      successMessage.value = null;
    }, 3000);
  } else {
    successMessage.value = 'Błąd podczas zapisu ustawień.';
  }
};
</script>

<style scoped>
/* Dodaj style dla formularza ustawień, jeśli potrzebujesz */
</style>