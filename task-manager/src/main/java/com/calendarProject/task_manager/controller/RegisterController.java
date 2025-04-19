package com.calendarProject.task_manager.controller;

import com.calendarProject.task_manager.model.User;
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

    public RegisterController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }



    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String password,
                               @RequestParam String confirmPassword, Model model) {

        // Sprawdzenie, czy hasła się zgadzają
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Hasła nie pasują do siebie.");
            return "register";
        }

        try {
            // Rejestracja użytkownika przez UserService
            userService.registerUser(email, password);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

        // Po udanej rejestracji przekierowanie na stronę logowania
        return "redirect:/login";
    }

}
