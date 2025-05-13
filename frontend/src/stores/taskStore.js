import {defineStore} from 'pinia';
import {reactive} from 'vue';

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
            { id: 1, text: 'Nauczyć się Vue', done: false, dueDate: '2025-06-15' },
            { id: 2, text: 'Zbudować formularz zadań', done: true, dueDate: '2025-04-20' },
            { id: 3, text: 'Połączyć z backendem Spring Boot', done: false, dueDate: '2025-06-01' },
            { id: 4, text: 'Zadanie na dzisiaj - test', done: false, dueDate: getFormattedTodayDate() },
            { id: 5, text: 'Zadanie przeterminowane', done: false, dueDate: '2025-05-01' },
            { id: 6, text: 'Zadanie ukończone przeterminowane', done: true, dueDate: '2025-05-02' },
            { id: 7, text: 'Zadanie ukończone na dzisiaj', done: true, dueDate: getFormattedTodayDate() }
        ]),
    }),

    getters: {

        allTasks: (state) => state.tasks,

        completedTasks: (state) => state.tasks.filter(task => task.done),

        todoTasks: (state) => state.tasks.filter(task => !task.done),

        overdueTasks: (state) => {
            const now = new Date();
            const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0, 0);

            return state.tasks.filter(task =>
                !task.done &&
                task.dueDate &&
                new Date(task.dueDate) < todayStart
            ).sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate));
        },

        remainingInboxTasks: (state) => {

            const now = new Date();
            const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0, 0);

            return state.tasks.filter(task =>
                !task.done &&
                (
                    !task.dueDate ||
                    new Date(task.dueDate) >= todayStart
                )
            );
        },

        todayTasks: (state) => {
            const todayString = getFormattedTodayDate();

            return state.tasks.filter(task => {
                if (task.done) {
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
                if (task.done) {
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
        async fetchTasks() {
            // Na razie nic nie robi, bo dane są już w stanie.
            // W przyszłości:
            // try {
            //   const response = await fetch('/api/tasks'); // Wywołanie API
            //   const data = await response.json();
            //   this.tasks = data; // Zaktualizuj stan danymi z API
            // } catch (error) {
            //   console.error('Error fetching tasks:', error);
            //   // Ustaw stan błędu
            // }
        },

        // TODO: Ta akcja będzie wywoływać endpoint API POST /tasks
        async addTask(newTaskData) {
            let maxId = 0;
            if(this.tasks.length > 0) {
                maxId = Math.max(...this.tasks.map(task => task.id));
            }
            const newId = maxId + 1;

            const newTask = {
                id: newId,
                ...newTaskData,
                done: false
            };

            this.tasks.push(newTask);

            // W przyszłości:
            // try {
            //   const response = await fetch('/api/tasks', {
            //     method: 'POST',
            //     headers: { 'Content-Type': 'application/json' },
            //     body: JSON.stringify(newTaskData) // Wyślij dane zadania do API
            //   });
            //   const addedTask = await response.json();
            //   // Opcjonalnie: Zaktualizuj stan, jeśli API zwraca zadanie z ID/timestamp
            //   // this.tasks.push(addedTask);
            // } catch (error) {
            //   console.error('Error adding task:', error);
            //   // Ustaw stan błędu
            // }
        },

        // TODO: Ta akcja będzie wywoływać endpoint API PUT/PATCH /tasks/{id}/toggle
        async toggleTaskDone(taskId) {
            const task = this.tasks.find(task => task.id === taskId);
            if (task) {
                task.done = !task.done; // Przełącz status w stanie lokalnie

                // W przyszłości:
                // try {
                //   await fetch(`/api/tasks/${taskId}/toggle`, { method: 'PUT' }); // Wywołanie API
                // } catch (error) {
                //   console.error('Error toggling task:', error);
                //   // Opcjonalnie: Cofnij zmianę statusu w stanie lokalnym lub odśwież dane
                // }
            }
        },

        // TODO: Ta akcja będzie wywoływać endpoint API DELETE /tasks/{id}
        async deleteTask(taskId) {
            this.tasks = this.tasks.filter(task => task.id !== taskId);

            // W przyszłości:
            // try {
            //   await fetch(`/api/tasks/${taskId}`, { method: 'DELETE' }); // Wywołanie API
            // } catch (error) {
            //   console.error('Error deleting task:', error);
            //   // Opcjonalnie: Pokaż komunikat o błędzie lub odśwież dane
            // }
        }
    },
});