package com.bit.usermanagementservice.service;

import com.bit.usermanagementservice.dto.adduser.AddUserReq;
import com.bit.usermanagementservice.dto.getuser.UserDTO;
import com.bit.usermanagementservice.dto.updateuser.UpdateUserReq;

import java.util.List;

/**
 * The UserService interface defines methods for managing users.
 * It provides operations to add, update, delete, reactivate, and retrieve users.
 * Implementations of this interface are responsible for performing user-related operations.
 */
public interface UserService {
    /**
     * Adds a new user.
     *
     * @param addUserReq the request object containing user details to add.
     */
    void addUser(AddUserReq addUserReq);

    /**
     * Updates an existing user.
     *
     * @param userId the ID of the user to update.
     * @param updateUserReq the request object containing updated user details.
     */
    void updateUser(Long userId, UpdateUserReq updateUserReq);

    /**
     * Deletes a user.
     *
     * @param userId the ID of the user to delete.
     */
    void deleteUser(Long userId);

    /**
     * Reactivates a deleted user.
     *
     * @param userId the ID of the user to reactivate.
     */
    void reactivateUser(Long userId);

    /**
     * Retrieves a list of users.
     *
     * @param pageNo      the page number of the result set.
     * @param pageSize    the size of each page in the result set.
     * @param deletedOnly Lists the users that have been deleted if true
     * @return a list of UserDTO objects representing users.
     */
    List<UserDTO> getUsers(int pageNo, int pageSize, boolean deletedOnly);

    /**
     * Searches for users by name.
     *
     * @param name the name to search for.
     * @param pageNo the page number of the result set.
     * @param pageSize the size of each page in the result set.
     * @return a list of UserDTO objects representing users matching the name.
     */
    List<UserDTO> searchUserByName(String name, int pageNo, int pageSize);
}
