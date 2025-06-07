<template>
  <div class="container mt-4">
    <h1>{{ t('settings.title') }}</h1>
    <p v-if="userStore.loading">{{ t('settings.loading') }}</p>
    <p v-if="userStore.error" class="text-danger">{{ t('settings.error', { message: userStore.error }) }}</p>

    <form @submit.prevent="saveAllSettings" class="mt-4">
      <div class="mb-3 text-center">
        <label for="profileImage" class="form-label d-block">{{ t('settings.form.profileImageLabel') }}</label>
        <img
            :src="formState.profileImageUrl || defaultProfileImage"
            alt="Zdjęcie profilowe"
            class="profile-image-preview img-fluid mb-2"
            @error="handleImagePreviewError"
        />
        <input type="file" id="profileImage" @change="handleImageUpload" class="form-control" accept="image/*" />
        <p v-if="imageUploadMessage" :class="imageUploadSuccess ? 'text-success' : 'text-danger'">
          {{ imageUploadMessage }}
        </p>
      </div>

      <div class="mb-3">
        <label for="nickname" class="form-label">{{ t('settings.form.nicknameLabel') }}</label>
        <input type="text" id="nickname" v-model="formState.nickname" class="form-control" />
      </div>

      <div class="mb-3">
        <label for="languagePreference" class="form-label">{{ t('settings.form.languageLabel') }}</label>
        <select id="languagePreference" v-model="formState.languagePreference" class="form-select">
          <option value="en-US">{{ t('settings.form.languageOptions.en') }}</option>
          <option value="pl-PL">{{ t('settings.form.languageOptions.pl') }}</option>
        </select>
      </div>

      <div class="mb-3">
        <label for="themePreference" class="form-label">{{ t('settings.form.themeLabel') }}</label>
        <select id="themePreference" v-model="formState.themePreference" class="form-select">
          <option value="light">{{ t('settings.form.themeOptions.light') }}</option>
          <option value="dark">{{ t('settings.form.themeOptions.dark') }}</option>
        </select>
      </div>

      <div class="mb-3">
        <label for="age" class="form-label">{{ t('settings.form.ageLabel') }}</label>
        <input type="number" id="age" v-model.number="formState.age" class="form-control" min="0" />
      </div>

      <div class="mb-3">
        <label for="gender" class="form-label">{{ t('settings.form.genderLabel') }}</label>
        <select id="gender" v-model="formState.gender" class="form-select">
          <option value="">{{ t('settings.form.genderOptions.notSpecified') }}</option>
          <option value="male">{{ t('settings.form.genderOptions.male') }}</option>
          <option value="female">{{ t('settings.form.genderOptions.female') }}</option>
          <option value="PanzerkampfwagenII">{{ t('settings.form.genderOptions.other') }}</option>
        </select>
      </div>

      <button type="submit" class="btn btn-primary" :disabled="userStore.loading">
        {{ userStore.loading ? t('settings.form.saveButtonSaving') : t('settings.form.saveButtonDefault') }}
      </button>
      <div v-if="successMessage" class="alert alert-success mt-3">{{ successMessage }}</div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'; // Dodaj 'watch'
import { useUserStore } from '@/stores/userStore';
import { useI18n } from 'vue-i18n';

const userStore = useUserStore();
const { t, locale } = useI18n();
const successMessage = ref(null);

// DODANE DLA UPLOADU ZDJĘCIA
const imageUploadMessage = ref(null);
const imageUploadSuccess = ref(false);
const defaultProfileImage = '/images/prof.jpg'; // Ścieżka do domyślnego obrazka, zakładamy, że jest w public/images/

const formState = reactive({
  nickname: '',
  languagePreference: localStorage.getItem('userLanguage') || 'en-US',
  themePreference: localStorage.getItem('theme') || 'light',
  age: null,
  gender: '',
  profileImageUrl: null, // DODANE: Aby przechowywać aktualny URL zdjęcia profilowego
});

const syncFormWithStore = () => {
  formState.nickname = userStore.user.nickname || '';
  formState.languagePreference = userStore.user.languagePreference || localStorage.getItem('userLanguage') || 'en-US';
  formState.themePreference = userStore.user.themePreference || localStorage.getItem('theme') || 'light';
  formState.age = userStore.user.age === undefined ? null : userStore.user.age;
  formState.gender = userStore.user.gender || '';
  // Ustawienie początkowego URL zdjęcia profilowego z userStore lub domyślnego
  formState.profileImageUrl = userStore.user.profileImageUrl || defaultProfileImage;
};

onMounted(async () => {
  if (!userStore.isAuthenticated || !userStore.user.email) {
    await userStore.fetchCurrentUser();
  }
  syncFormWithStore();
});

// DODANE: Watcher, aby reagować na zmiany w userStore.user.profileImageUrl
// To zapewni, że podgląd zdjęcia aktualizuje się po udanym uploadzie
watch(() => userStore.user.profileImageUrl, (newUrl) => {
  formState.profileImageUrl = newUrl || defaultProfileImage;
}, { immediate: true });


