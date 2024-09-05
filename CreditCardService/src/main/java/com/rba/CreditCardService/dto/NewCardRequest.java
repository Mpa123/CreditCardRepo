package com.rba.CreditCardService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for new card created by external Card Creation API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCardRequest {

    private String firstName;
    private String lastName;
    private String status;
    private String oib;


}
