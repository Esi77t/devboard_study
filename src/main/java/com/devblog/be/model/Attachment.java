package com.devblog.be.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Attachment extends Timestamped {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private String originalFileName;
	
	@Column(nullable=false)
	private String s3Url;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="post_id", nullable=true)
	private Post post;
	
	@Column(nullable=false)
	private boolean isTemporary = true;
	
	public Attachment(String originalFileName, String s3Url) {
		this.originalFileName = originalFileName;
		this.s3Url = s3Url;
	}
}
