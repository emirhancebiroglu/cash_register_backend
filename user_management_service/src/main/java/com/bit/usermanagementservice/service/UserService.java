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
     * Retrieves a list of active or inactive users with pagination support.
     * This method fetches users from the database based on the provided page number and page size.
     *
     * @param pageNo        The page number of the results to retrieve.
     * @param pageSize      The number of users per page.
     * @param status        Lists the users by their status (deleted or not)
     * @param searchingTerm The search term to receive users by their names
     * @param sortBy        Declares how to sort users
     * @return A list of UserDTO objects representing the users on the specified page.
     */
    List<UserDTO> getUsers(int pageNo, int pageSize, String status, String searchingTerm, String sortBy, String deletedOnly);
}
