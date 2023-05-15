package com.visionrent.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="t_payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;



    @OneToOne()
    @JoinColumn(name = "reservation_id",referencedColumnName = "id")
    private Reservation reservation;

    @Column(length = 30,nullable = false)
    private String cardHolder;

   @Column(nullable = false)
    private String expirationDate;

    @Column(length = 3,nullable = false)
    private String securityCode;

    @Column(length = 16,nullable = false)
    private String cardNumber;

    private LocalDateTime paymentDate;

}
