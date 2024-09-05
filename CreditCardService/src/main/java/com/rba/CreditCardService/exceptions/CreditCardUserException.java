package com.rba.CreditCardService.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class CreditCardUserException extends RuntimeException {

    private String errCode;
    private List<Object> args;

    public CreditCardUserException(String errCode) {
        super();
        this.errCode = errCode;
        this.args = new ArrayList<>();
    }

    public CreditCardUserException(String errCode, String message) {
        super(message);
        this.errCode = errCode;
        this.args = new ArrayList<>();
    }

    public CreditCardUserException(String errCode, List<Object> args) {
        this.errCode = errCode;
        this.args = args;
    }

    public CreditCardUserException(String errCode, List<Object> args, String message) {
        super(message);
        this.errCode = errCode;
        this.args = args;
    }
}