// DODANE: Metoda do obsługi wyboru pliku i jego uploadu
const handleImageUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) {
    imageUploadMessage.value = t('settings.form.noFileSelected');
    imageUploadSuccess.value = false;
    return;
  }

  // Wyczyść poprzednie komunikaty i pokaż komunikat o przesyłaniu
  imageUploadMessage.value = t('settings.form.uploadingImage');
  imageUploadSuccess.value = false;

  // Wywołaj akcję ze store do uploadu
  const success = await userStore.uploadProfileImage(file);
  if (success) {
    imageUploadMessage.value = t('settings.form.imageUploadSuccess');
    imageUploadSuccess.value = true;
    // userStore.user.profileImageUrl zostanie zaktualizowany przez fetchCurrentUser w akcji
    // formState.profileImageUrl zostanie zaktualizowany przez watcher powyżej
  } else {
    // Komunikat o błędzie, jeśli upload się nie powiódł
    imageUploadMessage.value = t('settings.form.imageUploadError', { message: userStore.error });
    imageUploadSuccess.value = false;
  }

  // Ukryj komunikat po kilku sekundach
  setTimeout(() => {
    imageUploadMessage.value = null;
  }, 4000);
};

const handleImagePreviewError = (event) => {
  console.error("Błąd ładowania obrazka podglądu dla:", event.target.src);
  if (event.target.src === defaultProfileImage) {
    event.target.src = '';
    console.warn("Domyślny obrazek profilowy (/images/prof.jpg) również nie może być załadowany. Sprawdź, czy plik jest w public/images/.");
  } else {
    event.target.src = defaultProfileImage;
  }
}


const formatGender = (genderKey) => {
  if (!genderKey || genderKey.trim() === '') return 'Nie podano';
  const genders = {
    male: 'Mężczyzna',
    female: 'Kobieta',
    panzerkampfwagenii: 'Inna', // Zostawiam dla zgodności z Twoim modelem
    other: 'Inna', // Dodaję 'other' jako ogólną kategorię, jeśli 'PanzerkampfwagenII' jest traktowane jako 'inna'
  };
  return genders[genderKey.toLowerCase()] || genderKey;
};

const formatLanguage = (langKey) => {
  if (!langKey) return 'Nie ustawiono';
  const languages = {
    pl: 'Polski',
    'pl-pl': 'Polski', // Zapewnij zgodność z 'pl-PL' z Twojego DTO
    en: 'English',
    'en-us': 'English', // Zapewnij zgodność z 'en-US' z Twojego DTO
  };
  return languages[langKey.toLowerCase()] || langKey;
};

const formatTheme = (themeKey) => {
  if (!themeKey) return 'Nie ustawiono';
  const themes = {
    light: 'Jasny',
    dark: 'Ciemny',
  };
  return themes[themeKey.toLowerCase()] || themeKey;
};

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

  // Upewnij się, że używasz userStore.user do porównań
  let settingsActuallyChanged =
      settingsDataPayload.nickname !== userStore.user.nickname ||
      settingsDataPayload.languagePreference !== userStore.user.languagePreference ||
      settingsDataPayload.themePreference !== userStore.user.themePreference;

  let profileActuallyChanged =
      profileDataPayload.age !== userStore.user.age ||
      profileDataPayload.gender !== userStore.user.gender;

  // Specjalne sprawdzenie dla null/undefined w wieku i płci
  if (formState.age === null && (userStore.user.age === undefined || userStore.user.age === null)) {
    // Jeśli oba są null/undefined, nie ma zmiany, więc ustaw na false
    profileActuallyChanged = profileActuallyChanged && (profileDataPayload.age !== null);
  }
  if (formState.gender === '' && (userStore.user.gender === undefined || userStore.user.gender === null)) {
    // Jeśli oba są null/undefined, nie ma zmiany, więc ustaw na false
    profileActuallyChanged = profileActuallyChanged && (profileDataPayload.gender !== null);
  }


  let overallSuccess = true;

  if (settingsActuallyChanged) {
    // Wywołaj akcję updateSettings z userStore
    const settingsUpdateSuccess = await userStore.updateSettings(settingsDataPayload);
    if (!settingsUpdateSuccess) {
      overallSuccess = false;
    } else {
      if (formState.languagePreference !== locale.value) {
        locale.value = formState.languagePreference;
        localStorage.setItem('userLanguage', formState.languagePreference);
      }

      if (formState.themePreference !== localStorage.getItem('theme')) {
        localStorage.setItem('theme', formState.themePreference);
        if (formState.themePreference === 'dark') {
          document.documentElement.classList.add('dark-mode');
        } else {
          document.documentElement.classList.remove('dark-mode');
        }
      }
    }
  }

  if (overallSuccess && profileActuallyChanged) {
    if (userStore.error) {
      overallSuccess = false;
    } else {
      // Wywołaj akcję updateProfile z userStore
      const profileUpdateSuccess = await userStore.updateProfile(profileDataPayload);
      if (!profileUpdateSuccess) {
        overallSuccess = false;
      }
    }
  }

  if (overallSuccess) {
    if (settingsActuallyChanged || profileActuallyChanged) {
      successMessage.value = t('settings.form.successMessageSaved');
      syncFormWithStore(); // Ponowna synchronizacja po zapisie
    } else {
      successMessage.value = t('settings.form.successMessageNoChange');
    }
  } else {
    // Jeśli userStore.error jest już ustawione przez updateSettings/updateProfile,
    // nie nadpisuj go ogólnym komunikatem.
    if (!userStore.error) {
      successMessage.value = t('settings.form.errorMessageGeneric');
    }
  }

  setTimeout(() => {
    successMessage.value = null;
  }, 4000);
};
</script>

<style scoped>
.profile-image-preview {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e9ecef;
  background-color: #f8f9fa;
}

.form-select {
  background-color: var(--color-background-soft);
  color: var(--color-text);
  border: none;
}

.form-control{
  background-color: var(--color-background-soft);
  color: var(--color-text);
  border: none;
}

label {
  font-size: 1.2rem;
  font-weight: bold;
}
</style>