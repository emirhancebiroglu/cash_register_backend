package com.bit.usermanagementservice.service;

import com.bit.usermanagementservice.dto.AddUser.AddUserReq;
import com.bit.usermanagementservice.dto.UpdateUser.UpdateUserReq;

public interface UserService {
    void addUser(AddUserReq addUserReq);
    void updateUser(Long userId, UpdateUserReq updateUserReq);
    void deleteUser(Long user_id);
    void reactivateUser(Long user_id);
}
