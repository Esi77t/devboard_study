package com.devblog.be.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devblog.be.model.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
	List<Attachment> findAllByIsTemporaryTrueAndCreatedAtBefore(LocalDateTime dateTime);
}
