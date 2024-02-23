package com.bit.user_management_service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PasswordGenerator {
    public String createPassword(String email, Long userId){
        StringBuilder password = new StringBuilder();

        String emailPrefix = email.substring(0, 3).toUpperCase();
        password.append(emailPrefix);

        password.append(userId);

        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            password.append(random.nextInt(10));
        }

        String specialCharacters = "!@#*_,.?";
        char randomSpecialCharacter = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password.append(randomSpecialCharacter);

        return password.toString();
    }

}
