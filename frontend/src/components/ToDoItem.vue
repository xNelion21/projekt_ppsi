<script setup>
import {computed, defineEmits, defineProps} from 'vue';
import {getFormattedTodayDate, useTaskStore} from "@/stores/taskStore.js";

const props = defineProps({
  task: {
    type: Object,
    required: true,
    validator: (value) => {
      return typeof value.id === 'number' &&
          typeof value.text === 'string' &&
          typeof value.done === 'boolean';
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
  const isConfirmed = confirm('Czy na pewno chcesz usunąć zadanie: "' + props.task.text + '"?');
  if (isConfirmed) {
    emit('deleteTask', props.task.id);
    console.log(`Potwierdzono usunięcie zadania: ${props.task.id}`);
  } else {
    console.log(`Anulowano usunięcie zadania: ${props.task.id}`);
  }
};

const isDueDatePast = computed(() => {

  if (!props.task.dueDate || props.task.dueDate.trim() === '') {
    return false;
  }

  const dueDateObj = new Date(props.task.dueDate);

  if (isNaN(dueDateObj.getTime())) {
    console.warn("TodoItem.vue (isDueDatePast): Invalid Date object created from:", props.task.dueDate);
    return false;
  }

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  return dueDateObj < today;
});

const formattedDueDate = computed(() => {

  if (!props.task.dueDate || props.task.dueDate.trim() === '') {
    return 'Brak terminu';
  }

  const parts = props.task.dueDate.split('-');

  if (parts.length !== 3 || parts.some(isNaN)) {
    console.warn("TodoItem.vue (formattedDueDate): Invalid date string format (not YYYY-MM-DD):", props.task.dueDate);
    return 'Błędny format daty';
  }

  const [year, monthStr, dayStr] = parts;
  const yearNum = Number(year);
  const monthNum = Number(monthStr);
  const dayNum = Number(dayStr);

  const dateObj = new Date(yearNum, monthNum - 1, dayNum);

  if (isNaN(dateObj.getTime())) {
    console.warn("TodoItem.vue (formattedDueDate): Invalid Date object created from parts:", yearNum, monthNum - 1, dayNum, "from string:", props.task.dueDate);
    return 'Błędna data';
  }
  const isDueDatePast = computed(() => {
    if (!props.task.dueDate || props.task.completed) return false;
    const today = new Date(getFormattedTodayDate());
    const dueDate = new Date(props.task.dueDate);
    return dueDate < today;
  });

  const options = { day: 'numeric', month: 'long' };
  try {
    return new Intl.DateTimeFormat('pl-PL', options).format(dateObj);
  } catch (e) {
    console.error("TodoItem.vue (formattedDueDate): Error formatting date with Intl.DateTimeFormat:", dateObj, e);
    return dateObj.toLocaleDateString();
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
      :checked="props.task.done"
          @change="handleToggleComplete(props.task)"
      />

      <div class="todo-item-content flex-grow-1">
        <label
            :for="'task-' + props.task.id"
            class="todo-text mb-0"
        :class="{ 'todo-done': props.task.done }" > {{ props.task.text }}
        </label>

        <div class="todo-meta small mt-1">
          <span :class="{ 'text-danger': isDueDatePast, 'text-warning': formattedDueDate === getFormattedTodayDate && !isDueDatePast, 'text-muted': !isDueDatePast && formattedDueDate !== getFormattedTodayDate() }"> <i class="bi bi-calendar me-1"></i> {{ formattedDueDate }}
           </span>
        </div>
      </div>
    </div>

    <div class="todo-item-right">
      <button @click="handleDeleteTask" class="btn btn-sm btn-outline-danger todo-delete-btn">Usuń <i class="bi bi-trash"></i></button>

    </div>

  </li>
</template>

<style scoped>

.todo-item {
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 12px 15px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background-color 0.2s ease;
}

.todo-item:hover {
  background-color: #f9f9f9;
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
  border: 2px solid #ccc;
  cursor: pointer;
  flex-shrink: 0;
  margin-top: 0.25em;
  appearance: none;
  -webkit-appearance: none;
  position: relative;
}

.todo-checkbox::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0.6em;
  height: 0.6em;
  background-color: green;
  border-radius: 50%;
  transform: translate(-50%, -50%) scale(0);
  transition: transform 0.2s ease-in-out;
}

.todo-checkbox:checked {
  border-color: green;
  background-color: green;
}

.todo-checkbox:checked::before {
  transform: translate(-50%, -50%) scale(1);
  background-color: white;
}

 .todo-item.todo-today .todo-checkbox {
   border-color: orange;
}

.todo-text {
  font-size: 1rem;
  color: #333;
  flex-grow: 1;
  line-height: 1.4;
}

.todo-text.todo-done {
  text-decoration: line-through;
  color: #888;
}

.todo-meta {
  font-size: 0.85rem;
  color: #888;
  display: flex;
  align-items: center;
  margin-top: 4px;
}

.todo-meta .text-danger { color: #e74c3c !important; }
.todo-meta .text-warning { color: #f39c12 !important; }
.todo-meta .text-muted { color: #888 !important; }

.todo-item-right {
  flex-shrink: 0;
}

.todo-delete-btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
}

</style>