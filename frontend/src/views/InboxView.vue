<script setup>
import { useTaskStore } from '../stores/taskStore';
import { computed, ref, onMounted } from 'vue';

import TodoItem from '../components/ToDoItem.vue';
import AddTaskModal from '../components/AddTaskModal.vue';

const taskStore = useTaskStore();
const overdueTasks = computed(() => taskStore.overdueTasks || []);
const remainingTasks = computed(() => taskStore.remainingInboxTasks || []);
const taskToEdit = ref(null);
const isAddTaskModalVisible = ref(false);

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
        <h2 class="view-title">Skrzynka</h2>
      </div>

      <div class="view-actions">
        <a href="#" class="text-decoration-none add-task-btn"
           data-bs-toggle="modal"
           data-bs-target="#addTaskModal">
          <i class="bi bi-plus-lg me-2"></i>
          Dodaj zadanie
        </a>
      </div>
    </div>

    <div class="view-content mt-4">

      <div v-if="overdueTasks.length > 0" class="overdue-tasks-section mb-4">

        <h4 class="tasks-section-title overdue-title">Zaległe</h4>

        <TransitionGroup name="task-list" tag="ul" class="list-group tasks-list-container">
          <TodoItem
              v-for="task in overdueTasks"
              :key="task.id"
              :task="task"
              @toggleDone="toggleTaskDone"
              @deleteTask="deleteTask"
          />
        </TransitionGroup>
      </div>


      <div v-if="overdueTasks.length > 0 || remainingTasks.length > 0" class="remaining-tasks-section">

        <h4 v-if="overdueTasks.length > 0" class="tasks-section-title remaining-title">Pozostałe</h4>
        <h4 v-else-if="remainingTasks.length > 0" class="tasks-section-title remaining-title">Skrzynka</h4>


        <TransitionGroup name="task-list" tag="ul" class="list-group tasks-list-container">
          <TodoItem
              v-for="task in remainingTasks"
              :key="task.id"
              :task="task"
              @toggleDone="toggleTaskDone"
              @deleteTask="deleteTask"
          />
        </TransitionGroup>

      </div>
      <div v-if="overdueTasks.length === 0 && remainingTasks.length === 0" class="no-tasks-message text-muted text-center">
        Skrzynka pusta! Świetna robota!
      </div>


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
  color: #333;
  line-height: 1.2;
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

.tasks-section-title {
  font-size: 1.3rem;
  font-weight: bold;
  color: #555;
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 5px;
}

.tasks-section-title.overdue-title {
  color: rgb(212, 69, 61);
  border-bottom-color: #d9534f;
}

.no-tasks-message {
  margin-top: 30px;
  font-size: 1.5rem;
  color: #999;
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