package com.bit.usermanagementservice.controller;

import com.bit.usermanagementservice.dto.adduser.AddUserReq;
import com.bit.usermanagementservice.dto.getuser.UserDTO;
import com.bit.usermanagementservice.dto.updateuser.UpdateUserReq;
import com.bit.usermanagementservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class AdminControllerTest {
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
        AddUserReq addUserReq = new AddUserReq();

        ResponseEntity<String> response = adminController.addUser(addUserReq);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", response.getBody());
        verify(userService, times(1)).addUser(addUserReq);
    }

    @Test
    void testUpdateUser() {
        Long userId = 1L;
        UpdateUserReq updateUserReq = new UpdateUserReq();

        ResponseEntity<String> response = adminController.updateUser(userId, updateUserReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated successfully", response.getBody());
        verify(userService, times(1)).updateUser(userId, updateUserReq);
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;

        ResponseEntity<String> response = adminController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void testReactivateUser() {
        Long userId = 1L;

        ResponseEntity<String> response = adminController.reactivateUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User reactivated successfully", response.getBody());
        verify(userService, times(1)).reactivateUser(userId);
    }

    @Test
    void testGetUsers() {
        List<UserDTO> expectedUsers = new ArrayList<>();
        UserDTO user1 = new UserDTO();
        UserDTO user2 = new UserDTO();
        expectedUsers.add(user1);
        expectedUsers.add(user2);

        when(userService.getUsers(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(expectedUsers);

        adminController.getUsers(0, 15, null, null, null, null);

        verify(userService, times(1)).getUsers(0, 15, null, null, null, null);
    }

}
