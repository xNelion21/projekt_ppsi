<script setup>
import {computed, defineEmits, defineProps} from 'vue';
import {useTaskStore} from "@/stores/taskStore.js";
import {useI18n} from "vue-i18n";

const { locale, t } = useI18n();

const props = defineProps({
  task: {
    type: Object,
    required: true,
    validator: (value) => {
      return typeof value.id === 'number' &&
          typeof value.text === 'string' &&
          typeof value.completed === 'boolean';
    }
  }
});

const emit = defineEmits(['toggleDone', 'deleteTask']);

const taskStore = useTaskStore()

const handleToggleComplete = async (task) => {
  if (task && task.id) {
    await taskStore.toggleTaskCompletion(task.id);
  }
};

const handleDeleteTask = () => {
  const isConfirmed = confirm(t('tasks.confirmDelete', { taskText: props.task.text }));
  if (isConfirmed) {
    emit('deleteTask', props.task.id);
    console.log(`Potwierdzono usunięcie zadania: ${props.task.id}`);
  } else {
    console.log(`Anulowano usunięcie zadania: ${props.task.id}`);
  }
};


const parseDateString = (dateString) => {
  if (!dateString || typeof dateString !== 'string' || dateString.trim() === '') {
    return null;
  }
  const parts = dateString.split('-');
  if (parts.length !== 3 || parts.some(isNaN)) {
    console.warn("TodoItem.vue (parseDateString): Invalid date string format (not YYYY-MM-DD):", dateString);
    return null;
  }
  const [year, monthStr, dayStr] = parts;
  const dateObj = new Date(Number(year), Number(monthStr) - 1, Number(dayStr));
  if (isNaN(dateObj.getTime()) || dateObj.getFullYear() !== Number(year) || (dateObj.getMonth() + 1) !== Number(monthStr) || dateObj.getDate() !== Number(dayStr)) {
    console.warn("TodoItem.vue (parseDateString): Invalid Date object created from parts:", year, monthStr, dayStr, "from string:", dateString);
    return null;
  }
  return dateObj;
};

const isDueDatePast = computed(() => {
  if (!props.task.dueDate || props.task.completed) {
    return false;
  }

  const dueDateObj = parseDateString(props.task.dueDate);
  if (!dueDateObj) return false;

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  return dueDateObj < today;
});

const isDueDateToday = computed(() => {
  if (!props.task.dueDate || props.task.completed) {
    return false;
  }

  const dueDateObj = parseDateString(props.task.dueDate);
  if (!dueDateObj) return false;

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  return dueDateObj.getTime() === today.getTime();
});


const formattedDueDate = computed(() => {
  if (!props.task.dueDate || props.task.dueDate.trim() === '') {
    return t('tasks.noDueDate');
  }

  const dateObj = parseDateString(props.task.dueDate);

  if (!dateObj) {
    return t('tasks.invalidDateFormat');
  }

  const options = { day: 'numeric', month: 'long' };
  try {
    return new Intl.DateTimeFormat(locale.value, options).format(dateObj);
  } catch (e) {
    console.error("TodoItem.vue (formattedDueDate): Error formatting date with Intl.DateTimeFormat:", dateObj, e);
    return dateObj.toLocaleDateString(locale.value);
  }
});

</script>

<template>
  <li class="todo-item">
    <div class="todo-item-left">
      <input
          class="form-check-input todo-checkbox me-2"
          type="checkbox"
          :id="'task-' + props.task.id"
          :checked="props.task.completed"
      @change="handleToggleComplete(props.task)"
      />

      <div class="todo-item-content flex-grow-1">
        <label
            :for="'task-' + props.task.id"
            class="todo-text mb-0"
            :class="{ 'todo-done': props.task.completed }" >
          {{ props.task.text }}
        </label>

        <div class="todo-meta small mt-1">
          <span :class="{
            'text-danger': isDueDatePast,
            'text-warning': isDueDateToday && !isDueDatePast,
            'text-muted': !isDueDatePast && !isDueDateToday
          }">
            <i class="bi bi-calendar me-1"></i> {{ formattedDueDate }}
          </span>
        </div>
      </div>
    </div>

    <div class="todo-item-right">
      <button @click="handleDeleteTask" class="btn btn-sm btn-outline-danger todo-delete-btn">
        {{ t('tasks.deleteBtn') }} <i class="bi bi-trash"></i>
      </button>
    </div>

  </li>
</template>

<style scoped>

.todo-item {
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 12px 15px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background-color 0.2s ease, border-color 0.2s ease;
}

.todo-item:hover {
  background-color: var(--color-background-mute);
}

.todo-item-left {
  display: flex;
  align-items: flex-start;
  flex-grow: 1;
  margin-right: 15px;
}

.todo-checkbox {
  width: 1.2em;
  height: 1.2em;
  border-radius: 50%;
  border: 2px solid var(--color-text-mute);
  cursor: pointer;
  flex-shrink: 0;
  margin-top: 0.25em;
  appearance: none;
  -webkit-appearance: none;
  position: relative;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.todo-checkbox::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0.6em;
  height: 0.6em;
  background-color: var(--color-success);
  border-radius: 50%;
  transform: translate(-50%, -50%) scale(0);
  transition: transform 0.2s ease-in-out;
}

.todo-checkbox:checked {
  border-color: var(--color-success);
  background-color: var(--color-success);
}

.todo-checkbox:checked::before {
  transform: translate(-50%, -50%) scale(1);
  background-color: var(--color-background-soft);
}


.todo-text {
  font-size: 1rem;
  color: var(--color-text);
  flex-grow: 1;
  line-height: 1.4;
}

.todo-text.todo-done {
  text-decoration: line-through;
  color: var(--color-text-mute);
}

.todo-meta {
  font-size: 0.85rem;
  color: var(--color-text-mute);
  display: flex;
  align-items: center;
  margin-top: 4px;
}

.todo-meta .text-danger { color: var(--color-danger) !important; }
.todo-meta .text-warning { color: var(--color-warning) !important; }
.todo-meta .text-muted { color: var(--color-text-mute) !important; }

.todo-item-right {
  flex-shrink: 0;
}

.todo-delete-btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
}

</style>