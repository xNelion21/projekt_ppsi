<template>

  <nav> <a href="http://localhost:8080/loginpage">Login</a> |
    <router-link to="/dashboard">Dashboard</router-link></nav>
  <div class="container mt-4">
    <h1 class="mb-3">Witaj, uzytkowniku !</h1>
    <button class="btn btn-danger mb-4" @click="handleLogout">Wyloguj</button>

    <h2>Zadania</h2>

    <!-- Formularz dodawania zadania -->
    <form @submit.prevent="addTask" class="mb-4">
      <div class="mb-3">
        <input v-model="newTask.title" class="form-control" placeholder="Tytuł zadania" required />
      </div>
      <div class="mb-3">
        <input v-model="newTask.description" class="form-control" placeholder="Opis zadania" />
      </div>
      <button type="submit" class="btn btn-primary">Dodaj zadanie</button>
    </form>

    <!-- Lista zadań -->
    <div v-if="tasks.length > 0">
      <ul class="list-group">
        <li v-for="task in tasks" :key="task.id" class="list-group-item d-flex align-items-center justify-content-between">
          <div class="form-check">
            <input type="checkbox" class="form-check-input me-2" :checked="task.completed" @change="toggleTaskCompletion(task)" />
            <span :class="{ 'text-decoration-line-through': task.completed }">
              <strong>{{ task.title }}</strong> – {{ task.description }}
            </span>
          </div>
          <div>
            <button class="btn btn-sm btn-outline-success me-2" @click="editTask(task)">✏️</button>
            <button class="btn btn-sm btn-outline-danger" @click="deleteTask(task.id)">🗑️</button>
          </div>
        </li>
      </ul>
    </div>
    <div v-else>
      <p>Brak zadań do wyświetlenia.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
//import AuthService from '@/services/authService';
import { useUserStore } from '@/stores/userStore';

export default {
  name: 'DashboardView',
  setup() {
    const userStore = useUserStore(); // Użyj user store
    return { userStore }; // Udostępnij userStore w szablonie
  },
  data() {
    return {
      tasks: [],
      newTask: {
        title: '',
        description: ''
      }
    };
  },
  methods: {
    async handleLogout() {
      try {
        await this.userStore.logout();
        console.log('Wylogowano pomyślnie poprzez store. Spring Security powinien przekierować.');
        window.location.href = 'http://localhost:8080/loginpage';
      } catch (error) {
        console.error('Błąd podczas wylogowywania:', error);
      }
    },
    async loadTasks() {
      try {
        const response = await axios.get('http://localhost:8080/api/tasks/my-tasks');
        this.tasks = response.data;
        console.log('ŁADUJĘ ZADANIA...');
      } catch (error) {
        console.error('Błąd przy pobieraniu zadań:', error);
      }
    },
    async addTask() {
      try {
        const response = await axios.post('http://localhost:8080/api/tasks', this.newTask);
        this.tasks.push(response.data);
        this.newTask.title = '';
        this.newTask.description = '';
        this.loadTasks();
      } catch (error) {
        console.error('Błąd przy dodawaniu zadania:', error);
      }
    },
    async deleteTask(id) {
      try {
        await axios.delete(`http://localhost:8080/api/tasks/${id}`);
        this.tasks = this.tasks.filter(task => task.id !== id);
      } catch (error) {
        console.error('Błąd przy usuwaniu zadania:', error);
        if (error.response && error.response.status === 401) {
          this.userStore.isAuthenticated = false;
          window.location.href = 'http://localhost:8080/loginpage';
        }
      }
    },
    async toggleTaskCompletion(task) {
      try {
        task.completed = !task.completed;
        await axios.put(`http://localhost:8080/api/tasks/${task.id}`, task);
      } catch (error) {
        console.error('Błąd przy aktualizacji statusu zadania:', error);
        if (error.response && error.response.status === 401) {
          this.userStore.isAuthenticated = false;
          window.location.href = 'http://localhost:8080/loginpage';
        }
      }
    },
    editTask(task) {
      const newTitle = prompt('Nowy tytuł:', task.title);
      const newDesc = prompt('Nowy opis:', task.description);

      if (newTitle !== null || newDesc !== null || newText !== null || newDueDate !== null) {
        const updatedTask = {
          ...task, // Skopiuj istniejące dane
          title: newTitle !== null ? newTitle : task.title,
          description: newDesc !== null ? newDesc : task.description,
          text: newText !== null ? newText : (task.text || ''),
          dueDate: newDueDate !== null ? newDueDate : (task.dueDate ? task.dueDate.split('T')[0] : null)
        };

        this.toggleTaskCompletion(updatedTask); // wyśle PUT z nowymi danymi
      }
    }
  },
  mounted() {
    console.log('DASHBOARD MOUNTED');
    console.log('UserStore user:', this.userStore.user);
    this.loadTasks();
  }
};
</script>

<style scoped>
.text-decoration-line-through {
  text-decoration: line-through;
}
</style>
