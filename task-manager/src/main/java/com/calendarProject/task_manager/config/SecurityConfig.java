package com.calendarProject.task_manager.config;

import com.calendarProject.task_manager.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.calendarProject.task_manager.repository.UserRepository;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Użytkownik DetailsService bez implementacji interfejsu
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // Ładowanie użytkownika z bazy danych
            User user = userRepository.findByEmail(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + username);
            }
            return user;
        };
    }

    // Konfiguracja bezpieczeństwa bez przestarzałych metod
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/register", "/login", "/loginpage").permitAll() // Pozwól na dostęp do stron rejestracji i logowania
                        .anyRequest().authenticated() // Reszta stron wymaga zalogowania
                )
                .formLogin(form -> form
                        .loginPage("/loginpage") // Strona logowania
                        .loginProcessingUrl("/login") // URL do obsługi logowania
                        .defaultSuccessUrl("/index", true) // Po udanym logowaniu przekierowanie
                        .permitAll() // Zezwól na dostęp do strony logowania
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // URL dla wylogowania
                        .permitAll() // Zezwól na dostęp do strony wylogowywania
                );

        return http.build();
    }

    // Konfiguracja PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
