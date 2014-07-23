package com.appengine.planit.domain;

import java.util.Date;

import com.appengine.planit.form.CommentForm;
import com.google.appengine.repackaged.com.google.api.client.util.Preconditions;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class Comment {
	
	
	@Id
	private Long commentId;
	
	private String userId;
	
	private long eventId;
	
	private String text;
	
	private Date createdDate;
	
	public Comment() {
		
	}
	
	public Comment(final long commentId, final String userId, final long eventId, final CommentForm commentForm) {
		Preconditions.checkNotNull(commentForm.getText(), "Some text is required in your comment!");
		this.commentId = commentId;
		this.eventId = eventId;
		updateWithCommentForm(commentForm);
	}
	
	
	////////////////////////// GETTERS //////////////////////////
	
    public Long getCommentId() {
		return commentId;
	}

	public String getUserId() {
		return userId;
	}

	public String getEventId() {
		return eventId;
	}

	public String getText() {
		return text;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	
	/////////////////////// END GETTERS ////////////////////////
	
	
	
	/**
     * Updates the Comment with CommentForm.
     * This method is used upon object creation as well as updating existing Comments.
     *
     * @param commentForm contains form data sent from the client.
     */
	public void updateWithCommentForm(CommentForm commentForm) {
		
		this.text = commentForm.getText();
		Date createdDate = commentForm.getCreatedDate();
		this.createdDate = createdDate == null ? null : new Date(createdDate.getTime());
		
	}
	
	
	
}
