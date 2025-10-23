package com.bankmanagement.service;

import com.bankmanagement.model.Asset;
import com.bankmanagement.model.Portfolio;
import com.bankmanagement.model.Position;
import com.bankmanagement.repository.PortfolioRepository;
import com.bankmanagement.repository.PositionRepository;
import com.bankmanagement.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing user portfolios and positions
 */
@Service
public class PortfolioService {
    private static final Logger log = LoggerFactory.getLogger(PortfolioService.class);
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private PositionRepository positionRepository;
    
    @Autowired
    private AssetRepository assetRepository;
    
    /**
     * Create a new portfolio for user
     */
    public Portfolio createPortfolio(Long userId, String portfolioName) {
        Portfolio portfolio = new Portfolio(userId, portfolioName);
        portfolio.setLastUpdated(LocalDateTime.now());
        return portfolioRepository.save(portfolio);
    }
    
    /**
     * Get all portfolios for user
     */
    public List<Portfolio> getUserPortfolios(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }
    
    /**
     * Get user's default portfolio or create one if not exists
     */
    public Portfolio getOrCreateDefaultPortfolio(Long userId) {
        Optional<Portfolio> existing = portfolioRepository.findByUserIdAndPortfolioName(userId, "Default");
        if (existing.isPresent()) {
            return existing.get();
        }
        return createPortfolio(userId, "Default");
    }
    
    /**
     * Get portfolio by ID
     */
    public Optional<Portfolio> getPortfolioById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId);
    }
    
    /**
     * Add or update position in portfolio
     */
    @Transactional
    public Position addOrUpdatePosition(Long portfolioId, Long assetId, BigDecimal quantity, 
                                        BigDecimal pricePerUnit) {
        
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
        
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
        
        Optional<Position> existingPosition = positionRepository
                .findByPortfolioIdAndAssetId(portfolioId, assetId);
        
        Position position;
        
        if (existingPosition.isPresent()) {
            position = existingPosition.get();
            // Update average buy price
            BigDecimal totalQuantity = position.getQuantity().add(quantity);
            BigDecimal totalCost = position.getQuantity().multiply(position.getAverageBuyPrice())
                    .add(quantity.multiply(pricePerUnit));
            position.setAverageBuyPrice(totalCost.divide(totalQuantity, 4, java.math.RoundingMode.HALF_UP));
            position.setQuantity(totalQuantity);
        } else {
            position = new Position(portfolioId, assetId, quantity, pricePerUnit, asset.getCurrentPrice());
        }
        
        // Update position values
        position.setCurrentPrice(asset.getCurrentPrice());
        position.setTotalValue(position.getQuantity().multiply(asset.getCurrentPrice()));
        
        BigDecimal gainLoss = position.getTotalValue()
                .subtract(position.getQuantity().multiply(position.getAverageBuyPrice()));
        position.setUnrealizedGainLoss(gainLoss);
        
        if (position.getAverageBuyPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal gainLossPercent = gainLoss.divide(
                    position.getQuantity().multiply(position.getAverageBuyPrice()),
                    4, java.math.RoundingMode.HALF_UP
            ).multiply(BigDecimal.valueOf(100));
            position.setGainLossPercent(gainLossPercent);
        }
        
        position.setLastUpdated(LocalDateTime.now());
        Position savedPosition = positionRepository.save(position);
        
        // Update portfolio values
        updatePortfolioValues(portfolio);
        
        log.info("✅ Position added/updated for portfolio {} and asset {}", portfolioId, assetId);
        return savedPosition;
    }
    
    /**
     * Remove position from portfolio
     */
    @Transactional
    public void removePosition(Long positionId) {
        Optional<Position> position = positionRepository.findById(positionId);
        if (position.isPresent()) {
            Long portfolioId = position.get().getPortfolioId();
            positionRepository.deleteById(positionId);
            
            Optional<Portfolio> portfolio = portfolioRepository.findById(portfolioId);
            portfolio.ifPresent(this::updatePortfolioValues);
            
            log.info("✅ Position removed: {}", positionId);
        }
    }
    
    /**
     * Get all positions in portfolio
     */
    public List<Position> getPortfolioPositions(Long portfolioId) {
        return positionRepository.findByPortfolioId(portfolioId);
    }
    
    /**
     * Update portfolio values based on current positions
     */
    @Transactional
    public Portfolio updatePortfolioValues(Portfolio portfolio) {
        List<Position> positions = getPortfolioPositions(portfolio.getPortfolioId());
        
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;
        
        for (Position position : positions) {
            BigDecimal positionCost = position.getQuantity().multiply(position.getAverageBuyPrice());
            totalInvested = totalInvested.add(positionCost);
            currentValue = currentValue.add(position.getTotalValue());
        }
        
        portfolio.setTotalInvested(totalInvested);
        portfolio.setCurrentValue(currentValue);
        
        BigDecimal gainLoss = currentValue.subtract(totalInvested);
        portfolio.setTotalGainLoss(gainLoss);
        
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal gainLossPercent = gainLoss.divide(totalInvested, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            portfolio.setGainLossPercent(gainLossPercent);
        }
        
        portfolio.setLastUpdated(LocalDateTime.now());
        return portfolioRepository.save(portfolio);
    }
    
    /**
     * Delete portfolio
     */
    public void deletePortfolio(Long portfolioId) {
        portfolioRepository.deleteById(portfolioId);
        log.info("✅ Portfolio deleted: {}", portfolioId);
    }
}