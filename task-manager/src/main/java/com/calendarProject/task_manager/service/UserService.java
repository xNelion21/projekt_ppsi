package com.calendarProject.task_manager.service;

import com.calendarProject.task_manager.model.User;
import com.calendarProject.task_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // BCrypt do szyfrowania haseł

    public void registerUser(String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email already in use!");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(email, encodedPassword);
        userRepository.save(newUser);
    }



    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
