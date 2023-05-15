package com.visionrent.controller;

import com.graphbuilder.curve.ValueVector;
import com.visionrent.domain.Car;
import com.visionrent.domain.User;
import com.visionrent.dto.ReservationDTO;
import com.visionrent.dto.request.ReservationRequest;
import com.visionrent.dto.request.ReservationUpdateRequest;
import com.visionrent.dto.response.CarAvailabilityResponse;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.service.CarService;
import com.visionrent.service.ReservationService;
import com.visionrent.service.UserService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.PanelUI;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CarService carService;
    /**

     * {
     *     "pickUpTime":"07/16/2022 19:00:00",
     *     "dropOffTime":"07/17/2022 21:00:00",
     *     "pickUpLocation":"Ankara",
     *     "dropOffLocation":"Ankara"
     * }
     * http://localhost:8080/reservations/add?carId=1
     */

    //current user kendi için rezervasyon yapacak
    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<VRResponse> makeReservation(@RequestParam("carId") Long carId,
                                                      @RequestBody @Valid ReservationRequest reservationRequest){

        Car car = carService.findCarById(carId);

        User user = userService.getCurrentUser();

        reservationService.createReservation(reservationRequest,user,car);

        VRResponse response = new VRResponse(ResponseMessage.RESERVATION_CREATED_RESPONSE_MESSAGE,true);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //admin herhangi bir kullanıcı için rezervasyon yapacak
    @PostMapping("/add/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VRResponse> addReservation(@RequestParam("carId") Long carId,
                                                     @RequestParam("userId")Long userId,
                                                     @RequestBody @Valid ReservationRequest reservationRequest){

        Car car=carService.findCarById(carId);
        User user=userService.getById(userId);

        reservationService.createReservation(reservationRequest,user,car);
        VRResponse response=new VRResponse(ResponseMessage.RESERVATION_CREATED_RESPONSE_MESSAGE,true);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("/admin/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VRResponse> updateReservation(@RequestParam("carId")Long carId,
                                                        @RequestParam("reservationId")Long reservationId,
                                                        @RequestBody @Valid ReservationUpdateRequest reservationUpdateRequest){
        Car car=carService.findCarById(carId);
        reservationService.updateReservation(reservationId,car,reservationUpdateRequest);

        VRResponse response=new VRResponse(ResponseMessage.RESERVATION_UPDATED_RESPONSE_MESSAGE,true);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @GetMapping("/auth")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<VRResponse> checkCarIsAvailable(@RequestParam("carId") Long carId,
                                                          @RequestParam("pickUpTime")@DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") LocalDateTime pickUpTime,
                                                          @RequestParam("dropOffTime")@DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss")LocalDateTime dropOffTime){
        Car car=carService.findCarById(carId);
        boolean isAvailable=reservationService.checkCarAvailability(car,pickUpTime,dropOffTime);

        if (!isAvailable) {
            List<String> candidateTimes = new ArrayList<>();
            LocalDateTime rangeStart = pickUpTime.minusDays(15);
            LocalDateTime rangeEnd = dropOffTime.plusDays(15);
            while (rangeStart.isBefore(rangeEnd)) {
                LocalDateTime candidatePickupTime = rangeStart.plusDays(1);
                LocalDateTime candidateDropOffTime = candidatePickupTime.plusDays(2);
                boolean isCandidateAvailable = reservationService.checkCarAvailability(car, candidatePickupTime, candidateDropOffTime);
                if (isCandidateAvailable) {
                    Double candidateTotalPrice = reservationService.getTotalPrice(car, candidatePickupTime, candidateDropOffTime);
                    String candidateTimeRange = candidatePickupTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " - " +
                                                candidateDropOffTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                   candidateTimes.add(candidateTimeRange /* + " (Total Price: " + candidateTotalPrice + ")"*/);
                    if (candidateTimes.size()>30){
                        break;
                    }
                }
                rangeStart = rangeStart.plusDays(1);
            }
            VRResponse response = new CarAvailabilityResponse(ErrorMessage.CAR_NOT_AVAILABLE_MESSAGE,true, candidateTimes);
            return ResponseEntity.ok(response);
        } else {
            Double totalPrice = reservationService.getTotalPrice(car, pickUpTime, dropOffTime);
            VRResponse response = new CarAvailabilityResponse(ResponseMessage.CAR_AVAILABLE_MESSAGE, true, true, totalPrice);
            return ResponseEntity.ok(response);


    }}
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations(){
        List<ReservationDTO> allReservations=reservationService.getAllReservations();
        return ResponseEntity.ok(allReservations);
    }
    @GetMapping("/admin/auth/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDTO>> getAllUserReservationsByPage(@RequestParam("userId")Long userId,
                                                                             @RequestParam("page")int page,
                                                                             @RequestParam("size") int size,
                                                                             @RequestParam("sort") String prop,
                                                                             @RequestParam(value="direction",required = false,defaultValue = "DESC")Sort.Direction direction){

        Pageable pageable =PageRequest.of(page,size,Sort.by(direction,prop));
        User user=userService.getById(userId);

        Page<ReservationDTO> allReservations=reservationService.findReservationPageByUser(user,pageable);
        return ResponseEntity.ok(allReservations);

    }

    @GetMapping("/admin/all/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDTO>> getAllUserReservationByPage(@RequestParam("page")int page,
                                                                                  @RequestParam("size") int size,
                                                                                  @RequestParam("sort") String prop,
                                                                                  @RequestParam(value="direction",required = false,defaultValue = "DESC")Sort.Direction direction){

        Pageable pageable =PageRequest.of(page,size,Sort.by(direction,prop));


        Page<ReservationDTO> allReservations=reservationService.getReservationPage(pageable);
        return ResponseEntity.ok(allReservations);

    }
    @GetMapping("{id}/admin")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id){
        ReservationDTO reservationDTO=reservationService.getReservationDTO(id);
        return ResponseEntity.ok(reservationDTO);
    }

    @GetMapping("/auth/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDTO>> getLoggedInUserReservationsByPage(@RequestParam("page")int page,
                                                                             @RequestParam("size") int size,
                                                                             @RequestParam("sort") String prop,
                                                                             @RequestParam(value="direction",required = false,defaultValue = "DESC")Sort.Direction direction){

        Pageable pageable =PageRequest.of(page,size,Sort.by(direction,prop));
        User user=userService.getCurrentUser();

        Page<ReservationDTO> allReservations=reservationService.findReservationPageByUser(user,pageable);
        return ResponseEntity.ok(allReservations);

    }

    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ReservationDTO>getLoggedInUserReservationById(@PathVariable Long id){
        User user=userService.getCurrentUser();
        ReservationDTO reservationDTO=reservationService.findByReservationIdAndLoggedInUser(id,user);
        return ResponseEntity.ok(reservationDTO);
    }

    @DeleteMapping("/admin/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VRResponse> deleteReservation(@PathVariable Long id){
        reservationService.removeByReservationId(id);
        VRResponse response=new VRResponse(ResponseMessage.RESERVATION_DELETED_RESPONSE_MESSAGE,true);

        return ResponseEntity.ok(response);
    }
}
