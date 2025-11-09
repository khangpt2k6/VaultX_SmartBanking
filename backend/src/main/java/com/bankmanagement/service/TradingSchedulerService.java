package com.bankmanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * Service for scheduled trading tasks like updating asset prices
 */
@Service
public class TradingSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(TradingSchedulerService.class);
    
    @Autowired
    private MarketDataService marketDataService;
    
    /**
     * Update all asset prices every 30 seconds
     * Simulates real market data updates
     * Handles transaction errors gracefully to prevent scheduled task failures
     */
    @Scheduled(fixedRate = 30000)
    public void updateAssetPrices() {
        try {
            marketDataService.updateAllAssetPrices();
            log.info("📈 Asset prices updated automatically");
        } catch (DataAccessException e) {
            log.error("❌ Database error updating asset prices: {}", e.getMessage(), e);
            // Don't rethrow - allow scheduled task to continue
        } catch (UnexpectedRollbackException e) {
            log.error("❌ Transaction rollback error updating asset prices: {}", e.getMessage(), e);
            // Don't rethrow - allow scheduled task to continue
        } catch (Exception e) {
            log.error("❌ Unexpected error updating asset prices: {}", e.getMessage(), e);
            // Don't rethrow - allow scheduled task to continue
        }
    }
    
    /**
     * Log market summary every 5 minutes
     * Handles errors gracefully to prevent scheduled task failures
     */
    @Scheduled(fixedRate = 300000)
    public void logMarketSummary() {
        try {
            var summary = marketDataService.getMarketSummary();
            log.info("📊 Market Summary - Gainers: {}, Losers: {}, Unchanged: {}", 
                    summary.get("gainers"), 
                    summary.get("losers"), 
                    summary.get("unchanged"));
        } catch (DataAccessException e) {
            log.error("❌ Database error logging market summary: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ Error logging market summary: {}", e.getMessage(), e);
        }
    }
}