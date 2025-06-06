<script setup>

import { useTaskStore } from '../stores/taskStore';
import { computed } from 'vue';

import TodoItem from '../components/ToDoItem.vue';
import AddTaskModal from '../components/AddTaskModal.vue';

const taskStore = useTaskStore();

const taskCount = computed(() => {
  return taskStore.todayTasks.length;
});

const taskCountText = computed(() => {
  const count = taskCount.value;

  if (count === 1) {
    return 'zadanie';
  } else if (count >= 2 && count <= 4) {
    return 'zadania';
  } else {

    const lastDigit = count % 10;
    const lastTwoDigits = count % 100;

    if (lastTwoDigits >= 12 && lastTwoDigits <= 14) {
      return 'zadań';
    } else if (lastDigit >= 2 && lastDigit <= 4) {
      return 'zadania';
    } else {
      return 'zadań';
    }
  }
});


const addTask = (newTaskData) => {
  taskStore.addTask(newTaskData);
};

const toggleTaskDone = (taskId) => {
  taskStore.toggleTaskDone(taskId);
};

const deleteTask = (taskId) => {
  taskStore.deleteTask(taskId);
};

</script>

<template>
  <div class="view-container">
    <div class="view-header d-flex justify-content-between align-items-center">

      <div class="view-title-section">
        <h2 class="view-title">{{ $t('today.title') }}</h2>
        <div v-if="taskCount > 0" class="view-task-count-container">
          <i class="bi bi-clock me-1 view-task-count-icon"></i> <span class="view-task-count-badge badge rounded-pill bg-warning text-dark">
                  {{ $t('today.tasksCount', taskCount) }}
        </span>
        </div>
      </div>

      <div class="view-actions">
        <a href="#" class="text-decoration-none add-task-btn"
           data-bs-toggle="modal"
           data-bs-target="#addTaskModal">
          <i class="bi bi-plus-lg me-2"></i>
          {{ $t('tasks.addTask') }}
        </a>
      </div>
    </div>

    <div class="view-content mt-4">

      <TransitionGroup name="task-list" tag="ul" class="list-group tasks-list-container">
        <TodoItem
            v-for="task in taskStore.todayTasks"
            :key="task.id"
            :task="task"
            @toggleDone="toggleTaskDone"
            @deleteTask="deleteTask"
        />
      </TransitionGroup>

    </div>

    <div v-if="taskCount === 0" class="no-tasks-message text-muted text-center">
      {{ $t('today.noTasks') }}
    </div>
  </div>

  <AddTaskModal @taskAdded="addTask" />

</template>

<style scoped>

.view-container {
  padding: 30px 20px;
}

.view-header {
  margin-bottom: 20px;
}

.view-title-section {
  flex-shrink: 0;
}

.view-title {
  margin-bottom: 0;
  font-size: 2rem;
  font-weight: bold;
  color: var(--color-text);
  line-height: 1.2;
}

.view-task-count-badge {
  font-size: 1.1rem;
  padding: 0;
  margin-left: 6px !important;
  background-color: transparent !important;
  color: var(--color-text-mute) !important;
  font-weight: normal !important;
  line-height: 1.2;
}

.view-task-count-container {
  margin-top: 15px;
}

.view-actions {
  flex-shrink: 0;
}

.add-task-btn {
  color: #ffad00;
  font-weight: bold;
  display: flex;
  align-items: center;
  font-size: 1rem;
}

.view-content {
  margin-top: 0;
}

.no-tasks-message {
  margin-top: 30px;
  font-size: 1.5rem;
  color: var(--color-text) !important;
}

.task-list-leave-active {
  transition: opacity 0.5s ease;
}

.task-list-leave-to {
  opacity: 0;
}

.task-list-move {
  transition: transform 0.5s ease;
}

</style>
