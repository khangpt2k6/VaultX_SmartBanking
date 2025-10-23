package com.bankmanagement.repository;

import com.bankmanagement.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);
    
    List<Asset> findByAssetType(Asset.AssetType assetType);
    
    List<Asset> findByIsActiveTrue();
    
    @Query("SELECT a FROM Asset a WHERE a.isActive = true ORDER BY a.symbol ASC")
    List<Asset> findAllActiveAssets();
}