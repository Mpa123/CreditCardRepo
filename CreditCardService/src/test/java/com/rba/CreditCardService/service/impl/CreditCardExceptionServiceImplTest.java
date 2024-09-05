package com.rba.CreditCardService.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CreditCardExceptionServiceImplTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CreditCardExceptionServiceImpl creditCardExceptionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createErrorMessagesWithValidErrorCodeTest() {
        String errorCode = "ERR_CODE_123";
        List<Object> args = List.of("arg1", "arg2");
        String expectedMessage = "Expected error message";

        when(messageSource.getMessage(errorCode, args.toArray(), Locale.getDefault()))
                .thenReturn(expectedMessage);

        List<String> errorMessages = creditCardExceptionService.createErrorMessages(errorCode, args);

        assertEquals(1, errorMessages.size());
        assertEquals(expectedMessage, errorMessages.get(0));
    }

    @Test
    public void createErrorMessagesWithNullArgumentsTest() {
        String errorCode = "ERR_CODE_123";
        String expectedMessage = "Expected error message";

        when(messageSource.getMessage(errorCode, null, Locale.getDefault()))
                .thenReturn(expectedMessage);

        List<String> errorMessages = creditCardExceptionService.createErrorMessages(errorCode, null);

        assertEquals(1, errorMessages.size());
        assertEquals(expectedMessage, errorMessages.get(0));
    }

    @Test
    public void testCreateErrorMessages_WithEmptyArguments() {
        String errorCode = "ERR_CODE_123";
        String expectedMessage = "Expected error message";

        when(messageSource.getMessage(errorCode, new Object[0], Locale.getDefault()))
                .thenReturn(expectedMessage);

        List<String> errorMessages = creditCardExceptionService.createErrorMessages(errorCode, List.of());

        assertEquals(1, errorMessages.size());
        assertEquals(expectedMessage, errorMessages.get(0));
    }
}