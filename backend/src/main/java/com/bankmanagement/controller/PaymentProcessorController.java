package com.bankmanagement.controller;

import com.bankmanagement.model.PaymentRequest;
import com.bankmanagement.service.PaymentProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Multi-threaded Payment Processor
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentProcessorController {
    private static final Logger log = LoggerFactory.getLogger(PaymentProcessorController.class);

    @Autowired
    private PaymentProcessorService paymentProcessorService;

    /**
     * Process multiple payments concurrently
     * POST /api/payments/process-batch
     */
    @PostMapping("/process-batch")
    public ResponseEntity<?> processBatchPayments(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> payments = (List<Map<String, Object>>) request.get("payments");
            int count = request.containsKey("count") ? (Integer) request.get("count") : payments.size();

            List<PaymentRequest> paymentRequests = new ArrayList<>();

            if (payments != null && !payments.isEmpty()) {
                // Use provided payments
                for (Map<String, Object> payment : payments) {
                    Long fromAccountId = Long.valueOf(payment.get("fromAccountId").toString());
                    Long toAccountId = Long.valueOf(payment.get("toAccountId").toString());
                    BigDecimal amount = new BigDecimal(payment.get("amount").toString());
                    paymentRequests.add(paymentProcessorService.createPaymentRequest(fromAccountId, toAccountId, amount));
                }
            } else {
                // Generate random payments for demo
                paymentRequests = generateRandomPayments(count);
            }

            Map<String, Object> result = paymentProcessorService.processPaymentsConcurrently(paymentRequests);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Payments processed concurrently");
            response.put("results", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing batch payments: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to process payments: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get processing metrics
     * GET /api/payments/metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics() {
        try {
            Map<String, Object> metrics = paymentProcessorService.getMetrics();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("metrics", metrics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting metrics: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to get metrics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Reset metrics
     * POST /api/payments/reset-metrics
     */
    @PostMapping("/reset-metrics")
    public ResponseEntity<?> resetMetrics() {
        try {
            paymentProcessorService.resetMetrics();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Metrics reset successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error resetting metrics: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to reset metrics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Generate random payment requests for demo
     */
    private List<PaymentRequest> generateRandomPayments(int count) {
        List<PaymentRequest> requests = new ArrayList<>();
        // This would need actual account IDs from your database
        // For demo purposes, using placeholder IDs
        for (int i = 0; i < count; i++) {
            Long fromAccountId = 1L + (i % 5); // Cycle through accounts 1-5
            Long toAccountId = 2L + (i % 5);
            if (fromAccountId.equals(toAccountId)) {
                toAccountId = (toAccountId % 5) + 1L;
            }
            BigDecimal amount = BigDecimal.valueOf(10 + Math.random() * 990); // $10-$1000
            requests.add(paymentProcessorService.createPaymentRequest(fromAccountId, toAccountId, amount));
        }
        return requests;
    }
}

