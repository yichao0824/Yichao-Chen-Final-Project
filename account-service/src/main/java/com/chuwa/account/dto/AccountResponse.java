package com.chuwa.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String email;
    private String username;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private LocalDateTime createdAt;
}