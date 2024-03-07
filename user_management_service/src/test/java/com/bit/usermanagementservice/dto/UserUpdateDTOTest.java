package com.bit.usermanagementservice.dto;

import com.bit.usermanagementservice.dto.kafka.UserUpdateDTO;
import com.bit.usermanagementservice.entity.Role;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserUpdateDTOTest {
    @Test
    void testUserUpdateDTO() {
        // Create roles
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(1L, "testemail@gmail.com", "user123", roles);

        assertEquals(1L, userUpdateDTO.getId());
        assertEquals("testemail@gmail.com", userUpdateDTO.getEmail());
        assertEquals("user123", userUpdateDTO.getUserCode());
        assertEquals(2, userUpdateDTO.getRoles().size());

        assertEquals(1L, userUpdateDTO.getId());
        assertEquals("testemail@gmail.com", userUpdateDTO.getEmail());
        assertEquals("user123", userUpdateDTO.getUserCode());
        assertEquals(roles, userUpdateDTO.getRoles());
    }
}
