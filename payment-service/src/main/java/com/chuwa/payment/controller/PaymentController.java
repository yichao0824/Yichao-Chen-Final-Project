package com.chuwa.payment.controller;

import com.chuwa.payment.dto.PaymentRequest;
import com.chuwa.payment.entity.Payment;
import com.chuwa.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private  final PaymentService paymentService;

    @PostMapping
    public Payment pay(@RequestBody PaymentRequest req) {
        return paymentService.pay(req);
    }
}
