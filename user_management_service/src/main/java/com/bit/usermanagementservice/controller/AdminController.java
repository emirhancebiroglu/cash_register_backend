package com.bit.usermanagementservice.controller;

import com.bit.usermanagementservice.dto.adduser.AddUserReq;
import com.bit.usermanagementservice.dto.getuser.UserDTO;
import com.bit.usermanagementservice.dto.updateuser.UpdateUserReq;
import com.bit.usermanagementservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling admin-related user operations.
 */
@RestController
@RequestMapping("/api/users/admin")
@AllArgsConstructor
public class AdminController {
    private UserService userService;

    /**
     * Endpoint for adding a new user.
     */
    @PostMapping("/add-user")
    public ResponseEntity<String> addUser(@RequestBody AddUserReq addUserReq){
        userService.addUser(addUserReq);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    /**
     * Endpoint for updating an existing user.
     */
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId,
                                             @RequestBody UpdateUserReq updateUserReq){
        userService.updateUser(userId, updateUserReq);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
    }

    /**
     * Endpoint for deleting a user.
     */
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    /**
     * Endpoint for reactivating a user.
     */
    @PostMapping("/reactivate-user/{userId}")
    public ResponseEntity<String> reactivateUser(@PathVariable Long userId){
        userService.reactivateUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("User reactivated successfully");
    }

    /**
     * Endpoint for retrieving users.
     */
    @GetMapping("/get-users")
    public List<UserDTO> getUsers(@RequestParam(defaultValue = "0") int pageNo,
                                  @RequestParam(defaultValue = "15") int pageSize,
                                  @RequestParam(defaultValue = "false") boolean deletedOnly){
        return userService.getUsers(pageNo, pageSize, deletedOnly);
    }

    /**
     * Endpoint for searching users by name.
     */
    @GetMapping("/search-user-by-name")
    public List<UserDTO> searchUserByName(@RequestParam String name,
                                          @RequestParam(defaultValue = "0") int pageNo,
                                          @RequestParam(defaultValue = "15") int pageSize){
        return userService.searchUserByName(name, pageNo, pageSize);
    }
}