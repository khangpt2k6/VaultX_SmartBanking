package com.bankmanagement.controller;

import com.bankmanagement.model.Trade;
import com.bankmanagement.service.TradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for trading operations
 */
@RestController
@RequestMapping("/api/trading")
public class TradingController {
    private static final Logger log = LoggerFactory.getLogger(TradingController.class);
    
    @Autowired
    private TradingService tradingService;
    
    /**
     * Execute a BUY trade
     * POST /api/trading/buy
     */
    @PostMapping("/buy")
    public ResponseEntity<?> executeBuyTrade(
            @RequestParam Long userId,
            @RequestParam Long assetId,
            @RequestParam BigDecimal quantity,
            @RequestParam BigDecimal pricePerUnit,
            @RequestParam(required = false) Long portfolioId) {
        
        try {
            Trade trade = tradingService.executeBuyTrade(userId, assetId, quantity, pricePerUnit, portfolioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Buy trade executed successfully");
            response.put("trade", trade);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error executing buy trade: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to execute buy trade: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Execute a SELL trade
     * POST /api/trading/sell
     */
    @PostMapping("/sell")
    public ResponseEntity<?> executeSellTrade(
            @RequestParam Long userId,
            @RequestParam Long assetId,
            @RequestParam BigDecimal quantity,
            @RequestParam BigDecimal pricePerUnit,
            @RequestParam Long portfolioId) {
        
        try {
            Trade trade = tradingService.executeSellTrade(userId, assetId, quantity, pricePerUnit, portfolioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Sell trade executed successfully");
            response.put("trade", trade);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error executing sell trade: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to execute sell trade: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's trading history
     * GET /api/trading/history/{userId}
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getUserTrades(@PathVariable Long userId) {
        try {
            List<Trade> trades = tradingService.getUserTrades(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("totalTrades", trades.size());
            response.put("trades", trades);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching user trades: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch trades: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get trade by ID
     * GET /api/trading/{tradeId}
     */
    @GetMapping("/{tradeId}")
    public ResponseEntity<?> getTradeById(@PathVariable Long tradeId) {
        try {
            Optional<Trade> trade = tradingService.getTradeById(tradeId);
            
            if (trade.isEmpty()) {
                Map<String, Object> notFound = new HashMap<>();
                notFound.put("status", "error");
                notFound.put("message", "Trade not found");
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("trade", trade.get());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching trade: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch trade: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's buy trades
     * GET /api/trading/buy/{userId}
     */
    @GetMapping("/buy/{userId}")
    public ResponseEntity<?> getUserBuyTrades(@PathVariable Long userId) {
        try {
            List<Trade> trades = tradingService.getUserBuyTrades(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("totalBuyTrades", trades.size());
            response.put("trades", trades);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching buy trades: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch buy trades: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's sell trades
     * GET /api/trading/sell/{userId}
     */
    @GetMapping("/sell/{userId}")
    public ResponseEntity<?> getUserSellTrades(@PathVariable Long userId) {
        try {
            List<Trade> trades = tradingService.getUserSellTrades(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("totalSellTrades", trades.size());
            response.put("trades", trades);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching sell trades: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch sell trades: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
}