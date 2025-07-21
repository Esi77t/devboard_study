package com.devblog.be.jwt;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devblog.be.security.UserDetailsService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	
	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailService;
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String[] excluedPath = {"/"};
		String path = request.getRequestURI();
		return Arrays.stream(excluedPath).anyMatch(path::equals);
	}
	
	// JWT 인증/인가
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String tokenValue = jwtUtil.getTokenFromRequest(request);
		log.info("Token extracted: [{}]", tokenValue);
		
		if(StringUtils.hasText(tokenValue)) {
			if(jwtUtil.validateToken(tokenValue)) {
				Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
				
				String username = info.getSubject();
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				UserDetails userDetails = userDetailService.loadUserByUsername(username);
				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				context.setAuthentication(authentication);
				SecurityContextHolder.setContext(context);
			}
		}
		
		filterChain.doFilter(request, response);
	}
}
