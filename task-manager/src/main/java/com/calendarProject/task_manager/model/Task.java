package com.calendarProject.task_manager.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Mapuje na kolumnę "title"

    private String description; // Domyślnie mapuje na kolumnę "description"

    @Column(name = "due_date") // Mapuje na kolumnę "due_date"
    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean completed; // Mapuje na kolumnę "completed"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false) // Mapuje na kolumnę "creator_id"
    private User creator;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_assigned_users", // Nazwa tabeli pośredniczącej
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedUsers = new HashSet<>();

    // Gettery i Settery

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public Set<User> getAssignedUsers() { return assignedUsers; }
    public void setAssignedUsers(Set<User> assignedUsers) { this.assignedUsers = assignedUsers; }

    // Metody pomocnicze do zarządzania przypisanymi użytkownikami
    public void addAssignedUser(User user) {
        this.assignedUsers.add(user);
        if (user.getAssignedTasks() != null) { // Dodatkowe zabezpieczenie przed NullPointerException
            user.getAssignedTasks().add(this);
        }
    }

    public void removeAssignedUser(User user) {
        this.assignedUsers.remove(user);
        if (user.getAssignedTasks() != null) { // Dodatkowe zabezpieczenie
            user.getAssignedTasks().remove(this);
        }
    }
}