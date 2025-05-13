// === frontend/src/router/index.js ===
import { createRouter, createWebHistory } from 'vue-router';
import TodayView from '@/views/TodayView.vue';
import InboxView from "@/views/InboxView.vue";
import UpcomingView from "@/views/UpcomingView.vue";
import CompletedView from "@/views/CompletedView.vue";
import CalendarView from "@/views/CalendarView.vue";

const routes = [
    {
        path: '/',
        redirect: '/today'
    },
    {
        path: '/inbox',
        name: 'inbox',
        component: InboxView
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
    }

];


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: routes
});


export default router;