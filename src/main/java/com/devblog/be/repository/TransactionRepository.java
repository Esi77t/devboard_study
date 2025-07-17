package com.devblog.be.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devblog.be.model.Transaction;
import com.devblog.be.model.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
	List<Transaction> findAllByUserOrderByDateDesc(User user);
	List<Transaction> findAllByUser(User user);
	
	@Query("SELECT t FROM Transaction t WHERE t.user = :user ORDER BY t.date DESC, t.id DESC")
	List<Transaction> findAllByUserOrderByDateAndIdDesc(@Param("user") User user);
	
	@Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = '지출' AND t.date BETWEEN :startDate AND :endDate ORDER BY t.id DESC")
    Double sumAmountByUserAndTypeAndDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
