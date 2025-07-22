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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devblog.be.dto.LoginRequestDTO;
import com.devblog.be.dto.ProfileUpdateRequestDTO;
import com.devblog.be.dto.SignUpRequestDTO;
import com.devblog.be.dto.UserProfileDTO;
import com.devblog.be.dto.UserResponseDTO;
import com.devblog.be.jwt.JwtUtil;
import com.devblog.be.model.User;
import com.devblog.be.security.UserDetailsImpl;
import com.devblog.be.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {
	
	private final UserService userService;
	private final JwtUtil jwtUtil;
	
	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequestDTO requestDTO) {
		
		userService.signUp(requestDTO);
		
		return ResponseEntity.ok("회원가입이 완료 됐습니다.");
	}
	
	@PostMapping("/login")
	public ResponseEntity<UserProfileDTO> login(@RequestBody LoginRequestDTO requestDTO) {
		User user = userService.login(requestDTO);
		
		String token = jwtUtil.createToken(user.getUsername());
		
		UserProfileDTO userProfileDto = new UserProfileDTO(user.getUsername(), user.getNickname(), user.getEmail());
	    
		return ResponseEntity.ok()
				.header(JwtUtil.AUTHORIZATION_HEADER, token)
				.body(userProfileDto);
	}
	
	@GetMapping("/user/profile")
	public ResponseEntity<UserProfileDTO> getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
		
		String username = userDetails.getUser().getUsername();
		String nickname = userDetails.getUser().getNickname();
		String email = userDetails.getUser().getEmail();
		
		UserProfileDTO responseDto = new UserProfileDTO(username, nickname, email);
		
		return ResponseEntity.ok(responseDto);
	}
	
	@PutMapping("/user/profile")
	public ResponseEntity<String> updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ProfileUpdateRequestDTO requestDto) {
		userService.updateProfile(userDetails.getUser(), requestDto);
		
		return ResponseEntity.ok("프로필이 성공적으로 수정됐습니다.");
	}
	
	@GetMapping("/check-username/{username}")
	public ResponseEntity<Boolean> checkUsername(@PathVariable("username") String username) {
		boolean isDuplicated = userService.isUsernameDuplicated(username);
		return ResponseEntity.ok(isDuplicated);
	}
	
	@GetMapping("/check-nickname/{nickname}")
	public ResponseEntity<Boolean> checkNickname(@PathVariable("nickname") String nickname) {
		boolean isDuplicated = userService.isNicknameDuplicated(nickname);
		return ResponseEntity.ok(isDuplicated);
	}
	
//	@GetMapping("/check-email/{email}")
//	public ResponseEntity<Boolean> checkEmail(@PathVariable("email") String email) {
//		boolean isDuplicated = userService.isEmailDuplicated(email);
//		return ResponseEntity.ok(isDuplicated);
//	}
	
	@GetMapping("/find-username")
	public ResponseEntity<String> findUsername(@RequestParam("email") String email) {
		String username = userService.findUsernameByEmail(email);
		
		return ResponseEntity.ok("회원님의 아이디는 " + username + " 입니다.");
	}
	
	@DeleteMapping("/user")
	public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		userService.deleteUser(userDetails.getUser());
		
		return ResponseEntity.ok("회원 탈퇴가 성공적으로 진행되었습니다.");
	}
	
	@GetMapping("/admin/users")
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		List<UserResponseDTO> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}
}
