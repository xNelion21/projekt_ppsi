<script setup>

import { computed, ref } from 'vue';
import { useTaskStore } from '../stores/taskStore.js';
import { Calendar } from "v-calendar";
import { useI18n } from "vue-i18n";
import ToDoItem from "@/components/ToDoItem.vue";
import AddTaskModal from "@/components/AddTaskModal.vue";

const taskStore = useTaskStore();

const allTasks = computed(() => taskStore.allTasks);

const today = new Date();

const fetchTasks = async () => {
  await taskStore.fetchMyTasks();
};

const taskToEdit = ref(null);

const { t, locale } = useI18n();

const initialPageToday = {
  month: today.getMonth() + 1,
  year: today.getFullYear(),
};

const calendarAttributes = computed(() => {
  const tasksWithDueDate = allTasks.value.filter(task => task.dueDate);

  return tasksWithDueDate.map(task => {
    const taskDueDate = task.dueDate ? new Date(task.dueDate) : null;
      console.log(`Task ${task.id} completed:`, task.completed);
    if (taskDueDate && !isNaN(taskDueDate.getTime())) {
      let color = 'blue';
      const todayStart = new Date(today.getFullYear(), today.getMonth(), today.getDate());
      const taskDayStart = new Date(taskDueDate.getFullYear(), taskDueDate.getMonth(), taskDueDate.getDate());



      if (taskDayStart < todayStart) {
        color = 'red';
      } else if (taskDayStart.getTime() === todayStart.getTime()) {
        color = 'yellow';
      }

      if (task.completed){
        color = 'green'
      }

      const year = taskDueDate.getFullYear();
      const month = (taskDueDate.getMonth() + 1).toString().padStart(2, '0');
      const day = taskDueDate.getDate().toString().padStart(2, '0');
      const formattedDate = `${year}-${month}-${day}`;

      return {
        key: `task-${task.id}`,
        dot: color,
        dates: formattedDate,
        customData: task,
        popover: {
          label: task.text
        }
      };
    }
    return null;
  }).filter(Boolean);
});

const selectedDay = ref(null);

const selectedDayTasks = computed(() => {
  if (!selectedDay.value) return [];

  const selectedDate = new Date(selectedDay.value);
  const startOfSelectedDay = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), selectedDate.getDate());

  return allTasks.value.filter(task => {
    if (!task.dueDate) return false;

    const taskDueDate = new Date(task.dueDate);

    const startOfTaskDueDate = new Date(taskDueDate.getFullYear(), taskDueDate.getMonth(), taskDueDate.getDate());

    return startOfTaskDueDate.getTime() === startOfSelectedDay.getTime();
  });

});

const handleDayClick = (day) => {

  selectedDay.value = day.date;

};

