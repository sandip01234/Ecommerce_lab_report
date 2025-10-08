package com.example.khalti.integration.controller;

/**
 * ███████╗ █████╗ ███╗   ██╗██████╗ ██╗██████╗
 * ██╔════╝██╔══██╗████╗  ██║██╔══██╗██║██╔══██╗
 * ███████╗███████║██╔██╗ ██║██║  ██║██║██████╔╝
 * ╚════██║██╔══██║██║╚██╗██║██║  ██║██║██╔═══╝
 * ███████║██║  ██║██║ ╚████║██████╔╝██║██║
 * ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═════╝ ╚═╝╚═╝
 *
 * @author HP VICTUS on 10/2/2025
 */
import com.example.khalti.integration.dto.PaymentRequest;
import com.example.khalti.integration.service.KhaltiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final KhaltiService khaltiService;

    @PostMapping("/initiate")
    public ResponseEntity<String> initiate(@RequestBody PaymentRequest request) {
        String paymentUrl = khaltiService.initiatePayment(request);
        // Option 1: Return the payment URL (recommended for API)
        return ResponseEntity.ok(paymentUrl);

        // Option 2: If you want to redirect (for browser clients), use:
        // return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(paymentUrl)).build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam String pidx,
            @RequestParam String amount,
            @RequestParam String mobile,
            @RequestParam String status) {
        var verified = khaltiService.verifyTransaction(pidx);
        if ("Completed".equals(verified.getStatus())) {
            return ResponseEntity.ok("Payment successful! Transaction ID: " + pidx + ". Redirecting to success page...");
        } else {
            return ResponseEntity.badRequest().body("Payment " + verified.getStatus() + ". Transaction ID: " + pidx);
        }
    }
}