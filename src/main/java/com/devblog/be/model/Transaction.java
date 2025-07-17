package com.devblog.be.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Transaction {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private LocalDate date;  // 거래 날짜
	
	@Column(nullable=false)
	private String type;     // 수입, 지출
	
	@Column(nullable=false)
	private String category; // 식비, 월급, 교통비 등
	
	@Column(nullable=false)
	private Double amount;   // 금액
	
	@Column
	private String description; // 간단한 메모
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    public Transaction(LocalDate date, String type, String category, Double amount, String description, User user) {
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.user = user;
    }
}
