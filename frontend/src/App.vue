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
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router'; // Dodaj, jeśli potrzebujesz routera programatycznie
import Sidebar from '@/components/Sidebar.vue';
import AddTaskModal from '@/components/AddTaskModal.vue';
import { useUserStore } from '@/stores/userStore';
import { useTaskStore } from '@/stores/taskStore';

const userStore = useUserStore();
const taskStore = useTaskStore();
const router = useRouter(); // Zainicjuj, jeśli będziesz używać

const isSidebarOpen = ref(true);
const addTaskModalRef = ref(null);
const currentTaskForEdit = ref(null);

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

// Ta metoda byłaby wywoływana, gdybyś chciał edytować zadanie np. z listy
const handleOpenEditTaskModal = (task) => {
  console.log('App.vue: Otwieranie modala do edycji zadania:', task);
  currentTaskForEdit.value = task;
  // Modal powinien sam się otworzyć dzięki watch(() => props.taskToEdit, ...) w AddTaskModal.vue
  // Jeśli nie, można by dodać:
  // nextTick(() => {
  //   if (addTaskModalRef.value) {
  //     addTaskModalRef.value.modalShow = true; // Zakładając, że modalShow jest nadal exposed
  //   }
  // });
};

const handleTaskSaved = async () => {
  console.log('App.vue: Zadanie zostało zapisane.');
  currentTaskForEdit.value = null; // Wyczyść po zapisie
  // Odśwież listę zadań po zapisaniu nowego lub edycji istniejącego
  // Sprawdź, czy użytkownik jest nadal uwierzytelniony, zanim pobierzesz zadania
  if (userStore.isAuthenticated) {
    await taskStore.fetchMyTasks();
  } else {
    console.warn('App.vue: Użytkownik nie jest uwierzytelniony, nie można pobrać zadań po zapisie.');
    // Możesz rozważyć przekierowanie do logowania, jeśli sesja wygasła
    // router.push({ name: 'login' }); // Upewnij się, że masz zdefiniowaną ścieżkę 'login'
  }
};

const handleModalClosed = () => {
  console.log('App.vue: Modal został zamknięty.');
  currentTaskForEdit.value = null;
};

onMounted(async () => {
  console.log('App.vue mounted, attempting to fetch current user...');
  await userStore.fetchCurrentUser(); // Poczekaj na zakończenie
  if (userStore.isAuthenticated) {
    console.log('App.vue: User is authenticated:', userStore.user.email);
    await taskStore.fetchMyTasks(); // Pobierz zadania tylko jeśli użytkownik jest zalogowany
  } else {
    console.log('App.vue: User is NOT authenticated after fetch attempt.');
    // Możliwe przekierowanie na stronę logowania, jeśli jest to wymagane
    // Przykład: if (router.currentRoute.value.meta.requiresAuth) { router.push({ name: 'LoginPage' }); }
  }

  if (window.innerWidth < 768) {
    isSidebarOpen.value = false;
  }
});
</script>

<style>
/* Twoje style globalne i dla App.vue - pozostają bez zmian z poprzedniej wersji */
html,
body,
#app,
.container-fluid,
.row {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  overflow-y: hidden;
}

.app-sidebar-col {
  width: 280px;
  flex-shrink: 0;
  left: auto;
  transition: width 0.3s ease-in-out, margin-left 0.3s ease-in-out;
  z-index: auto;
  overflow: hidden;
  margin-left: 0;
  height: 100%;
}

.main-content-col {
  margin-left: 0;
  transition: margin-left 0.3s ease-in-out;
  padding-top: 0;
  padding-right: 15px;
  height: 100%;
  overflow-y: auto;
}
.main-content-col .h-100-content {
  /* min-height: 100%; */
}


.btn-sidebar {
  border: none;
  background-color: transparent;
  font-size: 1.5rem;
  color: #6c757d;
}

.btn-sidebar:hover {
  background-color: rgba(128, 128, 128, 0.1);
  color: #343a40;
}

@media (max-width: 767.98px) {
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
    background-color: rgba(0, 0, 0, 0.5);
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
    overflow-y: auto;
  }

  .container-fluid:not(.sidebar-open) .app-sidebar-col {
    width: 80px; /* Możesz ustawić na 0, jeśli chcesz całkowicie schować */
  }
  .container-fluid.sidebar-open .app-sidebar-col {
    width: 280px;
  }

  .main-content-col {
    margin-left: 0;
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
</style>