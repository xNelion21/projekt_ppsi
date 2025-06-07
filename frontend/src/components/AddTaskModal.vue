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
  console.log("AddTaskModal: resetForm called");
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

watch(() => props.taskToEdit, (currentTask) => {
  console.log("AddTaskModal: props.taskToEdit watcher triggered. currentTask:", currentTask);
  if (currentTask && currentTask.id != null) {
    console.log("AddTaskModal: Preparing form for editing task ID:", currentTask.id);
    taskData.value.id = currentTask.id;
    taskData.value.title = currentTask.title || '';
    taskData.value.description = currentTask.description || '';
    taskData.value.dueDate = currentTask.dueDate ? currentTask.dueDate.split('T')[0] : '';
    taskData.value.assignedUserIds = currentTask.assignedUsers ? currentTask.assignedUsers.map(u => u.id).filter(id => id != null) : [];
    taskData.value.completed = currentTask.completed || false;
    modalShow.value = true;
  } else {
  }
}, { deep: true });

watch(modalShow, (newValue, oldValue) => {
  console.log(`AddTaskModal: modalShow watcher. New: ${newValue}, Old: ${oldValue}`);
  if (newValue === true && (!props.taskToEdit || props.taskToEdit.id == null)) {
    console.log("AddTaskModal: modalShow became true for a new task. Preparing form.");
    prepareFormForNewTask();
  }
});

const saveTask = async () => {
  console.log("AddTaskModal: saveTask called");
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
    success = await taskStore.updateTask(taskData.value.id, payload);
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
  console.log("AddTaskModal: @hidden event triggered. Resetting form and emitting 'closed'.");
  resetForm();
  emit('closed');
};

const closeModal = () => {
  console.log("AddTaskModal: closeModal called by button. Setting modalShow to false.");
  modalShow.value = false;
};

const openModalForNewTask = () => {
  console.log("AddTaskModal: openModalForNewTask (exposed method) called.");
  prepareFormForNewTask();
  modalShow.value = true;
};

onMounted(async () => {
  console.log("AddTaskModal: onMounted");
  if (!userStore.isAuthenticated && !userStore.loading) {
    // Można poczekać na fetchCurrentUser z App.vue lub wywołać tutaj, jeśli App.vue tego nie robi globalnie
    // await userStore.fetchCurrentUser(); // Rozważ, czy to potrzebne tutaj, App.vue już to robi
  }

  if (!taskStore.allUsers || taskStore.allUsers.length === 0) {
    console.log("AddTaskModal: Fetching all users.");
    await taskStore.fetchAllUsers();
  }
});

const modalTitle = computed(() => (props.taskToEdit && props.taskToEdit.id != null) ? t('tasks.editTaskTitle') : t('tasks.addTask')); // Tłumaczenie tytułu modala

defineExpose({ openModalForNewTask, modalShow }); // Udostępnij modalShow, aby rodzic mógł sprawdzić stan, jeśli potrzebuje

</script>

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
          {{ (taskData.id != null) ? t('tasks.saveChangesBtn') : t('tasks.addBtn') }}
        </button>
      </div>
    </form>
  </BModal>
</template>

<style scoped>
.form-label {
  font-weight: bold;
  color: #555;
  margin-bottom: 0.3rem;
}

.text-danger {
  color: #dc3545 !important;
}
</style>
