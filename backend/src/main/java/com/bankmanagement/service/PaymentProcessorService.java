package com.bankmanagement.service;

import com.bankmanagement.model.Account;
import com.bankmanagement.model.PaymentRequest;
import com.bankmanagement.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Multi-threaded Payment Processor Simulator
 * Demonstrates concurrent transaction processing with thread-safe account management
 */
@Service
public class PaymentProcessorService {
    private static final Logger log = LoggerFactory.getLogger(PaymentProcessorService.class);

    @Autowired
    private AccountRepository accountRepository;

    // Thread pool for concurrent payment processing
    private ExecutorService executorService;
    private static final int THREAD_POOL_SIZE = 8;

    // Thread-safe account storage with locks
    private final ConcurrentHashMap<Long, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    // Asynchronous logging with BlockingQueue (Producer-Consumer pattern)
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private Thread loggerThread;
    private volatile boolean running = true;

    // Transaction validation thresholds
    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("100000");
    private static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("0.01");
    private static final int MAX_RETRY_ATTEMPTS = 3;

    // Metrics
    private final AtomicLong processedCount = new AtomicLong(0);
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failedCount = new AtomicLong(0);
    private final AtomicLong retryCount = new AtomicLong(0);

    @PostConstruct
    public void init() {
        // Initialize thread pool
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, 
            r -> {
                Thread t = new Thread(r, "PaymentProcessor-" + System.nanoTime());
                t.setDaemon(false);
                return t;
            });

        // Start asynchronous logger thread (Consumer)
        loggerThread = new Thread(this::consumeLogs, "PaymentLogger");
        loggerThread.setDaemon(true);
        loggerThread.start();

