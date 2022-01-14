package com.verzel.motors.database.Validators;

import com.verzel.motors.database.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailValidator {
    private final UserRepository userRepository;

    public EmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean isValid(String email) {
        return !userRepository.existsByEmail(email);
    }
}
