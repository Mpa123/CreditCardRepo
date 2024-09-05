package com.rba.CreditCardService.exceptions;


import com.rba.CreditCardService.service.CreditCardExceptionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
public class WebRestControllerAdvice {

    private static final Logger ourLog = LoggerFactory.getLogger(WebRestControllerAdvice.class);

    private final CreditCardExceptionService creditCardExceptionService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(CreditCardUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorData handleCreditCardUserException(CreditCardUserException ex, HttpServletRequest request) {
        ourLog.warn(ex.getErrCode());
        ourLog.warn("checkedException: ", ex);

        Error error = Error.builder()
                .timestamp(String.valueOf(LocalDateTime.now()))
                .status(ex.getErrCode() != null ? ex.getErrCode() : ErrorEnum.UNEXPECTED_ERROR.code)
                .path(request != null ? request.getRequestURI() : null)
                .message(creditCardExceptionService.createErrorMessages(
                        ex.getErrCode() != null ? ex.getErrCode() : ErrorEnum.UNEXPECTED_ERROR.code,
                        ex.getArgs().isEmpty() ? null : ex.getArgs()
                ))
                .error(ex.getMessage())
                .build();


        return new ErrorData(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorData handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ourLog.warn(ex.getTypeMessageCode());
        ourLog.warn("checkedException: ", ex);
        Map<String, String> errors = new HashMap<>();
        List<String> errorMessages = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        errorMessages.add("Validation error: " + errors);

        ex.getStatusCode();
        ex.getMessage();
        Error error = Error.builder()
                .timestamp(String.valueOf(LocalDateTime.now()))
                .status(ex.getStatusCode().toString())
                .path(request != null ? request.getRequestURI() : null)
                .message(errorMessages)
                .error(ex.getMessage())
                .build();

        return new ErrorData(error);
    }
}
