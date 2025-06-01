package com.calendarProject.task_manager.controller;
import com.calendarProject.task_manager.dto.UserSummaryDTO;
import com.calendarProject.task_manager.dto.UserSettingsRequestDTO;
import com.calendarProject.task_manager.dto.UserProfileRequestDTO;
import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private static final String UPLOAD_DIR = "uploads/profile_images/"; // Katalog do przechowywania zdjęć
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Pomocnicza metoda do pobierania aktualnie zalogowanego użytkownika
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Brak zalogowanego użytkownika
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername());
    }

    @PutMapping("/me/settings")
    public ResponseEntity<?> updateUserSettings(@RequestBody UserSettingsRequestDTO settingsRequest) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean changed = false;
        if (settingsRequest.getNickname() != null && !settingsRequest.getNickname().equals(currentUser.getNickname())) {
            currentUser.setNickname(settingsRequest.getNickname());
            changed = true;
        }
        if (settingsRequest.getLanguagePreference() != null && !settingsRequest.getLanguagePreference().equals(currentUser.getLanguagePreference())) {
            currentUser.setLanguagePreference(settingsRequest.getLanguagePreference());
            changed = true;
        }
        if (settingsRequest.getThemePreference() != null && !settingsRequest.getThemePreference().equals(currentUser.getThemePreference())) {
            currentUser.setThemePreference(settingsRequest.getThemePreference());
            changed = true;
        }

        if (changed) {
            userRepository.save(currentUser);
            logger.info("Ustawienia użytkownika {} zostały zaktualizowane.", currentUser.getEmail());
        } else {
            logger.info("Brak zmian w ustawieniach dla użytkownika {}.", currentUser.getEmail());
        }
        // Zawsze zwracaj UserSummaryDTO, nawet jeśli nie było zmian, aby frontend miał aktualne dane
        return ResponseEntity.ok(AuthController.getUserSummaryDTO(currentUser)); // Lub UserMapper.toUserSummaryDTO(currentUser)
    }


    @PutMapping("/me/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileRequestDTO profileRequest) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean changed = false;
        if (profileRequest.getAge() != null && !profileRequest.getAge().equals(currentUser.getAge())) {
            currentUser.setAge(profileRequest.getAge());
            changed = true;
        }
        if (profileRequest.getGender() != null && !profileRequest.getGender().equals(currentUser.getGender())) {
            currentUser.setGender(profileRequest.getGender());
            changed = true;
        }

        if (changed) {
            userRepository.save(currentUser);
            logger.info("Profil użytkownika {} został zaktualizowany.", currentUser.getEmail());
        } else {
            logger.info("Brak zmian w profilu dla użytkownika {}.", currentUser.getEmail());
        }
        return ResponseEntity.ok(AuthController.getUserSummaryDTO(currentUser)); // Lub UserMapper.toUserSummaryDTO(currentUser)
    }


    @PostMapping("/me/profile/upload-image")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Proszę wybrać plik do przesłania.");
        }

        try {
            // Upewnij się, że katalog do uploadu istnieje
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Unikalna nazwa pliku, aby uniknąć kolizji
            String fileName = currentUser.getId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Zapisz ścieżkę do obrazu w bazie danych
            currentUser.setProfileImageUrl("/" + UPLOAD_DIR + fileName); // Ścieżka URL dostępna z frontendu
            userRepository.save(currentUser);

            return ResponseEntity.ok("Zdjęcie profilowe zostało przesłane pomyślnie.");
        } catch (IOException e) {
            logger.error("Nie udało się przesłać pliku dla użytkownika {}: {}", (currentUser.getEmail()), e.getMessage(), e);
            return ResponseEntity.status(500).body("Nie udało się przesłać pliku: " + e.getMessage());
        }
    }
}