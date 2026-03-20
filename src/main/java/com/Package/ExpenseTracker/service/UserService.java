package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String name, String email, String password) {
        log.info("Registering user: {}", email);

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        User saved = userRepository.save(user);
        log.info("User registered: id={}, email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    public User login(String email, String password) {
        log.info("Login attempt: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        log.info("Login successful: {}", email);
        return user;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}