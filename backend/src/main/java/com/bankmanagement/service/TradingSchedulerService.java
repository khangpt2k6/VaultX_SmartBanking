package com.bankmanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
     */
    @Scheduled(fixedRate = 30000)
    public void updateAssetPrices() {
        try {
            marketDataService.updateAllAssetPrices();
            log.info("📈 Asset prices updated automatically");
        } catch (Exception e) {
            log.error("❌ Error updating asset prices: {}", e.getMessage());
        }
    }
    
    /**
     * Log market summary every 5 minutes
     */
    @Scheduled(fixedRate = 300000)
    public void logMarketSummary() {
        try {
            var summary = marketDataService.getMarketSummary();
            log.info("📊 Market Summary - Gainers: {}, Losers: {}, Unchanged: {}", 
                    summary.get("gainers"), 
                    summary.get("losers"), 
                    summary.get("unchanged"));
        } catch (Exception e) {
            log.error("❌ Error logging market summary: {}", e.getMessage());
        }
    }
}