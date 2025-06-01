
package com.calendarProject.task_manager.dto;

import java.util.Set;

public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String text;
    private String dueDate; // Represented as String (e.g., "YYYY-MM-DD") for response
    private boolean completed;
    private UserSummaryDTO creator;
    private Set<UserSummaryDTO> assignedUsers;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public UserSummaryDTO getCreator() {
        return creator;
    }

    public void setCreator(UserSummaryDTO creator) {
        this.creator = creator;
    }

    public Set<UserSummaryDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<UserSummaryDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
}