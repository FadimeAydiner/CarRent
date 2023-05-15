package com.visionrent.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PaymentRequest {


    @Size(max=50,message = "Name can be max 50 chars")
    @NotBlank(message = "Please provide card holder name")
    private String cardHolder;


    //@JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="MM/yy")
    @NotNull(message = "Please provide expiration date of your card")
    private String expirationDate;

    @Size(max=3,message = "Security code is the code back of your card with 3 digits")
    @NotBlank(message = "Please provide security code")
    private String securityCode;

    @Size(max=16,message = "Card number must be 16 digits")
    @NotBlank(message = "Please provide card number")
    private String cardNumber;
}
