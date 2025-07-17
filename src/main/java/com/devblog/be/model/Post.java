package com.devblog.be.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class Post extends Timestamped {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private String title;
	
	@Column(nullable=false, columnDefinition="TEXT")
	private String content;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="category_id")
	private Category category;
	
	@Column
	private Integer views = 0;
	
	@OneToMany(mappedBy="post", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Comment> comments = new ArrayList<>();
	
	@OneToMany(mappedBy="post", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Attachment> attachments = new ArrayList<>();
	
	public Post(String title, String content, User user, Category category) {
		this.title = title;
		this.content = content;
		this.user = user;
		this.category = category;
	}
	
	public void increaseViews() {
		if (this.views == null) {
	        this.views = 0;
	    }
		this.views++;
	}
}
