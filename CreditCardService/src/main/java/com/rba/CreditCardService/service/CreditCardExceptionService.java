package com.rba.CreditCardService.service;

import java.util.List;

public interface CreditCardExceptionService {

    List<String> createErrorMessages(String errorCode, List<Object> args);
}
