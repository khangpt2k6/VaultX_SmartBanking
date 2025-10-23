package com.bankmanagement.service;

import com.bankmanagement.model.Asset;
import com.bankmanagement.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     */
    public Asset updateAssetPrice(Long assetId) {
        Optional<Asset> assetOpt = assetRepository.findById(assetId);
        if (assetOpt.isEmpty()) {
            return null;
        }
        
        Asset asset = assetOpt.get();
        BigDecimal previousPrice = asset.getCurrentPrice();
        
        // Simulate price change (±2% variation)
        double changePercent = (random.nextDouble() - 0.5) * 0.04;
        BigDecimal newPrice = previousPrice.multiply(BigDecimal.valueOf(1 + changePercent));
        
        asset.setPreviousPrice(previousPrice);
        asset.setCurrentPrice(newPrice);
        asset.setChangePercent(calculateChangePercent(previousPrice, newPrice));
        asset.setLastUpdated(LocalDateTime.now());
        
        return assetRepository.save(asset);
    }
    
    /**
     * Update all active asset prices
     */
    public List<Asset> updateAllAssetPrices() {
        List<Asset> assets = assetRepository.findByIsActiveTrue();
        for (Asset asset : assets) {
            updateAssetPrice(asset.getAssetId());
        }
        return assets;
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
    public Map<String, Object> getMarketSummary() {
        List<Asset> assets = getAllActiveAssets();
        
        int gainers = 0;
        int losers = 0;
        int unchanged = 0;
        
        for (Asset asset : assets) {
            BigDecimal change = asset.getChangePercent();
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                gainers++;
            } else if (change.compareTo(BigDecimal.ZERO) < 0) {
                losers++;
            } else {
                unchanged++;
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