package com.appengine.planit.domain;

import java.util.ArrayList;
import java.util.List;

import com.appengine.planit.form.ProfileForm.TeeShirtSize;
import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Profile {
	
	String displayName;
	String mainEmail;
	int age;
	TeeShirtSize teeShirtSize;
	
	@Id String userId;
	
	private List<String> eventsToAttendKeys = new ArrayList(0);
	
	
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
	
	/**
	 * Just make the default constructor private
	 */
	private Profile(){
		
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
	
	public List<String> getEventsToAttendKeys() {
		return ImmutableList.copyOf(this.eventsToAttendKeys);
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
	
	
	private void addToEventsToAttendKeys(String eventKey) {
		eventsToAttendKeys.add(eventKey);
	}
	
	
	/**
	 * Unregister a user from an event by removing the event's
	 * key from the list of the user's upcoming events
	 * 
	 * This must be a strongly consistent transaction where both the user is 
	 * unregistered from the event, and the event is updated to reflect an additional
	 * availability. 
	 */
	public void unregisterFromEvent(String eventKey) {
		
		if (!eventsToAttendKeys.contains(eventKey)) {
			throw new IllegalArgumentException("The user is not registered for this event");
		}
		eventsToAttendKeys.
		
		
	}
}
