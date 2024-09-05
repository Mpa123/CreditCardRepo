package com.rba.CreditCardService.service.impl;

import com.rba.CreditCardService.exceptions.CreditCardUserException;
import com.rba.CreditCardService.exceptions.ErrorEnum;
import com.rba.CreditCardService.repository.UserRepository;
import com.rba.CreditCardService.model.CreditCardStatusType;
import com.rba.CreditCardService.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    User userValid;

    User userInValid;

    @BeforeEach
    public void setup() {
        userValid = User.builder()
                .id(1L)
                .firstName("Ivan")
                .lastName("Ivanic")
                .OIB("11145678915")
                .status(new CreditCardStatusType(1L, "Approved"))
                .build();

        userInValid = User.builder()
                .id(1L)
                .firstName("Ivan")
                .lastName("Krivic")
                .OIB("11145677751")
                .status(new CreditCardStatusType(1L, "KriviStatus"))
                .build();
    }

    @Test
    void createUserOkTest() {
        given(userRepository.save(userValid)).willReturn(userValid);
        User savedUser = userService.createUser(userValid);

        assertThat(savedUser).isNotNull();
    }

    @Test
    public void getUserByOIBOkTest() {
        String oib = "11145678915";

        when(userRepository.findByOIB(oib)).thenReturn(Optional.of(userValid));
        User userReturned = userService.getUserByOIB(oib);

        assertNotNull(userReturned);
        assertEquals(oib, userReturned.getOIB());
        verify(userRepository).findByOIB(oib);
    }

//    @Test
//    public void testGetUserByOIB_UserNotFound() {
//        String oib = "12345678912";
//        when(userRepository.findByOIB(oib)).thenReturn(Optional.empty());
//
//        // Action & Verify
//        CreditCardUserException thrown = assertThrows(CreditCardUserException.class, () -> {
//            userService.getUserByOIB(oib);
//        });
//        assertEquals(ErrorEnum.USER_NOT_FOUND.code, thrown.getMessage());
//        verify(userRepository).findByOIB(oib);
//    }

    @Test
    @Transactional
    public void testDeleteUserByOIB_Success() {
        // Prepare
        String oib = "12345678912";
        User user = new User();
        user.setOIB(oib);

        when(userRepository.findByOIB(oib)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteByOIB(oib);

        boolean result = userService.deleteUserByOIB(oib);

        assertTrue(result);
        verify(userRepository).findByOIB(oib);
        verify(userRepository).deleteByOIB(oib);
    }
}