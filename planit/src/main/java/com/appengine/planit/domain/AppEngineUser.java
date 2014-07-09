package com.appengine.planit.domain;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Wraps a User object along with an email address
 * 
 * This allows us to develop a functionality where we
 * receive a User without an "id" property through an API call
 * (e.g. a call from an Android app), and still retrieve the user ID from the Datastore
 * by...
 * 
 * 1) Having the API initialize the instance with the current session's User object 
 * 
 * 2) saving the AppEngineUser to the Datastore
 * 
 * 3) loading the AppEngineUser object from the Datastore , getting its User object, and then getting
 * 	  the User object's UserId
 *
 */
@Entity
public class AppEngineUser {
	
	@Id
	private String email;
	private User user;
	
	private AppEngineUser(){}
	
	public AppEngineUser(User user) {
		this.user = user;
		this.email = user.getEmail();
	}
	
	public User getUser() {
		return this.user;
	}
	
	public Key<AppEngineUser> getKey() {
		return Key.create(AppEngineUser.class, email);
	}
	
	
}