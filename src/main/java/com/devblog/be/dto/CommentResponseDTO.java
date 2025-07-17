package com.devblog.be.dto;

import java.time.LocalDateTime;

import com.devblog.be.model.Comment;

import lombok.Getter;

@Getter
public class CommentResponseDTO {
	private Long id;
	private String content;
	private String author;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	
	public CommentResponseDTO(Comment comment) {
		this.id = comment.getId();
		this.content = comment.getContent();
		this.author = comment.getUser().getNickname();
		this.createdAt = comment.getCreatedAt();
		this.modifiedAt = comment.getModifiedAt();
	}
}
