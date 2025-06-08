<template>
  <div class="container-fluid d-flex flex-column h-100" :class="{ 'sidebar-open': isSidebarOpen }">
    <div class="row h-100">
      <div class="col-auto p-0 app-sidebar-col">
        <Sidebar @open-add-task-modal="handleOpenNewTaskModal" :toggleSidebar="toggleSidebar" />
      </div>

      <div class="col p-0 main-content-col">
        <div class="p-2 simple-toggle-button-container">
          <button class="btn btn-sidebar" @click="toggleSidebar">
            <i class="bi bi-layout-sidebar"></i>
          </button>
        </div>

        <main class="h-100">
          <div class="p-4 h-100-content">
            <router-view />
          </div>
        </main>

        <AddTaskModal
            ref="addTaskModalRef"
            :taskToEdit="currentTaskForEdit"
            @taskSaved="handleTaskSaved"
            @closed="handleModalClosed" />
      </div>
    </div>
  </div>

  <div
      class="overlay d-md-none"
      :class="{ 'show': isSidebarOpen }"
      @click="toggleSidebar"
  ></div>
</template>

<script setup>
import { ref, onMounted, nextTick, watchEffect } from 'vue';
import { useRouter } from 'vue-router';
import Sidebar from '@/components/Sidebar.vue';
import AddTaskModal from '@/components/AddTaskModal.vue';
import { useUserStore } from '@/stores/userStore';
import { useTaskStore } from '@/stores/taskStore';

const userStore = useUserStore();
const taskStore = useTaskStore();
const router = useRouter();

const isSidebarOpen = ref(true);
const addTaskModalRef = ref(null);
const currentTaskForEdit = ref(null);

const theme = ref(localStorage.getItem('theme') || 'light');

watchEffect(() => {
  if (theme.value === 'dark') {
    document.documentElement.classList.add('dark-mode');
  } else {
    document.documentElement.classList.remove('dark-mode');
  }
});

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;
};

const handleOpenNewTaskModal = () => {
  console.log('App.vue: Otrzymano żądanie otwarcia modala dla nowego zadania.');
  currentTaskForEdit.value = null;
  nextTick(() => {
    if (addTaskModalRef.value && typeof addTaskModalRef.value.openModalForNewTask === 'function') {
      addTaskModalRef.value.openModalForNewTask();
    } else {
      console.error('AddTaskModal ref is not available or openModalForNewTask method is missing.');
    }
  });
};

const handleOpenEditTaskModal = (task) => {
  console.log('App.vue: Otwieranie modala do edycji zadania:', task);
  currentTaskForEdit.value = task;
};

const handleTaskSaved = async () => {
  console.log('App.vue: Zadanie zostało zapisane.');
  currentTaskForEdit.value = null;
  if (userStore.isAuthenticated) {
    await taskStore.fetchMyTasks();
  } else {
    console.warn('App.vue: Użytkownik nie jest uwierzytelniony, nie można pobrać zadań po zapisie.');
  }
};

const handleModalClosed = () => {
  console.log('App.vue: Modal został zamknięty.');
  currentTaskForEdit.value = null;
  if (userStore.isAuthenticated) {
    taskStore.fetchMyTasks();
  }
};

onMounted(async () => {
  console.log('App.vue mounted, attempting to fetch current user...');
  await userStore.fetchCurrentUser();

  if (userStore.isAuthenticated) {
    console.log('App.vue: User is authenticated:', userStore.user.email);
    await taskStore.fetchMyTasks();

    if (router.currentRoute.value.name === 'loginpage' || router.currentRoute.value.name === 'register' || router.currentRoute.value.path === '/' || router.currentRoute.value.path === '/' || router.currentRoute.value.path === '/') {
      console.log('App.vue: Użytkownik zalogowany, przekierowuję na /inbox.');
      router.push({ name: 'inbox' });
    }

  } else {
    console.log('App.vue: User is NOT authenticated after fetch attempt.');
    if (router.currentRoute.value.meta.requiresAuth && router.currentRoute.value.name !== 'loginpage') {
      console.log('App.vue: Niezalogowany użytkownik na chronionej trasie, przekierowuję na /loginpage.');
      router.push({ name: 'loginpage' });
    }
  }

  if (window.innerWidth < 768) {
    isSidebarOpen.value = false;
  }
});
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

html,
body {
  font-family: 'Inter', sans-serif;
  height: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  overflow-y: hidden;
  background-color: var(--color-background);
  color: var(--color-text);
  transition: background-color 0.3s ease, color 0.3s ease;
}

#app {
  height: 100%;
}

:root {

  --color-background: #ffffff;
  --color-text: #212529;
  --color-background-soft: #ffffff;
  --color-background-mute: #ffffff;
  --color-border: #dee2e6;
  --color-heading: #212529;
  --color-text-soft: #6c757d;
  --color-text-mute: #adb5bd;
  --color-accent: #ffc107;
  --color-accent-dark: #d39e00;
  --color-danger: #dc3545;
  --color-warning: #ffc107;
  --color-success: #28a745;
  --color-shadow: rgba(0, 0, 0, 0.1);
  --color-shadow-light: rgba(0, 0, 0, 0.05);

  --sidebar-bg: #fefcf5;

  --sidebar-border: #e9ecef;
  --sidebar-text: #212529;
  --sidebar-text-mute: #6c757d;
  --sidebar-hover-bg: #e9ecef;
  --sidebar-active-bg: rgba(255, 193, 7, 0.7);
  --sidebar-active-text: #212529;
  --sidebar-accent-btn-bg: rgba(255, 212, 116, 0.35);
  --sidebar-accent-btn-text: #212529;
  --sidebar-accent-btn-hover-bg: rgba(255, 212, 116, 0.8);

  --btn-sidebar-color: #6c757d;
  --btn-sidebar-hover-bg: rgba(128, 128, 128, 0.1);
  --btn-sidebar-hover-color: #343a40;
  --overlay-bg: rgba(0, 0, 0, 0.5);
}