        log.info("✅ Payment Processor initialized with {} threads", THREAD_POOL_SIZE);
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        loggerThread.interrupt();
        log.info("🛑 Payment Processor shutdown complete");
    }

    /**
     * Process multiple payments concurrently
     */
    public Map<String, Object> processPaymentsConcurrently(List<PaymentRequest> requests) {
        long startTime = System.currentTimeMillis();
        log.info("🚀 Processing {} payments concurrently", requests.size());

        // Submit all payment requests to thread pool
        List<CompletableFuture<PaymentRequest>> futures = requests.stream()
            .map(request -> CompletableFuture.supplyAsync(() -> processPayment(request), executorService))
            .collect(Collectors.toList());

        // Wait for all to complete and collect results
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        try {
            allOf.get(); // Wait for all to complete
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error waiting for payment processing", e);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("totalRequests", requests.size());
        result.put("processed", processedCount.get());
        result.put("successful", successCount.get());
        result.put("failed", failedCount.get());
        result.put("retries", retryCount.get());
        result.put("durationMs", duration);
        result.put("throughput", requests.size() / (duration / 1000.0));

        log.info("✅ Batch processing complete: {} payments in {}ms ({} payments/sec)", 
            requests.size(), duration, String.format("%.2f", result.get("throughput")));

        return result;
    }

    /**
     * Process a single payment with retry mechanism
     */
    private PaymentRequest processPayment(PaymentRequest request) {
        processedCount.incrementAndGet();
        request.setStatus(PaymentRequest.PaymentStatus.PROCESSING);

        // Retry logic with CompletableFuture
        CompletableFuture<PaymentRequest> future = CompletableFuture
            .supplyAsync(() -> executePayment(request), executorService);

        // Retry up to MAX_RETRY_ATTEMPTS times
        for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                PaymentRequest result = future.get(5, TimeUnit.SECONDS);
                if (result.getStatus() == PaymentRequest.PaymentStatus.SUCCESS) {
                    return result;
                }
            } catch (TimeoutException | ExecutionException e) {
                log.warn("Payment {} failed on attempt {}, retrying...", 
                    request.getRequestId(), attempt + 1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                request.setStatus(PaymentRequest.PaymentStatus.FAILED);
                request.setErrorMessage("Interrupted");
                return request;
            }

            // Retry
            if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                request.incrementRetryCount();
                retryCount.incrementAndGet();
                request.setStatus(PaymentRequest.PaymentStatus.RETRYING);
                future = CompletableFuture.supplyAsync(() -> executePayment(request), executorService);
            }
        }

        // All retries exhausted
        request.setStatus(PaymentRequest.PaymentStatus.FAILED);
        failedCount.incrementAndGet();
        return request;
    }

    /**
     * Execute payment with network delay simulation
     */
    private PaymentRequest executePayment(PaymentRequest request) {
        try {
            // Simulate network delay (50-200ms)
            simulateNetworkDelay();

            // Validate transaction
            if (!validateTransaction(request)) {
                request.setStatus(PaymentRequest.PaymentStatus.FAILED);
                failedCount.incrementAndGet();
                return request;
            }

            // Get account locks
            ReentrantLock fromLock = getAccountLock(request.getFromAccountId());
            ReentrantLock toLock = getAccountLock(request.getToAccountId());

            // Acquire locks in consistent order to prevent deadlock
            if (request.getFromAccountId() < request.getToAccountId()) {
                fromLock.lock();
                toLock.lock();
            } else {
                toLock.lock();
                fromLock.lock();
            }

            try {
                // Fetch accounts
                Account fromAccount = accountRepository.findById(request.getFromAccountId())
                    .orElseThrow(() -> new RuntimeException("From account not found"));
                Account toAccount = accountRepository.findById(request.getToAccountId())
                    .orElseThrow(() -> new RuntimeException("To account not found"));

                // Check sufficient funds
                if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
                    request.setStatus(PaymentRequest.PaymentStatus.FAILED);
                    request.setErrorMessage("Insufficient funds");
                    failedCount.incrementAndGet();
                    return request;
                }

                // Atomic debit and credit
                fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
                toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

                // Save accounts
                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);

                // Success
                request.setStatus(PaymentRequest.PaymentStatus.SUCCESS);
                request.setProcessedAt(LocalDateTime.now());
                successCount.incrementAndGet();

                // Log asynchronously
                logAsync(String.format("✅ Payment %s: $%.2f from account %d to %d - SUCCESS", 
                    request.getRequestId(), request.getAmount(), 
                    request.getFromAccountId(), request.getToAccountId()));

            } finally {
                // Release locks in reverse order
                if (request.getFromAccountId() < request.getToAccountId()) {
                    toLock.unlock();
                    fromLock.unlock();
                } else {
                    fromLock.unlock();
                    toLock.unlock();
                }
            }

        } catch (Exception e) {
            request.setStatus(PaymentRequest.PaymentStatus.FAILED);
            request.setErrorMessage(e.getMessage());
            failedCount.incrementAndGet();
            logAsync(String.format("❌ Payment %s FAILED: %s", request.getRequestId(), e.getMessage()));
        }

        return request;
    }

    /**
     * Validate transaction (fraud detection, amount limits)
     */
    private boolean validateTransaction(PaymentRequest request) {
        // Check amount limits
        if (request.getAmount().compareTo(MIN_TRANSACTION_AMOUNT) < 0) {
            request.setErrorMessage("Amount too small");
            return false;
        }

        if (request.getAmount().compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            request.setErrorMessage("Amount exceeds maximum limit");
            return false;
        }

        // Check account IDs
        if (request.getFromAccountId() == null || request.getToAccountId() == null) {
            request.setErrorMessage("Invalid account IDs");
            return false;
        }

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            request.setErrorMessage("Cannot transfer to same account");
            return false;
        }

        return true;
    }

    /**
     * Get or create lock for account (thread-safe)
     */
    private ReentrantLock getAccountLock(Long accountId) {
        return accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
    }

    /**
     * Simulate network delay (50-200ms)
     */
    private void simulateNetworkDelay() {
        try {
            int delay = 50 + (int)(Math.random() * 150); // 50-200ms
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Asynchronous logging (Producer)
     */
    private void logAsync(String message) {
        try {
            logQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Consumer thread for logging
     */
    private void consumeLogs() {
        while (running || !logQueue.isEmpty()) {
            try {
                String message = logQueue.poll(1, TimeUnit.SECONDS);
                if (message != null) {
                    log.info("[PaymentLogger] {}", message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Create a payment request
     */
    public PaymentRequest createPaymentRequest(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        return new PaymentRequest(requestId, fromAccountId, toAccountId, amount);
    }

    /**
     * Get processing metrics
     */
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        metrics.put("processed", processedCount.get());
        metrics.put("successful", successCount.get());
        metrics.put("failed", failedCount.get());
        metrics.put("retries", retryCount.get());
        metrics.put("successRate", processedCount.get() > 0 ? 
            (successCount.get() * 100.0 / processedCount.get()) : 0);
        metrics.put("threadPoolSize", THREAD_POOL_SIZE);
        metrics.put("pendingLogs", logQueue.size());
        return metrics;
    }

    /**
     * Reset metrics
     */
    public void resetMetrics() {
        processedCount.set(0);
        successCount.set(0);
        failedCount.set(0);
        retryCount.set(0);
    }
}

