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
    
    private String organizerUserId;
    
    private int attendees;
    
    private int maxAttendees;
    
    private String address1;
    
    private String address2;
    
    private String city;
    
    private String state;
    
    private String zip;

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
     * @param address1
     * @param address2
     * @param city
     * @param state
     * @param zip
     */
    public EventForm(String title, Date startDate, Date endDate, 
    	String description, List<String> categories, String organizerUserId,
    	int attendees, int maxAttendees, String address1, String address2, String city, String state, String zip) {

    	this.title = title;
    	this.startDate = startDate == null ? null : new Date(startDate.getTime());
    	this.endDate = endDate == null ? null : new Date(endDate.getTime());
    	this.description = description;
    	this.categories = categories == null ? null : categories;
    	this.organizerUserId = organizerUserId;
    	this.attendees = attendees;
    	this.maxAttendees = maxAttendees;
    	this.address1 = address1;
    	this.address2 = address2;
    	this.city = city;
    	this.state = state;
    	this.zip = zip;
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

	
	public String getOrganizerUserId() {
		return organizerUserId;
	}
	
	

	public int getAttendees() {
		return attendees;
	}

	public int getMaxAttendees() {
		return maxAttendees;
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

	public String getZip() {
		return zip;
	}
    
  
    
}


