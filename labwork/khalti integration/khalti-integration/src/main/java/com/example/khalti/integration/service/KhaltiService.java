

package com.example.khalti.integration.service;

import com.example.khalti.integration.config.KhaltiConfig;
import com.example.khalti.integration.dto.LookupResponse;
import com.example.khalti.integration.dto.PaymentRequest;
import com.example.khalti.integration.dto.PaymentResponse;
import com.example.khalti.integration.entity.Payment;
import com.example.khalti.integration.repository.PaymentRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class KhaltiService {

    private static final Logger logger = LoggerFactory.getLogger(KhaltiService.class);

    private final RestTemplate restTemplate;
    private final KhaltiConfig khaltiConfig;
    private final PaymentRepository paymentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String initiatePayment(PaymentRequest request) {
        logger.info("Initiating payment with request: {}", request);
        // Set defaults if missing (required per docs)
        if (request.getReturnUrl() == null) request.setReturnUrl(khaltiConfig.getReturnUrl());
        if (request.getWebsiteUrl() == null) request.setWebsiteUrl(khaltiConfig.getWebsiteUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());  // Confirmed format from docs

        HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<PaymentResponse> response = restTemplate.postForEntity(
                    khaltiConfig.getBaseUrl() + "/initiate",  // Correct endpoint from docs
                    entity,
                    PaymentResponse.class
            );

            logger.info("Khalti API response: status={}, body={}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String transactionId = response.getBody().getPidx();  // Use pidx from body per docs
                String mobile = (request.getCustomerInfo() != null) ? request.getCustomerInfo().getPhone() : null;
                Payment payment = Payment.builder()
                        .transactionId(transactionId)
                        .orderId(request.getPurchaseOrderId())
                        .amount(request.getAmount().intValue())  // Convert to int if needed for entity
                        .status("Initiated")
                        .mobile(mobile)
                        .build();
                paymentRepository.save(payment);
                logger.info("Saved payment to DB: {}", payment);
                return response.getBody().getPaymentUrl();  // Redirect to this URL
            }
            logger.error("Payment initiation failed: status={}", response.getStatusCode());
            throw new RuntimeException("Payment initiation failed: status=" + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            logger.error("Khalti API error: status={}, response={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Payment initiation failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public LookupResponse verifyTransaction(String transactionId) {  // transactionId = pidx
        logger.info("Verifying transaction: {}", transactionId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());

        // Body for lookup per docs: {"token": "pidx"}
        LookupRequest lookupRequest = new LookupRequest(transactionId);
        HttpEntity<LookupRequest> entity = new HttpEntity<>(lookupRequest, headers);

        try {
            ResponseEntity<LookupResponse> response = restTemplate.postForEntity(
                    khaltiConfig.getBaseUrl() + "/lookup/",  // POST /lookup/ with body
                    entity,
                    LookupResponse.class
            );

            logger.info("Khalti lookup response: status={}, body={}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                LookupResponse lookup = response.getBody();
                paymentRepository.findByTransactionId(transactionId).ifPresent(payment -> {
                    payment.setStatus(lookup.getStatus());
                    payment.setAmount(lookup.getAmount().intValue());
                    payment.setMobile(lookup.getMobile());
                    paymentRepository.save(payment);
                    logger.info("Updated payment in DB: {}", payment);
                });
                return lookup;
            }
            logger.error("Transaction verification failed: status={}", response.getStatusCode());
            throw new RuntimeException("Transaction verification failed: status=" + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            logger.error("Khalti lookup error: status={}, response={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Transaction verification failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // Helper DTO for lookup body
    @Data
    public static class LookupRequest {
        @JsonProperty("token")
        private String token;

        public LookupRequest(String token) {
            this.token = token;
        }
    }
}