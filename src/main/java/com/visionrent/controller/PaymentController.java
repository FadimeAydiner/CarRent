package com.visionrent.controller;

import com.visionrent.domain.Payment;
import com.visionrent.domain.Reservation;
import com.visionrent.dto.request.PaymentRequest;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.service.PaymentService;
import com.visionrent.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReservationService reservationService;


    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<VRResponse> payment(@RequestParam("reservationId") Long reservationId,
                                              @RequestBody @Valid PaymentRequest paymentRequest){

        Reservation reservation=reservationService.getReservationByReservationId(reservationId);

        paymentService.payment(paymentRequest,reservation);

        VRResponse response=new VRResponse(ResponseMessage.PAYMENT_RESPONSE_MESSAGE+" for "+reservation.getTotalPrice() ,true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }


}
