<script setup>
import { useTaskStore } from '../stores/taskStore';
import {computed, ref} from 'vue';
import { useI18n } from 'vue-i18n';

import TodoItem from '../components/ToDoItem.vue';
import AddTaskModal from "@/components/AddTaskModal.vue";

const taskStore = useTaskStore();
const { t } = useI18n();

const fetchTasks = async () => {
  await taskStore.fetchMyTasks();
};

const taskToEdit = ref(null);

const tasks = computed(() => taskStore.completedTasks);

const taskCount = computed(() => {
  return taskStore.completedTasks.length;
});

const toggleTaskDone = (taskId) => {
  taskStore.toggleTaskCompletion(taskId);
};

const handleEditTask = (task) => {
  console.log("TaskListView: Otrzymano zadanie do edycji:", task);
  taskToEdit.value = task;
};

const deleteTask = (taskId) => {
  taskStore.deleteTask(taskId);
};
</script>

<template>
  <div class="view-container">
    <div class="view-header d-flex justify-content-between align-items-center">

      <div class="view-title-section">
        <h2 class="view-title">{{ t('completed.title') }}</h2> <div class="view-task-count-container">
        <i class="bi bi-check-circle view-task-count-icon"></i>
        <span class="view-task-count-badge badge rounded-pill bg-warning text-dark">
               {{ t('today.tasksCount', taskCount) }}
        </span>
      </div>
      </div>

    </div>

    <div class="view-content mt-4">

      <TransitionGroup name="task-list" tag="ul" class="list-group tasks-list-container">
        <TodoItem
            v-for="task in tasks"
            :key="task.id"
            :task="task"
            @toggleDone="toggleTaskDone"
            @deleteTask="deleteTask"
            @editTask="handleEditTask"
        />
      </TransitionGroup>
      <div v-if="tasks.length === 0" class="no-tasks-message text-muted text-center">
        {{ t('completed.noCompletedTasks') }} </div>

    </div>
  </div>

  <AddTaskModal
      :taskToEdit="taskToEdit"
      @taskSaved="fetchTasks"
      @closed="taskToEdit = null"
  />

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
  margin-left: 6px;
  background-color: transparent !important;
  color: var(--color-text-soft) !important;
  font-weight: normal;
  line-height: 1.2;
}

.view-task-count-container {
  margin-top: 15px;
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