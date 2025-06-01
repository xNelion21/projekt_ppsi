
package com.calendarProject.task_manager.dto;

import java.util.Set;

public class TaskRequestDTO {
    private String title;
    private String description;
    private String text;
    private String dueDate; // Represented as String (e.g., "YYYY-MM-DD") for request
    private Boolean completed; // For updating task completion status
    private Set<Long> assignedUserIds; // IDs of users to assign/reassign

    // Getters and Setters
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Set<Long> getAssignedUserIds() {
        return assignedUserIds;
    }

    public void setAssignedUserIds(Set<Long> assignedUserIds) {
        this.assignedUserIds = assignedUserIds;
    }
}