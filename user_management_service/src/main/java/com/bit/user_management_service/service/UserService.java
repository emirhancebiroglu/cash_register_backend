package com.bit.user_management_service.service;


import com.bit.user_management_service.dto.UserDTO;

public interface UserService {
    void addUser(UserDTO userDTO);
    void updateUser(Long user_id, UserDTO userDTO);
    void deleteUser(Long user_id) throws Exception;
}
