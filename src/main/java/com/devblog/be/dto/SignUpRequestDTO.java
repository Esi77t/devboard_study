package com.devblog.be.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequestDTO {
	
	@NotBlank(message="아이디는 필수 입력입니다.")
	@Size(min=4, max=20, message="아이디는 4자 이상 20자 이하로 입력해주세요.")
	private String username;
	
	@NotBlank(message="비밀번호는 필수 입력입니다.")
	@Size(min=6, max=20, message="비밀번호는 6자 이상 20자 이하로 입력해주세요.")
	private String password;
	
	@Email(message="이메일 형식이 올바르지 않습니다.")
	@NotBlank(message="이메일은 필수 입력입니다.")
	private String email;
	
	@NotBlank(message="닉네임은 필수 입력입니다.")
	private String nickname;
}
