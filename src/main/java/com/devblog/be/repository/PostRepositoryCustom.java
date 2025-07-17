package com.devblog.be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.devblog.be.dto.PostSearchCond;
import com.devblog.be.model.Post;

@Repository
public interface PostRepositoryCustom {
	Page<Post> search(Long categoryId, PostSearchCond condition, Pageable pageable);
}
