package com.bankmanagement.repository;

import com.bankmanagement.model.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByUserId(Long userId);
    
    List<Trade> findByUserIdOrderByExecutedAtDesc(Long userId);
    
    Page<Trade> findByUserIdOrderByExecutedAtDesc(Long userId, Pageable pageable);
    
    List<Trade> findByUserIdAndAssetId(Long userId, Long assetId);
    
    List<Trade> findByUserIdAndTradeType(Long userId, Trade.TradeType tradeType);
    
    @Query("SELECT t FROM Trade t WHERE t.userId = :userId AND t.executedAt BETWEEN :startDate AND :endDate ORDER BY t.executedAt DESC")
    List<Trade> findTradesByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    long countByUserId(Long userId);
}