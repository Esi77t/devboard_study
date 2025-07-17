package com.devblog.be.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devblog.be.dto.CommentRequestDTO;
import com.devblog.be.dto.CommentResponseDTO;
import com.devblog.be.security.UserDetailsImpl;
import com.devblog.be.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
	
	private final CommentService commentService;
	
	@PostMapping("/posts/{postId}/comments")
	public ResponseEntity<CommentResponseDTO> createComment(@PathVariable("postId") Long postId, @RequestBody CommentRequestDTO requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		CommentResponseDTO responseDto = commentService.createComment(postId, requestDto, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
	
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		commentService.deleteComment(commentId, userDetails.getUser());
		
		return ResponseEntity.ok("댓글이 성공적으로 삭제됐습니다.");
	}
	
	@GetMapping("/posts/{postId}/comments")
	public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable("postId") Long postId) {
		List<CommentResponseDTO> responseDto = commentService.getComments(postId);
		
		return ResponseEntity.ok(responseDto);
	}
	
	@PutMapping("/comments/{commentId}")
	public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable("commentId") Long commentId, @RequestBody CommentRequestDTO requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		CommentResponseDTO responseDto = commentService.updateComment(commentId, requestDto, userDetails.getUser());
		
		return ResponseEntity.ok(responseDto);
	}
}
