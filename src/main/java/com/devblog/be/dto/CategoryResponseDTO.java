package com.devblog.be.dto;

import com.devblog.be.model.Category;

import lombok.Getter;

@Getter
public class CategoryResponseDTO {
	private Long id;
	private String name;
	
	public CategoryResponseDTO(Category category) {
		this.id = category.getId();
		this.name = category.getName();
	}
}
