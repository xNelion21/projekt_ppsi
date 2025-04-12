package controller;

import jakarta.servlet.http.HttpSession;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session) {

        User user = userService.getUserByEmail(email);

        if (user != null && userService.checkPassword(user, password)) {
            System.out.println("Zalogowany użytkownik: " + user.getEmail());
            session.setAttribute("user", user); // zapis do sesji
            return "redirect:/index"; // zalogowany -> strona główna
        } else {
            return "redirect:/loginpage?error=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // wylogowanie
        return "redirect:/loginpage";
    }
}
