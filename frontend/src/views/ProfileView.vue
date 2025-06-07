<template>
  <div class="container mt-4">
    <h1>Mój Profil</h1>

    <div v-if="userStore.loading && !userDataLoaded" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Ładowanie profilu...</span>
      </div>
      <p class="mt-2">Ładowanie profilu...</p>
    </div>

    <div v-else-if="loadError" class="alert alert-danger">
      Nie udało się załadować danych profilu. Proszę spróbować odświeżyć stronę lub skontaktować się z pomocą techniczną.
    </div>

    <div v-else-if="userDataLoaded && userStore.user && userStore.user.email" class="card mt-4 shadow-sm">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col-md-3 text-center mb-3 mb-md-0">
            <img
                :src="userStore.user.profileImageUrl || defaultProfileImage"
                alt="Zdjęcie profilowe"
                class="profile-image img-fluid"
                @error="handleImageError"
            />
          </div>
          <div class="col-md-9">
            <h2 class="card-title mb-3">{{ userStore.user.nickname || 'Brak pseudonimu' }}</h2>
            <dl class="row">
              <dt class="col-sm-4 col-lg-3">Adres Email:</dt>
              <dd class="col-sm-8 col-lg-9">{{ userStore.user.email }}</dd>

              <dt class="col-sm-4 col-lg-3">Wiek:</dt>
              <dd class="col-sm-8 col-lg-9">{{ userStore.user.age != null ? userStore.user.age : 'Nie podano' }}</dd>

              <dt class="col-sm-4 col-lg-3">Płeć:</dt>
              <dd class="col-sm-8 col-lg-9">{{ formatGender(userStore.user.gender) }}</dd>

              <dt class="col-sm-4 col-lg-3">Preferowany język:</dt>
              <dd class="col-sm-8 col-lg-9">{{ formatLanguage(userStore.user.languagePreference) }}</dd>

              <dt class="col-sm-4 col-lg-3">Preferowany wygląd:</dt>
              <dd class="col-sm-8 col-lg-9">{{ formatTheme(userStore.user.themePreference) }}</dd>
            </dl>
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="!userStore.loading && !loadError" class="alert alert-warning">
      Nie znaleziono danych użytkownika.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();
const userDataLoaded = ref(false);
const loadError = ref(false);

const defaultProfileImage = '/images/prof.jpg';

const finalImageUrl = ref(defaultProfileImage);

const initializeImageUrl = () => {
  if (userStore.user && userStore.user.profileImageUrl) {
    finalImageUrl.value = userStore.user.profileImageUrl;
  } else {
    finalImageUrl.value = defaultProfileImage;
  }
};

const syncUserData = async () => {
  if (userStore.isAuthenticated && userStore.user && userStore.user.email) {
    userDataLoaded.value = true;
    loadError.value = false;
    initializeImageUrl();
  } else {

    const success = await userStore.fetchCurrentUser();
    if (success) {
      userDataLoaded.value = true;
      loadError.value = false;
      initializeImageUrl();
    } else {
      userDataLoaded.value = false;
      loadError.value = true;
      finalImageUrl.value = defaultProfileImage;
    }
  }
};

onMounted(() => {
  syncUserData();
});

watch(() => userStore.user, (newUser) => {
  if (newUser && newUser.email) {
    userDataLoaded.value = true;
    loadError.value = false;
    initializeImageUrl();
  }
}, { deep: true });


const formatGender = (genderKey) => {
  if (!genderKey || genderKey.trim() === '') return 'Nie podano';
  const genders = {
    male: 'Mężczyzna',
    female: 'Kobieta',
    other: 'Inna',
  };
  return genders[genderKey.toLowerCase()] || genderKey;
};

const formatLanguage = (langKey) => {
  if (!langKey) return 'Nie ustawiono';
  const languages = {
    pl: 'Polski',
    en: 'English',
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

const handleImageError = (event) => {
  console.error("Błąd ładowania obrazka dla:", event.target.src);

  if (event.target.src === defaultProfileImage) {
    event.target.src = '';
    console.warn("Domyślny obrazek profilowy (/images/prof.jpg) również nie może być załadowany. Sprawdź, czy plik jest w public/images/.");
  } else {

    finalImageUrl.value = defaultProfileImage;
  }
}

</script>

<style scoped>
.profile-image {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e9ecef;
  background-color: #f8f9fa;
}

.card {
  border: none;
  background-color: var(--color-background-mute);
  color: var(--color-text);
  border-radius: 25px;
}

dt {
  font-weight: 600;
  color: var(--color-text);
}

dd {
  margin-bottom: 0.8rem;
  color: var(--color-text-soft);
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}
</style>