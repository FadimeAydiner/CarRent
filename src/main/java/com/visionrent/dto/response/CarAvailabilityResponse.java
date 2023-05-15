package com.visionrent.dto.response;

import com.visionrent.repository.ReservationRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class CarAvailabilityResponse extends VRResponse{

    private boolean isAvailable;
    private double totalPrice;



    private List<String> candidateTimes=new ArrayList<>();


    public CarAvailabilityResponse(String message, boolean success, boolean isAvailable, double totalPrice){
        super(message,success);
        this.isAvailable=isAvailable;
        this.totalPrice=totalPrice;


    }


    public CarAvailabilityResponse(String carNotAvailableMessage, boolean success,List<String> candidateTimes) {


        super(carNotAvailableMessage,success);
        this.candidateTimes=candidateTimes;


    }
}
