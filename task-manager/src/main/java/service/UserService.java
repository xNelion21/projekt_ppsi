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
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email already in use!");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(email, encodedPassword);
        userRepository.save(newUser);
    }

    // ⬇⬇⬇ TE DWIE METODY DODAJ ⬇⬇⬇

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
