package com.appengine.planit.form;

import java.util.Date;

public class ReviewForm {

	private String text;
	
	private Date createdDate;
	
	private String userId;
	
	private int stars;
	
	
	private ReviewForm(){
		
	}
	
	public ReviewForm(String text, Date createdDate, int stars) {
		this.text = text;
		this.createdDate = createdDate == null ? null : new Date(createdDate.getTime());
		this.stars = stars;
	}
	
	

	public String getText() {
		return text;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	
	public int getStars() {
		return stars;
	}
	
	
	
	
	
}
