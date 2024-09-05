package com.rba.CreditCardService.exceptions;

public enum ErrorEnum {
    UNEXPECTED_ERROR("ERR_CARD_1000"),
    USER_NOT_FOUND("ERR_CARD_1001"),
    GENERIC_EXCEPTION("ERR_CARD_1002"),
    CARD_CREATION_BAD_REQUEST("ERR_CARD_1003"),
    CARD_CREATION_UNAUTHORIZED("ERR_CARD_1004"),
    CARD_CREATION_NOT_FOUND("ERR_CARD_1005"),
    CARD_CREATION_INTERNAL_SERVER_ERROR("ERR_CARD_1006"),
    USER_ALREADY_EXISTS("ERR_CARD_1007"),
    INVALID_CARD_STATUS("ERR_CARD_1008");

    public final String code;

    ErrorEnum(String code) {
        this.code = code;
    }
}
