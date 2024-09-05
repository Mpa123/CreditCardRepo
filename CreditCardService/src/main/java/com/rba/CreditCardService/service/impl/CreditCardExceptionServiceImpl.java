package com.rba.CreditCardService.service.impl;

import com.rba.CreditCardService.service.CreditCardExceptionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CreditCardExceptionServiceImpl implements CreditCardExceptionService {

    private static final Logger ourLog = LoggerFactory.getLogger(CreditCardExceptionServiceImpl.class);

    private final MessageSource messageSource;
    @Override
    public List<String> createErrorMessages(String errorCode, List<Object> args) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(messageSource.getMessage(errorCode, args != null ? args.toArray() : null, Locale.getDefault()));
        return errorMessages;
    }
}
