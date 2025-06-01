package com.calendarProject.task_manager.controller;
import com.calendarProject.task_manager.dto.UserSummaryDTO;
import com.calendarProject.task_manager.dto.LoginRequest;
import com.calendarProject.task_manager.dto.LoginResponse;
import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired; // Można pominąć, jeśli jest tylko jeden konstruktor
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository; // Dobrą praktyką jest uczynienie pola finalnym, jeśli jest wstrzykiwane przez konstruktor

    @Autowired // Adnotacja @Autowired jest opcjonalna, jeśli klasa ma tylko jeden konstruktor (od Spring 4.3)
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository) { // Dodano UserRepository jako parametr
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository; // Teraz Spring wstrzyknie tutaj poprawną instancję
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            return ResponseEntity.ok(new LoginResponse("Użytkownik zalogowany pomyślnie!", email));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(new LoginResponse("Błąd logowania: " + e.getMessage(), loginRequest.getEmail()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body("Użytkownik niezalogowany.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // Sprawdzenie, czy userRepository nie jest null (dodatkowe zabezpieczenie, ale po poprawce nie powinno być potrzebne)
        if (this.userRepository == null) {
            System.err.println("FATAL: UserRepository is still null in getCurrentUser() after constructor fix attempt!");
            return ResponseEntity.status(500).body("Wewnętrzny błąd serwera: Repozytorium użytkowników nie jest dostępne.");
        }
        User currentUser = userRepository.findByEmail(email);


        if (currentUser == null) {
            return ResponseEntity.status(404).body("Użytkownik nie znaleziony.");
        }
        UserSummaryDTO userDto = getUserSummaryDTO(currentUser);

        return ResponseEntity.ok(userDto);
    }

    public static UserSummaryDTO getUserSummaryDTO(User currentUser) {
        UserSummaryDTO userDto = new UserSummaryDTO();
        userDto.setId(currentUser.getId());
        userDto.setEmail(currentUser.getEmail());
        userDto.setNickname(currentUser.getNickname());
        userDto.setLanguagePreference(currentUser.getLanguagePreference());
        userDto.setThemePreference(currentUser.getThemePreference());
        userDto.setAge(currentUser.getAge());
        userDto.setGender(currentUser.getGender());
        userDto.setProfileImageUrl(currentUser.getProfileImageUrl());
        userDto.setRoles(currentUser.getRoles()); // Zakładając, że getRoles() zwraca Set<String>
        return userDto;
    }
}