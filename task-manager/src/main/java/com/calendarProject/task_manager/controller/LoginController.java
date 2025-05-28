package com.calendarProject.task_manager.controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import com.calendarProject.task_manager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.calendarProject.task_manager.service.UserService;

@Controller
public class LoginController {



  /*  @PostMapping("/login")
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
    } */

    @GetMapping("/index")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/loginpage";
        }
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // wylogowanie
        return "redirect:/login";
    }


    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/index";
    }

}
