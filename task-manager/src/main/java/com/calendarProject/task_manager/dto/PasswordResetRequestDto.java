// Utwórz plik PasswordResetRequestDto.java w paczce dto
package com.calendarProject.task_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;


public class PasswordResetRequestDto {
    @NotEmpty(message = "Adres e-mail nie może być pusty.")
    @Email(message = "Proszę podać poprawny format adresu e-mail.")
    private String email;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}