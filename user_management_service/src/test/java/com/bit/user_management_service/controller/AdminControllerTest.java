package com.bit.user_management_service.controller;

import com.bit.user_management_service.dto.UserDTO;
import com.bit.user_management_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AdminControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUser() {
        UserDTO userDTO = new UserDTO("Emirhan", "Cebiroglu", "emirhancebiroglu21@hotmail.com", "Emirhan2165", Collections.singleton("ADMIN"));

        adminController.addUser(userDTO);

        verify(userService, times(1)).addUser(userDTO);
    }

    @Test
    void testUpdateUser() {
        Long userId = 1L;
        UserDTO userDTO = new UserDTO("Emirhan", "Cebiroglu", "emirhancebiroglu21@hotmail.com", "Emirhan2165", Collections.singleton("ADMIN"));

        adminController.updateUser(userId, userDTO);

        verify(userService, times(1)).updateUser(userId, userDTO);
    }

    @Test
    void testDeleteUser() throws Exception {
        Long userId = 1L;

        adminController.deleteUser(userId);

        verify(userService, times(1)).deleteUser(userId);
    }
}
