package com.verzel.motors.controllers;

import com.verzel.motors.Services.S3Service;
import com.verzel.motors.database.UserModel;
import com.verzel.motors.database.UserRepository;
import com.verzel.motors.database.Validators.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class User {
    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    private final PasswordEncoder encoder;
    private final S3Service s3Service;

    public User(UserRepository userRepository, EmailValidator emailValidator, PasswordEncoder encoder, S3Service s3Service) {
        this.userRepository = userRepository;
        this.emailValidator = emailValidator;
        this.encoder = encoder;
        this.s3Service = s3Service;
    }

    @PostMapping()
    public Object create(@RequestBody @Valid UserModel user) {
        String userEmail = user.getEmail();
        String password = user.getPassword();

        boolean isValid = emailValidator.isValid(userEmail);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("already exists");
        }

        String filename = UUID.randomUUID() +"-"+ user.getAvatar();
        user.setAvatar(filename);

        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(s3Service.putPresignedUrl(filename));
    }

    @GetMapping()
    public Object view(@RequestParam String email) {
        Optional<UserModel> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found email");
        }

        URL url = s3Service.getPresignedUrl(user.get().getAvatar());

        user.orElseThrow().setAvatar(url.toString());
        user.orElseThrow().setPassword("*");

        return ResponseEntity.ok(user);
    }

    @PatchMapping()
    public Object update(@RequestBody @Valid UserModel user) {
        UserModel userModel = userRepository.getById(user.getId());

        String filename = UUID.randomUUID() +"-"+ user.getAvatar();
        user.setAvatar(filename);

        user.setPassword(userModel.getPassword());
        userRepository.save(user);

        return ResponseEntity.ok(s3Service.putPresignedUrl(filename));
    }
}
