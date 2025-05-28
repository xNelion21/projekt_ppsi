// src/services/authService.js
import axios from 'axios';

// Ustaw bazowy URL dla żądań do Twojego API backendowego
// Upewnij się, że port jest poprawny (ten, na którym działa Spring Boot)
const API_URL = 'http://localhost:5173/api/auth/'; // Zmień port, jeśli jest inny

class AuthService {
    login(userCredentials) {
        return axios.post(API_URL + 'login', {
            email: userCredentials.email,
            password: userCredentials.password
        });
        // Axios zwraca Promise, który będzie zawierał odpowiedź serwera
        // w .then() lub błąd w .catch()
    }

    logout() {
        // Jeśli przechowujesz token/dane użytkownika lokalnie, usuń je tutaj
        // localStorage.removeItem('user'); // Przykład
        // Możesz też chcieć wywołać endpoint /api/auth/logout na backendzie,
        // chociaż dla JWT często wystarczy usunięcie tokenu po stronie klienta.
        // Dla sesji backendowych, wywołanie /logout jest ważne.
        return axios.post(API_URL + 'logout'); // Przykład wywołania backendowego wylogowania
    }

    // Możesz tu dodać inne metody, np. register(), getCurrentUser() itp.
}

export default new AuthService();