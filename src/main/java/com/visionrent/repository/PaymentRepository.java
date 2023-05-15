package com.visionrent.repository;

import com.visionrent.domain.Payment;
import com.visionrent.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

boolean existsByReservation(Reservation reservation);

}
