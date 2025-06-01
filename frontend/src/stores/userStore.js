import { defineStore } from 'pinia';
import axios from 'axios';
axios.defaults.withCredentials = true;
export const useUserStore = defineStore('user', {
    state: () => ({
        user: {
            email: null,
            nickname: null,
            languagePreference: 'pl',
            themePreference: 'light',
            age: null,
            gender: null,
            profileImageUrl: null,
            id: null,
        },
        isAuthenticated: false,
        loading: false,
        error: null
    }),
    getters: {
        // Możesz dodać gettery, np. do sprawdzenia, czy użytkownik jest zalogowany
        // isAuthenticated: (state) => !!state.user.email, // Zastąpione przez stan isAuthenticated
        displayName: (state) => state.user.nickname || state.user.email || 'Gość'
    },
    actions: {
        async login(email, password) {
            this.loading = true;
            this.error = null;
            try {
                const response = await axios.post('http://localhost:8080/api/auth/login', { email, password });
                const { email: userEmail, nickname, languagePreference, themePreference } = response.data;

                this.user.email = userEmail;
                this.user.nickname = nickname;
                this.user.languagePreference = languagePreference;
                this.user.themePreference = themePreference;
                this.isAuthenticated = true;

                // Tutaj możesz zapisać token JWT do localStorage, jeśli backend go zwraca
                // const token = response.data.token;
                // localStorage.setItem('jwt_token', token);

                // Ustaw domyślne nagłówki dla axios (jeśli używasz tokena)
                // axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

                return true; // Sukces
            } catch (err) {
                this.error = 'Błąd logowania: ' + (err.response?.data?.message || err.message);
                this.isAuthenticated = false;
                return false; // Błąd
            } finally {
                this.loading = false;
            }
        },
        async fetchCurrentUser() {
            this.loading = true;
            this.error = null;
            try {

                const response = await axios.get('http://localhost:8080/api/auth/me');
                const userData = response.data;
                this.user.id = userData.id
                this.user.email = userData.email;
                this.user.nickname = userData.nickname;
                this.user.languagePreference = userData.languagePreference;
                this.user.themePreference = userData.themePreference;
                this.user.age = userData.age;
                this.user.gender = userData.gender;
                this.user.profileImageUrl = userData.profileImageUrl;
                this.isAuthenticated = true;
                console.log('userStore: fetchCurrentUser successful, user data with ID:', this.user);
                return true;
            } catch (err) {
                console.error("Błąd podczas pobierania danych użytkownika:", err);
                this.error = 'Nie udało się pobrać danych użytkownika.';
                this.isAuthenticated = false;
                this.user = { email: null, nickname: null, languagePreference: 'pl', themePreference: 'light', age: null, gender: null, profileImageUrl: null }; // Wyczyść dane
                return false;
            } finally {
                this.loading = false;
            }
        },
        async updateSettings(settingsData) {
            this.loading = true;
            this.error = null;
            console.log('Wysyłane dane ustawień DO BACKENDU:', JSON.stringify(settingsData));
            try {
                const response = await axios.put('http://localhost:8080/api/users/me/settings', settingsData);
                this.user.nickname = response.data.nickname;
                this.user.languagePreference = response.data.languagePreference;
                this.user.themePreference = response.data.themePreference;
                return true;
            } catch (err) {
                this.error = 'Błąd podczas aktualizacji ustawień: ' + (err.response?.data || err.message);
                return false;
            } finally {
                this.loading = false;
            }
        },
        async updateProfile(profileData) {
            this.loading = true;
            this.error = null;
            try {
                const response = await axios.put('http://localhost:8080/api/users/me/profile', profileData);
                this.user.age = response.data.age;
                this.user.gender = response.data.gender;
                return true;
            } catch (err) {
                this.error = 'Błąd podczas aktualizacji profilu: ' + (err.response?.data || err.message);
                return false;
            } finally {
                this.loading = false;
            }
        },
        async uploadProfileImage(file) {
            this.loading = true;
            this.error = null;
            try {
                const formData = new FormData();
                formData.append('file', file);
                const response = await axios.post('http://localhost:8080/api/users/me/profile/upload-image', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });
                // Po udanym przesłaniu, odśwież dane użytkownika, aby pobrać nowy URL zdjęcia
                await this.fetchCurrentUser();
                return true;
            } catch (err) {
                this.error = 'Błąd podczas przesyłania zdjęcia: ' + (err.response?.data || err.message);
                return false;
            } finally {
                this.loading = false;
            }
        },
        async logout() {
            this.loading = true;
            this.error = null;
            try {
                await axios.post('http://localhost:8080/logout'); // Endpoint Spring Security logout
                this.user = { email: null, nickname: null, languagePreference: 'pl', themePreference: 'light', age: null, gender: null, profileImageUrl: null };
                this.isAuthenticated = false;
                // localStorage.removeItem('jwt_token'); // Usuń token, jeśli używasz JWT
                // delete axios.defaults.headers.common['Authorization']; // Usuń nagłówek autoryzacji
                return true;
            } catch (err) {
                this.error = 'Błąd podczas wylogowywania: ' + (err.response?.data || err.message);
                return false;
            } finally {
                this.loading = false;
            }
        }
    }
});