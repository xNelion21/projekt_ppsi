<template>
  <div class="container mt-4">
    <h1>Ustawienia</h1>
    <p v-if="userStore.loading">Ładowanie ustawień...</p>
    <p v-if="userStore.error" class="text-danger">{{ userStore.error }}</p>

    <form @submit.prevent="saveAllSettings" class="mt-4">
      <div class="mb-3">
        <label for="nickname" class="form-label">Pseudonim:</label>
        <input type="text" id="nickname" v-model="formState.nickname" class="form-control" />
      </div>

      <div class="mb-3">
        <label for="languagePreference" class="form-label">Język:</label>
        <select id="languagePreference" v-model="formState.languagePreference" class="form-select">
          <option value="pl">Polski</option>
          <option value="en">English</option>
        </select>
      </div>

      <div class="mb-3">
        <label for="themePreference" class="form-label">Wygląd:</label>
        <select id="themePreference" v-model="formState.themePreference" class="form-select">
          <option value="light">Jasny</option>
          <option value="dark">Ciemny</option>
        </select>
      </div>

      <div class="mb-3">
        <label for="age" class="form-label">Wiek:</label>
        <input type="number" id="age" v-model.number="formState.age" class="form-control" min="0" />
      </div>

      <div class="mb-3">
        <label for="gender" class="form-label">Płeć:</label>
        <select id="gender" v-model="formState.gender" class="form-select">
          <option value="">Nie określono</option>
          <option value="male">Mężczyzna</option>
          <option value="female">Kobieta</option>
          <option value="other">Inne</option>
        </select>
      </div>

      <button type="submit" class="btn btn-primary" :disabled="userStore.loading">
        {{ userStore.loading ? 'Zapisywanie...' : 'Zapisz zmiany' }}
      </button>
      <div v-if="successMessage" class="alert alert-success mt-3">{{ successMessage }}</div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();
const successMessage = ref(null);

const formState = reactive({
  nickname: '',
  languagePreference: 'pl',
  themePreference: 'light',
  age: null,
  gender: '',
});

const syncFormWithStore = () => {
  formState.nickname = userStore.user.nickname || '';
  formState.languagePreference = userStore.user.languagePreference || 'pl';
  formState.themePreference = userStore.user.themePreference || 'light';
  formState.age = userStore.user.age === undefined ? null : userStore.user.age;
  formState.gender = userStore.user.gender || '';
};

onMounted(async () => {
  if (!userStore.isAuthenticated || !userStore.user.email) {
    await userStore.fetchCurrentUser();
  }
  syncFormWithStore();
});

const saveAllSettings = async () => {
  userStore.error = null;
  successMessage.value = null;

  const settingsDataPayload = {
    nickname: formState.nickname,
    languagePreference: formState.languagePreference,
    themePreference: formState.themePreference,
  };

  const profileDataPayload = {
    age: formState.age === '' ? null : Number(formState.age),
    gender: formState.gender === '' ? null : formState.gender,
  };

  let settingsActuallyChanged =
      settingsDataPayload.nickname !== userStore.user.nickname ||
      settingsDataPayload.languagePreference !== userStore.user.languagePreference ||
      settingsDataPayload.themePreference !== userStore.user.themePreference;

  let profileActuallyChanged =
      profileDataPayload.age !== userStore.user.age ||
      profileDataPayload.gender !== userStore.user.gender;

  if (profileDataPayload.age === null && userStore.user.age === undefined && profileDataPayload.gender === null && userStore.user.gender === undefined) {
    // Special case if initial values are undefined and form sets them to null, treat as no change if they were conceptually "empty"
    if (formState.age === null && userStore.user.age === undefined) profileActuallyChanged = false;
    else if (formState.gender === '' && userStore.user.gender === undefined) profileActuallyChanged = false;
  }


  let overallSuccess = true;

  if (settingsActuallyChanged) {
    const settingsUpdateSuccess = await userStore.updateSettings(settingsDataPayload);
    if (!settingsUpdateSuccess) {
      overallSuccess = false;
    }
  }

  if (overallSuccess && profileActuallyChanged) {
    if (userStore.error) { // If an error occurred from updateSettings, don't proceed
      overallSuccess = false;
    } else {
      const profileUpdateSuccess = await userStore.updateProfile(profileDataPayload);
      if (!profileUpdateSuccess) {
        overallSuccess = false;
      }
    }
  }

  if (overallSuccess) {
    if (settingsActuallyChanged || profileActuallyChanged) {
      successMessage.value = 'Zmiany zapisane pomyślnie!';
      syncFormWithStore();
    } else {
      successMessage.value = 'Nie wprowadzono żadnych zmian.';
    }
  } else {
    if (!userStore.error) {
      // This case should ideally not happen if actions set error on failure
      // but as a fallback:
      successMessage.value = 'Wystąpił błąd podczas zapisu. Sprawdź konsolę.';
    }
    // Error message from userStore.error will be displayed by the template
  }

  setTimeout(() => {
    successMessage.value = null;
  }, 4000);
};
</script>

<style scoped>
/* Dodaj style dla formularza ustawień, jeśli potrzebujesz */
</style>