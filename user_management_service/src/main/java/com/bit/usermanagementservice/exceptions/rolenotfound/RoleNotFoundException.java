package com.bit.usermanagementservice.exceptions.rolenotfound;

import org.springframework.dao.DataAccessException;

public class RoleNotFoundException extends DataAccessException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
