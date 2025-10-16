package com.fifabest.icons.controller;


import com.fifabest.icons.domain.User;
import com.fifabest.icons.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Show login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Show register page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // Processes login form submission
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "found", false,
                    "message", "Username is required."
            ));
        }

        return userRepository.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst()
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(Map.of(
                        "found", true,
                        "message", "Welcome back, " + username + "!"
                )))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of(
                        "found", false,
                        "message", "User not found. You can register."
                )));

    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");

        if (username == null || username.isBlank() || email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Username and email are required."
            ));
        }

        // Basic validation (you can enhance later)
        if (!username.matches("^[a-zA-Z0-9._-]{3,20}$")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid username format."
            ));
        }

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid email format."
            ));
        }

        // Check for duplicates
        boolean exists = userRepository.findAll().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username.trim()) ||
                        u.getEmail().equalsIgnoreCase(email.trim()));

        if (exists) {
            return ResponseEntity.status(409).body(Map.of(
                    "success", false,
                    "message", "Username or email already exists."
            ));
        }

        // Create new user
        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setCreatedAt(java.time.LocalDateTime.now());

        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Account created successfully!"
        ));
    }

}

