<script setup>
import { ref, onMounted } from 'vue';
import { BModal } from 'bootstrap-vue-next';

const emit = defineEmits(['taskAdded']);

const newTaskText = ref('');
const newDueDate = ref('');
const modalShow = ref(false);

const resetForm = () => {
  newTaskText.value = '';
  newDueDate.value = '';
};

const saveTask = () => {
  if (newTaskText.value.trim() === '') {
    alert('Nazwa zadania nie może być pusta!');
    return;
  }

  emit('taskAdded', {
    text: newTaskText.value.trim(),
    dueDate: newDueDate.value || null,
  });
  modalShow.value = false;
  resetForm();
};

onMounted(() => {
  if (newDueDate.value === '') {
    const today = new Date();
    const nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
    const year = nextWeek.getFullYear();
    const month = (nextWeek.getMonth() + 1).toString().padStart(2, '0');
    const day = nextWeek.getDate().toString().padStart(2, '0');
    newDueDate.value = `${year}-${month}-${day}`;
  }
});
</script>

<template>
  <BModal
      id="addTaskModal"
      v-model="modalShow"
      @hidden="resetForm"
      @shown="() => document.getElementById('taskTextInput').focus()"
      title="Dodaj Nowe Zadanie"
  >
    <div class="modal-body">
      <div class="mb-3">
        <label for="taskTextInput" class="form-label">
          Nazwa Zadania
        </label>
        <input
            type="text"
            class="form-control"
            id="taskTextInput"
            placeholder="Zrobić projekt na studia"
            v-model="newTaskText"
            @keyup.enter="saveTask"
        />
      </div>

      <div class="mb-3">
        <label for="taskDueDatePicker" class="form-label">
          Termin
        </label>
        <input
            type="date"
            class="form-control"
            id="taskDueDatePicker"
            v-model="newDueDate"
        />
      </div>
    </div>
    <template #modal-footer>
      <button
          type="button"
          class="btn btn-secondary"
          @click="modalShow = false"
      >
        Anuluj
      </button>
      <button type="button" class="btn btn-primary" @click="saveTask">
        Zapisz Zadanie
      </button>
    </template>
  </BModal>
</template>

<style scoped>
.modal-content {
  border-radius: 10px;
  border: none;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.modal-header {
  background-color: #fefcf5;
  border-bottom: 1px solid #eee;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}

.modal-title {
  color: #333;
  font-weight: bold;
}

.modal-body {
  padding: 20px;
  background-color: #fff;
}

.modal-footer {
  border-top: 1px solid #eee;
  background-color: #fff;
  border-bottom-left-radius: 10px;
  border-bottom-right-radius: 10px;
  padding: 15px 20px;
}

.form-label {
  font-weight: bold;
  color: #555;
  margin-bottom: 5px;
}

.form-control {
  border-radius: 5px;
  border: 1px solid #ccc;
  padding: 10px 12px;
}

.form-control:focus {
  border-color: #ffad00;
  box-shadow: 0 0 0 0.25rem rgba(255, 173, 0, 0.25);
}

.modal-footer .btn {
  padding: 8px 15px;
  border-radius: 5px;
  font-weight: bold;
}

.modal-footer .btn-primary {
  background-color: #ffad00;
  border-color: #ffad00;
  color: white;
}

.modal-footer .btn-primary:hover {
  background-color: #ff9900;
  border-color: #ff9900;
}

.modal-footer .btn-secondary {
  background-color: #e9ecef;
  border-color: #e9ecef;
  color: #333;
}

.modal-footer .btn-secondary:hover {
  background-color: #dee2e6;
  border-color: #dee2e6;
}
</style>
