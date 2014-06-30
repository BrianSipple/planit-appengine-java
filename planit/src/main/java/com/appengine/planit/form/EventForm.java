package com.appengine.planit.form;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.users.User;

public class EventForm {

    private String title;

    private Date startDate;

    private Date endDate;
    
    private String description;
    
    private List<String> categories;
    
    private User organizer;
    
    private List<User> attendees;
    
    private int maxAttendees;
    
    private int registrationsAvailable;
    
    private String address1;
    
    private String address2;
    
    private String city;
    
    private String state;
    
    private String zipCode;

    private EventForm() {

    }

    /**
     * Public constructor is solely for Unit Test.
     * @param title
     * @param startDate
     * @param endDate
     * @param description
     * @param categories
     * @param organizer
     * @param attendees
     * @param maxAttendees
     * @param registrationsAvailable
     * @param address1
     * @param address2
     * @param city
     * @param state
     * @param zipCode
     */
    public EventForm(String title, Date startDate, Date endDate, 
    	String description, List<String> categories, User organizer,
    	List<User> attendess, int maxAttendess, int registrationsAvailable,
    	String address1, String address2, String city, String state, String zipCode) {

    	this.title = title;
    	this.startDate = startDate == null ? null : new Date(startDate.getTime());
    	this.endDate = endDate == null ? null : new Date(endDate.getTime());
    	this.description = description;
    	this.categories = categories == null ? null : categories;
    	this.organizer = organizer;
    	this.attendees = attendees == null ? null : attendees;
    	this.maxAttendees = maxAttendees;
    	this.registrationsAvailable = registrationsAvailable;
    	this.address1 = address1;
    	this.address2 = address2;
    	this.city = city;
    	this.state = state;
    	this.zipCode = zipCode;
    }
    
    ////////// GETTTERS and SETTERS //////////

	public String getTitle() {
		return title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getCategories() {
		return categories;
	}

	public User getOrganizer() {
		return organizer;
	}

	public List<User> getAttendees() {
		return attendees;
	}

	public int getMaxAttendees() {
		return maxAttendees;
	}

	public int getRegistrationsAvailable() {
		return registrationsAvailable;
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZipCode() {
		return zipCode;
	}
    
  
    
}


