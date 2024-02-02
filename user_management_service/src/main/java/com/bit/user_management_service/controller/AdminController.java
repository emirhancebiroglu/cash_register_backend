package com.bit.user_management_service.controller;

import com.bit.user_management_service.dto.UserDTO;
import com.bit.user_management_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/admin")
@AllArgsConstructor
public class AdminController {
    private UserService userService;

    @PostMapping("/add-user")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@RequestBody UserDTO userDTO){
        userService.addUser(userDTO);
    }

    @PutMapping("/update-user/{user_id}")
    public void updateUser(@PathVariable Long user_id,
                           @RequestBody UserDTO userDTO){
        userService.updateUser(user_id, userDTO);
    }

    @DeleteMapping("/delete-user/{user_id}")
    public void deleteUser(@PathVariable Long user_id) throws Exception{
        userService.deleteUser(user_id);
    }
}
