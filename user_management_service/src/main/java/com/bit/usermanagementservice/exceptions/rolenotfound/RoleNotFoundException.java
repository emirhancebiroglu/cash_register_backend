package com.bit.usermanagementservice.exceptions.rolenotfound;

import org.springframework.dao.DataAccessException;

/**
 * Custom exception indicating that a role was not found.
 */
public class RoleNotFoundException extends DataAccessException {
    /**
     * Constructs a new RoleNotFoundException with the specified detail message.
     * @param message the detail message.
     */
    public RoleNotFoundException(String message) {
        super(message);
    }
}
