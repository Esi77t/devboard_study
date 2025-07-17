package com.devblog.be.s3;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
	
	private final S3Client s3Client;
	
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	
	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
		
		// 파일 이름 중복되지 않게 UUID를 추가
		String fileName = dirName + "/" + UUID.randomUUID() + "-" + multipartFile .getOriginalFilename();
		
		// S3에 업로드할 요청 객체를 생성
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.contentType(multipartFile.getContentType())
				.contentLength(multipartFile.getSize())
				.build();
		
		// 파일을 s3에 업로드
		s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
		
		// 업로드된 파일의 URL을 반환
		return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(fileName)).toExternalForm();
	}
	
	public void deleteFile(String fileUrl) {
		try {
            // URL에서 S3 객체 키(파일 경로)를 추출
            String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
            String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(decodedKey)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 파일 삭제 성공: " + decodedKey);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: " + fileUrl, e);
        }
	}
}
