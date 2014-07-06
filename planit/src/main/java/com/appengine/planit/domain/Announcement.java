package com.appengine.planit.domain;

/**
 * A simple wrapper for an announcement message
 * 
 * Note that we don't annotate this as an "@Entity".
 * We only want to be using announcements with Memcache;
 * we don't want to be saving them to the Datastore
 * 
 *
 */
public class Announcement {

	private String message;
	
	public Announcement() {
		
	}
	
	public Announcement(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
}
