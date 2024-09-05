package com.rba.CreditCardService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardStatusUpdateMessage {

    private String oib;
    private Long cardStatusTypeId;

    @Override
    public String toString() {
        return "CardStatusUpdateMessage{" +
                "oib='" + oib + '\'' +
                ", cardStatus='" + cardStatusTypeId + '\'' +
                '}';
    }
}
