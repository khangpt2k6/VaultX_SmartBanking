package com.bankmanagement.controller;

import com.bankmanagement.model.Portfolio;
import com.bankmanagement.model.Position;
import com.bankmanagement.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for portfolio management
 */
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private static final Logger log = LoggerFactory.getLogger(PortfolioController.class);
    
    @Autowired
    private PortfolioService portfolioService;
    
    /**
     * Create new portfolio
     * POST /api/portfolio/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPortfolio(
            @RequestParam Long userId,
            @RequestParam String portfolioName) {
        
        try {
            Portfolio portfolio = portfolioService.createPortfolio(userId, portfolioName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Portfolio created successfully");
            response.put("portfolio", portfolio);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error creating portfolio: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to create portfolio: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's portfolios
     * GET /api/portfolio/user/{userId}
     * Admins can view any user's portfolios, users can only view their own
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.details['userId']")
    public ResponseEntity<?> getUserPortfolios(@PathVariable Long userId) {
        try {
            List<Portfolio> portfolios = portfolioService.getUserPortfolios(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("totalPortfolios", portfolios.size());
            response.put("portfolios", portfolios);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching user portfolios: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch portfolios: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get portfolio by ID
     * GET /api/portfolio/{portfolioId}
     */
    @GetMapping("/{portfolioId}")
    public ResponseEntity<?> getPortfolioById(@PathVariable Long portfolioId) {
        try {
            Optional<Portfolio> portfolio = portfolioService.getPortfolioById(portfolioId);
            
            if (portfolio.isEmpty()) {
                Map<String, Object> notFound = new HashMap<>();
                notFound.put("status", "error");
                notFound.put("message", "Portfolio not found");
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("portfolio", portfolio.get());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching portfolio: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch portfolio: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get portfolio positions
     * GET /api/portfolio/{portfolioId}/positions
     */
    @GetMapping("/{portfolioId}/positions")
    public ResponseEntity<?> getPortfolioPositions(@PathVariable Long portfolioId) {
        try {
            List<Position> positions = portfolioService.getPortfolioPositions(portfolioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("totalPositions", positions.size());
            response.put("positions", positions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching positions: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch positions: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Update portfolio values
     * POST /api/portfolio/{portfolioId}/update-values
     */
    @PostMapping("/{portfolioId}/update-values")
    public ResponseEntity<?> updatePortfolioValues(@PathVariable Long portfolioId) {
        try {
            Optional<Portfolio> portfolioOpt = portfolioService.getPortfolioById(portfolioId);
            
            if (portfolioOpt.isEmpty()) {
                Map<String, Object> notFound = new HashMap<>();
                notFound.put("status", "error");
                notFound.put("message", "Portfolio not found");
                return ResponseEntity.notFound().build();
            }
            
            Portfolio updatedPortfolio = portfolioService.updatePortfolioValues(portfolioOpt.get());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Portfolio values updated");
            response.put("portfolio", updatedPortfolio);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error updating portfolio values: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to update portfolio: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Delete portfolio
     * DELETE /api/portfolio/{portfolioId}
     */
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<?> deletePortfolio(@PathVariable Long portfolioId) {
        try {
            portfolioService.deletePortfolio(portfolioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Portfolio deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error deleting portfolio: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to delete portfolio: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
}