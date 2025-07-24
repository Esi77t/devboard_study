package com.devblog.be.repository;

import com.devblog.be.dto.PostSearchCond;
import com.devblog.be.model.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.devblog.be.model.QPost.post;
import static com.devblog.be.model.QComment.comment;

public class PostRepositoryImpl implements PostRepositoryCustom {
	
	private final JPAQueryFactory queryFactory;
	
	public PostRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}
	
	@Override
	public Page<Post> search(Long categoryId, PostSearchCond condition, Pageable pageable) {
		List<Post> content = queryFactory
				.selectFrom(post)
				.leftJoin(post.comments, comment)
				.distinct()
				.where(
					categoryIdEq(categoryId),
					searchTypeEq(condition.getSearchType(),
					condition.getKeyword())
				)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.orderBy(post.id.desc())
				.fetch();
		
		long total = queryFactory
				.select(post.countDistinct())
				.from(post)
				.leftJoin(post.comments, comment)
				.where(
						categoryIdEq(categoryId),
						searchTypeEq(condition.getSearchType(), 
						condition.getKeyword())
				)
				.fetchOne();
		
		return new PageImpl<>(content, pageable, total);
	}
	
	private BooleanExpression searchTypeEq(String searchType, String keyword) {
		if(!StringUtils.hasText(keyword)) {
			return null;
		}
		
		if("title".equals(searchType)) {
			return post.title.contains(keyword);
		} else if("content".equals(searchType)) {
			return post.content.contains(keyword);
		} else if("title_content".equals(searchType)) {
			return post.title.contains(keyword).or(post.content.contains(keyword));
		} else if("author".equals(searchType)) {
			return post.user.nickname.contains(keyword);
		} else if("comment".equals(searchType)) {
			return comment.content.contains(keyword);
		}
		
		return null;
	}
	
	private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? post.category.id.eq(categoryId) : null;
    }
}
