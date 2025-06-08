<script setup>
import { useRoute } from 'vue-router';
import { useTaskStore } from '../stores/taskStore';
import { useUserStore } from '@/stores/userStore';
import { computed } from 'vue';
import { useI18n } from "vue-i18n";

const route = useRoute();
const taskStore = useTaskStore();
const userStore = useUserStore();
const { locale, t } = useI18n();

const emit = defineEmits(['open-add-task-modal', 'open-help-modal']);


const isActive = (routeName) => {
  return route.name === routeName;
};

const displayName = computed(() => userStore.displayName);


const handleLogout = async () => {
  try {
    await userStore.logout();
    console.log('Wylogowano pomyślnie poprzez store. Spring Security powinien przekierować.');
    window.location.href = 'http://localhost:8080/loginpage';
  } catch (error) {
    console.error('Błąd podczas wylogowywania z sidebara:', error);
    alert('Wystąpił błąd podczas wylogowywania. Spróbuj ponownie.');

  }
};

const requestNewTaskModal = () => {
  emit('open-add-task-modal');
};

const requestHelpModal = () => {
  emit('open-help-modal');
};
</script>


<template>
  <div class="app-sidebar d-flex flex-column flex-shrink-0 p-3 vh-100">

    <div class="dropdown ">
      <a href="#" class="d-flex align-items-center link-dark text-decoration-none dropdown-toggle sidebar-user-link" id="dropdownUser2" data-bs-toggle="dropdown" aria-expanded="false">
        <i class="bi bi-person-circle me-2 sidebar-link-icon"></i>
        <strong>{{ displayName }}</strong>
      </a>
      <ul class="dropdown-menu text-small shadow" aria-labelledby="dropdownUser2">
        <li><router-link class="dropdown-item" :to="{ name: 'settings' }">{{ t('navigation.settings') }}</router-link></li>
        <li><router-link class="dropdown-item" :to="{ name: 'profile' }">{{ t('navigation.profile') }}</router-link></li>
        <li><hr class="dropdown-divider"></li>
        <li><a class="dropdown-item" href="#" @click.prevent="handleLogout">{{ t('navigation.logout') }}</a></li>
      </ul>
    </div>
    <hr>

    <button class="btn text-start fw-bold fs-5 sidebar-add-task-btn" @click="requestNewTaskModal">
      <span class="sidebar-custom-plus-icon me-2"> <i class="bi bi-plus-lg"></i> </span>
      {{ t('navigation.addTask') }}
    </button>

    <ul class="nav nav-pills flex-column mb-auto">
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'dashboard' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('dashboard') }">
          <i class="bi-layout-text-window-reverse me-2 sidebar-link-icon"></i>
          {{t('navigation.dashboard')}}
        </router-link>
      </li>
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'inbox' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('inbox') }">
          <i class="bi bi-inbox-fill me-2 sidebar-link-icon"></i>
          {{t('navigation.inbox')}} <span class="badge ms-auto sidebar-badge">{{ taskStore.todoTasks.length }}</span>
        </router-link>
      </li>
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'today' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('today') }" aria-current="page">
          <i class="bi bi-calendar-day me-2 sidebar-link-icon"></i>
          {{ t('navigation.today') }} <span class="badge ms-auto sidebar-badge">{{taskStore.todayTasks.length}}</span>
        </router-link>
      </li>
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'upcoming' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('upcoming') }">
          <i class="bi bi-calendar-week me-2 sidebar-link-icon"></i>
          {{ t('navigation.upcoming') }} <span class="badge ms-auto sidebar-badge">{{ taskStore.upcomingTasks.length }}</span>
        </router-link>
      </li>
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'completed' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('completed') }">
          <i class="bi bi-check-circle me-2 sidebar-link-icon"></i>
          {{ t('navigation.completed') }}
        </router-link>
      </li>
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'calendar' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('calendar') }">
          <i class="bi bi-calendar3 me-2 sidebar-link-icon"></i>
          {{ t('navigation.calendar') }}
        </router-link>
      </li>
    </ul>

    <hr>
    <ul class="nav nav-pills flex-column ">
      <li class="nav-item">
        <router-link :to="{ name: 'help'}" class="nav-link d-flex align-items-center sidebar-nav-link sidebar-help-link">
          <i class="bi bi-question-circle me-2 sidebar-link-icon"></i>
          {{ t('navigation.help') }}
        </router-link>
      </li>
    </ul>
  </div>
