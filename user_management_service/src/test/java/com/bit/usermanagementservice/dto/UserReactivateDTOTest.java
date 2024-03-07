package com.bit.usermanagementservice.dto;

import com.bit.usermanagementservice.dto.kafka.UserReactivateDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserReactivateDTOTest {

    @Test
    void testUserReactivateDTO() {
        UserReactivateDTO userReactivateDTO = new UserReactivateDTO(1L, "password123", false);

        assertEquals(1L, userReactivateDTO.getId());
        assertEquals("password123", userReactivateDTO.getPassword());
        assertFalse(userReactivateDTO.isDeleted());

        assertEquals(1L, userReactivateDTO.getId());
        assertEquals("password123", userReactivateDTO.getPassword());
        assertFalse(userReactivateDTO.isDeleted());
    }
}