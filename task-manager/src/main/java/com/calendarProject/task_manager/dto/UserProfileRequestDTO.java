package com.calendarProject.task_manager.dto;

public class UserProfileRequestDTO {
    private Integer age;
    private String gender;

    // Gettery i Settery
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}