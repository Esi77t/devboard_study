package com.devblog.be.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.devblog.be.dto.TransactionRequestDTO;
import com.devblog.be.dto.TransactionResponseDTO;
import com.devblog.be.model.Transaction;
import com.devblog.be.model.User;
import com.devblog.be.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
	
	private final TransactionRepository transactionRepository;
	
	public TransactionResponseDTO createTransaction(TransactionRequestDTO requestDto, User user) {
		Transaction transaction = new Transaction(
				requestDto.getDate(),
				requestDto.getType(),
				requestDto.getCategory(),
				requestDto.getAmount(),
				requestDto.getDescription(),
				user
		);
		
		Transaction savedTransaction = transactionRepository.save(transaction);
		
		return new TransactionResponseDTO(savedTransaction);
	}
	
	public List<TransactionResponseDTO> getTransactions(User user) {
		return transactionRepository.findAllByUserOrderByDateAndIdDesc(user).stream().map(TransactionResponseDTO::new).toList();
	}
	
	public Map<String, Double> getMonthlyExpenditureSummary(User user) {
		LocalDate today = LocalDate.now();
		
		YearMonth currentMonth = YearMonth.from(today);
		LocalDate currentMonthStart = currentMonth.atDay(1);
		LocalDate currentMonthEnd = currentMonth.atEndOfMonth();
		Double currentMonthTotal = transactionRepository.sumAmountByUserAndTypeAndDateBetween(user, currentMonthStart, currentMonthEnd);
		
		YearMonth lastMonth = currentMonth.minusMonths(1);
		LocalDate lastMonthStart = lastMonth.atDay(1);
		LocalDate lastMonthEnd = lastMonth.atEndOfMonth();
		Double lastMonthTotal = transactionRepository.sumAmountByUserAndTypeAndDateBetween(user, lastMonthStart, lastMonthEnd);
		
		Map<String, Double> summary = new HashMap<>();
		summary.put("currentMonthTotal", currentMonthTotal != null ? currentMonthTotal : 0.0);
		summary.put("lastMonthTotal", lastMonthTotal != null ? lastMonthTotal : 0.0);
		
		return summary;
	}
	
	public void deleteTransaction(Long transactionId, User user) {
		Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new IllegalArgumentException("해당 거래 내역을 찾을 수 없습니다."));
		
		if(!transaction.getUser().getUsername().equals(user.getUsername())) {
			throw new IllegalArgumentException("삭제할 권한이 없습니다.");
		}
		
		transactionRepository.delete(transaction);
	}
}
