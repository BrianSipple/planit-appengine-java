package com.appengine.planit.domain;

import com.appengine.planit.form.ProfileForm.TeeShirtSize;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Profile {
	
	String displayName;
	String mainEmail;
	int age;
	TeeShirtSize teeShirtSize;
	
	@Id String userId;
	
	
    /**
     * Public constructor for Profile.
     * @param userId The user id, obtained from the email
     * @param displayName Any string user wants us to display him/her on this system.
     * @param mainEmail User's main e-mail address.
     * @param teeShirtSize The User's tee shirt size
     * 
     */
	public Profile(String userId, String displayName, String mainEmail, int age, TeeShirtSize teeShirtSize) {
		this.userId = userId;
		this.displayName = displayName;
		this.mainEmail = mainEmail;
		this.age = age;
		this.teeShirtSize = teeShirtSize;
	}
	
	public void update(String displayName, int age, TeeShirtSize teeShirtSize) {
		if (displayName != null) {
			this.displayName = displayName;
		}
		
		
		if (age != 0) {
			this.age = age;
		}
		
		if (teeShirtSize != null) {
			this.teeShirtSize = teeShirtSize;
		}
	}

	
	public String getDisplayName() {
		return this.displayName;
	}


	public String getMainEmail() {
		return this.mainEmail;
	}


	public int getAge() {
		return this.age;
	}


	public TeeShirtSize getTeeShirtSize() {
		return this.teeShirtSize;
	}


	public String getUserId() {
		return this.userId;
	}
	
	/**
	 * Just make the default constructor private
	 */
	private Profile(){
		
	}
}
