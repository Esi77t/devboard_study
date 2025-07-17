package com.devblog.be.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devblog.be.dto.TransactionRequestDTO;
import com.devblog.be.dto.TransactionResponseDTO;
import com.devblog.be.security.UserDetailsImpl;
import com.devblog.be.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransactionController {
	
	private final TransactionService transactionService;
	
	@PostMapping("/transactions")
	public ResponseEntity<TransactionResponseDTO> createTransaction(
			@RequestBody TransactionRequestDTO requestDto,
			@AuthenticationPrincipal UserDetailsImpl userDetails
			) {
		
		TransactionResponseDTO responseDto = transactionService.createTransaction(requestDto, userDetails.getUser());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
	
	@GetMapping("/transactions")
	public ResponseEntity<List<TransactionResponseDTO>> getTransactions(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		if(userDetails == null) {
			return ResponseEntity.ok(Collections.emptyList());
		}
		
		List<TransactionResponseDTO> responseDto = transactionService.getTransactions(userDetails.getUser());
		
		return ResponseEntity.ok(responseDto);
	}
	
	@GetMapping("/transactions/summary")
	public ResponseEntity<Map<String, Double>> getTransactionSummary(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		if(userDetails == null) {
			return ResponseEntity.ok(Collections.emptyMap());
		}
		
		Map<String, Double> summary = transactionService.getMonthlyExpenditureSummary(userDetails.getUser());
		
		return ResponseEntity.ok(summary);
	}
	
	@DeleteMapping("/transactions/{transactionId}")
	public ResponseEntity<String> deleteTransaction(@PathVariable("transactionId") Long transactionId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		transactionService.deleteTransaction(transactionId, userDetails.getUser());
		return ResponseEntity.ok("거래 내역 삭제에 성공했습니다.");
	}
}
