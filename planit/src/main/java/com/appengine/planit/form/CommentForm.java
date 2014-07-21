package com.appengine.planit.form;

import java.util.Date;

public class CommentForm {

	private String text;
	
	private Date createdDate;
	
	
	private CommentForm(){
		
	}
	
	public CommentForm(String text, Date createdDate) {
		this.text = text;
		this.createdDate = createdDate == null ? null : new Date(createdDate.getTime());
	}

	public String getText() {
		return text;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	
	
	
	
	
}
