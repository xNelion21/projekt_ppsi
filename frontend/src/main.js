
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from "pinia";
import { useUserStore } from '@/stores/userStore';


import 'bootstrap/dist/css/bootstrap.css';


import './assets/main.css';
import 'v-calendar/dist/style.css';

const app = createApp(App);
const pinia = createPinia()

app.use(pinia)
app.use(router)


console.log('main.js: Aplikacja Vue startuje. Czekam na gotowość routera...');
router.isReady().then(async () => {
    console.log('main.js: Router gotowy. Próbuję pobrać dane bieżącego użytkownika (fetchCurrentUser)...');

    const userStore = useUserStore();
    // Spróbuj pobrać dane użytkownika. Jeśli jest zalogowany, zostaną załadowane.
    // Jeśli nie, isAuthenticated pozostanie false.
    await userStore.fetchCurrentUser();
    console.log(`main.js: Po fetchCurrentUser - isAuthenticated: ${userStore.isAuthenticated}`);
    console.log('main.js: Stan użytkownika:', userStore.user);



app.mount('#app')});