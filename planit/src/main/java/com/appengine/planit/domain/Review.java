package com.appengine.planit.domain;

import java.util.Date;

import com.appengine.planit.form.ReviewForm;
import com.google.appengine.repackaged.com.google.api.client.util.Preconditions;
import com.googlecode.objectify.annotation.Id;

public class Review {
	
	
	@Id
	private Long reviewId;
	
	private String userId;
	
	private String eventId;
	
	private String text;
	
	private int stars;
	
	private Date createdDate;
	
	public Review() {
		
	}
	
	public Review(final long id, final String userId, final String eventId, final ReviewForm reviewForm) {
		Preconditions.checkNotNull(reviewForm.getText(), "Some text is required in your review");
		Preconditions.checkNotNull(reviewForm.getStars(), "You need to leave a rating");
		this.reviewId = id;
		this.eventId = eventId;
		this.userId = userId;
		updateWithReviewForm(reviewForm);
	}
	
	
    /**
     * Updates the Comment with CommentForm.
     * This method is used upon object creation as well as updating existing Comments.
     *
     * @param commentForm contains form data sent from the client.
     */
	public void updateWithReviewForm(ReviewForm reviewForm) {
		
		this.text = reviewForm.getText();
		Date createdDate = reviewForm.getCreatedDate();
		this.createdDate = createdDate == null ? null : createdDate;
		this.stars = reviewForm.getStars();
		
	}
}