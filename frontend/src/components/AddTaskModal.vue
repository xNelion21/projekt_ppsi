<template>
  <BModal
      v-model="modalShow"
      :title="modalTitle"
      @hidden="handleModalHidden"
      size="lg"
      hide-footer
      id="addTaskModalVueControlled"
  >
    <form @submit.prevent="saveTask" novalidate>
      <div class="mb-3">
        <label for="taskTitle" class="form-label">Tytuł zadania <span class="text-danger">*</span></label>
        <input type="text" class="form-control" id="taskTitle" v-model="taskData.title" required />
      </div>

      <div class="mb-3">
        <label for="taskDescription" class="form-label">Opis</label>
        <textarea class="form-control" id="taskDescription" rows="2" v-model="taskData.description"></textarea>
      </div>

      <div class="mb-3">
        <label for="taskText" class="form-label">Dodatkowa treść (opcjonalnie)</label>
        <textarea class="form-control" id="taskText" rows="4" v-model="taskData.text"></textarea>
      </div>

      <div class="row">
        <div class="col-md-6 mb-3">
          <label for="taskDueDate" class="form-label">Termin wykonania</label>
          <input type="date" class="form-control" id="taskDueDate" v-model="taskData.dueDate" />
        </div>
      </div>

      <div class="mb-3">
        <label for="taskAssignedUsers" class="form-label">Przypisz użytkowników</label>
        <select multiple class="form-select" id="taskAssignedUsers" v-model="taskData.assignedUserIds" size="3">
          <option v-if="taskStore.isLoadingUsers" disabled value="">
            Ładowanie użytkowników...
          </option>
          <option v-else-if="!taskStore.allUsers || taskStore.allUsers.length === 0" disabled value="">
            Brak użytkowników do wyświetlenia.
          </option>
          <option v-for="user in taskStore.allUsers" :key="user.id" :value="user.id">
            {{ user.nickname || user.email }} (ID: {{ user.id }})
          </option>
        </select>
        <small class="form-text text-muted">Przytrzymaj Ctrl (lub Cmd na Mac) aby zaznaczyć/odznaczyć wielu użytkowników.</small>
      </div>

      <div v-if="taskData.id != null" class="mb-3 form-check">
        <input type="checkbox" class="form-check-input" id="taskCompleted" v-model="taskData.completed" />
        <label class="form-check-label" for="taskCompleted">Ukończone</label>
      </div>

      <hr />
      <div class="d-flex justify-content-end gap-2">
        <button type="button" class="btn btn-secondary" @click="closeModal">Anuluj</button>
        <button type="submit" class="btn btn-primary">
          {{ (taskData.id != null) ? 'Zapisz zmiany' : 'Dodaj zadanie' }}
        </button>
      </div>
    </form>
  </BModal>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useTaskStore } from '@/stores/taskStore';
import { useUserStore } from '@/stores/userStore';
import { BModal } from 'bootstrap-vue-next';

const props = defineProps({
  taskToEdit: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['taskSaved', 'closed']);

const taskStore = useTaskStore();
const userStore = useUserStore();

const taskData = ref({
  id: null,
  title: '',
  description: '',
  text: '',
  dueDate: '',
  assignedUserIds: [],
  completed: false,
});

const modalShow = ref(false); // Wewnętrzny stan kontrolujący widoczność

const resetForm = () => {
  console.log("AddTaskModal: resetForm called");
  taskData.value.id = null;
  taskData.value.title = '';
  taskData.value.description = '';
  taskData.value.text = '';
  taskData.value.dueDate = '';
  taskData.value.assignedUserIds = [];
  taskData.value.completed = false;
};

const setDefaultDueDate = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = (today.getMonth() + 1).toString().padStart(2, '0');
  const day = today.getDate().toString().padStart(2, '0');
  taskData.value.dueDate = `${year}-${month}-${day}`;
};

const prepareFormForNewTask = () => {
  console.log("AddTaskModal: prepareFormForNewTask called");
  resetForm();
  setDefaultDueDate();
  if (userStore.user && userStore.user.id) {
    if (!taskData.value.assignedUserIds.includes(userStore.user.id)) {
      taskData.value.assignedUserIds = [userStore.user.id];
    }
  } else {
    taskData.value.assignedUserIds = [];
  }
};

