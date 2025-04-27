package com.calendarProject.task_manager.controller;


import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DebugController {

    private final UserService userService;

    @Autowired
    public DebugController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/debug/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();  // musimy dodać tę metodę w serwisie
    }
    @GetMapping("/debug/addTestUser")
    public String addTestUser() {
        userService.registerUser("ror@xyz.pl", "1212");
        return "User added!";
    }

}
