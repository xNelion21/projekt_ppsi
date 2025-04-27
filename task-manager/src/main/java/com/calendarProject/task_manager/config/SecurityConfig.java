package com.calendarProject.task_manager.config;


import com.calendarProject.task_manager.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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


    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // <-- wyłącz ochronę CSRF
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/register", "/register", "/loginpage", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .anyRequest().permitAll() // Podmienilam narazie bo wywala 403
                )
          /*  .formLogin(form -> form
                        .loginPage("/loginpage") // Strona logowania
                        .loginProcessingUrl("/login") // URL do obsługi logowania
                        .defaultSuccessUrl("/index", true) // Po udanym logowaniu przekierowanie
                        .permitAll() // Zezwól na dostęp do strony logowania
                ) */
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
