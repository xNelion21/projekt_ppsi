package service;

import model.User;
import repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;  // BCrypt do szyfrowania haseł

    public void registerUser(String email, String password) {
        // Sprawdzamy, czy użytkownik już istnieje
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email already in use!");
        }

        // Szyfrowanie hasła
        String encodedPassword = passwordEncoder.encode(password);

        // Tworzymy nowego użytkownika i zapisujemy w bazie
        User newUser = new User(email, encodedPassword);
        userRepository.save(newUser);
    }
}
