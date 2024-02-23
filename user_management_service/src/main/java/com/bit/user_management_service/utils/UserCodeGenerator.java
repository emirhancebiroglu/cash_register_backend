package com.bit.user_management_service.utils;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserCodeGenerator {
    public String createUserCode(Set<String> roles, Long userId){
        StringBuilder userCode = new StringBuilder();

        for (String role : roles) {
            switch (role) {
                case "ROLE_ADMIN":
                    userCode.append("A");
                    break;
                case "ROLE_CASHIER":
                    userCode.append("C");
                    break;
                case "ROLE_STORE_MANAGER":
                    userCode.append("S");
                    break;
                default:
                    break;
            }
        }

        userCode.append(userId);

        for (int i = 0; i < 6; i++) {
            userCode.append((int) (Math.random() * 10));
        }

        return userCode.toString();
    }
}
