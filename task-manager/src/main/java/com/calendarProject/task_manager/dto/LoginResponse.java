package com.calendarProject.task_manager.dto;

public class LoginResponse {
    private String message;
    private String email;
    private String nickname;
    private String languagePreference;
    private String themePreference;

    // Możesz tu dodać więcej informacji, np. token JWT, role użytkownika itp.

    public LoginResponse(String message, String email) {
        this.message = message;
        this.email = email;
    }

    public LoginResponse(String message, String email, String nickname, String languagePreference, String themePreference) {
        this.message = message;
        this.email = email;
        this.nickname = nickname;
        this.languagePreference = languagePreference;
        this.themePreference = themePreference;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getLanguagePreference() {
        return languagePreference;
    }
    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    public String getThemePreference() {
        return themePreference;

    }
    public void setThemePreference(String themePreference) {
        this.themePreference = themePreference;
    }




}
