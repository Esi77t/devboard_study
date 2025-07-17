package com.devblog.be.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devblog.be.dto.PostRequestDTO;
import com.devblog.be.dto.PostResponseDTO;
import com.devblog.be.dto.PostSearchCond;
import com.devblog.be.model.Attachment;
import com.devblog.be.model.Category;
import com.devblog.be.model.Post;
import com.devblog.be.model.User;
import com.devblog.be.repository.AttachmentRepository;
import com.devblog.be.repository.CategoryRepository;
import com.devblog.be.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	
	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final AttachmentRepository attachmentRepository;
	
	// 게시글 생성
	public PostResponseDTO createPosts(PostRequestDTO requestDto, User user) {
		// 카테고리 조회
		Category category = categoryRepository.findById(requestDto.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
		
		// Post 객체 생성
		Post post = new Post(requestDto.getTitle(), requestDto.getContent(), user, category);
		postRepository.save(post);
		
		if(requestDto.getAttachmentIds() != null && !requestDto.getAttachmentIds().isEmpty()) {
			List<Attachment> attachments = attachmentRepository.findAllById(requestDto.getAttachmentIds());
			
			for(Attachment attachment : attachments) {
				attachment.setPost(post);
				attachment.setTemporary(false);
				attachmentRepository.save(attachment);
			}
		}
		
		return new PostResponseDTO(post);
	}
	
	// 게시글 전체 조회
	public Page<PostResponseDTO> searchPosts(Long categoryId, PostSearchCond condition, Pageable pageable) {
		return postRepository.search(categoryId, condition, pageable).map(PostResponseDTO::new);
	}
	
	// 게시글 한 건 조회
	@Transactional
	public PostResponseDTO getPost(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
		
		post.increaseViews();
		
		return new PostResponseDTO(post);
	}
	
	// 게시글 수정
	@Transactional
	public PostResponseDTO updatePost(Long id, PostRequestDTO requestDto, User user) {
		Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
		
		if(!post.getUser().getUsername().equals(user.getUsername())) {
			throw new IllegalArgumentException("게시글을 수정할 권리가 없습니다.");
		}
		
		post.setTitle(requestDto.getTitle());
		post.setContent(requestDto.getContent());
		
		return new PostResponseDTO(post);
	}
	
	// 게시글 삭제
	public void deletePost(Long id, User user) {
		Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
		
		if(!post.getUser().getUsername().equals(user.getUsername())) {
			throw new IllegalArgumentException("게시글을 삭제할 권리가 없습니다.");
		}
		
		postRepository.delete(post);
	}
}
