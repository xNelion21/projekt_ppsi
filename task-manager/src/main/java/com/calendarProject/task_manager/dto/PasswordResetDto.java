
package com.calendarProject.task_manager.dto;

import jakarta.validation.constraints.NotEmpty;

public class PasswordResetDto {

    @NotEmpty
    private String token;
    @NotEmpty(message = "Nowe hasło nie może być puste.")
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}