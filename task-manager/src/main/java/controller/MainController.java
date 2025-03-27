package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "index";  // Nazwa szablonu, np. index.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/loginpage")
    public String loginPage() {
        return "loginpage";
    }
}