// Obserwuj zmiany w props.taskToEdit (gdy modal jest otwierany do edycji przez rodzica)
watch(() => props.taskToEdit, (currentTask) => {
  console.log("AddTaskModal: props.taskToEdit watcher triggered. currentTask:", currentTask);
  if (currentTask && currentTask.id != null) {
    console.log("AddTaskModal: Preparing form for editing task ID:", currentTask.id);
    taskData.value.id = currentTask.id;
    taskData.value.title = currentTask.title || '';
    taskData.value.description = currentTask.description || '';
    taskData.value.text = currentTask.text || '';
    taskData.value.dueDate = currentTask.dueDate ? currentTask.dueDate.split('T')[0] : '';
    taskData.value.assignedUserIds = currentTask.assignedUsers ? currentTask.assignedUsers.map(u => u.id).filter(id => id != null) : [];
    taskData.value.completed = currentTask.completed || false;
    modalShow.value = true; // Otwórz modal, gdy przekazano zadanie do edycji
  } else {
    // Jeśli props.taskToEdit jest null (np. po zamknięciu edycji lub przy otwieraniu dla nowego)
    // a modal jest już widoczny (co nie powinno się zdarzyć jeśli logika otwierania jest w rodzicu),
    // lub jeśli modalShow.value staje się true (dla nowego zadania), to resetujemy.
    // Ta logika jest teraz bardziej obsługiwana przez openModalForNewTask i watch(modalShow)
    if (modalShow.value && !currentTask) {
      // Jeśli modal jest już otwarty (modalShow.value === true) i taskToEdit staje się null,
      // to znaczy, że rodzic chce "przełączyć" na nowy task, lub zamknąć.
      // Jeśli rodzic chce zamknąć, to powinien ustawić modalShow na false.
      // Jeśli rodzic chce nowy task, wywoła openModalForNewTask.
      // console.log("AddTaskModal: taskToEdit became null while modal was open. Preparing for new task.");
      // prepareFormForNewTask(); // Może być redundantne, jeśli openModalForNewTask jest używane
    }
  }
}, { deep: true }); // immediate: true usunięte, aby uniknąć uruchomienia przed onMounted i załadowaniem userStore


// Obserwuj zmiany w modalShow (gdy modal jest programatycznie otwierany/zamykany)
watch(modalShow, (newValue, oldValue) => {
  console.log(`AddTaskModal: modalShow watcher. New: ${newValue}, Old: ${oldValue}`);
  if (newValue === true && (!props.taskToEdit || props.taskToEdit.id == null)) {
    // Modal jest otwierany (newValue === true) ORAZ nie jest to edycja istniejącego zadania
    console.log("AddTaskModal: modalShow became true for a new task. Preparing form.");
    prepareFormForNewTask();
  }
  // Jeśli modal jest zamykany (newValue === false), @hidden event zajmie się resztą.
});

const saveTask = async () => {
  console.log("AddTaskModal: saveTask called");
  if (!taskData.value.title || taskData.value.title.trim() === '') {
    alert('Tytuł zadania nie może być pusty!');
    return;
  }

  const payload = {
    title: taskData.value.title.trim(),
    description: taskData.value.description?.trim() || '',
    text: taskData.value.text?.trim() || '',
    dueDate: taskData.value.dueDate || null,
    assignedUserIds: taskData.value.assignedUserIds || [],
  };

  let success = false;
  if (taskData.value.id != null) {
    payload.completed = taskData.value.completed;
    success = await taskStore.updateTask(taskData.value.id, payload);
  } else {
    success = await taskStore.addTask(payload);
  }

  if (success) {
    modalShow.value = false; // To wyzwoli zdarzenie @hidden
    emit('taskSaved');
  } else {
    alert(taskStore.error || 'Wystąpił błąd podczas zapisywania zadania.');
  }
};

// Handler dla zdarzenia @hidden komponentu BModal
const handleModalHidden = () => {
  console.log("AddTaskModal: @hidden event triggered. Resetting form and emitting 'closed'.");
  // modalShow.value powinien już być false w tym momencie dzięki v-model
  resetForm();
  emit('closed');
};

// Metoda do programatycznego zamknięcia modala (np. przez przycisk Anuluj)
const closeModal = () => {
  console.log("AddTaskModal: closeModal called by button. Setting modalShow to false.");
  modalShow.value = false; // To wyzwoli zdarzenie @hidden
};

const openModalForNewTask = () => {
  console.log("AddTaskModal: openModalForNewTask (exposed method) called.");
  // props.taskToEdit powinien być już null (ustawiony przez rodzica)
  prepareFormForNewTask(); // Przygotuj formularz na wszelki wypadek
  modalShow.value = true;
};

onMounted(async () => {
  console.log("AddTaskModal: onMounted");
  // Upewnij się, że userStore jest załadowany przed próbą dostępu do userStore.user.id w prepareForm
  // To jest istotne, jeśli modal jest renderowany od początku.
  if (!userStore.isAuthenticated && !userStore.loading) {
    // Można poczekać na fetchCurrentUser z App.vue lub wywołać tutaj, jeśli App.vue tego nie robi globalnie
    // await userStore.fetchCurrentUser(); // Rozważ, czy to potrzebne tutaj, App.vue już to robi
  }

  if (!taskStore.allUsers || taskStore.allUsers.length === 0) {
    console.log("AddTaskModal: Fetching all users.");
    await taskStore.fetchAllUsers();
  }
});

const modalTitle = computed(() => (props.taskToEdit && props.taskToEdit.id != null) ? 'Edytuj Zadanie' : 'Dodaj Nowe Zadanie');

defineExpose({ openModalForNewTask, modalShow }); // Udostępnij modalShow, aby rodzic mógł sprawdzić stan, jeśli potrzebuje

</script>

<style scoped>
/* Twoje style CSS pozostają bez zmian */
.form-label {
  font-weight: bold;
  color: #555;
  margin-bottom: 0.3rem;
}

.text-danger {
  color: #dc3545 !important;
}
</style>