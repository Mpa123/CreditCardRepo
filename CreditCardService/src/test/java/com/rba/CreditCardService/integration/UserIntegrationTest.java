package com.rba.CreditCardService.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rba.CreditCardService.exceptions.CreditCardUserException;
import com.rba.CreditCardService.exceptions.ErrorEnum;
import com.rba.CreditCardService.repository.CardStatusRepository;
import com.rba.CreditCardService.repository.UserRepository;
import com.rba.CreditCardService.model.CreditCardStatusType;
import com.rba.CreditCardService.model.User;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardStatusRepository cardStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setup(){
        // Clean up the database before each test
        userRepository.deleteAll();

        //insert allowed credit card statuses
        cardStatusRepository.save(new CreditCardStatusType(1L, "Approved"));
        cardStatusRepository.save(new CreditCardStatusType(2L, "Rejected"));
        cardStatusRepository.save(new CreditCardStatusType(3L, "Pending"));

    }

    @Test
    void saveUserSuccessTest() throws Exception {
        User user = User.builder()
                .id(1L)
                .firstName("Hrvoje")
                .lastName("Horvat")
                .OIB("12345678912")
                .status(new CreditCardStatusType(1L, "Approved"))
                .build();
        //action and verify
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.oib", is(user.getOIB())));
    }

    @Test
    void shouldThrowExceptionWhenOIBNotUniqueTest() throws Exception {
        User user = User.builder()
                .id(1L)
                .firstName("Hrvoje")
                .lastName("Horvat")
                .OIB("12345678912")
                .status(new CreditCardStatusType(1L, "Approved"))
                .build();

        userRepository.save(user);

        User userSameOib = User.builder()
                .id(2L)
                .firstName("Iva")
                .lastName("Ivić")
                .OIB("12345678912")
                .status(new CreditCardStatusType(1L, "Approved"))
                .build();

        //action and verify
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSameOib)))
                .andExpect(status().isNotFound());  // 404 is returned for CreditCardUserException in WebRestControllerAdvice

    }

    @Test
    void saveUserFailOIBNotProperSizeTest() throws Exception {
        User user = User.builder()
                .id(3L)
                .firstName("Hrvoje")
                .lastName("Horvat")
                .OIB("12345")
                .status(new CreditCardStatusType(1L, "Approved"))
                .build();
        //action and verify
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
}
