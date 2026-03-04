package com.chuwa.account.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private String email;
    private String username;
    private Instant createdAt;
}
