package com.bankmanagement.service;

import com.bankmanagement.model.Asset;
import com.bankmanagement.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Service for managing market data and asset pricing
 * Simulates real market data with mocked APIs
 */
@Service
public class MarketDataService {
    private static final Logger log = LoggerFactory.getLogger(MarketDataService.class);
    
    @Autowired
    private AssetRepository assetRepository;
    
    // Self-injection to ensure transaction proxy is used for REQUIRES_NEW
    // @Lazy prevents circular dependency issues
    @Autowired
    @Lazy
    private MarketDataService self;
    
    private final Random random = new Random();
    
    /**
     * Initialize default assets (stocks, crypto, commodities)
     */
    public void initializeDefaultAssets() {
        if (assetRepository.count() > 0) {
            log.info("Assets already initialized, skipping initialization");
            return;
        }
        
        List<Asset> defaultAssets = new ArrayList<>();
        
        // Popular Stocks
        defaultAssets.add(createAsset("AAPL", "Apple Inc.", Asset.AssetType.STOCK, 195.50));
        defaultAssets.add(createAsset("GOOGL", "Alphabet Inc.", Asset.AssetType.STOCK, 140.25));
        defaultAssets.add(createAsset("MSFT", "Microsoft Corporation", Asset.AssetType.STOCK, 380.75));
        defaultAssets.add(createAsset("AMZN", "Amazon.com Inc.", Asset.AssetType.STOCK, 175.50));
        defaultAssets.add(createAsset("TSLA", "Tesla Inc.", Asset.AssetType.STOCK, 245.30));
        defaultAssets.add(createAsset("META", "Meta Platforms Inc.", Asset.AssetType.STOCK, 480.50));
        defaultAssets.add(createAsset("NVDA", "NVIDIA Corporation", Asset.AssetType.STOCK, 520.75));
        
        // Cryptocurrencies
        defaultAssets.add(createAsset("BTC", "Bitcoin", Asset.AssetType.CRYPTOCURRENCY, 42500.00));
        defaultAssets.add(createAsset("ETH", "Ethereum", Asset.AssetType.CRYPTOCURRENCY, 2280.50));
        defaultAssets.add(createAsset("BNB", "Binance Coin", Asset.AssetType.CRYPTOCURRENCY, 315.25));
        defaultAssets.add(createAsset("ADA", "Cardano", Asset.AssetType.CRYPTOCURRENCY, 0.98));
        defaultAssets.add(createAsset("XRP", "Ripple", Asset.AssetType.CRYPTOCURRENCY, 0.52));
        defaultAssets.add(createAsset("SOL", "Solana", Asset.AssetType.CRYPTOCURRENCY, 105.50));
        
        // Commodities
        defaultAssets.add(createAsset("GOLD", "Gold (per oz)", Asset.AssetType.COMMODITY, 2045.50));
        defaultAssets.add(createAsset("OIL", "Crude Oil (WTI)", Asset.AssetType.COMMODITY, 78.50));
        defaultAssets.add(createAsset("SILVER", "Silver (per oz)", Asset.AssetType.COMMODITY, 24.85));
        defaultAssets.add(createAsset("COPPER", "Copper (per lb)", Asset.AssetType.COMMODITY, 4.05));
        
        // Indices
        defaultAssets.add(createAsset("SPX", "S&P 500", Asset.AssetType.INDEX, 4780.50));
        defaultAssets.add(createAsset("IXIC", "NASDAQ Composite", Asset.AssetType.INDEX, 14890.75));
        
        assetRepository.saveAll(defaultAssets);
        log.info("✅ Initialized {} default assets", defaultAssets.size());
    }
    
    /**
     * Create an asset with random price variation
     */
    private Asset createAsset(String symbol, String name, Asset.AssetType type, double basePrice) {
        Asset asset = new Asset(symbol, name, type, BigDecimal.valueOf(basePrice));
        asset.setPreviousPrice(BigDecimal.valueOf(basePrice * (0.98 + random.nextDouble() * 0.04)));
        asset.setChangePercent(calculateChangePercent(asset.getPreviousPrice(), asset.getCurrentPrice()));
        asset.setLastUpdated(LocalDateTime.now());
        return asset;
    }
    
