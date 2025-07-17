package com.devblog.be.dto;

import java.time.LocalDate;

import com.devblog.be.model.Transaction;

import lombok.Getter;

@Getter
public class TransactionResponseDTO {
	private Long id;
	private LocalDate date;
	private String type;
	private String category;
	private Double amount;
	private String description;
	
	public TransactionResponseDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.date = transaction.getDate();
        this.type = transaction.getType();
        this.category = transaction.getCategory();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
    }
}
