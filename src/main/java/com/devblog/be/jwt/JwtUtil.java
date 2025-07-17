package com.devblog.be.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
	
	// JWT 데이터
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";
	private final long TOKEN_TIME = 60 * 60 * 1000L;	// 60분
	
	@Value("${jwt.secret.key}")		// application.properties에 보관된 secretKey
	private String secretKey;
	
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	
	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}
	
	// 토큰 생성
	public String createToken(String username) {
		Date date = new Date();
		
		return BEARER_PREFIX +
				Jwts.builder()
					.setSubject(username)  // 사용자 식별 값
					.setExpiration(new Date(date.getTime() + TOKEN_TIME))  // 토큰 만료 시간
					.setIssuedAt(date)  // 발급일
					.signWith(key, signatureAlgorithm)
					.compact();
	}
	
	// JWT 가져오기
	public String getTokenFromRequest(HttpServletRequest req) {
		String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}
	
	// 토큰 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			log.warn("Invalid JWT token: {}", e.getMessage());
		}
		return false;
	}
	
	public Claims getUserInfoFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}
}
