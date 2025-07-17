package com.devblog.be.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devblog.be.dto.CommentRequestDTO;
import com.devblog.be.dto.CommentResponseDTO;
import com.devblog.be.model.Comment;
import com.devblog.be.model.Post;
import com.devblog.be.model.User;
import com.devblog.be.repository.CommentRepository;
import com.devblog.be.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	
	// 댓글 생성
	public CommentResponseDTO createComment(Long postId, CommentRequestDTO requestDto, User user) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
		
		Comment comment = new Comment(requestDto.getContent(), user, post);
		commentRepository.save(comment);
		
		return new CommentResponseDTO(comment);
	}
	
	// 댓글 삭제
	@Transactional
	public void deleteComment(Long commentId, User user) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 댓글을 찾을 수 없습니다."));
		
		if(!comment.getUser().getUsername().equals(user.getUsername())) {
			throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
		}
		
		commentRepository.delete(comment);
	}
	
	// 댓글 목록 조회
	public List<CommentResponseDTO> getComments(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
		
		return commentRepository.findAllByPostOrderByCreatedAtAsc(post)
				.stream()
				.map(CommentResponseDTO::new)
				.toList();
	}
	
	// 댓글 수정
	@Transactional
	public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO requestDto,User user) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 댓글을 찾을 수 없습니다."));
		
		if(!comment.getUser().getUsername().equals(user.getUsername())) {
			throw new IllegalArgumentException("댓글을 수정할 권리가 없습니다.");
		}
		
		comment.setContent(requestDto.getContent());
		
		return new CommentResponseDTO(comment);
	}
}
