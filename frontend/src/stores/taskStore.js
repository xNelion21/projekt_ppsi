import {defineStore} from 'pinia';
import {reactive} from 'vue';
import axios from "axios";

const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api', // Twój adres backendu
    withCredentials: true
});

export function getFormattedTodayDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = (today.getMonth() + 1).toString().padStart(2, '0');
    const day = today.getDate().toString().padStart(2, '0');

    return `${year}-${month}-${day}`;
}

export function formatIsoDateString(date) {
    if (!date) return null;
    try {
        const d = new Date(date);
        if (isNaN(d.getTime())) {
            console.warn(`Invalid date string: ${date}`);
            return null;
        }
        const year = d.getFullYear();
        const month = (d.getMonth() + 1).toString().padStart(2, '0');
        const day = d.getDate().toString().padStart(2, '0');
        return `${year}-${month}-${day}`;
    } catch (e) {
        console.error(`Error formatting date string: ${date}`, e);
        return null;
    }
    }

export const useTaskStore = defineStore('taskStore', {
    state: () => ({

        tasks: reactive([
            { id: 1, text: 'Nauczyć się Vue', completed: false, dueDate: '2025-06-15' },
            { id: 2, text: 'Zbudować formularz zadań', completed: true, dueDate: '2025-04-20' },
            { id: 3, text: 'Połączyć z backendem Spring Boot', completed: false, dueDate: '2025-06-01' },
            { id: 4, text: 'Zadanie na dzisiaj - test', completed: false, dueDate: getFormattedTodayDate() },
            { id: 5, text: 'Zadanie przeterminowane', completed: false, dueDate: '2025-05-01' },
            { id: 6, text: 'Zadanie ukończone przeterminowane', completed: true, dueDate: '2025-05-02' },
            { id: 7, text: 'Zadanie ukończone na dzisiaj', completed: true, dueDate: getFormattedTodayDate() }
        ]),
    }),

    getters: {

        allTasks: (state) => state.tasks,

        completedTasks: (state) => state.tasks.filter(task => task.completed),

        todoTasks: (state) => state.tasks.filter(task => !task.completed),

        overdueTasks: (state) => {
            const now = new Date();
            const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0, 0);

            return state.tasks.filter(task =>
                !task.completed &&
                task.dueDate &&
                new Date(task.dueDate) < todayStart
            ).sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate));
        },

        remainingInboxTasks: (state) => {

            const now = new Date();
            const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0, 0);

            return state.tasks.filter(task =>
                !task.completed &&
                (
                    !task.dueDate ||
                    new Date(task.dueDate) >= todayStart
                )
            );
        },

        todayTasks: (state) => {
            const todayString = getFormattedTodayDate();

            return state.tasks.filter(task => {
                if (task.completed) {
                    return false;
                }

                if (!task.dueDate) {
                    return false;
                }

                const taskDueDateString = formatIsoDateString(task.dueDate);

                if (!taskDueDateString) {
                    return false;
                }

                return taskDueDateString === todayString;
            });
        },

        upcomingTasks: (state) => {
            const todayString = getFormattedTodayDate();

            return state.tasks.filter(task => {
                if (task.completed) {
                    return false;
                }

                if (!task.dueDate) {
                    return false;
                }

                const taskDueDateString = formatIsoDateString(task.dueDate);

                if (!taskDueDateString) {
                    return false;
                }

                return taskDueDateString > todayString;
            });
        },
    },

    actions: {

        // TODO: Ta akcja będzie wywoływać endpoint API GET /tasks
        async fetchAllUsers() {
            this.isLoading = true;
            this.error = null;
            try {
                const response = await apiClient.get('/tasks/users'); // Endpoint z TaskRestController
                this.allUsers = response.data;
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to fetch users';
                console.error("Error fetching users:", err);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchMyTasks() {
            this.isLoading = true;
            this.error = null;
            try {
                const response = await apiClient.get('/tasks/my-tasks');
                this.tasks = response.data; // Oczekujemy listy TaskResponseDTO
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to fetch tasks';
                console.error("Error fetching tasks:", err);
                this.tasks = []; // Wyczyść w razie błędu
            } finally {
                this.isLoading = false;
            }
        },
        async addTask(taskData) { // taskData to obiekt TaskRequestDTO
            this.isLoading = true;
            this.error = null;
            try {
                // taskData = { title, description, text, dueDate (YYYY-MM-DD), assignedUserIds (Set<Long>) }
                const response = await apiClient.post('/tasks', taskData);
                // Można dodać nowo utworzone zadanie do listy lub przeładować wszystkie
                this.tasks.push(response.data); // response.data to TaskResponseDTO
                // lub await this.fetchMyTasks();
                return true; // Sukces
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to add task';
                console.error("Error adding task:", err);
                return false; // Błąd
            } finally {
                this.isLoading = false;
            }
        },
        async updateTask(taskId, taskData) { // taskData to obiekt TaskRequestDTO
            this.isLoading = true;
            this.error = null;
            try {
                // taskData = { title, description, text, dueDate, completed, assignedUserIds }
                const response = await apiClient.put(`/tasks/${taskId}`, taskData);
                const index = this.tasks.findIndex(t => t.id === taskId);
                if (index !== -1) {
                    this.tasks[index] = response.data; // response.data to TaskResponseDTO
                }
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to update task';
                console.error("Error updating task:", err);
                return false;
            } finally {
                this.isLoading = false;
            }
        },
        async toggleTaskCompletion(taskId) {
            this.isLoading = true;
            this.error = null;
            try {
                const response = await apiClient.put(`/tasks/${taskId}/toggle-complete`);
                const index = this.tasks.findIndex(t => t.id === taskId);
                if (index !== -1) {
                    this.tasks[index] = response.data;
                }
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to toggle task completion';
                console.error("Error toggling task:", err);
                return false;
            } finally {
                this.isLoading = false;
            }
        },


        async deleteTask(taskId) {
            this.isLoading = true;
            this.error = null;
            try {
                await apiClient.delete(`/tasks/${taskId}`);
                this.tasks = this.tasks.filter(t => t.id !== taskId);
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to delete task';
                console.error("Error deleting task:", err);
                return false;
            } finally {
                this.isLoading = false;
            }
        },
        async assignUsersToTask(taskId, userIds) { // userIds to Set<Long>
            this.isLoading = true;
            this.error = null;
            try {
                const response = await apiClient.post(`/tasks/${taskId}/assign-users`, userIds);
                const index = this.tasks.findIndex(t => t.id === taskId);
                if (index !== -1) {
                    this.tasks[index] = response.data;
                }
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to assign users';
                console.error("Error assigning users:", err);
                return false;
            } finally {
                this.isLoading = false;
            }
        },
        async removeUserFromTask(taskId, userIdToRemove) {
            this.isLoading = true;
            this.error = null;
            try {
                const response = await apiClient.post(`/tasks/${taskId}/remove-user/${userIdToRemove}`);
                const index = this.tasks.findIndex(t => t.id === taskId);
                if (index !== -1) {
                    this.tasks[index] = response.data;
                }
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to remove user';
                console.error("Error removing user:", err);
                return false;
            } finally {
                this.isLoading = false;
            }
        }

    },
});