package com.visionrent.controller;

import com.visionrent.domain.Car;
import com.visionrent.domain.User;
import com.visionrent.dto.request.ReservationRequest;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.service.CarService;
import com.visionrent.service.ReservationService;
import com.visionrent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.PanelUI;
import javax.validation.Valid;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CarService carService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<VRResponse> makeReservation(@RequestParam("car_Id") Long carId, @RequestBody @Valid ReservationRequest reservationRequest){


        Car car=carService.findCarById(carId);
        User user=userService.getCurrentUser();

        return null;


    }


}
