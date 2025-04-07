package controller;

import service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    // Obsługuje GET request, wyświetlając formularz rejestracji
    @GetMapping("/register")
    public ModelAndView showRegistrationForm() {
        return new ModelAndView("register");  // Nazwa widoku rejestracji (HTML)
    }

    // Obsługuje POST request, rejestrując użytkownika
    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String password) {
        try {
            userService.registerUser(email, password);
            return "redirect:/index.html";  // Przekierowanie na stronę index
        } catch (Exception e) {
            return "register";  // Zwrócenie formularza rejestracji w przypadku błędu
        }
    }
}
