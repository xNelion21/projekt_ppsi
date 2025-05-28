<template>
  <div class="container mt-4">
    <h1>Mój Profil</h1>
    <p v-if="userStore.loading">Ładowanie profilu...</p>
    <p v-if="userStore.error" class="text-danger">{{ userStore.error }}</p>

    <div class="card mt-4">
      <div class="card-body">
        <div class="text-center mb-4">
          <img
              :src="userStore.user.profileImageUrl || '/default-profile.png'"
              alt="Zdjęcie profilowe"
              class="profile-image mb-2"
          />
          <input type="file" @change="onFileSelected" class="form-control-file" />
          <button @click="uploadImage" class="btn btn-sm btn-secondary mt-2" :disabled="!selectedFile || userStore.loading">
            {{ userStore.loading ? 'Przesyłanie...' : 'Zmień zdjęcie' }}
          </button>
        </div>

        <form @submit.prevent="saveProfile">
          <div class="mb-3">
            <label for="email" class="form-label">Email:</label>
            <input type="email" id="email" :value="userStore.user.email" class="form-control" disabled />
          </div>

          <div class="mb-3">
            <label for="age" class="form-label">Wiek:</label>
            <input type="number" id="age" v-model="profile.age" class="form-control" />
          </div>

          <div class="mb-3">
            <label for="gender" class="form-label">Płeć:</label>
            <select id="gender" v-model="profile.gender" class="form-select">
              <option value="">Wybierz...</option>
              <option value="male">Mężczyzna</option>
              <option value="female">Kobieta</option>
              <option value="other">Inna</option>
            </select>
          </div>

          <button type="submit" class="btn btn-primary" :disabled="userStore.loading">
            {{ userStore.loading ? 'Zapisywanie...' : 'Zapisz profil' }}
          </button>
          <div v-if="successMessage" class="alert alert-success mt-3">{{ successMessage }}</div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();
const successMessage = ref(null);
const selectedFile = ref(null);

const profile = ref({
  age: userStore.user.age,
  gender: userStore.user.gender
});

// Wczytaj dane użytkownika, jeśli nie są jeszcze wczytane
onMounted(async () => {
  if (!userStore.user.email) {
    await userStore.fetchCurrentUser();
    profile.value.age = userStore.user.age;
    profile.value.gender = userStore.user.gender;
  }
});

const onFileSelected = (event) => {
  selectedFile.value = event.target.files[0];
};

const uploadImage = async () => {
  if (!selectedFile.value) {
    alert('Proszę wybrać plik do przesłania.');
    return;
  }
  const success = await userStore.uploadProfileImage(selectedFile.value);
  if (success) {
    successMessage.value = 'Zdjęcie profilowe zostało przesłane pomyślnie!';
    selectedFile.value = null; // Wyczyść wybrany plik
    setTimeout(() => {
      successMessage.value = null;
    }, 3000);
  } else {
    successMessage.value = 'Błąd podczas przesyłania zdjęcia.';
  }
};

const saveProfile = async () => {
  const success = await userStore.updateProfile(profile.value);
  if (success) {
    successMessage.value = 'Profil zapisany pomyślnie!';
    setTimeout(() => {
      successMessage.value = null;
    }, 3000);
  } else {
    successMessage.value = 'Błąd podczas zapisu profilu.';
  }
};
</script>

<style scoped>
.profile-image {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ddd;
}
</style>