package com.devblog.be.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.devblog.be.model.Category;
import com.devblog.be.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // 애플리케이션 시작 시 실행될 코드
        // 카테고리가 비어있을 때만 기본 카테고리 추가
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("Java"));
            categoryRepository.save(new Category("React"));
            categoryRepository.save(new Category("AWS"));
            categoryRepository.save(new Category("etc"));
        }
    }
}