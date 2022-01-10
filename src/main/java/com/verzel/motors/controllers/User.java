package com.verzel.motors.controllers;

import com.verzel.motors.database.UserModel;
import com.verzel.motors.database.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/users")
public class User {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public User(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @PostMapping("/")
    public ResponseEntity<UserModel> create(@RequestBody UserModel user) {
        String userEmail = user.getEmail();

        return (ResponseEntity<UserModel>) badRequest();
    }

    @GetMapping("/")
    public Object view(@RequestParam String email, @RequestParam String password) {
        Optional<UserModel> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not foud email");
        }

        boolean valid = false;

        valid = encoder.matches(password, user.get().getPassword());

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        return ResponseEntity.ok(user);
    }
}
