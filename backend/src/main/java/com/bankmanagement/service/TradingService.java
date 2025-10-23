package com.bankmanagement.service;

import com.bankmanagement.model.Account;
import com.bankmanagement.model.Asset;
import com.bankmanagement.model.Position;
import com.bankmanagement.model.Portfolio;
import com.bankmanagement.model.Trade;
import com.bankmanagement.repository.AccountRepository;
import com.bankmanagement.repository.AssetRepository;
import com.bankmanagement.repository.PortfolioRepository;
import com.bankmanagement.repository.PositionRepository;
import com.bankmanagement.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for executing trades and managing trading operations
 */
@Service
public class TradingService {
    private static final Logger log = LoggerFactory.getLogger(TradingService.class);
    
    @Autowired
    private TradeRepository tradeRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private PositionRepository positionRepository;
    
    @Autowired
    private PortfolioService portfolioService;
    
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.001"); // 0.1% commission
    
    /**
     * Execute a BUY trade
     */
    @Transactional
    public Trade executeBuyTrade(Long userId, Long assetId, BigDecimal quantity, BigDecimal pricePerUnit, 
                                  Long portfolioId) {
        
        // Verify asset exists
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
        
        // Calculate total cost with commission
        BigDecimal totalCost = quantity.multiply(pricePerUnit);
        BigDecimal commission = totalCost.multiply(COMMISSION_RATE);
        BigDecimal totalWithCommission = totalCost.add(commission);
        
        // Get user's trading account or create one
        Portfolio portfolio = getOrCreateUserPortfolio(userId, portfolioId);
        
        // Verify sufficient balance
        verifyAccountBalance(userId, totalWithCommission);
        
        // Create trade record
        Trade trade = new Trade(userId, assetId, Trade.TradeType.BUY, quantity, pricePerUnit);
        trade.setCommission(commission);
        trade.setStatus(Trade.TradeStatus.COMPLETED);
        trade.setExecutedAt(LocalDateTime.now());
        
        // Deduct from account balance
        updateAccountBalance(userId, totalWithCommission.negate());
        
        // Add position to portfolio
        portfolioService.addOrUpdatePosition(portfolio.getPortfolioId(), assetId, quantity, pricePerUnit);
        
        Trade savedTrade = tradeRepository.save(trade);
        log.info("✅ Buy trade executed - User: {}, Asset: {}, Quantity: {}", userId, assetId, quantity);
        
        return savedTrade;
    }
    
    /**
     * Execute a SELL trade
     */
    @Transactional
    public Trade executeSellTrade(Long userId, Long assetId, BigDecimal quantity, BigDecimal pricePerUnit, 
                                   Long portfolioId) {
        
        // Verify asset exists
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
        
        // Get portfolio and verify position exists
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
        
        Position position = positionRepository.findByPortfolioIdAndAssetId(portfolioId, assetId)
                .orElseThrow(() -> new IllegalArgumentException("Position not found in portfolio"));
        
        // Verify sufficient quantity
        if (position.getQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Insufficient position quantity. Available: " + 
                    position.getQuantity() + ", Requested: " + quantity);
        }
        
        // Calculate proceeds with commission
        BigDecimal totalProceeds = quantity.multiply(pricePerUnit);
        BigDecimal commission = totalProceeds.multiply(COMMISSION_RATE);
        BigDecimal netProceeds = totalProceeds.subtract(commission);
        
        // Create trade record
        Trade trade = new Trade(userId, assetId, Trade.TradeType.SELL, quantity, pricePerUnit);
        trade.setCommission(commission);
        trade.setStatus(Trade.TradeStatus.COMPLETED);
        trade.setExecutedAt(LocalDateTime.now());
        
        // Add proceeds to account balance
        updateAccountBalance(userId, netProceeds);
        
        // Update position quantity
        BigDecimal newQuantity = position.getQuantity().subtract(quantity);
        
        if (newQuantity.compareTo(BigDecimal.ZERO) > 0) {
            position.setQuantity(newQuantity);
            position.setTotalValue(newQuantity.multiply(asset.getCurrentPrice()));
            position.setLastUpdated(LocalDateTime.now());
            positionRepository.save(position);
        } else {
            // Remove position if quantity becomes zero
            positionRepository.delete(position);
        }
        
        // Update portfolio
        portfolioService.updatePortfolioValues(portfolio);
        
        Trade savedTrade = tradeRepository.save(trade);
        log.info("✅ Sell trade executed - User: {}, Asset: {}, Quantity: {}", userId, assetId, quantity);
        
        return savedTrade;
    }
    
    /**
     * Get user's trading history
     */
    public List<Trade> getUserTrades(Long userId) {
        return tradeRepository.findByUserIdOrderByExecutedAtDesc(userId);
    }
    
    /**
     * Get user's trades for specific asset
     */
    public List<Trade> getUserTradesForAsset(Long userId, Long assetId) {
        return tradeRepository.findByUserIdAndAssetId(userId, assetId);
    }
    
    /**
     * Get user's buy trades
     */
    public List<Trade> getUserBuyTrades(Long userId) {
        return tradeRepository.findByUserIdAndTradeType(userId, Trade.TradeType.BUY);
    }
    
    /**
     * Get user's sell trades
     */
    public List<Trade> getUserSellTrades(Long userId) {
        return tradeRepository.findByUserIdAndTradeType(userId, Trade.TradeType.SELL);
    }
    
    /**
     * Get trade by ID
     */
    public Optional<Trade> getTradeById(Long tradeId) {
        return tradeRepository.findById(tradeId);
    }
    
    /**
     * Calculate portfolio performance
     */
    public Object calculatePortfolioPerformance(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
        
        return Map.of(
                "totalInvested", portfolio.getTotalInvested(),
                "currentValue", portfolio.getCurrentValue(),
                "totalGainLoss", portfolio.getTotalGainLoss(),
                "gainLossPercent", portfolio.getGainLossPercent()
        );
    }
    
    // Helper methods
    
    private Portfolio getOrCreateUserPortfolio(Long userId, Long portfolioId) {
        if (portfolioId != null) {
            return portfolioRepository.findById(portfolioId)
                    .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
        }
        return portfolioService.getOrCreateDefaultPortfolio(userId);
    }
    
    private void verifyAccountBalance(Long userId, BigDecimal requiredAmount) {
        Optional<Account> accountOpt = accountRepository.findByCustomerId(userId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("No trading account found for user");
        }
        
        Account account = accountOpt.get();
        if (account.getBalance().compareTo(requiredAmount) < 0) {
            throw new IllegalArgumentException("Insufficient account balance. Required: " + 
                    requiredAmount + ", Available: " + account.getBalance());
        }
    }
    
    private void updateAccountBalance(Long userId, BigDecimal amount) {
        Optional<Account> accountOpt = accountRepository.findByCustomerId(userId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("No trading account found for user");
        }
        
        Account account = accountOpt.get();
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }
}