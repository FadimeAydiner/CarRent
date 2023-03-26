package com.visionrent.service;

import com.visionrent.domain.Car;
import com.visionrent.domain.Reservation;
import com.visionrent.domain.User;
import com.visionrent.domain.enums.ReservationStatus;
import com.visionrent.dto.request.ReservationRequest;
import com.visionrent.exception.BadRequestException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.mapper.ReservationMapper;
import com.visionrent.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationMapper reservationMapper;


    public void createReservation(ReservationRequest reservationRequest, User user, Car car){
        //we need to check the times
        checkReservationTimeIsCorrect(reservationRequest.getPickUpTime(),reservationRequest.getDropOffTime());

        //we have to check if this car is free or reservation
        checkCarAvailability(car,reservationRequest.getPickUpTime(),reservationRequest.getDropOffTime());

    }

    private boolean checkCarAvailability(Car car,LocalDateTime pickUpTime,LocalDateTime dropOffTime){
        //List<Reservation> existReservations=
        return false;
    }

    private List<Reservation> getConflictReservations(Car car,LocalDateTime pickUpTime,LocalDateTime dropOffTime){
        if(pickUpTime.isAfter(dropOffTime)){
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }
        ReservationStatus [] status={ReservationStatus.CANCELLED,ReservationStatus.DONE};

      //  List<Reservation> existReservations=reservationRepository.
        return null;

    }
    private void checkReservationTimeIsCorrect(LocalDateTime pickUpTime,LocalDateTime dropOffTime){

        LocalDateTime now=LocalDateTime.now();
        if(pickUpTime.isBefore(now)){
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
             }

        //pick up time can not be equal to drop off time
        boolean isEqual=pickUpTime.isEqual(dropOffTime);
        //pick up time can not be later than drop off time
        boolean isBefore=pickUpTime.isBefore(dropOffTime);

        if(isEqual || !isBefore){
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

    }

}
