package com.bit.usermanagementservice.dto;

import com.bit.usermanagementservice.dto.kafka.UserCredentialsDTO;
import com.bit.usermanagementservice.entity.Role;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserCredentialsDTOTest {

    @Test
    void testUserCredentialsDTO() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);

        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO(1L, "user123", "password123", roles, true);

        assertEquals(1L, userCredentialsDTO.getId());
        assertEquals("user123", userCredentialsDTO.getUserCode());
        assertEquals("password123", userCredentialsDTO.getPassword());
        assertEquals(2, userCredentialsDTO.getRoles().size());
        assertTrue(userCredentialsDTO.isDeleted());

        assertEquals(1L, userCredentialsDTO.getId());
        assertEquals("user123", userCredentialsDTO.getUserCode());
        assertEquals("password123", userCredentialsDTO.getPassword());
        assertEquals(roles, userCredentialsDTO.getRoles());
        assertTrue(userCredentialsDTO.isDeleted());
    }
}
