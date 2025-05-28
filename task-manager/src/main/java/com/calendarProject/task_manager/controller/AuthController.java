package com.calendarProject.task_manager.controller;

import com.calendarProject.task_manager.dto.LoginRequest;
import com.calendarProject.task_manager.dto.LoginResponse;
import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Wspólny prefix dla endpointów autoryzacji
public class AuthController {

    private final AuthenticationManager authenticationManager;
    public  UserRepository userRepository;

    @Autowired // Wstrzykiwanie przez konstruktor jest preferowane
    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
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

            // Po udanym uwierzytelnieniu, możesz pobrać szczegóły użytkownika
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername(); // Zakładając, że username to email

            // Tutaj w przyszłości możesz generować i zwracać token JWT
            // Na razie zwracamy prostą odpowiedź
            return ResponseEntity.ok(new LoginResponse("Użytkownik zalogowany pomyślnie!", email));

        } catch (Exception e) {

            return ResponseEntity.status(401).body(new LoginResponse("Błąd logowania: " + e.getMessage(), loginRequest.getEmail()));
        }
    }

    @GetMapping("/me") // Nowy endpoint do pobierania danych zalogowanego użytkownika
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body("Użytkownik niezalogowany.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User currentUser = userRepository.findByEmail(email);

        if (currentUser == null) {
            return ResponseEntity.status(404).body("Użytkownik nie znaleziony.");
        }

        // Zwracamy obiekt User (możesz stworzyć DTO, jeśli nie chcesz zwracać hasła itp.)
        // Na potrzeby tego przykładu zwrócimy pełny obiekt User, ale w realnej aplikacji lepiej DTO.
        return ResponseEntity.ok(currentUser);
    }





}