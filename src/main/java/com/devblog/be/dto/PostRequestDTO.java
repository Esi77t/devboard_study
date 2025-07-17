package com.devblog.be.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PostRequestDTO {
	
	private String title;
	private String content;
	private Long categoryId;
	private List<Long> attachmentIds;
}
