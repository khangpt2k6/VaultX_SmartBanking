package com.bankmanagement.controller;

import com.bankmanagement.model.Asset;
import com.bankmanagement.service.MarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for asset and market data management
 */
@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private static final Logger log = LoggerFactory.getLogger(AssetController.class);
    
    @Autowired
    private MarketDataService marketDataService;
    
    /**
     * Initialize default assets
     * POST /api/assets/init
     */
    @PostMapping("/init")
    public ResponseEntity<?> initializeAssets() {
        try {
            marketDataService.initializeDefaultAssets();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Default assets initialized successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error initializing assets: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to initialize assets: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get all active assets
     * GET /api/assets/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllAssets() {
        try {
            List<Asset> assets = marketDataService.getAllActiveAssets();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("totalAssets", assets.size());
            response.put("assets", assets);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching assets: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch assets: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get asset by symbol
     * GET /api/assets/symbol/{symbol}
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<?> getAssetBySymbol(@PathVariable String symbol) {
        try {
            Optional<Asset> asset = marketDataService.getAssetBySymbol(symbol);
            
            if (asset.isEmpty()) {
                Map<String, Object> notFound = new HashMap<>();
                notFound.put("status", "error");
                notFound.put("message", "Asset not found: " + symbol);
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("asset", asset.get());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching asset: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch asset: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get asset by ID
     * GET /api/assets/{assetId}
     */
    @GetMapping("/{assetId}")
    public ResponseEntity<?> getAssetById(@PathVariable Long assetId) {
        try {
            Optional<Asset> asset = marketDataService.getAssetById(assetId);
            
            if (asset.isEmpty()) {
                Map<String, Object> notFound = new HashMap<>();
                notFound.put("status", "error");
                notFound.put("message", "Asset not found");
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("asset", asset.get());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching asset: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch asset: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get assets by type
     * GET /api/assets/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getAssetsByType(@PathVariable String type) {
        try {
            Asset.AssetType assetType = Asset.AssetType.valueOf(type.toUpperCase());
            List<Asset> assets = marketDataService.getAssetsByType(assetType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("type", assetType);
            response.put("totalAssets", assets.size());
            response.put("assets", assets);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid asset type: {}", type);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Invalid asset type: " + type);
            
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("❌ Error fetching assets by type: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch assets: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Update all asset prices
     * POST /api/assets/update-prices
     */
    @PostMapping("/update-prices")
    public ResponseEntity<?> updateAllPrices() {
        try {
            List<Asset> updatedAssets = marketDataService.updateAllAssetPrices();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Asset prices updated");
            response.put("updatedCount", updatedAssets.size());
            response.put("assets", updatedAssets);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error updating asset prices: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to update prices: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get market summary
     * GET /api/assets/market/summary
     */
    @GetMapping("/market/summary")
    public ResponseEntity<?> getMarketSummary() {
        try {
            Map<String, Object> summary = marketDataService.getMarketSummary();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("summary", summary);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching market summary: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch market summary: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
}