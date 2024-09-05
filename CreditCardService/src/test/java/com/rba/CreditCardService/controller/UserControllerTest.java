package com.rba.CreditCardService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rba.CreditCardService.exceptions.CreditCardUserException;
import com.rba.CreditCardService.exceptions.ErrorEnum;
import com.rba.CreditCardService.exceptions.WebRestControllerAdvice;
import com.rba.CreditCardService.model.CreditCardStatusType;
import com.rba.CreditCardService.model.User;
import com.rba.CreditCardService.service.CardRequestService;
import com.rba.CreditCardService.service.CreditCardExceptionService;
import com.rba.CreditCardService.service.UserService;
import com.rba.CreditCardService.service.impl.CreditCardExceptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CardRequestService cardRequestService;

    @MockBean
    private WebRestControllerAdvice webRestControllerAdvice;

    @Autowired
    private ObjectMapper objectMapper;

    User user;
    User userInValid;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Ivan")
                .lastName("Ivanic")
                .OIB("11145678912")
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
    void createUserTest() throws Exception {
        given(userService.createUser(any(User.class))).willReturn(user);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        //verify
        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.oib", is(user.getOIB())));
    }

    @Test
    void searchUserByOIB() throws Exception {
        given(userService.getUserByOIB(user.getOIB())).willReturn(user);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/users/{oib}", user.getOIB()));

        //verify
        resultActions.andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteUserByOIB() throws Exception {
        // precondition
        given(userService.deleteUserByOIB(user.getOIB())).willReturn(true);

        // action
        ResultActions response = mockMvc.perform(delete("/api/v1/users/{oib}", user.getOIB()));

        // verify
        response.andExpect(status().isOk());
    }

    @Test
    void createUserWhenInvalidOIBThenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInValid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchUserByOIBWhenDoesNotExistThenReturnBadRequest() throws Exception {
        given(userService.getUserByOIB(user.getOIB())).willThrow(new CreditCardUserException((ErrorEnum.USER_NOT_FOUND.code)));
        ResultActions resultActions = mockMvc.perform(get("/api/v1/users/{oib}", user.getOIB()));
        //verify
        resultActions.andExpect(status().isNotFound());
    }
}