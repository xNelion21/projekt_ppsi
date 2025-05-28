package com.calendarProject.task_manager.controller;

import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Do obsługi przesyłania plików (zdjęcie profilowe)

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
    public ResponseEntity<?> updateUserSettings(@RequestBody User updatedUser) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Użytkownik niezalogowany.");
        }

        // Aktualizuj tylko dozwolone pola ustawień
        currentUser.setNickname(updatedUser.getNickname());
        currentUser.setLanguagePreference(updatedUser.getLanguagePreference());
        currentUser.setThemePreference(updatedUser.getThemePreference());

        userRepository.save(currentUser);
        return ResponseEntity.ok(currentUser);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody User updatedUser) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Użytkownik niezalogowany.");
        }

        // Aktualizuj tylko dozwolone pola profilu
        currentUser.setAge(updatedUser.getAge());
        currentUser.setGender(updatedUser.getGender());

        userRepository.save(currentUser);
        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/me/profile/upload-image")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Użytkownik niezalogowany.");
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
            e.printStackTrace();
            return ResponseEntity.status(500).body("Nie udało się przesłać pliku: " + e.getMessage());
        }
    }
}