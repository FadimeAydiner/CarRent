package com.visionrent.controller;

import com.visionrent.dto.CarDTO;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PutMapping("/admin/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VRResponse> updateCar(@RequestParam("id") Long id,
                                                @RequestParam("imageId") String imageId,
                                                @Valid@RequestBody CarDTO carDTO){
        return null;




    }
}
