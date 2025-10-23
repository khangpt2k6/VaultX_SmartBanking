package com.bankmanagement.repository;

import com.bankmanagement.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByPortfolioId(Long portfolioId);
    
    Optional<Position> findByPortfolioIdAndAssetId(Long portfolioId, Long assetId);
    
    List<Position> findByAssetId(Long assetId);
}