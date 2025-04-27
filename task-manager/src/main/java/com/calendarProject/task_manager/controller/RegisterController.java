package com.calendarProject.task_manager.controller;
import com.calendarProject.task_manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {
    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String password,
                               @RequestParam String confirmPassword, Model model) {

        // Sprawdzenie, czy hasła się zgadzają
        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=Passwords do not match";
        }

        try {
            // Rejestracja użytkownika przez UserService
            userService.registerUser(email, password);
            return "redirect:/loginpage?registered";
        } catch (RuntimeException e) {
            return "redirect:/register?error=" + e.getMessage(); }

    }

}
