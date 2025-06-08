
import { createRouter, createWebHistory } from 'vue-router';
import TodayView from '@/views/TodayView.vue';
import InboxView from "@/views/InboxView.vue";
import UpcomingView from "@/views/UpcomingView.vue";
import CompletedView from "@/views/CompletedView.vue";
import CalendarView from "@/views/CalendarView.vue";
import DashboardView from '@/views/DashboardView.vue';
import SettingsView from '@/views/SettingsView.vue';
import ProfileView from '@/views/ProfileView.vue';

import ResetPasswordView from '../views/ResetPasswordView.vue'

const routes = [
    {
        path: '/',
        redirect: '/inbox'
    },

    {
        path: '/inbox',
        name: 'inbox',
        component: InboxView
    },
    {
        path: '/reset-password', // <-- DODAJ NOWĄ ŚCIEŻKĘ
        name: 'reset-password',
        component: ResetPasswordView
    },

    {
        path: '/today',
        name: 'today',
        component: TodayView
    },
    {
        path: '/upcoming',
        name: 'upcoming',
        component: UpcomingView
    },
    {
        path: '/completed',
        name: 'completed',
        component: CompletedView
    },
    {
        path: '/calendar',
        name: 'calendar',
        component: CalendarView
    },

    {
        path: '/dashboard',
        name: 'dashboard',
        component: DashboardView,

    },

    {
        path: '/settings',
        name: 'settings',
        component: SettingsView,

    },
    {
        path: '/profile',
        name: 'profile',
        component: ProfileView,

    }

];


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: routes
});

export default router;