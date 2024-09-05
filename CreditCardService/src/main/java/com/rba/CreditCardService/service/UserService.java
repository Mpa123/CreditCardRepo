package com.rba.CreditCardService.service;

import com.rba.CreditCardService.model.User;

/**
 * Service used for managing User(bank client) operations
 */
public interface UserService {

    /**
     * method for creating new credit card user
     * @param User user object
     * @return returns newly created User object
     */
    User createUser(User user);

    /**
     * method for getting user by OIB
     *
     * @param OIB
     * @return return User from database if exists for given OIB
     */
    User getUserByOIB(String OIB);

    /**
     * method for deleting user by OIB
     * @param OIB
     * @return return User from database if exists for given OIB
     */
    boolean deleteUserByOIB(String userOIB);
}
