package com.appengine.planit.domain;

import java.util.ArrayList;
import java.util.List;

import com.appengine.planit.form.ProfileForm.PizzaTopping;
import com.appengine.planit.form.ProfileForm.TeeShirtSize;
import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class Profile {
	
	private String displayName;
	private String mainEmail;
	private int age;
	private TeeShirtSize teeShirtSize;
	private PizzaTopping pizzaTopping;
	
	@Id String userId;
	
	private List<String> eventsToAttendKeys = new ArrayList<String>();
	
	private List<String> commentsCreatedKeys = new ArrayList<String>();
	
	private List<String> reviewsCreatedKeys = new ArrayList<String>();
	
    /**
     * Public constructor for Profile.
     * @param userId The user id, obtained from the email
     * @param displayName Any string user wants us to display him/her on this system.
     * @param mainEmail User's main e-mail address.
     * @param teeShirtSize The User's tee shirt size
     * 
     */
	public Profile(String userId, String displayName, String mainEmail, int age, 
					TeeShirtSize teeShirtSize, PizzaTopping pizzaTopping) {
		this.userId = userId;
		this.displayName = displayName;
		this.mainEmail = mainEmail;
		this.age = age;
		this.teeShirtSize = teeShirtSize;
		this.pizzaTopping = pizzaTopping;
	}
	
	public void update(String displayName, int age, TeeShirtSize teeShirtSize, PizzaTopping pizzaTopping) {
		if (displayName != null) {
			this.displayName = displayName;
		}
		
		
		if (age != 0) {
			this.age = age;
		}
		
		if (teeShirtSize != null) {
			this.teeShirtSize = teeShirtSize;
		}
		
		if (pizzaTopping != null) {
			this.pizzaTopping = pizzaTopping;
		}
	}

	
	public String getDisplayName() {
		return displayName;
	}


	public String getMainEmail() {
		return mainEmail;
	}


	public int getAge() {
		return age;
	}


	public TeeShirtSize getTeeShirtSize() {
		return teeShirtSize;
	}


	public String getUserId() {
		return userId;
	}
	
	public PizzaTopping getPizzaTopping() {
		return pizzaTopping;
	}
	
	public List<String> getEventsToAttendKeys() {
		return ImmutableList.copyOf(this.eventsToAttendKeys);
	}
	
	/**
	 * Just make the default constructor private
	 */
	private Profile(){
		
	}
	
	
	
	public void addToEventsToAttendKeys(String eventKey) {
		eventsToAttendKeys.add(eventKey);
	}
	
	
	/**
	 * Strongly consistent transaction that both unregisters the 
	 * user from an event, and increments the number of the event's
	 * openings by one
	 */
	public void unregisterFromEvent(String eventKey) {
		
		if (eventsToAttendKeys.contains(eventKey)) {
			eventsToAttendKeys.remove(eventKey);
		} else {
			throw new IllegalArgumentException("Invalid event key: " + eventKey);
		}
	}

	public void addToCommentsCreatedKeys(String commentKey) {
		this.commentsCreatedKeys.add(commentKey);
		
	}
	
	

	
	
	
}
