package com.rba.CreditCardService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_card_status_type")
public class CreditCardStatusType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_status_id_seq")
    @SequenceGenerator(name = "card_status_id_seq", sequenceName = "card_status_id_seq", allocationSize = 1)
    private Long id;

    @Pattern(regexp = "Approved|Rejected|Pending", message = "Invalid Status. Allowed values: Approved, Pending, Rejected")
    private String name;
}
