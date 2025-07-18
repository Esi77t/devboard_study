package com.devblog.be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@TestPropertySource(properties = "spring.autoconfigure.exclude=io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration")
public class BeApplicationTests {
	
	@MockBean
	private S3Client s3Client;

	@Test
	void contextLoads() {
	}

}
