package com.bit.usermanagementservice.controller;

import com.bit.usermanagementservice.dto.AddUser.AddUserReq;
import com.bit.usermanagementservice.dto.UpdateUser.UpdateUserReq;
import com.bit.usermanagementservice.service.UserService;
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
    public ResponseEntity<String> addUser(@RequestBody AddUserReq addUserReq){
        userService.addUser(addUserReq);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @PutMapping("/update-user/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable Long user_id,
                                             @RequestBody UpdateUserReq updateUserReq){
        userService.updateUser(user_id, updateUserReq);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
    }

    @DeleteMapping("/delete-user/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id){
        userService.deleteUser(user_id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    @PostMapping("/reactivate-user/{user_id}")
    public ResponseEntity<String> reactivateUser(@PathVariable Long user_id){
        userService.reactivateUser(user_id);
        return ResponseEntity.status(HttpStatus.OK).body("User reactivated successfully");
    }
}