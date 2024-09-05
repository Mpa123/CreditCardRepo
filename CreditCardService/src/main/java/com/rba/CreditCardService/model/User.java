package com.rba.CreditCardService.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_seq")
    @SequenceGenerator(name = "client_id_seq", sequenceName = "client_id_seq", allocationSize = 1)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(name = "oib", unique = true, length = 11)
    @Size(min = 11, max = 11, message = "OIB must be 11 characters long")
    private String OIB;

    @Valid
    @ManyToOne
    @JoinColumn(name = "credit_card_status_type_id")
    private CreditCardStatusType status; //"Pending", "Approved", "Rejected"
}
