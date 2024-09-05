package com.rba.CreditCardService.repository;

import com.rba.CreditCardService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOIB(String oib);

    void deleteByOIB(String userOIB);

    boolean existsByOIB(String oib);
}
