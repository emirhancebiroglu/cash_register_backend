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

    @PutMapping("/update-user/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId,
                                             @RequestBody UpdateUserReq updateUserReq){
        userService.updateUser(userId, updateUserReq);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    @PostMapping("/reactivate-user/{userId}")
    public ResponseEntity<String> reactivateUser(@PathVariable Long userId){
        userService.reactivateUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("User reactivated successfully");
    }

    @GetMapping("/get-users")
    public List<UserDTO> getUsers(@RequestParam(defaultValue = "0") int pageNo,
                                  @RequestParam(defaultValue = "15") int pageSize){
        return userService.getUsers(pageNo, pageSize);
    }

    @GetMapping("/get-deleted-users")
    public List<UserDTO> getDeletedUsers(@RequestParam(defaultValue = "0") int pageNo,
                                         @RequestParam(defaultValue = "15") int pageSize){
        return userService.getDeletedUsers(pageNo, pageSize);
    }

    @GetMapping("/search-user-by-name")
    public List<UserDTO> searchUserByName(@RequestParam String name,
                                          @RequestParam(defaultValue = "0") int pageNo,
                                          @RequestParam(defaultValue = "15") int pageSize){
        return userService.searchUserByName(name, pageNo, pageSize);
    }
}