const selectedDayFormatted = computed(() => {
  if (!selectedDay.value) {
    return t('calendar.noDaySelected');
  }

  const currentLocale = locale.value;
  const options = { day: 'numeric', month: 'long' };

  try {
    return new Intl.DateTimeFormat(currentLocale, options).format(selectedDay.value);
  } catch (e) {
    console.error("CalendarView.vue (selectedDayFormatted): Error formatting date with Intl.DateTimeFormat:", selectedDay.value, e);
    return new Date(selectedDay.value).toLocaleDateString(currentLocale);
  }
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

const addTask = (newTaskData) => {
  taskStore.addTask(newTaskData);
};

selectedDay.value = today;

</script>

<template>
  <div class="view-content mt-4">
    <div class="view-title mb-3">{{ t('calendar.title') }}</div>
    <div class="calendar-tasks-container">

      <calendar
          class="custom-calendar"
          :attributes="calendarAttributes"
          is-expanded
          title-position="left"
          transition="slide-h"
          :locale="locale"
          @dayclick="handleDayClick"
          :initial-page = "initialPageToday"
      />

      <div v-if="selectedDay" class="selected-day-tasks-section mt-4">
        <h4 class="selected-day-title tasks-section-title">{{ t('calendar.tasksFor') }} {{ selectedDayFormatted }}</h4>

        <TransitionGroup name="task-list" tag="ul" class="list-group tasks-list-container">
          <ToDoItem
              v-for="task in selectedDayTasks"
              :key="task.id"
              :task="task"
              @toggleDone="toggleTaskDone"
              @deleteTask="deleteTask"
              @editTask="handleEditTask"
          />
        </TransitionGroup>
        <div v-if="selectedDayTasks.length === 0" class="no-tasks-for-day-message text-muted text-center mt-3">
          {{ t('calendar.noTasks') }}
        </div>
      </div>

    </div>

    <div v-if="!selectedDay && calendarAttributes.length === 0" class="no-tasks-message text-muted text-center mt-4">
      Brak zadań z terminem do wyświetlenia w kalendarzu.
    </div>

  </div>

  <AddTaskModal
      :taskToEdit="taskToEdit"
      @taskSaved="fetchTasks"
      @closed="taskToEdit = null"
  />

</template>

<style>

.view-title {
  margin-bottom: 0;
  font-size: 2rem;
  font-weight: bold;
  color: var(--color-heading);
  line-height: 1.2;
}

.view-content {
  margin-top: 20px;
  display: block;
}

.calendar-tasks-container {
  display: flex;
  align-items: flex-start;
  gap: 40px;
  flex-wrap: wrap;
}

.custom-calendar.vc-container {
  border: 1px solid var(--color-border);
  border-radius: 10px;
  font-family: 'Inter', sans-serif;
  color: var(--color-text);
  background-color: var(--color-background-soft);
  box-shadow: 0 2px 8px var(--color-shadow-light);
  flex: 1 1 600px;
  width: 100%;
  transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease, box-shadow 0.3s ease;
}

.custom-calendar.vc-container .vc-header {
  padding: 15px 20px;
  display: flex;
  justify-content: space-evenly;
  margin-bottom: 5px;
  border-bottom: 1px solid var(--color-border);
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.custom-calendar.vc-container .vc-header .vc-title {
  flex-grow: 1;
  text-align: center;
  font-size: 1.3rem;
  font-weight: bold;
  color: var(--color-text) !important;
  visibility: visible;
  opacity: 1;
  background-color: transparent;
  transition: color 0.3s ease;
}

.custom-calendar.vc-container .vc-header .vc-arrows {
  margin-right: 0;
}

.custom-calendar.vc-container .vc-header button {
  opacity: 1;
  visibility: visible;
  color: var(--color-text);
  transition: color 0.3s ease;
  margin-bottom: 10px;
  background-color: var(--color-background);
}

.custom-calendar .vc-weekdays {
  padding: 15px 20px;
  background-color: var(--color-background-mute);
  border-bottom: 1px solid var(--color-border);
  font-size: 1.2rem;
  color: var(--color-text-soft);
  margin-bottom: 3px;
  transition: background-color 0.3s ease, border-color 0.3s ease, color 0.3s ease;
}

.custom-calendar.vc-container div.vc-weeks div.vc-day {
  transition: background-color 0.3s ease, border-color 0.3s ease, color 0.3s ease;
  cursor: pointer;
  width: 95px !important;
  height: 95px !important;
  border-radius: 55px;
  margin: 2px;
  color: var(--color-text);
}

.custom-calendar.vc-container div.vc-weeks div.vc-day.vc-highlight {
  background-color: var(--color-background-mute);
  border-color: var(--color-accent);
}

.custom-calendar.vc-container div.vc-weeks div.vc-day div.vc-day-content {
  font-size: 1.3rem;
  font-weight: bold;
  width: auto;
  height: auto;
  line-height: normal;
  box-sizing: content-box;
  display: block;
  flex: initial;
  padding: 35px;
}

.custom-calendar.vc-container div.vc-weeks div.vc-day div.vc-dots {
  margin-top: 6px;
  position: static;
  bottom: initial;
  left: initial;
  transform: none;
  display: block;
  text-align: center;
}
.custom-calendar.vc-container div.vc-weeks div.vc-day .vc-dot {
  width: 10px;
  height: 10px;
  margin: 0 2px;
  display: inline-block;
}

.selected-day-tasks-section {
  flex: 1 1 300px;
  width: 100%;
  border-top: none;
  padding-top: 0;
  margin-top: 20px;
}

.selected-day-tasks-section .selected-day-title {
  margin-top: 0;
  padding-top: 0;
  border-top: none;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 15px;
  color: var(--color-heading);
  transition: color 0.3s ease, border-color 0.3s ease;
}

/* Komunikaty o braku zadań */
.no-tasks-for-day-message, .no-tasks-message {
  font-size: 1rem;
  color: var(--color-text-mute);
  transition: color 0.3s ease;
}

.no-tasks-message {
  margin-top: 30px;
  font-size: 1.2rem;
}

.task-list-enter-from {
  opacity: 0;
}

.task-list-leave-active {
  transition: opacity 0.5s ease;
  left: 0; right: 0;
}

.task-list-leave-to {
  opacity: 0;
  transition: opacity 0.5s ease;
}

.task-list-move {
  transition: transform 0.5s ease;
}

ul.list-group.tasks-list-container {
  position: relative;
}

span {
  color: var(--color-text) !important;
}
</style>