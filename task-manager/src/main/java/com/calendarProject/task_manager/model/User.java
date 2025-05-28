package com.calendarProject.task_manager.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.List; // Import dla List
import java.util.ArrayList; // Import dla ArrayList
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;
    private String languagePreference = "pl";
    private String themePreference = "light";

    private Integer age;
    private String gender;
    private String profileImageUrl;

    @Column(nullable = false)
    private boolean enabled = true;  // Określa, czy użytkownik jest aktywny

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>(); // Przechowywanie ról użytkownika

    // Konstruktor bezparametrowy (wymagany przez JPA)
    public User() {
    }

    // Konstruktor z parametrami
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Gettery i settery
    public Long getId() {
        return id;
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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;

    }

    // Implementacja metod z UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String role : roles) {
            authorities.add(() -> role);  // Każda rola to uprawnienie
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Jeśli nie planujesz implementować wygasania konta, zwróć true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Jeśli nie planujesz implementować blokowania konta, zwróć true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Jeśli nie planujesz implementować wygasania poświadczeń, zwróć true
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
