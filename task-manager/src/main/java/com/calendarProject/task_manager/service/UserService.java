package com.calendarProject.task_manager.service;
import java.time.LocalDateTime;
import java.util.Optional;

import com.calendarProject.task_manager.dto.UserProfileRequestDTO;
import com.calendarProject.task_manager.dto.UserSettingsRequestDTO;
import com.calendarProject.task_manager.model.PasswordResetToken;
import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.PasswordResetTokenRepository;
import com.calendarProject.task_manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordResetTokenRepository tokenRepository,
                       EmailService emailService,
                       PasswordEncoder passwordEncoder) { // <-- Dodajemy PasswordEncoder do konstruktora
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initiatePasswordReset(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("Użytkownik z podanym adresem e-mail nie istnieje.");
        }  String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void completePasswordReset(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Nieprawidłowy lub zużyty token resetujący."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken); // Jeśli wygasł, usuwamy go.
            throw new RuntimeException("Token resetujący wygasł. Proszę ponownie zażądać resetu hasła.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }


    private void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);
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
            return userRepository.save(user);
        }
        return user;
    }

    @Transactional
    public User updateUserProfile(Long userId, UserProfileRequestDTO profileRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        User user = userOptional.get();

        boolean changed = false;

        if ((profileRequest.getAge() != null && !profileRequest.getAge().equals(user.getAge())) || (profileRequest.getAge() == null && user.getAge() != null)) {
            user.setAge(profileRequest.getAge());
            changed = true;
        }

        if ((profileRequest.getGender() != null && !profileRequest.getGender().equals(user.getGender())) || (profileRequest.getGender() == null && user.getGender() != null)) {
            user.setGender(profileRequest.getGender());
            changed = true;
        }

        if (changed) {
            return userRepository.save(user);
        }
        return user;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void saveUser(User user) {
        userRepository.save(user);
    }

}
