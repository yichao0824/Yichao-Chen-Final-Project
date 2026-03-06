package com.chuwa.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAccountRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 2, max = 50)
    private String username;

    @Size(max = 255)
    private String shippingAddress;

    @Size(max = 255)
    private String billingAddress;

    @Size(max = 100)
    private String paymentMethod;
}