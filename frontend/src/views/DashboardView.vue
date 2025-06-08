<script>
import axios from 'axios';
import { useUserStore } from '@/stores/userStore';
import TodoItem from '@/components/ToDoItem.vue';
import AddTaskModal from '../components/AddTaskModal.vue';


export default {
  name: 'DashboardView',
  components: {
    TodoItem,
    AddTaskModal
  },
  setup() {
    const userStore = useUserStore();
    return { userStore };
  },
  data() {
    return {
      tasks: [],
      taskToEdit: null,
      isAddTaskModalVisible: false
    };
  },
  computed: {
    completedTasks() {
      return this.tasks.filter(task => task.completed).length;
    },
    overdueTasks() {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return this.tasks.filter(task => {
        if (!task.dueDate) return false;
        const dueDate = new Date(task.dueDate);
        dueDate.setHours(0, 0, 0, 0);
        return dueDate < today && !task.completed;
      });
    },
    upcomingTasks() {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return this.tasks.filter(task => {
        if (!task.dueDate) return false;
        const dueDate = new Date(task.dueDate);
        dueDate.setHours(0, 0, 0, 0);
        return dueDate >= today && !task.completed;
      });
    },
    latestTasks() {
      return [...this.tasks].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, 5);
    }
  },
  methods: {
    async handleLogout() {
      try {
        await this.userStore.logout();
        window.location.href = 'http://localhost:8080/loginpage';
      } catch (error) {
        console.error('Błąd podczas wylogowywania:', error);
      }
    },
    async loadTasks() {
      try {
        const response = await axios.get('http://localhost:8080/api/tasks/my-tasks');
        this.tasks = response.data;
      } catch (error) {
        console.error('Błąd przy pobieraniu zadań:', error);
      }
    },
    async deleteTask(id) {
      try {
        await axios.delete(`http://localhost:8080/api/tasks/${id}`);
        this.tasks = this.tasks.filter(task => task.id !== id);
      } catch (error) {
        console.error('Błąd przy usuwaniu zadania:', error);
      }
    },
    async toggleTaskCompletion(task) {
      try {
        task.completed = !task.completed;
        await axios.put(`http://localhost:8080/api/tasks/${task.id}`, task);
      } catch (error) {
        console.error('Błąd przy aktualizacji statusu zadania:', error);
      }
    },
    openAddTaskModal() {
      this.taskToEdit = null;
      this.isAddTaskModalVisible = true;
    },
    openEditTaskModal(task) {
      this.taskToEdit = { ...task };
      this.isAddTaskModalVisible = true;
    },
    closeModal() {
      this.taskToEdit = null;
      this.isAddTaskModalVisible = false;
    },
    async onTaskSaved() {
      await this.loadTasks();
      this.closeModal();
    }
  },
  mounted() {
    this.loadTasks();
  }
};
</script>

<template>
  <div class="dashboard-container d-flex">

    <main class="dashboard-main p-4 flex-grow-1">

      <div class="dashboard-header d-flex justify-content-between align-items-center mb-4">
        <h2>{{ $t('dashboard.welcome', { name: userStore.displayName }) }}</h2>
        <button class="btn btn-danger" @click="handleLogout">{{ $t('dashboard.logout') }}</button>
      </div>

      <div class="dashboard-stats d-flex mb-4">
        <div class="stat-card bg-light p-3 me-3 rounded flex-fill text-center">
          <h5>{{ $t('dashboard.allTasks') }}</h5>
          <p class="display-6">{{ tasks.length }}</p>
        </div>
        <div class="stat-card bg-light p-3 me-3 rounded flex-fill text-center">
          <h5>{{ $t('dashboard.completed') }}</h5>
          <p class="display-6">{{ completedTasks }}</p>
        </div>
        <div class="stat-card bg-light p-3 me-3 rounded flex-fill text-center">
          <h5>{{ $t('dashboard.overdue') }}</h5>
          <p class="display-6">{{ overdueTasks.length }}</p>
        </div>
      </div>

      <div class="tasks-section mb-4">
        <h4>{{ $t('dashboard.upcomingTasks') }}</h4>
        <ul class="list-group">
          <TodoItem
              v-for="task in upcomingTasks"
              :key="task.id"
              :task="task"
              @toggleDone="toggleTaskCompletion"
              @deleteTask="deleteTask"
              @editTask="openEditTaskModal"
          />
          <li v-if="upcomingTasks.length === 0" class="list-group-item text-muted">
            {{ $t('dashboard.noUpcomingTasks') }}
          </li>
        </ul>
      </div>

      <div class="tasks-section mb-4">
        <h4>{{ $t('dashboard.overdueTasks') }}</h4>
        <ul class="list-group">
          <TodoItem
              v-for="task in overdueTasks"
              :key="task.id"
              :task="task"
              @toggleDone="toggleTaskCompletion"
              @deleteTask="deleteTask"
              @editTask="openEditTaskModal"
          />
          <li v-if="overdueTasks.length === 0" class="list-group-item text-muted">
            {{ $t('dashboard.noOverdueTasks') }}
          </li>
        </ul>
      </div>

      <div class="tasks-section">
        <h4>{{ $t('dashboard.latestTasks') }}</h4>
        <ul class="list-group">
          <TodoItem
              v-for="task in latestTasks"
              :key="task.id"
              :task="task"
              @toggleDone="toggleTaskCompletion"
              @deleteTask="deleteTask"
              @editTask="openEditTaskModal"
          />
          <li v-if="latestTasks.length === 0" class="list-group-item text-muted">
            {{ $t('dashboard.noLatestTasks') }}
          </li>
        </ul>
      </div>

      <!-- Modal dodawania/edycji -->
      <AddTaskModal
          v-if="isAddTaskModalVisible"
          :taskToEdit="taskToEdit"
          @taskSaved="onTaskSaved"
          @closed="closeModal"
      />
    </main>
  </div>
</template>

<style scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
  background-color: var(--color-background) !important;
  color: var(--color-text);
}

.dashboard-main {
  flex-grow: 1;
  padding: 20px;
  background-color: var(--color-background);
  color: var(--color-text);
}

.dashboard-header {
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.dashboard-stats .stat-card {
  background-color: var(--color-background) !important;
  border: 1px solid var(--color-border);
  padding: 15px;
  margin-right: 10px;
  border-radius: 8px;
  text-align: center;
  color: var(--color-text);
}

.tasks-section {
  margin-bottom: 20px;
}

.tasks-section h4 {
  border-bottom: 1px solid var(--color-text) !important;
  padding-bottom: 5px;
  margin-bottom: 10px;
  color: var(--color-heading);
}

.list-group-item {
  background-color: var(--color-background-soft);
  color: var(--color-text) !important;
  border-color: var(--color-border);
}
</style>
