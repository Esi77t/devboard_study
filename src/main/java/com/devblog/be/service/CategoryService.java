package com.devblog.be.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devblog.be.dto.CategoryResponseDTO;
import com.devblog.be.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	
	private final CategoryRepository categoryRepository;
	
	public List<CategoryResponseDTO> getCategories() {
		return categoryRepository.findAll().stream()
				.map(CategoryResponseDTO::new)
				.toList();
	}
	
}
