package com.bit.user_management_service.controller;

import com.bit.user_management_service.dto.UserDTO;
import com.bit.user_management_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/admin")
@AllArgsConstructor
public class AdminController {
    private UserService userService;

    @PostMapping("/add-user")
    public ResponseEntity<String> addUser(@RequestBody UserDTO userDTO){
        try {
            userService.addUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user: " + e.getMessage());
        }
    }

    @PutMapping("/update-user/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable Long user_id,
        @RequestBody UserDTO userDTO){
        try {
            userService.updateUser(user_id, userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-user/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id){
        try {
            userService.deleteUser(user_id);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user: " + e.getMessage());
        }
    }
}
