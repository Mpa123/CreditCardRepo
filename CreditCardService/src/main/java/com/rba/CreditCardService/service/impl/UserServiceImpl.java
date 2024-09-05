package com.rba.CreditCardService.service.impl;

import com.rba.CreditCardService.exceptions.CreditCardUserException;
import com.rba.CreditCardService.exceptions.ErrorEnum;
import com.rba.CreditCardService.repository.UserRepository;
import com.rba.CreditCardService.model.User;
import com.rba.CreditCardService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger ourLog = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        ourLog.info("Creating user: {}", user);
        if (userRepository.existsByOIB(user.getOIB())) {
            ourLog.info("User already exists: {}", user.getOIB());
            throw new CreditCardUserException(ErrorEnum.USER_ALREADY_EXISTS.code);
        }
        User createdUser = userRepository.save(user);
        return createdUser;
    }

    @Override
    public User getUserByOIB(String userOIB) {
        ourLog.info("Getting user by OIB: {}", userOIB);
        User userOptional = userRepository.findByOIB(userOIB).orElseThrow(() -> {
            ourLog.info("User not found: {}", userOIB);
            throw new CreditCardUserException(ErrorEnum.USER_NOT_FOUND.code);
        });

        return userOptional;
    }

    @Override
    @Transactional
    public boolean deleteUserByOIB(String userOIB) {
        ourLog.info("Deleting user by OIB: {}", userOIB);
        ourLog.info("First check if user exists: {}", userOIB);
        if (userRepository.findByOIB(userOIB).isEmpty()) {
            ourLog.info("User not found: {}", userOIB);
            throw new CreditCardUserException(ErrorEnum.USER_NOT_FOUND.code);
        }
        userRepository.deleteByOIB(userOIB);
        return true;
    }


}
