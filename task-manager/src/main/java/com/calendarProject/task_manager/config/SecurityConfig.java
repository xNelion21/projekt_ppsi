package com.calendarProject.task_manager.config;
import com.calendarProject.task_manager.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.calendarProject.task_manager.repository.UserRepository;

@Configuration
public class SecurityConfig {
     final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
//AI MI MOWI ZEBY ZROBIC SWOJ WLASNY SPOSOB LOGOWANIA A POTEM NIC NIE DZIALA

@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // <-- wyłącz ochronę CSRF
                .cors(Customizer.withDefaults())
                .cors(cors -> {})
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers( "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll() // Podmienilam narazie bo wywala 403
                )
            .formLogin(form -> form
                        .loginPage("/loginpage") // Strona logowania
                        .loginProcessingUrl("/login") // URL do obsługi logowania

                    .successHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.sendRedirect("http://localhost:5173/dashboard");
                    })
                    .permitAll()

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
