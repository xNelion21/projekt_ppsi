<script setup>

import { useRoute, useRouter } from 'vue-router';
import { useTaskStore } from '../stores/taskStore';
import { useUserStore } from '@/stores/userStore';

const route = useRoute();
const router = useRouter();
const taskStore = useTaskStore();
const userStore = useUserStore();

const isActive = (routeName) => {
  return route.name === routeName;
};

const displayName = userStore.displayName;
const handleLogout = async () => {
  await userStore.logout();
  router.push('/login');
};

</script>


<template>
  <div class="app-sidebar d-flex flex-column flex-shrink-0 p-3 bg-light vh-100">

    <div class="dropdown "> <a href="#" class="d-flex align-items-center link-dark text-decoration-none dropdown-toggle sidebar-user-link" id="dropdownUser2" data-bs-toggle="dropdown" aria-expanded="false">
      <i class="bi bi-person-circle me-2 sidebar-link-icon"></i>
      <strong>{{ displayName }}</strong>
    </a>
      <ul class="dropdown-menu text-small shadow" aria-labelledby="dropdownUser2">
        <li><a class="dropdown-item" href="#">Ustawienia</a></li>
        <li><a class="dropdown-item" href="#">Profil</a></li>
        <li><hr class="dropdown-divider"></li>
        <li><a class="dropdown-item" href="#">Wyloguj</a></li>
      </ul>
    </div>
    <hr>

    <button class="btn text-start fw-bold fs-5 sidebar-add-task-btn"
            data-bs-toggle="modal" data-bs-target="#addTaskModal">
      <span class="sidebar-custom-plus-icon me-2"> <i class="bi bi-plus-lg"></i> </span>
      Dodaj zadanie
    </button>

    <ul class="nav nav-pills flex-column mb-auto">
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'inbox' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('inbox') }">
          <i class="bi bi-inbox-fill me-2 sidebar-link-icon"></i>
          Skrzynka <span class="badge ms-auto sidebar-badge">{{ taskStore.todoTasks.length }}</span>
        </router-link>
      </li>
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'today' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('today') }" aria-current="page">
          <i class="bi bi-calendar-day me-2 sidebar-link-icon"></i>
          Dziś <span class="badge ms-auto sidebar-badge">{{taskStore.todayTasks.length}}</span>
        </router-link>
      </li>
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'upcoming' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('upcoming') }">
          <i class="bi bi-calendar-week me-2 sidebar-link-icon"></i>
          Nadchodzące <span class="badge ms-auto sidebar-badge">{{ taskStore.upcomingTasks.length }}</span>
        </router-link>
      </li>
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'completed' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('completed') }">
          <i class="bi bi-check-circle me-2 sidebar-link-icon"></i>
          Ukończone
        </router-link>
      </li>
      <li class="nav-item mb-1">
        <router-link :to="{ name: 'calendar' }" class="nav-link d-flex align-items-center sidebar-nav-link" :class="{ 'active': isActive('calendar') }">
          <i class="bi bi-calendar3 me-2 sidebar-link-icon"></i>
          Kalendarz
        </router-link>
      </li>
    </ul>

    <hr> <ul class="nav nav-pills flex-column "> <li class="nav-item">
      <a href="#" class="nav-link d-flex align-items-center sidebar-nav-link sidebar-help-link">
        <i class="bi bi-question-circle me-2 sidebar-link-icon"></i>
        Pomoc
      </a>
    </li>
    </ul>


  </div>
</template>

<style scoped>

:root {
  --sidebar-yellow-dark: #f6ce5d;
  --sidebar-yellow-light: #ffc100;
  --sidebar-grey-text: #6c757d;
  --sidebar-dark-text: #343a40;
  --sidebar-black-text: #000;
}


.app-sidebar {
  background-color: #fefcf5 !important;
  border-right: 1px solid #e9ecef;
  width: 280px;
  padding-bottom: 15px !important;
}

.sidebar-add-task-btn {
  background-color: var(--sidebar-yellow-dark);
  color: #ffc100;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  font-size: 1.4rem !important;
  margin-bottom: 1rem;
}

.sidebar-add-task-btn:hover {
  background-color: rgba(128, 128, 128, 0.07);
  color: #ffc100;
}

.sidebar-custom-plus-icon {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 24px;
  height: 24px;
  background-color: #ffbd00 !important;
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
  color: var(--sidebar-black-text, #000);
  border-radius: 5px;
  margin-bottom: 0;
  padding: 10px 15px;
  text-decoration: none;
  transition: background-color 0.2s ease;
  font-size: 1rem;
}

.nav-pills .nav-item {
  margin-bottom: 5px;
}

.sidebar-nav-link:hover {
  background-color: rgba(128, 128, 128, 0.07);
  color: var(--sidebar-black-text, #000);
}

.sidebar-nav-link.active {
  background-color: rgba(250, 243, 211, 0.5) !important;
  color: #ffbd00 !important;
}

.sidebar-nav-link .sidebar-link-icon {
  color: var(--sidebar-grey-text);
  vertical-align: middle;
  transition: color 0.2s ease;
  font-size: 1rem;
}
.sidebar-nav-link:hover .sidebar-link-icon {
  color: var(--sidebar-dark-text);
}

.sidebar-nav-link.active .sidebar-link-icon {
  color: var(--sidebar-yellow-light, #ffbe00);
}

.sidebar-badge {
  background-color: var(--sidebar-yellow-dark);
  color: #f6ce5d;
  font-size: 0.9rem;
  padding: 0.25em 0.5em;
  border-radius: 0.25rem;
}

.sidebar-nav-link.active .sidebar-badge {
  background-color: #f6ce5d;
  color: white;
}

.sidebar-user-link {
  color: var(--sidebar-dark-text);
  padding: 10px 15px;
  border-radius: 5px;
  transition: background-color 0.2s ease;
}
.sidebar-user-link:hover {
  background-color: rgba(128, 128, 128, 0.07);
  color: var(--sidebar-dark-text);
}
.sidebar-user-link .bi {
  color: var(--sidebar-grey-text);
  transition: color 0.2s ease;
}
.sidebar-user-link:hover .bi {
  color: var(--sidebar-dark-text);
}


.sidebar-help-link {
  color: var(--sidebar-dark-text);
  padding: 10px 15px;
  border-radius: 5px;
  transition: background-color 0.2s ease;
}
.sidebar-help-link:hover {
  background-color: rgba(128, 128, 128, 0.07);
  color: var(--sidebar-dark-text);
}
.sidebar-help-link .bi {
  color: var(--sidebar-grey-text);
  transition: color 0.2s ease;
}
.sidebar-help-link:hover .bi {
  color: var(--sidebar-dark-text);
}

hr {
  border-top: 1px solid rgba(0, 0, 0, .1);
  margin-top: 1rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.app-sidebar .nav:last-child {
  margin-bottom: 0 !important;
}

</style>