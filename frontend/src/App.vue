<script setup>

import AddTaskModal from "@/components/AddTaskModal.vue";
import Sidebar from "@/components/Sidebar.vue";
import {ref} from "vue";

const isSidebarOpen = ref(true);

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;
};

</script>

<template>
  <div class="container-fluid d-flex flex-column h-100" :class="{ 'sidebar-open': isSidebarOpen }">
    <div class="row h-100"> <div class="col-auto p-0 app-sidebar-col">
      <Sidebar :toggleSidebar="toggleSidebar" />
    </div>

      <div class="col p-0 main-content-col">
        <div class="p-2 simple-toggle-button-container">
          <button class="btn btn-sidebar" @click="toggleSidebar"> <i class="bi bi-layout-sidebar"></i></button>
        </div>

        <main class="h-100"> <div class="p-4 h-100-content"> <router-view />
        </div>
        </main>

        <AddTaskModal @taskAdded="addTask" />

      </div>
    </div>
  </div>

  <div class="overlay d-md-none" :class="{ 'show': isSidebarOpen }" @click="toggleSidebar"></div>

</template>

<style>

html, body, #app, .container-fluid, .row {
  height: 100%;
  margin: 0; padding: 0;
  overflow-x: hidden;
  overflow-y: hidden;
}

.app-sidebar-col {
  width: 280px;
  flex-shrink: 0;
  position: static;
  left: auto;
  transition: width 0.3s ease-in-out, margin-left 0.3s ease-in-out;
  z-index: auto;
  overflow: hidden;
  margin-left: 0;
  height: 100%;
}

.main-content-col {
  margin-left: 0;
  transition: margin-left 0.3s ease-in-out;
  padding-top: 0;
  padding-right: 15px;
}

.btn-sidebar:hover  {
  background-color: rgba(128, 128, 128, 0.23);
}


@media (max-width: 767.98px) {

  html, body, .container-fluid, .row {
    overflow-y: auto;
  }

  .app-sidebar-col {
    position: fixed;
    top: 0;
    bottom: 0;
    left: -280px;
    width: 280px;
    z-index: 1045;
    transition: left 0.3s ease-in-out;
    margin-left: 0;
    overflow-y: auto;
    overflow-x: hidden;
  }
  .container-fluid.sidebar-open .app-sidebar-col {
    left: 0;
  }

  .main-content-col {
    margin-left: 0;
    transition: margin-left 0.3s ease-in-out;
    padding-right: 15px;
  }

  .simple-toggle-button-container {
    display: flex;
    align-items: center;
    padding: 10px 15px;
  }

  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 1040;
    display: none;
    cursor: pointer;
  }
  .overlay.show {
    display: block;
  }

}

@media (min-width: 768px) {

  .app-sidebar-col {
    display: block !important;
    position: static;
    left: auto;
    z-index: auto;
    overflow-y: auto;
  }

  .container-fluid:not(.sidebar-open) .app-sidebar-col {
    width: 0;
  }

  .main-content-col {
    margin-left: 0;
  }

  .container-fluid8.sidebar-open .main-content-col {
    margin-left: 0;
  }

  .container-fluid:not(.sidebar-open) .main-content-col {
    margin-left: 80px;
  }

  .simple-toggle-button-container {
    display: flex;
  }

  .overlay {
    display: none;
  }

}

ul.tasks-list-container li {
  background-color: #f9f9f9; border: 1px solid #eee; margin-bottom: 5px; padding: 10px; border-radius: 4px;
}

main {
  padding-top: 0;
}
</style>