package com.verzel.motors.controllers;

import com.verzel.motors.database.UserModel;
import com.verzel.motors.database.UserRepository;
import com.verzel.motors.database.Validators.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class User {
    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    private final PasswordEncoder encoder;

    public User(UserRepository userRepository, EmailValidator emailValidator, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.emailValidator = emailValidator;
        this.encoder = encoder;
    }

    @PostMapping()
    public Object create(@RequestBody @Valid UserModel user) {
        String userEmail = user.getEmail();
        String password = user.getPassword();

        boolean isValid = emailValidator.isValid(userEmail);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping()
    public Object view(@RequestParam String email, @RequestParam String password) {
        Optional<UserModel> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found email");
        }

        boolean valid = false;

        valid = encoder.matches(password, user.get().getPassword());

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        return ResponseEntity.ok(user);
    }
}
