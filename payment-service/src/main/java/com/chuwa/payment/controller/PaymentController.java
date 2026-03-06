package com.chuwa.payment.controller;

import com.chuwa.payment.dto.PaymentRequest;
import com.chuwa.payment.dto.UpdatePaymentRequest;
import com.chuwa.payment.entity.Payment;
import com.chuwa.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Payment pay(@RequestBody PaymentRequest req) {
        return paymentService.pay(req);
    }

    @GetMapping("/{paymentId}")
    public Payment getPayment(@PathVariable String paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @PutMapping("/{paymentId}")
    public Payment updatePayment(@PathVariable String paymentId,
                                 @RequestBody UpdatePaymentRequest req) {
        return paymentService.updatePayment(paymentId, req);
    }

    @PostMapping("/{paymentId}/refund")
    public Payment refund(@PathVariable String paymentId) {
        return paymentService.refund(paymentId);
    }
}