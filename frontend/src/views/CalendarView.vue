<script setup>

import { computed, ref } from 'vue';
import { useTaskStore } from '../stores/taskStore.js';
import { Calendar } from "v-calendar";
import ToDoItem from "@/components/ToDoItem.vue";
import AddTaskModal from "@/components/AddTaskModal.vue";

const taskStore = useTaskStore();

const allTasks = computed(() => taskStore.allTasks);

const today = new Date();

const initialPageToday = {
  month: today.getMonth() + 1,
  year: today.getFullYear(),
};

const calendarAttributes = computed(() => {
  const tasksWithDueDate = allTasks.value.filter(task => task.dueDate);

  return tasksWithDueDate.map(task => {
    const taskDueDate = task.dueDate ? new Date(task.dueDate) : null;

    if (taskDueDate && !isNaN(taskDueDate.getTime())) {
      let color = 'blue';
      const todayStart = new Date(today.getFullYear(), today.getMonth(), today.getDate());
      const taskDayStart = new Date(taskDueDate.getFullYear(), taskDueDate.getMonth(), taskDueDate.getDate());

      if (task.done) {
        color = 'green';
      } else {

        if (taskDayStart < todayStart) {
          color = 'red';
        } else if (taskDayStart.getTime() === todayStart.getTime()) {
          color = 'yellow';
        }

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
  if (!selectedDay.value) return '';

  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('pl-PL', options).format(selectedDay.value);
});

const toggleTaskDone = (taskId) => {
  taskStore.toggleTaskDone(taskId);
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
    <div class="view-title mb-3">Kalendarz</div>
    <div class="calendar-tasks-container">

      <calendar
          class="custom-calendar"
          :attributes="calendarAttributes"
          is-expanded
          title-position="left"
          transition="slide-h"
          locale="pl"
          @dayclick="handleDayClick"
          :initial-page = "initialPageToday"
      />

      <div v-if="selectedDay" class="selected-day-tasks-section mt-4">
        <h4 class="selected-day-title tasks-section-title">Zadania na {{ selectedDayFormatted }}</h4>

        <TransitionGroup name="task-list" tag="ul" class="list-group tasks-list-container">
          <ToDoItem
              v-for="task in selectedDayTasks"
              :key="task.id"
              :task="task"
              @toggleDone="toggleTaskDone"
              @deleteTask="deleteTask"
          />
        </TransitionGroup>
        <div v-if="selectedDayTasks.length === 0" class="no-tasks-for-day-message text-muted text-center mt-3">
          Brak zadań zaplanowanych na ten dzień.
        </div>
      </div>

    </div>

    <div v-if="!selectedDay && calendarAttributes.length === 0" class="no-tasks-message text-muted text-center mt-4">
      Brak zadań z terminem do wyświetlenia w kalendarzu.
    </div>

  </div>

  <AddTaskModal @taskAdded="addTask" />

</template>

<style>

.view-container { padding: 30px 20px; }
.view-header { margin-bottom: 20px; }
.view-title-section { flex-shrink: 0; }
.view-title { margin-bottom: 0; font-size: 2.3rem; font-weight: bold; color: #333; line-height: 1.2; }
.view-content { margin-top: 20px; display: block; }

.calendar-tasks-container {
  display: flex;
  align-items: flex-start;
  gap: 40px;
  flex-wrap: wrap;
}

.custom-calendar.vc-container {
  border: 1px solid #ddd; border-radius: 8px; font-family: 'Arial', sans-serif; color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex: 1 1 600px;
  width: 100%;
}

.custom-calendar.vc-container .vc-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-evenly;
}

.custom-calendar.vc-container .vc-header .vc-title {
  flex-grow: 1;
  text-align: center;
  font-size: 1.3rem;
  font-weight: bold;
  color: #333;
  visibility: visible;
  opacity: 1;
}

.custom-calendar.vc-container .vc-header .vc-arrows {
  margin-right: 0 !important;
}

button {
  opacity: 1;
  visibility: visible;
}


.custom-calendar .vc-weekdays {
  padding: 15px 20px;
  background-color: #f0f0f0;
  border-bottom: 1px solid #eee;
  font-size: 1.2rem;
  color: #555;
  margin-bottom: 3px;
}

.custom-calendar.vc-container div.vc-weeks div.vc-day {
  padding: 20px 10px;
  transition: background-color 0.3s ease;
  cursor: pointer;
  box-sizing: border-box;
  border-radius: 20px;
}

.custom-calendar.vc-container div.vc-weeks div.vc-day:hover { background-color: #eef; }
.custom-calendar.vc-container div.vc-weeks div.vc-day.vc-highlight { background-color: #dff ; }


.custom-calendar.vc-container div.vc-weeks div.vc-day div.vc-day-content {
  font-size: 1.3rem;
  font-weight: bold;
  width: auto;
  height: auto;
  line-height: normal;
  box-sizing: content-box;
  display: block;
  flex: initial;
  padding: 10px;
}

.custom-calendar.vc-container div.vc-weeks div.vc-day div.vc-dots {
  margin-top: 6px !important;
  position: static !important; bottom: initial !important; left: initial !important;
  transform: none !important; display: block !important; text-align: center;
}
.custom-calendar.vc-container div.vc-weeks div.vc-day .vc-dot {
  width: 10px !important; height: 10px !important;
  margin: 0 2px !important; display: inline-block !important;
}

.selected-day-tasks-section {
  flex: 1 1 300px;
  width: 100%;
  border-top: none !important;
  padding-top: 0 !important;
  margin-top: 20px;
}

.selected-day-tasks-section .selected-day-title {
  margin-top: 0;
  padding-top: 0;
  border-top: none;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}


.no-tasks-for-day-message
{
  font-size: 1rem; color: #999;
}

.no-tasks-message
{
  margin-top: 30px;
  font-size: 1.2rem;
  color: #999;
}


.task-list-enter-from {
  opacity: 0;
}

.task-list-leave-active {
  transition: opacity 0.5s ease;
  position: absolute;
  left: 0; right: 0;
  z-index: 1;
}

.task-list-leave-to {
  opacity: 0;
}

.task-list-move {
  transition: transform 0.5s ease;
}

ul.list-group.tasks-list-container {
  position: relative;
}

</style>