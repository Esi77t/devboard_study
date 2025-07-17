package com.devblog.be.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devblog.be.dto.LoginRequestDTO;
import com.devblog.be.dto.ProfileUpdateRequestDTO;
import com.devblog.be.dto.SignUpRequestDTO;
import com.devblog.be.model.Post;
import com.devblog.be.model.Transaction;
import com.devblog.be.model.User;
import com.devblog.be.repository.PostRepository;
import com.devblog.be.repository.TransactionRepository;
import com.devblog.be.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final TransactionRepository transactionRepository;
	private final PasswordEncoder passwordEncoder;
	
	// 회원가입
	public void signUp(SignUpRequestDTO requestDto) {
		
		// DTO에서 받은 데이터 가져오기
		String username = requestDto.getUsername();
		String nickname = requestDto.getNickname();
		String email = requestDto.getEmail();
		
		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
		
		// 아이디, 닉네임, 이메일 중복확인
		if(userRepository.findByUsername(username).isPresent()) {
			throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
		}
		if(userRepository.existsByNickname(nickname)) {
			throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
		}
		if(userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("이미 등록된 이메일입니다.");
		}
		
		// 객체 생성 후 저장
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setNickname(nickname);
		user.setPassword(encodedPassword);
		
		userRepository.save(user);
	}
	
	// 로그인
	public User login(LoginRequestDTO requestDto) {
		String username = requestDto.getUsername();
		String password = requestDto.getPassword();
		
		// 사용자 확인
		User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));
		
		// 비밀번호 확인
		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		
		// JWT 생성하고 반환
		return user;
	}
	
	// 프로필 수정
	@Transactional
	public void updateProfile(User user, ProfileUpdateRequestDTO requestDto) {
		if(userRepository.existsByNickname(requestDto.getNickname())) {
			throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
		}
		
		user.setNickname(requestDto.getNickname());
		userRepository.save(user);
	}
	
	// 중복체크
	public boolean isUsernameDuplicated(String username) {
		return userRepository.findByUsername(username).isPresent();
	}
	
	public boolean isNicknameDuplicated(String nickname) {
		return userRepository.existsByNickname(nickname);
	}
	
//	public boolean isEmailDuplicated(String email) {
//		return userRepository.existsByEmail(email);
//	}
	
	// 아이디 찾기
	public String findUsernameByEmail(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다."));
		
		return user.getUsername();
	}
	
	// 회원 탈퇴
	@Transactional
	public void deleteUser(User user) {
		List<Post> posts = postRepository.findAllByUser(user);
		postRepository.deleteAll(posts);
		List<Transaction> transactions = transactionRepository.findAllByUserOrderByDateDesc(user);
        transactionRepository.deleteAll(transactions);
		
		
		userRepository.delete(user);
	}
}
