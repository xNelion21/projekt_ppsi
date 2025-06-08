package com.calendarProject.task_manager.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class SecurityConfig {

@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // <-- wyłącz ochronę CSRF
                .cors(Customizer.withDefaults())
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
                        System.out.println("User: " + authentication.getName() + " zalogowany pomyślnie. Przekierowanie na dashboard.");
                        response.sendRedirect("http://localhost:5173/dashboard");
                    })
                    .permitAll()

                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // URL dla wylogowania
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK); // Zapewnij status OK
                            response.sendRedirect("http://localhost:8080/loginpage"); // Przekierowanie po wylogowaniu
                        })
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
