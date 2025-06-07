package com.calendarProject.task_manager.service;
import java.util.Optional;

import com.calendarProject.task_manager.dto.UserProfileRequestDTO;
import com.calendarProject.task_manager.dto.UserSettingsRequestDTO;
import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUserSettings(Long userId, UserSettingsRequestDTO settingsRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        User user = userOptional.get();

        boolean changed = false;
        if (settingsRequest.getNickname() != null && !settingsRequest.getNickname().equals(user.getNickname())) {
            user.setNickname(settingsRequest.getNickname());
            changed = true;
        }
        if (settingsRequest.getLanguagePreference() != null && !settingsRequest.getLanguagePreference().equals(user.getLanguagePreference())) {
            user.setLanguagePreference(settingsRequest.getLanguagePreference());
            changed = true;
        }
        if (settingsRequest.getThemePreference() != null && !settingsRequest.getThemePreference().equals(user.getThemePreference())) {
            user.setThemePreference(settingsRequest.getThemePreference());
            changed = true;
        }

        if (changed) {
            return userRepository.save(user); // Zapisz tylko jeśli były zmiany
        }
        return user; // Zwróć niezmienionego użytkownika, jeśli brak zmian
    }

    @Transactional
    public User updateUserProfile(Long userId, UserProfileRequestDTO profileRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        User user = userOptional.get();

        boolean changed = false;
        // Logic to handle age updates, including null values
        if ((profileRequest.getAge() != null && !profileRequest.getAge().equals(user.getAge())) || (profileRequest.getAge() == null && user.getAge() != null)) {
            user.setAge(profileRequest.getAge());
            changed = true;
        }
        // Logic to handle gender updates, including null values
        if ((profileRequest.getGender() != null && !profileRequest.getGender().equals(user.getGender())) || (profileRequest.getGender() == null && user.getGender() != null)) {
            user.setGender(profileRequest.getGender());
            changed = true;
        }

        if (changed) {
            return userRepository.save(user); // Zapisz tylko jeśli były zmiany
        }
        return user; // Zwróć niezmienionego użytkownika, jeśli brak zmian
    }





    public void registerUser(String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email already in use!");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(email, encodedPassword);
        saveUser(newUser);
    }

    @Transactional
    public User updateProfileImageUrl(Long userId, String imageUrl) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        User user = userOptional.get();
        user.setProfileImageUrl(imageUrl);
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

}
