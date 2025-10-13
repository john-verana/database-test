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

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

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
}
