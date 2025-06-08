<script setup>
import { useTaskStore } from '../stores/taskStore';
import { computed, ref, onMounted } from 'vue';

import TodoItem from '../components/ToDoItem.vue';
import AddTaskModal from '../components/AddTaskModal.vue';

const fetchTasks = async () => {
  await taskStore.fetchMyTasks();
};

const taskStore = useTaskStore();
const overdueTasks = computed(() => taskStore.overdueTasks || []);
const remainingTasks = computed(() => taskStore.remainingInboxTasks || []);
const taskToEdit = ref(null);
const isAddTaskModalVisible = ref(false);

const addTask = (newTaskData) => {
  taskStore.addTask(newTaskData);
};

const handleEditTask = (task) => {
  console.log("TaskListView: Otrzymano zadanie do edycji:", task);
  taskToEdit.value = task;
};

const toggleTaskDone = (taskId) => {
  taskStore.toggleTaskCompletion(taskId);
};

const deleteTask = (taskId) => {
  taskStore.deleteTask(taskId);
};

</script>

<template>
  <div class="view-container">
    <div class="view-header d-flex justify-content-between align-items-center">

      <div class="view-title-section">
        <h2 class="view-title">{{ $t('inbox.title') }}</h2>
      </div>


    </div>

    <div class="view-content mt-4">

      <div v-if="overdueTasks.length > 0" class="overdue-tasks-section mb-4">

        <h4 class="tasks-section-title overdue-title">{{ $t('inbox.due') }}</h4>

        <TransitionGroup name="task-list" tag="ul" class="list-group tasks-list-container">
          <TodoItem
              v-for="task in overdueTasks"
              :key="task.id"
              :task="task"
              @toggleDone="toggleTaskDone"
              @deleteTask="deleteTask"
              @editTask="handleEditTask"
          />
        </TransitionGroup>
      </div>


      <div v-if="overdueTasks.length > 0 || remainingTasks.length > 0" class="remaining-tasks-section">

        <h4 v-if="overdueTasks.length > 0" class="tasks-section-title remaining-title">{{ $t('inbox.other') }}</h4>


        <TransitionGroup name="task-list" tag="ul" class="list-group tasks-list-container">
          <TodoItem
              v-for="task in remainingTasks"
              :key="task.id"
              :task="task"
              @toggleDone="toggleTaskDone"
              @deleteTask="deleteTask"
              @editTask="handleEditTask"
          />
        </TransitionGroup>

      </div>
      <div v-if="overdueTasks.length === 0 && remainingTasks.length === 0" class="no-tasks-message text-muted text-center">
        {{ $t('inbox.noTasks') }}
      </div>


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
  color: var(--color-text-mute);
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