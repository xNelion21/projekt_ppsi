package com.calendarProject.task_manager;

import jakarta.servlet.http.HttpSession;
import com.calendarProject.task_manager.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Importuj Model
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/loginpage")
    public String loginPage() {
        return "loginpage";
    }


}
