package com.appengine.planit.domain;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class Comment {
	
	@Id
	private String commentId;
	
	private String userId;
	
	private String eventId;
	
	private String text;
	
	private Date createdAt;
	
	
	
}
