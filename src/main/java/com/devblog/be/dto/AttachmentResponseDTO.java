package com.devblog.be.dto;

import com.devblog.be.model.Attachment;

import lombok.Getter;

@Getter
public class AttachmentResponseDTO {
	private Long id;
	private String originalFileName;
	private String s3Url;
	
	public AttachmentResponseDTO(Attachment attachment) {
		this.id = attachment.getId();
		this.originalFileName = attachment.getOriginalFileName();
		this.s3Url = attachment.getS3Url();
	}
}
