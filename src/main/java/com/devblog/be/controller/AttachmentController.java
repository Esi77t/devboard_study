package com.devblog.be.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devblog.be.dto.AttachmentResponseDTO;
import com.devblog.be.model.Attachment;
import com.devblog.be.service.AttachmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttachmentController {
	
	private final AttachmentService attachmentService;
	
	@PostMapping("/attachments/temp")
	public ResponseEntity<AttachmentResponseDTO> uploadTempAttachment(@RequestParam("file") MultipartFile file) {
		try {
			Attachment savedAttachment = attachmentService.uploadTempAttachment(file);
			return ResponseEntity.ok(new AttachmentResponseDTO(savedAttachment));
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
//	@PostMapping("/posts/{postId}/attachments")
//	public ResponseEntity<String> uploadAttachment(@PathVariable("postId") Long postId, @RequestParam("file") MultipartFile file) {
//		try {
//			String fileUrl = attachmentService.uploadAttachment(postId, file);
//			return ResponseEntity.ok(fileUrl);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드에 실패했습니다.");
//		}
//	}
}
