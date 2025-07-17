package com.devblog.be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devblog.be.model.Comment;
import com.devblog.be.model.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByPostId(Long postId);
	List<Comment> findAllByPostOrderByCreatedAtAsc(Post post);
}