html.dark-mode {
  --color-background: #212529;
  --color-background-soft: #343a40;
  --color-background-mute: #495057;
  --color-border: #6c757d;
  --color-heading: #f8f9fa;
  --color-text: #f8f9fa;
  --color-text-soft: #ced4da;
  --color-text-mute: #adb5bd;
  --color-accent: #ffc107;
  --color-accent-dark: #d39e00;
  --color-danger: #dc3545;
  --color-warning: #ffc107;
  --color-success: #28a745;
  --color-shadow: rgba(0, 0, 0, 0.5);
  --color-shadow-light: rgba(0, 0, 0, 0.25);

  --sidebar-bg: #343a40;
  --sidebar-border: #495057;
  --sidebar-text: #f8f9fa;
  --sidebar-text-mute: #ced4da;
  --sidebar-hover-bg: #495057;
  --sidebar-active-bg: rgba(255, 193, 11, 0.65);
  --sidebar-active-text: #f8f9fa;
  --sidebar-accent-btn-bg: rgba(207, 160, 2, 0.68);
  --sidebar-accent-btn-text: #ffffff;
  --sidebar-accent-btn-hover-bg: rgba(211, 158, 0, 0.9);

  --btn-sidebar-color: #ced4da;
  --btn-sidebar-hover-bg: rgba(255, 255, 255, 0.1);
  --btn-sidebar-hover-color: #f8f9fa;
  --overlay-bg: rgba(0, 0, 0, 0.45);
}


.app-sidebar-col {
  background-color: var(--sidebar-bg);
  border-right: 1px solid var(--sidebar-border);
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.main-content-col {
  background-color: var(--color-background);
  color: var(--color-text);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.p-4.h-100-content {
  background-color: var(--color-background);
  color: var(--color-text);
  padding: 15px !important;

}

.btn-sidebar {
  border: none;
  background-color: transparent;
  font-size: 1.5rem;
  color: var(--btn-sidebar-color);
  transition: color 0.3s ease;
}

.btn-sidebar:hover {
  background-color: var(--btn-sidebar-hover-bg);
  color: var(--btn-sidebar-hover-color);
}

@media (max-width: 768px) {
  html,
  body,
  .container-fluid,
  .row {
    overflow-y: auto;
  }

  .app-sidebar-col {
    position: fixed;
    top: 0;
    bottom: 0;
    left: -280px;
    width: 280px;
    z-index: 1045;
    transition: left 0.3s ease-in-out;
    margin-left: 0;
    overflow-y: auto;
    overflow-x: hidden;
  }

  .container-fluid.sidebar-open .app-sidebar-col {
    left: 0;
  }

  .main-content-col {
    margin-left: 0;
    padding-right: 15px;
  }

  .simple-toggle-button-container {
    display: flex;
    align-items: center;
    padding: 10px 15px;
  }

  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--overlay-bg);
    z-index: 1040;
    display: none;
    cursor: pointer;
  }

  .overlay.show {
    display: block;
  }
}

@media (min-width: 768px) {
  .app-sidebar-col {
    display: block !important;
    position: static;
    left: auto;
    z-index: auto;
    transition: width 0.3s ease-in-out;
  }

  .container-fluid.sidebar-open .app-sidebar-col {
    width: 280px;
  }
  .container-fluid.sidebar-open .main-content-col {
    transition: margin-left 0.3s ease-in-out;
  }

  .container-fluid:not(.sidebar-open) .app-sidebar-col {
    width: 0;
    overflow: hidden;
  }
  .container-fluid:not(.sidebar-open) .main-content-col {
    margin-left: 0;
    transition: margin-left 0.3s ease-in-out;
  }

  .simple-toggle-button-container {
    display: flex;
  }

  .overlay {
    display: none;
  }
}

main {
  padding-top: 0;
}


.main-content-col {
  display: flex;
  flex-direction: column; /* Układamy elementy (przycisk i treść) jeden pod drugim */
  height: 100vh;         /* Kolumna zajmuje 100% wysokości okna przeglądarki */
}

/* 2. Upewniamy się, że kontener z przyciskiem do chowania menu nie rozciąga się */
.main-content-col > .simple-toggle-button-container {
  flex-shrink: 0; /* Zapobiega kurczeniu się tego elementu */
}

/* 3. Mówimy głównemu obszarowi <main>, aby rósł i pozwalał się przewijać */
.main-content-col > main {
  flex-grow: 1;       /* Pozwala temu elementowi rosnąć i wypełnić całą dostępną przestrzeń */
  overflow-y: auto;   /* Włącza pionowe przewijanie, gdy treść się nie mieści */
}

</style>
