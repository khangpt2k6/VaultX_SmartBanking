package com.bankmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Asset model representing tradeable assets (stocks, crypto, commodities)
 */
@Entity
@Table(name = "assets")
@EntityListeners(AuditingEntityListener.class)
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long assetId;
    
    @NotBlank(message = "Symbol is required")
    @Column(name = "symbol", nullable = false, unique = true)
    private String symbol;
    
    @NotBlank(message = "Asset name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull(message = "Asset type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType assetType;
    
    @NotNull(message = "Current price is required")
    @Column(name = "current_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentPrice;
    
    @Column(name = "previous_price", precision = 19, scale = 4)
    private BigDecimal previousPrice;
    
    @Column(name = "change_percent", precision = 8, scale = 2)
    private BigDecimal changePercent;
    
    @Column(name = "market_cap")
    private String marketCap;
    
    @Column(name = "currency")
    private String currency = "USD";
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public enum AssetType {
        STOCK, CRYPTOCURRENCY, COMMODITY, FOREX, INDEX
    }

    // Constructors
    public Asset() {
    }

    public Asset(String symbol, String name, AssetType assetType, BigDecimal currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.assetType = assetType;
        this.currentPrice = currentPrice;
        this.currency = "USD";
    }

    // Getters and Setters
    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(BigDecimal previousPrice) {
        this.previousPrice = previousPrice;
    }

    public BigDecimal getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(BigDecimal changePercent) {
        this.changePercent = changePercent;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "assetId=" + assetId +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", assetType=" + assetType +
                ", currentPrice=" + currentPrice +
                ", changePercent=" + changePercent +
                '}';
    }
}