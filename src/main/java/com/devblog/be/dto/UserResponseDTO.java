package com.devblog.be.dto;

import com.devblog.be.model.User;
import com.devblog.be.model.UserRoleEnum;

import lombok.Getter;

@Getter
public class UserResponseDTO {
	private Long id;
	private String username;
	private String nickname;
	private String email;
	private UserRoleEnum role;
	
	public UserResponseDTO(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.nickname = user.getNickname();
		this.email = user.getEmail();
		this.role = user.getRole();
	}
}