    /**
     * Update asset price with realistic variation
     * Uses REQUIRES_NEW propagation to ensure each update is in its own transaction
     * This prevents one failure from aborting the entire batch
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Asset updateAssetPrice(Long assetId) {
        try {
            Optional<Asset> assetOpt = assetRepository.findById(assetId);
            if (assetOpt.isEmpty()) {
                log.warn("Asset not found with ID: {}", assetId);
                return null;
            }
            
            Asset asset = assetOpt.get();
            BigDecimal previousPrice = asset.getCurrentPrice();
            
            // Validate price is not null or zero
            if (previousPrice == null || previousPrice.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Invalid price for asset ID {}: {}", assetId, previousPrice);
                return asset;
            }
            
            // Simulate price change (±2% variation)
            double changePercent = (random.nextDouble() - 0.5) * 0.04;
            BigDecimal newPrice = previousPrice.multiply(BigDecimal.valueOf(1 + changePercent));
            
            asset.setPreviousPrice(previousPrice);
            asset.setCurrentPrice(newPrice);
            asset.setChangePercent(calculateChangePercent(previousPrice, newPrice));
            asset.setLastUpdated(LocalDateTime.now());
            
            return assetRepository.save(asset);
        } catch (Exception e) {
            log.error("Error updating asset price for ID {}: {}", assetId, e.getMessage(), e);
            throw e; // Re-throw to trigger rollback
        }
    }
    
    /**
     * Update all active asset prices
     * Each asset update runs in its own transaction (REQUIRES_NEW) to prevent
     * one failure from aborting the entire batch
     */
    public List<Asset> updateAllAssetPrices() {
        try {
            // Fetch assets in a separate read transaction
            List<Asset> assets = fetchActiveAssets();
            log.debug("Updating {} active assets", assets.size());
            
            List<Asset> updatedAssets = new ArrayList<>();
            int successCount = 0;
            int failureCount = 0;
            
            for (Asset asset : assets) {
                try {
                    // Use self-injected proxy to ensure transaction boundaries are respected
                    Asset updated = self.updateAssetPrice(asset.getAssetId());
                    if (updated != null) {
                        updatedAssets.add(updated);
                        successCount++;
                    }
                } catch (Exception e) {
                    failureCount++;
                    log.error("Failed to update asset {} ({}): {}", 
                            asset.getAssetId(), asset.getSymbol(), e.getMessage());
                    // Continue with other assets instead of failing completely
                }
            }
            
            log.info("Asset price update completed: {} succeeded, {} failed out of {}", 
                    successCount, failureCount, assets.size());
            
            return updatedAssets;
        } catch (Exception e) {
            log.error("Error in updateAllAssetPrices: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Fetch active assets in a read-only transaction
     */
    @Transactional(readOnly = true)
    private List<Asset> fetchActiveAssets() {
        return assetRepository.findByIsActiveTrue();
    }
    
    /**
     * Get asset by symbol
     */
    public Optional<Asset> getAssetBySymbol(String symbol) {
        return assetRepository.findBySymbol(symbol);
    }
    
    /**
     * Get all active assets
     */
    @Transactional(readOnly = true)
    public List<Asset> getAllActiveAssets() {
        return assetRepository.findByIsActiveTrue();
    }
    
    /**
     * Get assets by type
     */
    public List<Asset> getAssetsByType(Asset.AssetType type) {
        return assetRepository.findByAssetType(type);
    }
    
    /**
     * Get asset by ID
     */
    public Optional<Asset> getAssetById(Long assetId) {
        return assetRepository.findById(assetId);
    }
    
    /**
     * Calculate percentage change between two prices
     */
    private BigDecimal calculateChangePercent(BigDecimal previousPrice, BigDecimal currentPrice) {
        if (previousPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(previousPrice)
                .divide(previousPrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * Get market data summary
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMarketSummary() {
        List<Asset> assets = getAllActiveAssets();
        
        int gainers = 0;
        int losers = 0;
        int unchanged = 0;
        
        for (Asset asset : assets) {
            BigDecimal change = asset.getChangePercent();
            if (change != null) {
                if (change.compareTo(BigDecimal.ZERO) > 0) {
                    gainers++;
                } else if (change.compareTo(BigDecimal.ZERO) < 0) {
                    losers++;
                } else {
                    unchanged++;
                }
            }
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalAssets", assets.size());
        summary.put("gainers", gainers);
        summary.put("losers", losers);
        summary.put("unchanged", unchanged);
        summary.put("lastUpdated", LocalDateTime.now());
        
        return summary;
    }
}