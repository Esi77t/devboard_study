package com.devblog.be.dto;

import lombok.Getter;

@Getter
public class UserProfileDTO {
	private String username;
	private String nickname;
	private String email;
	
	public UserProfileDTO(String username, String nickname, String email) {
		this.username = username;
		this.nickname = nickname;
		this.email = email;
	}
}
