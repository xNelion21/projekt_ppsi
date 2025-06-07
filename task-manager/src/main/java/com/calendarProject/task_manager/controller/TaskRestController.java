package com.calendarProject.task_manager.controller;

import com.calendarProject.task_manager.dto.TaskRequestDTO;
import com.calendarProject.task_manager.dto.TaskResponseDTO;
import com.calendarProject.task_manager.dto.UserSummaryDTO;
import com.calendarProject.task_manager.model.Task;
import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.UserRepository;
import com.calendarProject.task_manager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskRestController {

    private final TaskService taskService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(TaskRestController.class);

    @Autowired
    public TaskRestController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;

    }

    // Metoda pomocnicza do pobrania zalogowanego użytkownika
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("User not authenticated.");
        }
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new SecurityException("Authenticated user not found in database.");
        }
        return user;
    }

    // Metoda pomocnicza do konwersji encji Task na TaskResponseDTO
    private TaskResponseDTO convertToDTO(Task task) {
        if (task == null) return null;
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate() != null ? task.getDueDate().toString() : null);
        dto.setCompleted(task.isCompleted());

        if (task.getCreator() != null) {
            UserSummaryDTO creatorDto = new UserSummaryDTO();
            creatorDto.setId(task.getCreator().getId());
            creatorDto.setEmail(task.getCreator().getEmail());
            dto.setCreator(creatorDto);
        }

        if (task.getAssignedUsers() != null) {
            dto.setAssignedUsers(task.getAssignedUsers().stream().map(user -> {
                UserSummaryDTO userDto = new UserSummaryDTO();
                userDto.setId(user.getId());
                userDto.setEmail(user.getEmail());
                return userDto;
            }).collect(Collectors.toSet()));
        }
        return dto;
    }

    // Metoda pomocnicza do konwersji listy encji Task na listę TaskResponseDTO
    private List<TaskResponseDTO> convertToDTOList(List<Task> tasks) {
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    private ResponseEntity<Task> mapTaskRequestToTaskEntity(TaskRequestDTO taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());

        if (taskRequest.getDueDate() != null && !taskRequest.getDueDate().isEmpty()) {
            try {
                task.setDueDate(LocalDate.parse(taskRequest.getDueDate()));
            } catch (DateTimeParseException e) {

                return ResponseEntity.badRequest().body(null);
            }
        }
        // Ustawienie statusu ukończenia, jeśli jest w DTO
        if (taskRequest.getCompleted() != null) {
            task.setCompleted(taskRequest.getCompleted());
        }
        return ResponseEntity.ok(task); // Zwracamy Task w ciele ResponseEntity.ok()
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO taskRequest) {
        User currentUser = getCurrentUser();
        // Używamy nowej metody pomocniczej
        ResponseEntity<Task> mappedTaskResponse = mapTaskRequestToTaskEntity(taskRequest);
        if (mappedTaskResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.badRequest().body(null); // Błąd parsowania daty
        }
        Task taskToCreate = mappedTaskResponse.getBody();


        assert taskToCreate != null;
        Task createdTask = taskService.createTask(taskToCreate, currentUser.getId(), taskRequest.getAssignedUserIds());
        logger.info("User '{}' created a new task with ID {} and title '{}'.", currentUser.getEmail(), createdTask.getId(), createdTask.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdTask));

    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponseDTO>> getMyTasks() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getTasksForUser(currentUser.getId());
        return ResponseEntity.ok(convertToDTOList(tasks));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        return taskService.getTaskByIdForUser(taskId, currentUser.getId())
                .map(task -> ResponseEntity.ok(convertToDTO(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDTO taskRequest) {
        User currentUser = getCurrentUser();

        // Używamy nowej metody pomocniczej
        ResponseEntity<Task> mappedTaskResponse = mapTaskRequestToTaskEntity(taskRequest);
        if (mappedTaskResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.badRequest().body(null); // Błąd parsowania daty
        }
        Task taskDetails = mappedTaskResponse.getBody();

        // Obsługa assignedUserIds pozostaje tak, jak poprawiliśmy wcześniej
        if (taskRequest.getAssignedUserIds() != null) {
            Set<User> usersToSet = taskRequest.getAssignedUserIds().stream()
                    .map(userId -> userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found.")))
                    .collect(Collectors.toSet());
            assert taskDetails != null;
            taskDetails.setAssignedUsers(usersToSet);
        } else {
            assert taskDetails != null;
            taskDetails.setAssignedUsers(null);
        }

        try {
            Task updatedTask = taskService.updateTask(taskId, taskDetails, currentUser.getId());
            logger.info("User '{}' updated task with ID {}.", currentUser.getEmail(), taskId);
            return ResponseEntity.ok(convertToDTO(updatedTask));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User with ID") || e.getMessage().contains("Task not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{taskId}/toggle-complete")
    public ResponseEntity<TaskResponseDTO> toggleTaskCompletion(@PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        try {
            Task task = taskService.getTaskByIdForUser(taskId, currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Task not found or user not authorized."));

            // Tutaj modyfikujemy tylko status ukończenia na istniejącym obiekcie 'task'
            task.setCompleted(!task.isCompleted());

            // A następnie przekazujemy ten zmodyfikowany obiekt do serwisu
            Task updatedTask = taskService.updateTask(taskId, task, currentUser.getId());

            return ResponseEntity.ok(convertToDTO(updatedTask));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        try {
            taskService.deleteTask(taskId, currentUser.getId());
            logger.info("User '{}' deleted task with ID {}.", currentUser.getEmail(), taskId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{taskId}/assign-users")
    public ResponseEntity<TaskResponseDTO> assignUsersToTask(@PathVariable Long taskId, @RequestBody Set<Long> userIdsToAssign) {
        User currentUser = getCurrentUser();
        try {
            Task updatedTask = taskService.assignUsersToTask(taskId, userIdsToAssign, currentUser.getId());
            return ResponseEntity.ok(convertToDTO(updatedTask));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{taskId}/remove-user/{userIdToRemove}")
    public ResponseEntity<TaskResponseDTO> removeUserFromTask(@PathVariable Long taskId, @PathVariable Long userIdToRemove) {
        User currentUser = getCurrentUser();
        try {
            Task updatedTask = taskService.removeUserFromTask(taskId, userIdToRemove, currentUser.getId());
            return ResponseEntity.ok(convertToDTO(updatedTask));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserSummaryDTO> userDTOs = users.stream().map(user -> {
            UserSummaryDTO dto = new UserSummaryDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }
}