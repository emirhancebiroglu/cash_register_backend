package com.bit.usermanagementservice.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserCodeGeneratorTest {
    @Test
    void createUserCode(){
        UserCodeGenerator userCodeGenerator = new UserCodeGenerator();

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_STORE_MANAGER");

        Long userId = 1L;

        String userCode = userCodeGenerator.createUserCode(roles, userId);

        assertNotNull(userCode);
        assertTrue(userCode.startsWith("AS"));
        assertTrue(userCode.contains(userId.toString()));
    }
}
