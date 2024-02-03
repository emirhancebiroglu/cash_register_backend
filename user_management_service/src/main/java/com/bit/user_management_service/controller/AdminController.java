package com.bit.user_management_service.controller;

import com.bit.shared.entity.Role;
import com.bit.shared.entity.User;
import com.bit.shared.repository.RoleRepository;
import com.bit.shared.repository.UserRepository;
import com.bit.user_management_service.dto.UserDTO;
import com.bit.user_management_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/admin")
@AllArgsConstructor
public class AdminController {
    private UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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

    @GetMapping("/test")
    public String test(){
        List<User> users = userRepository.findAll();
        List<Role> roles = roleRepository.findAll();

        return "Users: " + users + "\n" +
                "Roles: " + roles;
    }
}
