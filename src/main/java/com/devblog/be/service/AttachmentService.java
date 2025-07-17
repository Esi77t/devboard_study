package com.devblog.be.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.devblog.be.model.Attachment;
import com.devblog.be.repository.AttachmentRepository;
import com.devblog.be.s3.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {
	
	private final AttachmentRepository attachmentRepository;
	private final S3Uploader s3Uploader;
	
	public Attachment uploadTempAttachment(MultipartFile file) throws IOException {
		String fileUrl = s3Uploader.upload(file, "temp");
		
		Attachment attachment = new Attachment(file.getOriginalFilename(), fileUrl);
		
		return attachmentRepository.save(attachment);
	}
	
//	public String uploadAttachment(Long postId, MultipartFile file) throws IOException {
//		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
//		
//		String fileUrl = s3Uploader.upload(file, "attachments");
//		
//		Attachment attachment = new Attachment(file.getOriginalFilename(), fileUrl, post);
//		attachmentRepository.save(attachment);
//		
//		return fileUrl;
//	}
}
