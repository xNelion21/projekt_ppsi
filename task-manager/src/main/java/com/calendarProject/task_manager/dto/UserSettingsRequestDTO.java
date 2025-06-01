package com.calendarProject.task_manager.dto;

public class UserSettingsRequestDTO {
    private String nickname;
    private String languagePreference;
    private String themePreference;

    // Gettery i Settery
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getLanguagePreference() { return languagePreference; }
    public void setLanguagePreference(String languagePreference) { this.languagePreference = languagePreference; }
    public String getThemePreference() { return themePreference; }
    public void setThemePreference(String themePreference) { this.themePreference = themePreference; }
}