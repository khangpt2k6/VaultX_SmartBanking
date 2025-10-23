package com.bankmanagement.repository;

import com.bankmanagement.model.Deposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByUserId(Long userId);
    
    List<Deposit> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<Deposit> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Deposit> findByUserIdAndStatus(Long userId, Deposit.DepositStatus status);
    
    @Query("SELECT d FROM Deposit d WHERE d.userId = :userId AND d.status = 'COMPLETED' AND d.createdAt BETWEEN :startDate AND :endDate ORDER BY d.createdAt DESC")
    List<Deposit> findCompletedDepositsByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    long countByUserId(Long userId);
}