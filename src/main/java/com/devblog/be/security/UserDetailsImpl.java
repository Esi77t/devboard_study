package com.devblog.be.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.devblog.be.model.User;

public class UserDetailsImpl implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	private final User user;
	
	public UserDetailsImpl(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}
	
	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}
	
	// 계정 만료 여부
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	// 계정 잠금 여부
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	// 계정 증명 만료 여부
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	// 계정 활성화 여부
	@Override
	public boolean isEnabled() {
		return true;
	}
}
