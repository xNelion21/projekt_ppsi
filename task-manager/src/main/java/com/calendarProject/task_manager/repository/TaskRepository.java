package com.calendarProject.task_manager.repository;

import com.calendarProject.task_manager.model.Task;
import com.calendarProject.task_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Tu możesz dodać metody np. findByUserId w przyszłości
    List<Task> findByAssignedUsersContains(User user);
    List<Task> findByCreator(User creator);
    List<Task> findByCreatorId(Long creatorId);
    List<Task> findTasksByAssignedUsers_Id(Long userId);
}
