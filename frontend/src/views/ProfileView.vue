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
const userDataLoaded = ref(false); // Flaga wskazująca, czy próba załadowania danych została zakończona
const loadError = ref(false);     // Flaga wskazująca, czy wystąpił błąd podczas ładowania

// Ścieżka do domyślnego obrazka profilowego w folderze `public` Twojego projektu Vue
const defaultProfileImage = '/img/default-profile.png'; // Upewnij się, że ten plik istnieje

// Funkcja do pobierania/synchronizacji danych użytkownika
const syncUserData = async () => {
  // Jeśli użytkownik jest już uwierzytelniony i ma email w store, zakładamy, że dane są załadowane
  if (userStore.isAuthenticated && userStore.user && userStore.user.email) {
    userDataLoaded.value = true;
    loadError.value = false;
  } else {
    // W przeciwnym razie, spróbuj pobrać dane
    // userStore.loading będzie zarządzane przez akcję fetchCurrentUser
    const success = await userStore.fetchCurrentUser();
    if (success) {
      userDataLoaded.value = true;
      loadError.value = false;
    } else {
      userDataLoaded.value = false; // Dane nie załadowane (ale fetch zakończony)
      loadError.value = true;     // Wystąpił błąd
    }
  }
};

onMounted(() => {
  syncUserData();
});

// Obserwuj zmiany w userStore.user (np. po aktualizacji w Ustawieniach), aby odświeżyć widok profilu
watch(() => userStore.user, (newUser) => {
  if (newUser && newUser.email) { // Sprawdź, czy nowy użytkownik ma email (wskaźnik załadowanych danych)
    userDataLoaded.value = true;
    loadError.value = false;
  }
}, { deep: true });


// Funkcje pomocnicze do formatowania wyświetlanych wartości
const formatGender = (genderKey) => {
  if (!genderKey || genderKey.trim() === '') return 'Nie podano';
  const genders = {
    male: 'Mężczyzna',
    female: 'Kobieta',
    other: 'Inna',
  };
  return genders[genderKey.toLowerCase()] || genderKey; // Zwróć klucz, jeśli nie ma tłumaczenia
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

// Obsługa błędu ładowania obrazka profilowego - ustawia domyślny obrazek
const handleImageError = (event) => {
  event.target.src = defaultProfileImage;
};
</script>

<style scoped>
.profile-image {
  width: 150px;  /* Możesz dostosować rozmiar */
  height: 150px;
  border-radius: 50%; /* Okrągły obrazek */
  object-fit: cover;   /* Zapewnia, że obrazek dobrze wypełnia ramkę */
  border: 3px solid #e9ecef; /* Subtelna ramka */
  background-color: #f8f9fa; /* Tło, na wypadek gdyby obrazek się nie ładował */
}

.card {
  border: none; /* Usuń domyślną ramkę karty dla czystszego wyglądu */
}

dt {
  font-weight: 600; /* Lekko pogrubione etykiety */
  color: #495057;
}

dd {
  margin-bottom: 0.8rem; /* Odstęp pod wartościami */
  color: #212529;
}

/* Dodatkowe style dla spinnera, jeśli domyślny jest za mały */
.spinner-border {
  width: 3rem;
  height: 3rem;
}
</style>