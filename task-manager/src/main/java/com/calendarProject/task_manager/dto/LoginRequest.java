package com.calendarProject.task_manager.dto;

public class LoginRequest {
    private String email;
    private String password;

    // Konstruktory, gettery i settery są potrzebne dla Jacksona (serializacja/deserializacja JSON)
    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}