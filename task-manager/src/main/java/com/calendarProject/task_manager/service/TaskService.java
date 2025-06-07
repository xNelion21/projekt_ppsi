package com.calendarProject.task_manager.service;

import com.calendarProject.task_manager.model.Task;
import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.TaskRepository;
import com.calendarProject.task_manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Task createTask(Task taskDetails, Long creatorUserId, Set<Long> assignedUserIds) {
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new RuntimeException("Creator not found with id: " + creatorUserId));

        Task newTask = new Task();
        newTask.setTitle(taskDetails.getTitle());
        newTask.setDescription(taskDetails.getDescription());
        newTask.setDueDate(taskDetails.getDueDate());
        newTask.setCompleted(false); // Domyślnie nieukończone
        newTask.setCreator(creator);

        // Dodaj twórcę do przypisanych użytkowników
        newTask.addAssignedUser(creator);

        // Dodaj innych przypisanych użytkowników
        if (assignedUserIds != null && !assignedUserIds.isEmpty()) {
            Set<User> usersToAssign = new HashSet<>(userRepository.findAllById(assignedUserIds));
            usersToAssign.forEach(newTask::addAssignedUser);
        }

        return taskRepository.save(newTask);
    }

    public List<Task> getTasksForUser(Long userId) {
        // Pobiera zadania, do których użytkownik jest przypisany
        return taskRepository.findTasksByAssignedUsers_Id(userId);
    }

    public Optional<Task> getTaskByIdForUser(Long taskId, Long userId) {
        // Zwraca zadanie tylko jeśli użytkownik jest do niego przypisany
        return taskRepository.findById(taskId)
                .filter(task -> task.getAssignedUsers().stream().anyMatch(user -> user.getId().equals(userId)));
    }

    @Transactional
    public Task updateTask(Long taskId, Task taskDetails, Long currentUserId) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + currentUserId));

        // Sprawdzenie, czy bieżący użytkownik jest przypisany do zadania
        if (existingTask.getAssignedUsers().stream().noneMatch(u -> u.getId().equals(currentUserId))) {
            throw new SecurityException("User not authorized to update this task.");
        }

        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setDueDate(taskDetails.getDueDate());
        existingTask.setCompleted(taskDetails.isCompleted());

        // Aktualizacja przypisanych użytkowników (jeśli przekazano)
        if (taskDetails.getAssignedUsers() != null) {
            Set<User> newAssignedUsers = taskDetails.getAssignedUsers().stream()
                    .map(u -> userRepository.findById(u.getId())
                            .orElseThrow(() -> new RuntimeException("Assigned user not found")))
                    .collect(Collectors.toSet());

            // Aby poprawnie zarządzać relacją ManyToMany, często czyści się istniejących i dodaje nowych
            // lub manualnie zarządza dodawaniem/usuwaniem.
            // Upewnij się, że twórca zadania zawsze pozostaje przypisany, jeśli taka jest logika.
            existingTask.getAssignedUsers().clear(); // Ostrożnie z tym, może być potrzebna bardziej zniuansowana logika
            newAssignedUsers.forEach(existingTask::addAssignedUser);
            // Upewnij się, że twórca jest zawsze przypisany, jeśli to wymagane
            if (existingTask.getAssignedUsers().stream().noneMatch(u -> u.getId().equals(existingTask.getCreator().getId()))) {
                existingTask.addAssignedUser(existingTask.getCreator());
            }
        }


        return taskRepository.save(existingTask);
    }

    @Transactional
    public void deleteTask(Long taskId, Long currentUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // Tylko twórca zadania może je usunąć
        if (!task.getCreator().getId().equals(currentUserId)) {
            throw new SecurityException("Only the creator can delete this task.");
        }

        // Usuń powiązania w tabeli pośredniczącej przed usunięciem zadania
        // Hibernate powinien to zrobić automatycznie jeśli cascade jest dobrze ustawione,
        // ale czasami lepiej zrobić to jawnie.
        task.getAssignedUsers().forEach(user -> user.getAssignedTasks().remove(task));
        task.getAssignedUsers().clear();
        taskRepository.delete(task);
    }

    @Transactional
    public Task assignUsersToTask(Long taskId, Set<Long> userIdsToAssign, Long currentUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        // Sprawdź, czy bieżący użytkownik jest przypisany do zadania (i może zarządzać innymi)
        if (task.getAssignedUsers().stream().noneMatch(u -> u.getId().equals(currentUserId))) {
            throw new SecurityException("User not authorized to manage assignments for this task.");
        }

        Set<User> users = new HashSet<>(userRepository.findAllById(userIdsToAssign));
        users.forEach(task::addAssignedUser); // Użyj metody pomocniczej dla spójności
        return taskRepository.save(task);
    }

    @Transactional
    public Task removeUserFromTask(Long taskId, Long userIdToRemove, Long currentUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
        User userToRemove = userRepository.findById(userIdToRemove)
                .orElseThrow(() -> new RuntimeException("User to remove not found: " + userIdToRemove));

        // Sprawdź, czy bieżący użytkownik jest przypisany i może zarządzać
        if (task.getAssignedUsers().stream().noneMatch(u -> u.getId().equals(currentUserId))) {
            throw new SecurityException("User not authorized to manage assignments for this task.");
        }

        // Twórca nie może być usunięty z zadania, które stworzył (chyba że zadanie jest usuwane)
        if (task.getCreator().getId().equals(userIdToRemove)) {
            throw new IllegalArgumentException("Creator cannot be removed from their own task.");
        }

        task.removeAssignedUser(userToRemove); // Użyj metody pomocniczej
        return taskRepository.save(task);
    }


}
