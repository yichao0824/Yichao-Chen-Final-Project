package com.chuwa.account.controller;

import com.chuwa.account.dto.AccountResponse;
import com.chuwa.account.dto.ChangePasswordRequest;
import com.chuwa.account.dto.CreateAccountRequest;
import com.chuwa.account.dto.LoginRequest;
import com.chuwa.account.dto.LoginResponse;
import com.chuwa.account.dto.UpdateAccountRequest;
import com.chuwa.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest req) {
        return accountService.create(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return accountService.login(req);
    }

    @GetMapping("/me")
    public AccountResponse me(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return accountService.getById(userId);
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable Long id) {
        return accountService.getById(id);
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateAccountRequest req,
                                  Authentication authentication) {
        Long loginUserId = Long.valueOf(authentication.getName());
        return accountService.updateMyAccount(loginUserId, id, req);
    }

    @PutMapping("/me/password")
    public Map<String, String> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                              Authentication authentication) {
        Long loginUserId = Long.valueOf(authentication.getName());
        accountService.changeMyPassword(loginUserId, req);
        return Map.of("message", "Password updated successfully");
    }
}