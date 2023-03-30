package com.visionrent.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class CarAvailabilityResponse extends VRResponse{
    private boolean isAvailable;

    private double totalPrice;

    public CarAvailabilityResponse(String message,boolean success,boolean isAvailable,double totalPrice){
        super(message,success);
        this.isAvailable=isAvailable;
        this.totalPrice=totalPrice;
    }
}
