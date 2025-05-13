<script setup>
import { ref } from 'vue';

const emit = defineEmits(['taskAdded']);

const newTaskText = ref('');

const addNewTask = () => {
  if (newTaskText.value.trim() === '') {
    alert('Nazwa zadania nie może być pusta!');
    return;
  }

  emit('taskAdded', newTaskText.value.trim());

  console.log('Zadanie zostało wyemitowane:', newTaskText.value);

  newTaskText.value = '';
};
</script>

<template>
  <div class="input-group mb-3 todo-input-container">
    <input
        v-model="newTaskText"
        @keyup.enter="addNewTask"
        placeholder="Dodaj nowe zadanie..."
        class="form-control task-input"
    />
    <button @click="addNewTask" class="btn btn-success add-button">Dodaj</button>
  </div>
</template>

<style scoped>

</style>