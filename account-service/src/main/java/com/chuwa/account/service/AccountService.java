package com.chuwa.account.service;

import com.chuwa.account.dto.AccountResponse;
import com.chuwa.account.dto.CreateAccountRequest;
import com.chuwa.account.dto.UpdateAccountRequest;
import com.chuwa.account.entity.User;
import com.chuwa.account.exception.ApiException;
import com.chuwa.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountResponse create(CreateAccountRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }

        // Build User
        User u = User.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .build();

        u = userRepository.save(u);
        return toResponse(u);
    }
    // Search by id
    public  AccountResponse getById(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(u);
    }

    // Update information
    public AccountResponse update(Long id, UpdateAccountRequest req) {
        User u = userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (!u.getEmail().equals(req.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (!u.getUsername().equals(req.getUsername()) && userRepository.existsByUsername(req.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }

        u.setEmail(req.getEmail());
        u.setUsername(req.getUsername());
        u = userRepository.save(u);
        return toResponse(u);

    }
    private AccountResponse toResponse(User u) {
        return AccountResponse.builder()
                .id(u.getId())
                .email((u.getEmail()))
                .username(u.getUsername())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
