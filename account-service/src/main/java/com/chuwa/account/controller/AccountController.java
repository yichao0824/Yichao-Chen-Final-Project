package com.chuwa.account.controller;

import com.chuwa.account.dto.AccountResponse;
import com.chuwa.account.dto.CreateAccountRequest;
import com.chuwa.account.dto.UpdateAccountRequest;
import com.chuwa.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest req) {
        return accountService.create(req);
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable Long id) {
        return accountService.getById(id);
    }
    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Long id, @Valid @RequestBody UpdateAccountRequest req) {
        return accountService.update(id, req);
    }
}
