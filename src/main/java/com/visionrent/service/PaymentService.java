package com.visionrent.service;

import com.visionrent.domain.Car;
import com.visionrent.domain.Payment;
import com.visionrent.domain.Reservation;
import com.visionrent.dto.request.PaymentRequest;
import com.visionrent.exception.BadRequestException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public void payment(PaymentRequest paymentRequest,Reservation reservation){
      checkExpirationTime("01-"+paymentRequest.getExpirationDate());


        if(paymentRepository.existsByReservation(reservation)){
            throw new BadRequestException(String.format(ErrorMessage.PAYMENT_ALREADY_DONE_MESSAGE,reservation.getId()));
        }

        Payment payment=new Payment();
        payment.setReservation(reservation);
        payment.setCardHolder(paymentRequest.getCardHolder());
        payment.setSecurityCode(paymentRequest.getSecurityCode());

       payment.setExpirationDate(paymentRequest.getExpirationDate());

        payment.setCardNumber(paymentRequest.getCardNumber());
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private void checkExpirationTime(String expirationTime){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        LocalDate dateTime = LocalDate.parse(expirationTime,formatter);
        if(dateTime.isBefore(LocalDate.now()))
            throw new BadRequestException(ErrorMessage.EXPIRATION_TIME_INCORRECT_MESSAGE);
    }

}
