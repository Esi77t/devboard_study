package com.devblog.be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devblog.be.dto.CategoryResponseDTO;
import com.devblog.be.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {
	
	private final CategoryService categoryService;
	
	@GetMapping("/categories")
	public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
		List<CategoryResponseDTO> categories = categoryService.getCategories();
		return ResponseEntity.ok(categories);
	}
	
}
