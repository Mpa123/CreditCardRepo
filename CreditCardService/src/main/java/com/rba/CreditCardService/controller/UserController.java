package com.rba.CreditCardService.controller;

import com.rba.CreditCardService.model.User;
import com.rba.CreditCardService.service.CardRequestService;
import com.rba.CreditCardService.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
 * Controller for managing User(bank client) operations.
 */
@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger ourLog = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /** used for sending request to Card Creation API */
    private final CardRequestService cardRequestService;

    /**
     * Create new credit card User
     *
     *
     * @param user The user data to create. Could use UserDTO but everything that User entity has
     *             is required at the moment.
     * @return A ResponseEntity with the created User object.
     * @consumes application/json
     * @produces application/json
     * @response 200 The created User object.
     * @response 400 If the user data is invalid.
     * @response 404 If user with given OIB already exists.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody(required = true) User user) {
        ourLog.info("Received request to create user ");
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Retrieves a user by their OIB.
     *
     * @param userOIB The OIB of the user to retrieve.
     * @return A ResponseEntity containing the user data if found, or a 404 status if not found.
     * @produces application/json
     * @response 200 A User object if the user is found.
     * @response 404 If the user is not found.
     */

    @GetMapping(path = "/{oib}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> searchUserByOIB(@PathVariable(name = "oib") String userOIB) {
        ourLog.info("Received request to get user with oib " + userOIB);
        User userByOIB = userService.getUserByOIB(userOIB);

        ourLog.debug("Found user with oib " + userByOIB.getOIB() + " in database");
        //call credit card creation service
        cardRequestService.forwardClientDetails(userByOIB);

        return ResponseEntity.ok(userByOIB);

    }

    /**
     * Delete User by OIB
     *
     * @param userOIB The OIB of the user to delete.
     * @return A ResponseEntity containing the String message that the user was deleted.
     * @produces application/json
     * @response 200 A User object if the user is found.
     * @response 404 If the user is not found.
     */

    @DeleteMapping(path = "/{oib}")
    public ResponseEntity<String> deleteUserByOIB(@PathVariable(name = "oib") String userOIB) {
        userService.deleteUserByOIB(userOIB);

        return ResponseEntity.ok("User with oib " + userOIB + " was deleted.");

    }

}
