package com.devblog.be.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.devblog.be.model.Post;

import lombok.Getter;

@Getter
public class PostResponseDTO {
	
	private Long id;
	private String title;
	private String content;
	private String author;
	private Integer views;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private String categoryName;
	
	private List<AttachmentResponseDTO> attachments;
	
	public PostResponseDTO(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.author = post.getUser().getNickname();
		this.createdAt = post.getCreatedAt();
		this.modifiedAt = post.getModifiedAt();
		this.views = post.getViews();
		this.categoryName = post.getCategory() != null ? post.getCategory().getName() : null;
		if (post.getAttachments() != null) {
            this.attachments = post.getAttachments().stream()
                    .map(AttachmentResponseDTO::new)
                    .collect(Collectors.toList());
        }
	}
}
