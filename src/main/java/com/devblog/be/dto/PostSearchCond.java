package com.devblog.be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchCond {
	private String searchType;
	private String keyword;
}
