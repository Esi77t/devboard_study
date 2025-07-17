package com.devblog.be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devblog.be.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	boolean existsByEmail(String email);
	boolean existsByNickname(String nickname);
	Optional<User> findByEmail(String email);
}
