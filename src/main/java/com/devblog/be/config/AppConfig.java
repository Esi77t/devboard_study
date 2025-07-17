package com.devblog.be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class AppConfig {
	@Bean
	public OpenAPI openApi() {
		Info info = new Info()
				.title("DevTransaction API Documentation")
				.version("1.0.0")
				.description("개인 작업용으로 만든 API 명세서입니다");
		
		return new OpenAPI()
				.components(new Components())
				.info(info);
	}
}
