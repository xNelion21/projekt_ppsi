package controller;

import model.User;
import service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;


    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String password, Model model) {

        if (userService.findByEmail(email) != null) {
            model.addAttribute("error", "Użytkownik o tym e-mailu już istnieje.");
            return "register";
        }

        // Tworzenie nowego użytkownika
        User user = new User(email, password);
        userService.saveUser(user);
        return "redirect:/login";
    }
}
