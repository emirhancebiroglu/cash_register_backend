package com.bit.usermanagementservice.service;

import com.bit.usermanagementservice.dto.adduser.AddUserReq;
import com.bit.usermanagementservice.dto.getuser.UserDTO;
import com.bit.usermanagementservice.dto.updateuser.UpdateUserReq;

import java.util.List;

public interface UserService {
    void addUser(AddUserReq addUserReq);
    void updateUser(Long userId, UpdateUserReq updateUserReq);
    void deleteUser(Long userId);
    void reactivateUser(Long userId);
    List<UserDTO> getUsers(int pageNo, int pageSize);
    List<UserDTO> getDeletedUsers(int pageNo, int pageSize);
    List<UserDTO> searchUserByName(String name, int pageNo, int pageSize);
}
