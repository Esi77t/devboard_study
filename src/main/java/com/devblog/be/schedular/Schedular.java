package com.devblog.be.schedular;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.devblog.be.model.Attachment;
import com.devblog.be.repository.AttachmentRepository;
import com.devblog.be.s3.S3Uploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Schedular {
	
	private final AttachmentRepository attachmentRepository;
	private final S3Uploader s3Uploader;
	
	@Scheduled(cron="0 0 4 * * *")
	@Transactional
	public void deleteOrphanAttachments() {
		log.info("오래된 첨부파일 삭제 스케줄러 시작");
		
		LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
		List<Attachment> oldTempFiles = attachmentRepository.findAllByIsTemporaryTrueAndCreatedAtBefore(oneDayAgo);
		
		if(oldTempFiles.isEmpty()) {
			log.info("삭제할 파일이 없습니다.");
			return;
		}
		
		for(Attachment attachment : oldTempFiles) {
			s3Uploader.deleteFile(attachment.getS3Url());
		}
		
		attachmentRepository.deleteAll(oldTempFiles);
		log.info("{}개의 오래된 임시 파일을 삭제했습니다.", oldTempFiles.size());
	}
	
}
