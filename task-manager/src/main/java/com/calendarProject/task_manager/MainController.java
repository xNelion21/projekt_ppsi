package com.calendarProject.task_manager;

import jakarta.servlet.http.HttpSession;
import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Importuj Model
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/loginpage")
    public String loginPage() {
        return "loginpage";
    }

    @GetMapping("/index")
    public String indexPage(HttpSession session, Model model) { // Dodaj Model jako argument
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/loginpage"; // Przekierowanie, jeśli użytkownik nie jest zalogowany
        }

        model.addAttribute("userName", user.getEmail()); // Przekazywanie danych do widoku
        return "index"; // Zwrócenie szablonu index.html
    }
}
