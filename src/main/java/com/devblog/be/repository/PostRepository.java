package com.devblog.be.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devblog.be.model.Category;
import com.devblog.be.model.Post;
import com.devblog.be.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{
	List<Post> findAllByOrderByModifiedAtDesc();
	List<Post> findAllByCategoryOrderByModifiedAtDesc(Category category);
	List<Post> findAllByUser(User user);
	Page<Post> findAll(Pageable pageable);
	Page<Post> findAllByCategory(Category category, Pageable pageable);
}