</template>

<style scoped>

.app-sidebar {
  background-color: var(--sidebar-bg);
  border-right: 1px solid var(--sidebar-border);
  width: 280px;
  padding-bottom: 15px !important;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.sidebar-add-task-btn {
  background-color: var(--sidebar-accent-btn-bg);
  color: var(--color-text);
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  font-size: 1.4rem !important;
  margin-bottom: 1rem;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.sidebar-add-task-btn:hover {
  background-color: var(--sidebar-accent-btn-hover-bg);
  color: var(--sidebar-accent-btn-text);
}

.sidebar-custom-plus-icon {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 24px;
  height: 24px;
  background-color: #fdd34a !important;
  border-radius: 50%;
  flex-shrink: 0;
  vertical-align: middle;
}

.sidebar-custom-plus-icon .bi-plus-lg {
  font-size: 1.3rem;
  color: white;
  font-weight: bold;
}

.sidebar-nav-link {
  color: var(--sidebar-text);
  border-radius: 5px;
  margin-bottom: 0;
  padding: 10px 15px;
  text-decoration: none;
  transition: background-color 0.2s ease, color 0.2s ease;
  font-size: 1rem;
}

.nav-pills .nav-item {
  margin-bottom: 5px;
}

.sidebar-nav-link:hover {
  background-color: var(--sidebar-hover-bg);
  color: var(--sidebar-text);
}

.sidebar-nav-link.active {
  background-color: var(--sidebar-active-bg) !important;
  color: var(--sidebar-active-text) !important;
}

.sidebar-nav-link .sidebar-link-icon {
  color: var(--sidebar-text-mute);
  vertical-align: middle;
  transition: color 0.2s ease;
  font-size: 1rem;
}

.sidebar-nav-link:hover .sidebar-link-icon {
  color: var(--sidebar-text);
}

.sidebar-nav-link.active .sidebar-link-icon {
  color: var(--sidebar-active-text);
}

.sidebar-badge {
  color: var(--sidebar-active-text);
  font-size: 0.9rem;
  padding: 0.25em 0.5em;
  border-radius: 0.25rem;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.sidebar-nav-link.active .sidebar-badge {
  color: var(--sidebar-active-text);
}

.sidebar-user-link {
  color: var(--sidebar-text);
  padding: 10px 15px;
  border-radius: 5px;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.sidebar-user-link:hover {
  background-color: var(--sidebar-hover-bg);
  color: var(--sidebar-text);
}

.sidebar-user-link .bi {
  color: var(--sidebar-text-mute);
  transition: color 0.2s ease;
}

.sidebar-user-link:hover .bi {
  color: var(--sidebar-text);
}

.sidebar-help-link {
  color: var(--sidebar-text);
  padding: 10px 15px;
  border-radius: 5px;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.sidebar-help-link:hover {
  background-color: var(--sidebar-hover-bg);
  color: var(--sidebar-text);
}

.sidebar-help-link .bi {
  color: var(--sidebar-text-mute);
  transition: color 0.2s ease;
}

.sidebar-help-link:hover .bi {
  color: var(--sidebar-text);
}

hr {
  border-top: 1px solid var(--color-text-mute);
  margin-top: 1rem;
  margin-bottom: 1rem;
  opacity: 0.5;
  transition: border-color 0.3s ease;
}

.app-sidebar .nav:last-child {
  margin-bottom: 0 !important;
}

strong {
  color: var(--color-text);
}

.dropdown-menu  {
  background-color: var(--color-background);
}

.dropdown-item {
  color: var(--color-text);
}

.dropdown-item:hover {
  background-color: var(--color-background-soft);
}



</style>
