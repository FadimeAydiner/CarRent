package com.visionrent.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {
    @NotBlank(message = "Please provide old password")
    private String oldPassword;

    @Size(min = 4, max = 20,message="Please Provide Correct Size for Password")
    @NotBlank(message = "Please provide new password")
    private String newPassword;
}
