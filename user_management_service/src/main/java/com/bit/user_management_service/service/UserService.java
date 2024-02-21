package com.bit.user_management_service.service;


import com.bit.user_management_service.dto.UserDto;

public interface UserService {
    void addUser(UserDto UserDto);
    void updateUser(Long user_id, UserDto UserDto) throws Exception;
    void deleteUser(Long user_id) throws Exception;
}
