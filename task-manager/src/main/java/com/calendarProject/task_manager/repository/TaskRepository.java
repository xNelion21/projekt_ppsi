package com.calendarProject.task_manager.repository;

import com.calendarProject.task_manager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Tu możesz dodać metody np. findByUserId w przyszłości
}
