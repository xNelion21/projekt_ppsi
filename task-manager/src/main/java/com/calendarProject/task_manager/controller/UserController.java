package com.calendarProject.task_manager.controller;
import com.calendarProject.task_manager.dto.UserSummaryDTO;
import com.calendarProject.task_manager.dto.UserSettingsRequestDTO;
import com.calendarProject.task_manager.dto.UserProfileRequestDTO;
import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.UserRepository;
import com.calendarProject.task_manager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${file.base-upload-dir}")
    private String baseUploadDir;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
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
        try {
            // ZMIEŃ TĘ LINIĘ: Wywołaj metodę w UserService, przekazując DTO
            User updatedUser = userService.updateUserSettings(currentUser.getId(), settingsRequest);
            logger.info("Ustawienia użytkownika {} zostały zaktualizowane.", updatedUser.getEmail());
            return ResponseEntity.ok(AuthController.getUserSummaryDTO(updatedUser));
        } catch (RuntimeException e) {
            logger.error("Błąd podczas aktualizacji ustawień dla użytkownika {}: {}", currentUser.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PutMapping("/me/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileRequestDTO profileRequest) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            // ZMIEŃ TĘ LINIĘ: Wywołaj metodę w UserService, przekazując DTO
            User updatedUser = userService.updateUserProfile(currentUser.getId(), profileRequest);
            logger.info("Profil użytkownika {} został zaktualizowany.", updatedUser.getEmail());
            return ResponseEntity.ok(AuthController.getUserSummaryDTO(updatedUser));
        } catch (RuntimeException e) {
            logger.error("Błąd podczas aktualizacji profilu dla użytkownika {}: {}", currentUser.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
            Path profileUploadPath = Paths.get(baseUploadDir, "profile_images");
            if (!Files.exists(profileUploadPath)) {
                Files.createDirectories(profileUploadPath);
            }
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = profileUploadPath.resolve(newFilename);

            Files.copy(file.getInputStream(), filePath);

            // UJEDNOLICENIE ŚCIEŻEK: Ścieżka URL, którą frontend będzie używał.
            // Ważne: /uploads/ jest mapowane w WebConfig na baseUploadDir.
            // "/profile_images/" jest podfolderem, który stworzyliśmy fizycznie i logicznie.
            String imageUrl = "/uploads/profile_images/" + newFilename;

            // DELEGUJ ZAPIS DO BAZY DANYCH DO SERWISU
            userService.updateProfileImageUrl(currentUser.getId(), imageUrl);

            logger.info("Zdjęcie profilowe dla użytkownika {} zostało przesłane pomyślnie. URL: {}", currentUser.getEmail(), imageUrl);
            return ResponseEntity.ok("Zdjęcie profilowe zostało przesłane pomyślnie.");
        } catch (IOException e) {
            logger.error("Nie udało się przesłać pliku dla użytkownika {}: {}", (currentUser.getEmail()), e.getMessage(), e);
            return ResponseEntity.status(500).body("Nie udało się przesłać pliku: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Wystąpił nieoczekiwany błąd podczas przesyłania zdjęcia dla użytkownika {}: {}", currentUser.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wystąpił nieoczekiwany błąd: " + e.getMessage());
        }
    }
}