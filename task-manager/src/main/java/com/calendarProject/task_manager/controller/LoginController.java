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

    @GetMapping("/index")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "index"; // Zwraca widok "index" (np. index.html z Thymeleaf)
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/index"; // Przekierowuje na /index
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }





}
