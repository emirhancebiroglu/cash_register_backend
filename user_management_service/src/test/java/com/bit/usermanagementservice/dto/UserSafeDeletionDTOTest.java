package com.bit.usermanagementservice.dto;

import com.bit.usermanagementservice.dto.kafka.UserSafeDeletionDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserSafeDeletionDTOTest {

    @Test
    void testUserSafeDeletionDTO() {
        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO(1L, true);

        assertEquals(1L, userSafeDeletionDTO.getId());
        assertTrue(userSafeDeletionDTO.isDeleted());

        assertEquals(1L, userSafeDeletionDTO.getId());
        assertTrue(userSafeDeletionDTO.isDeleted());
    }
}