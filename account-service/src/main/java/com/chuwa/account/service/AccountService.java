package com.chuwa.account.service;

import com.chuwa.account.dto.AccountResponse;
import com.chuwa.account.dto.ChangePasswordRequest;
import com.chuwa.account.dto.CreateAccountRequest;
import com.chuwa.account.dto.LoginRequest;
import com.chuwa.account.dto.LoginResponse;
import com.chuwa.account.dto.UpdateAccountRequest;
import com.chuwa.account.entity.User;
import com.chuwa.account.exception.ApiException;
import com.chuwa.account.repository.UserRepository;
import com.chuwa.account.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AccountResponse create(CreateAccountRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }

        User u = User.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .shippingAddress(req.getShippingAddress())
                .billingAddress(req.getBillingAddress())
                .paymentMethod(req.getPaymentMethod())
                .build();

        u = userRepository.save(u);
        return toResponse(u);
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public AccountResponse getById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(u);
    }

    public AccountResponse updateMyAccount(Long loginUserId, Long targetUserId, UpdateAccountRequest req) {
        if (!loginUserId.equals(targetUserId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only update your own account");
        }

        User u = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (!u.getEmail().equals(req.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (!u.getUsername().equals(req.getUsername()) && userRepository.existsByUsername(req.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }

        u.setEmail(req.getEmail());
        u.setUsername(req.getUsername());
        u.setShippingAddress(req.getShippingAddress());
        u.setBillingAddress(req.getBillingAddress());
        u.setPaymentMethod(req.getPaymentMethod());

        u = userRepository.save(u);
        return toResponse(u);
    }

    public void changeMyPassword(Long loginUserId, ChangePasswordRequest req) {
        User u = userRepository.findById(loginUserId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(req.getOldPassword(), u.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Old password is incorrect");
        }

        u.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(u);
    }

    private AccountResponse toResponse(User u) {
        return AccountResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .username(u.getUsername())
                .shippingAddress(u.getShippingAddress())
                .billingAddress(u.getBillingAddress())
                .paymentMethod(u.getPaymentMethod())
                .createdAt(u.getCreatedAt())
                .build();
    }
}