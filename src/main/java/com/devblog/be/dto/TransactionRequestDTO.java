package com.devblog.be.dto;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class TransactionRequestDTO {
	private LocalDate date;
	private String type;
	private String category;
	private Double amount;
	private String description;
}
