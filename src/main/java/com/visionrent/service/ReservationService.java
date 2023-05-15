package com.visionrent.service;

import com.visionrent.domain.Car;
import com.visionrent.domain.Reservation;
import com.visionrent.domain.User;
import com.visionrent.domain.enums.ReservationStatus;
import com.visionrent.dto.ReservationDTO;
import com.visionrent.dto.request.ReservationRequest;
import com.visionrent.dto.request.ReservationUpdateRequest;
import com.visionrent.exception.BadRequestException;
import com.visionrent.exception.ResourceNotFoundException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.mapper.ReservationMapper;
import com.visionrent.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationMapper reservationMapper;


    public void createReservation(ReservationRequest reservationRequest, User user, Car car){
        //pick up ve drop off zamanlarını kontrol edeceğiz
        checkReservationTimeIsCorrect(reservationRequest.getPickUpTime(),reservationRequest.getDropOffTime());

        //araba rezerve edilmiş mi diye kontrol ediyoruz
        boolean carStatus=checkCarAvailability(car,reservationRequest.getPickUpTime(),reservationRequest.getDropOffTime());

        Reservation reservation=reservationMapper.reservationRequestToReservation(reservationRequest);
        if(carStatus){
            reservation.setStatus(ReservationStatus.CREATED);
        }else{
            throw new BadRequestException(ErrorMessage.CAR_NOT_AVAILABLE_MESSAGE);
        }

        reservation.setCar(car);
        reservation.setUser(user);
        Double totalPrice=getTotalPrice(car,reservationRequest.getPickUpTime(),reservationRequest.getDropOffTime());
        reservation.setTotalPrice(totalPrice);

        reservationRepository.save(reservation);

    }

    public void updateReservation(Long reservationId, Car car, ReservationUpdateRequest reservationUpdateRequest){
        Reservation reservation=getReservationByReservationId(reservationId);

        //cancelled veya  done reservation güncellenemez
        if(reservation.getStatus().equals(ReservationStatus.CANCELLED)||reservation.getStatus().equals(ReservationStatus.DONE)){
            throw new BadRequestException(ErrorMessage.RESERVATION_STATUS_CAN_NOT_CHANGED_MESSAGE);
        }

        if(reservationUpdateRequest.getStatus()!=null && reservationUpdateRequest.getStatus()==ReservationStatus.CREATED){
            checkReservationTimeIsCorrect(reservationUpdateRequest.getPickUpTime(),reservationUpdateRequest.getDropOffTime());;
        }

        List<Reservation> conflictReservations=getConflictReservations(car,reservationUpdateRequest.getPickUpTime(),reservationUpdateRequest.getDropOffTime());

        if(!conflictReservations.isEmpty()){
            if(!(conflictReservations.size()==1 && conflictReservations.get(0).getId().equals(reservationId))){
                throw  new BadRequestException(ErrorMessage.CAR_NOT_AVAILABLE_MESSAGE);
            }
        }

        Double totalPrice=getTotalPrice(car,reservationUpdateRequest.getPickUpTime(),reservationUpdateRequest.getDropOffTime());
        reservation.setTotalPrice(totalPrice);
        reservation.setCar(car);
        reservation.setPickUpTime(reservationUpdateRequest.getPickUpTime());;
        reservation.setDropOffTime(reservationUpdateRequest.getDropOffTime());
        reservation.setPickUpLocation(reservationUpdateRequest.getPickUpLocation());
        reservation.setDropOffLocation(reservationUpdateRequest.getDropOffLocation());
        reservation.setStatus(reservationUpdateRequest.getStatus());
        reservationRepository.save(reservation);

    }




    public Reservation getReservationByReservationId(Long id){
        Reservation reservation=reservationRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE,id)));
        return reservation;
    }
    public Double getTotalPrice(Car car,LocalDateTime pickUpTime,LocalDateTime dropOfTime){
        Long minutes= ChronoUnit.MINUTES.between(pickUpTime,dropOfTime);
        double hours=Math.ceil(minutes/60.0);
        return car.getPricePerHour()*hours;
    }

    public boolean checkCarAvailability(Car car,LocalDateTime pickUpTime,LocalDateTime dropOffTime){
        List<Reservation> existReservations=getConflictReservations(car,pickUpTime,dropOffTime);



        return existReservations.isEmpty();
    }

    public List<ReservationDTO> getAllReservations(){
        List<Reservation> reservations=reservationRepository.findAll();
        return reservationMapper.map(reservations);
    }

    private List<Reservation> getConflictReservations(Car car,LocalDateTime pickUpTime,LocalDateTime dropOffTime){
        if(pickUpTime.isAfter(dropOffTime)){
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }
        ReservationStatus [] status={ReservationStatus.CANCELLED,ReservationStatus.DONE};

       List<Reservation> existReservations=reservationRepository.checkCarStatus(car.getId(),pickUpTime,dropOffTime,status);
        return existReservations;

    }


    private void checkReservationTimeIsCorrect(LocalDateTime pickUpTime,LocalDateTime dropOffTime){

        LocalDateTime now=LocalDateTime.now();
        //pick up time anlık tarih ve saatten önce olamaz
        if(pickUpTime.isBefore(now)){
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
             }

        //pick up time ,drop off time'a eşit olamaz
        boolean isEqual=pickUpTime.isEqual(dropOffTime);
        //pick up time ,drop off'tan sonra olamaz
        boolean isBefore=pickUpTime.isBefore(dropOffTime);

        if(isEqual || !isBefore){
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

    }

    public ReservationDTO getReservationDTO(Long id){
        Reservation reservation=getReservationByReservationId(id);
        return reservationMapper.reservationToReservationDTO(reservation);
    }

    public ReservationDTO findByReservationIdAndLoggedInUser(Long id,User user){
        Reservation reservation=reservationRepository.findByIdAndUser(id,user).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE,id)));
        return reservationMapper.reservationToReservationDTO(reservation);
    }

    public Page<ReservationDTO>getReservationPage(Pageable pageable){
        Page<Reservation>reservationPage=reservationRepository.findAll(pageable);
        return getReservationDTOPage(reservationPage);
    }
    public Page<ReservationDTO> findReservationPageByUser(User user, Pageable pageable){
        Page<Reservation>reservationPage=reservationRepository.findAllByUser(user,pageable);
        return getReservationDTOPage(reservationPage);
    }

    //mapper from reservationPage->reservationDTOPage
    private Page<ReservationDTO> getReservationDTOPage(Page<Reservation>reservationPage){
        return reservationPage.map(reservation -> reservationMapper.reservationToReservationDTO(reservation));

    }

    public void removeByReservationId(Long reservationId){

        boolean exist=reservationRepository.existsById(reservationId);

        if(!exist){
            throw new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE,reservationId));

        }
        reservationRepository.deleteById(reservationId);
    }

    public boolean existByCar(Car car){
        return reservationRepository.existsByCar(car);
    }

}
