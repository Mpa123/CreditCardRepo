package com.rba.CreditCardService.repository;

import com.rba.CreditCardService.model.CreditCardStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardStatusRepository extends JpaRepository<CreditCardStatusType, Long> {
}
