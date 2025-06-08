<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useTaskStore } from '@/stores/taskStore';
import { useUserStore } from '@/stores/userStore';
import { BModal } from 'bootstrap-vue-next';
import { useI18n } from "vue-i18n";

const { t } = useI18n();

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
  dueDate: '',
  assignedUserIds: [],
  completed: false,
});

const modalShow = ref(false);

const resetForm = () => {
  taskData.value.id = null;
  taskData.value.title = '';
  taskData.value.description = '';
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

watch(() => props.taskToEdit, (currentTask) => {
  if (currentTask && currentTask.id != null) {
    taskData.value.id = currentTask.id;
    taskData.value.title = currentTask.title || '';
    taskData.value.description = currentTask.description || '';
    taskData.value.dueDate = currentTask.dueDate ? currentTask.dueDate.split('T')[0] : '';
    taskData.value.assignedUserIds = currentTask.assignedUsers ? currentTask.assignedUsers.map(u => u.id).filter(id => id != null) : [];
    taskData.value.completed = currentTask.completed || false;
    modalShow.value = true;
  }
}, { deep: true });

watch(modalShow, (newValue) => {
  if (newValue === true && (!props.taskToEdit || props.taskToEdit.id == null)) {
    prepareFormForNewTask();
  }
});

const saveTask = async () => {
  if (!taskData.value.title || taskData.value.title.trim() === '') {
    alert(t('tasks.titleRequiredAlert'));
    return;
  }

  const payload = {
    title: taskData.value.title.trim(),
    description: taskData.value.description?.trim() || '',
    dueDate: taskData.value.dueDate || null,
    assignedUserIds: taskData.value.assignedUserIds || [],
  };

  let success = false;
  if (taskData.value.id != null) {
    payload.completed = taskData.value.completed;
    success = await taskStore.editTask(taskData.value.id, payload);
  } else {
    success = await taskStore.addTask(payload);
  }

  if (success) {
    modalShow.value = false;
    emit('taskSaved');
  } else {
    alert(taskStore.error || t('tasks.saveError'));
  }
};

const handleModalHidden = () => {
  resetForm();
  emit('closed');
};

const closeModal = () => {
  modalShow.value = false;
};

const openModalForNewTask = () => {
  prepareFormForNewTask();
  modalShow.value = true;
};

onMounted(async () => {
  if (!taskStore.allUsers || taskStore.allUsers.length === 0) {
    await taskStore.fetchAllUsers();
  }
});

const modalTitle = computed(() => (props.taskToEdit && props.taskToEdit.id != null) ? t('tasks.editBtn') : t('tasks.addTask'));

defineExpose({ openModalForNewTask, modalShow });
</script>

<template>
  <BModal
      hide-footer
      v-model="modalShow"
      :title="modalTitle"
      @hidden="handleModalHidden"
      size="lg"
      id="addTaskModalVueControlled"
  >
    <form @submit.prevent="saveTask" novalidate>
      <div class="mb-3">
        <label for="taskTitle" class="form-label">{{ t('tasks.titleLabel') }} <span class="text-danger">*</span></label>
        <input type="text" class="form-control" id="taskTitle" v-model="taskData.title" required />
      </div>

      <div class="mb-3">
        <label for="taskDescription" class="form-label">{{ t('tasks.descriptionLabel') }}</label>
        <textarea class="form-control" id="taskDescription" rows="2" v-model="taskData.description"></textarea>
      </div>

      <div class="row">
        <div class="col-md-6 mb-3">
          <label for="taskDueDate" class="form-label">{{ t('tasks.dueDateLabel') }}</label>
          <input type="date" class="form-control" id="taskDueDate" v-model="taskData.dueDate" />
        </div>
      </div>

      <div class="mb-3">
        <label for="taskAssignedUsers" class="form-label">{{ t('tasks.assignUsersLabel') }}</label>
        <select multiple class="form-select" id="taskAssignedUsers" v-model="taskData.assignedUserIds" size="3">
          <option v-if="taskStore.isLoadingUsers" disabled value="">
            {{ t('tasks.loadingUsers') }}
          </option>
          <option v-else-if="!taskStore.allUsers || taskStore.allUsers.length === 0" disabled value="">
            {{ t('tasks.noUsersAvailable') }}
          </option>
          <option v-for="user in taskStore.allUsers" :key="user.id" :value="user.id">
            {{ user.nickname || user.email }} (ID: {{ user.id }})
          </option>
        </select>
        <small class="form-text text-muted">{{ t('tasks.multipleUsersHint') }}</small>
      </div>

      <div v-if="taskData.id != null" class="mb-3 form-check">
        <input type="checkbox" class="form-check-input" id="taskCompleted" v-model="taskData.completed" />
        <label class="form-check-label" for="taskCompleted">{{ t('tasks.completedLabel') }}</label>
      </div>

      <hr />
      <div class="d-flex justify-content-end gap-2">
        <button type="button" class="btn btn-secondary" @click="closeModal">{{ t('tasks.cancelBtn') }}</button>
        <button type="submit" class="btn btn-primary">
          {{ taskData.id != null ? t('tasks.saveChangesBtn') : t('tasks.addBtn') }}
        </button>
      </div>
    </form>
  </BModal>
</template>

<style scoped>
.modal-content {
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.12);
  border: none;
  background-color: var(--color-background-soft);
}

.modal-header {
  border-bottom: 1px solid var(--color-border);
  padding: 1.25rem 1.75rem;
  font-weight: 700;
  font-size: 1.6rem;
  color: var(--color-accent-dark);
}

.modal-body {
  padding: 1.75rem 1.75rem 2rem;
}

.form-label {
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 0.5rem;
}

input.form-control,
textarea.form-control,
select.form-select {
  border-radius: 8px;
  border: 1.8px solid #ccc;
  transition: border-color 0.25s ease;
}

input.form-control:focus,
textarea.form-control:focus,
select.form-select:focus {
  border-color: var(--color-accent);
  box-shadow: 0 0 6px var(--color-accent);
  outline: none;
}

.text-danger {
  font-weight: 700;
  margin-left: 3px;
}

button.btn-primary {
  background-color: var(--color-accent);
  border-color: var(--color-accent);
  font-weight: 600;
  padding: 0.55rem 1.4rem;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

button.btn-primary:hover {
  background-color: var(--color-accent-dark);
  border-color: var(--color-accent-dark);
}

button.btn-secondary {
  background-color: var(--color-background-soft);
  border: 2px solid var(--color-border);
  color: var(--color-text-mute);
  font-weight: 600;
  padding: 0.55rem 1.4rem;
  border-radius: 8px;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

button.btn-secondary:hover {
  background-color: var(--color-border);
  border-color: var(--color-accent);
  color: var(--color-accent);
}

.d-flex.justify-content-end.gap-2 {
  gap: 1rem;
  margin-top: 1.5rem;
}

select.form-select[multiple] {
  height: 120px;
  border-radius: 8px;
}

.form-check-label {
  font-weight: 600;
  color: var(--color-text);
}

hr {
  border-color: var(--color-border);
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
}
.modal-footer {
  display: none !important;
}

</style>
