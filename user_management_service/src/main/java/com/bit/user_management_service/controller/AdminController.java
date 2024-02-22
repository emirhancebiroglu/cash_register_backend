package com.bit.user_management_service.controller;

import com.bit.user_management_service.dto.UserDto;
import com.bit.user_management_service.service.UserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<String> addUser(@RequestBody @Valid UserDto UserDto){
        userService.addUser(UserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @PutMapping("/update-user/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable Long user_id,
        @RequestBody UserDto userDto){
        userService.updateUser(user_id, userDto);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
    }

    @DeleteMapping("/delete-user/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id){
        userService.deleteUser(user_id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }
}
