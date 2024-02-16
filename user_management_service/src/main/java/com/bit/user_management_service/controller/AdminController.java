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
        userService.addUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @PutMapping("/update-user/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable Long user_id,
                           @RequestBody UserDTO userDTO){
        userService.updateUser(user_id, userDTO);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
    }

    @DeleteMapping("/delete-user/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id) throws Exception{
        userService.deleteUser(user_id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }


}
