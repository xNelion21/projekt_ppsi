import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from "pinia";
import { useUserStore } from '@/stores/userStore';
import './assets/main.css';
import 'v-calendar/dist/style.css';

import * as BootstrapVueNextModuleDiagnostics from 'bootstrap-vue-next';
console.log('Rzeczywiste eksporty z bootstrap-vue-next:', BootstrapVueNextModuleDiagnostics);
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue-next/dist/bootstrap-vue-next.css';
console.log('DIAGNOSTYKA - Zawartość BootstrapVueNextModuleDiagnostics:', BootstrapVueNextModuleDiagnostics);

const app = createApp(App);
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(BootstrapVueNextModuleDiagnostics);

console.log('main.js: Aplikacja Vue startuje. Czekam na gotowość routera...');
router.isReady().then(async () => {
    console.log('main.js: Router gotowy. Próbuję pobrać dane bieżącego użytkownika (fetchCurrentUser)...');

    const userStore = useUserStore();
    await userStore.fetchCurrentUser();
    console.log(`main.js: Po fetchCurrentUser - isAuthenticated: ${userStore.isAuthenticated}`);
    console.log('main.js: Stan użytkownika:', userStore.user);



app.mount('#app')});