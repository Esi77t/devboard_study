package com.devblog.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devblog.be.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
}
