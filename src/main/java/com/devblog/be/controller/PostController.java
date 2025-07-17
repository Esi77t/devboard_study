package com.devblog.be.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devblog.be.dto.PostRequestDTO;
import com.devblog.be.dto.PostResponseDTO;
import com.devblog.be.dto.PostSearchCond;
import com.devblog.be.s3.S3Uploader;
import com.devblog.be.security.UserDetailsImpl;
import com.devblog.be.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
	
	private final PostService postService;
	private final S3Uploader s3Uploader;
	
	@PostMapping("/posts")
	public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		PostResponseDTO responseDto = postService.createPosts(requestDto, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
	
	@GetMapping("/posts")
	public ResponseEntity<Page<PostResponseDTO>> getPosts(
			@RequestParam(value="categoryId", required=false) Long categoryId,
			@RequestParam(value="searchType", required=false) String searchType,
			@RequestParam(value="keyword", required=false) String keyword,
			@PageableDefault(size=10, sort="id", direction=Sort.Direction.DESC) Pageable pageable) {
		PostSearchCond condition = new PostSearchCond();
        condition.setSearchType(searchType);
        condition.setKeyword(keyword);

        Page<PostResponseDTO> responseDto = postService.searchPosts(categoryId, condition, pageable);
        return ResponseEntity.ok(responseDto);
	}
	
	@GetMapping("/posts/{id}")
	public ResponseEntity<PostResponseDTO> getPost(@PathVariable("id") Long id) {
		PostResponseDTO responseDto = postService.getPost(id);
		return ResponseEntity.ok(responseDto);
	}
	
	@PutMapping("/posts/{id}")
	public ResponseEntity<PostResponseDTO> updatePost(@PathVariable("id") Long id, @RequestBody PostRequestDTO requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostResponseDTO responseDto = postService.updatePost(id, requestDto, userDetails.getUser());
        return ResponseEntity.ok(responseDto);
    }
	
	@DeleteMapping("/posts/{id}")
	public ResponseEntity<String> deletePost(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		postService.deletePost(id, userDetails.getUser());
		return ResponseEntity.ok("게시글을 성공적으로 삭제했습니다.");
	}
	
	@PostMapping("/posts/image")
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		try {
			if(userDetails == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}
			String imageUrl = s3Uploader.upload(image, "images");	// "images"라는 디렉토리에 저장
			return ResponseEntity.ok(imageUrl);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드에 실패했습니다");
		}
	}
}
