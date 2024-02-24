package com.bit.user_management_service.service;

import com.bit.user_management_service.dto.AddUser.AddUserReq;
import com.bit.user_management_service.dto.UpdateUser.UpdateUserReq;

public interface UserService {
    void addUser(AddUserReq addUserReq);
    void updateUser(Long userId, UpdateUserReq updateUserReq);
    void deleteUser(Long user_id);
    void reactivateUser(Long user_id);
}
