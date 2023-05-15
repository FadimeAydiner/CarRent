package com.visionrent.controller;

import com.visionrent.domain.Car;
import com.visionrent.dto.CarDTO;
import com.visionrent.dto.response.CarAvailabilityResponse;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.service.CarService;
import com.visionrent.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {

    @Autowired
    private CarService carService;



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{imageId}/add")
    public ResponseEntity<VRResponse> saveCar(@PathVariable String imageId,@Valid @RequestBody CarDTO carDTO){

        carService.saveCar(imageId,carDTO);
        VRResponse response=new VRResponse(ResponseMessage.CAR_SAVED_RESPONSE_MESSAGE,true);


        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }



    //http://localhost:8084/car/visitors/all
    @GetMapping("/visitors/all")
    public ResponseEntity<List<CarDTO>> getAllCars(){
        List<CarDTO> allCars=carService.getAllCars();
        return ResponseEntity.ok(allCars);
    }

    //http://localhost:8084/car/visitors/1
    @GetMapping("/visitors/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id){
        CarDTO carDTO=carService.findById(id);
        return ResponseEntity.ok(carDTO);
    }

    //http://localhost:8084/car/admin/auth?id=1&imageId=5eee9f91-ad7f-45de-9c2e-9a2ec9e7f5e6
    @PutMapping("/admin/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VRResponse> updateCar(@RequestParam("id") Long id,
                                                @RequestParam("imageId") String imageId,
                                                @Valid@RequestBody CarDTO carDTO){
        carService.updateCar(id, imageId, carDTO);
        VRResponse response=new VRResponse(ResponseMessage.CAR_UPDATE_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);

    }
//http://localhost:8084/car/admin/1/auth
    @DeleteMapping("/admin/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VRResponse> deleteCar(@PathVariable Long id){

        carService.removeById(id);
        VRResponse response=new VRResponse(ResponseMessage.CAR_DELETE_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);
    }

    //http://localhost:8084/car/visitors/pages?size=2&page=0&sort=id&direction=DESC
    @GetMapping("/visitors/pages")
    public ResponseEntity<Page<CarDTO>>getAllCarsWithPage(@RequestParam("page")int page,
                                                          @RequestParam("size")int size,
                                                          @RequestParam("sort")String prop,
                                                          @RequestParam(value = "direction",required = false,defaultValue = "DESC")Direction direction){

        Pageable pageable= PageRequest.of(page,size, Sort.by(direction,prop));

        Page<CarDTO> pageDTO=carService.findAllWithPage(pageable);


        return ResponseEntity.ok(pageDTO);
    }
}
