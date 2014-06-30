package com.appengine.planit.domain;

import java.awt.List;
import java.util.Calendar;
import java.util.Date;

import com.appengine.planit.form.EventForm;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.search.checkers.Preconditions;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.condition.IfNotDefault;

/**
 * Event class stores event information
 * @Parent the user who created the event, represented by their userId
 *
 */
@Entity
public class Event {

	public static final String DEFAULT_CITY = "default city";
	
	public static final List<String> DEFAULT_CATEGORIES = ImmutableList.of("Default", "Category");
	
    /**
     * The id for the datastore key.
     *
     * We use automatic id assignment for entities of Event class.
     */
	@Id
	private Long id;
	
    /**
     * The title of the event.
     */
	@Index
	private String title;

	/**
	 * Start date of the event
	 */
	private Date startDate;

	/**
	 * End date of the event
	 */
	private Date endDate;
	
	/**
	 * The description of the event
	 */
	private String description;
	
	/**
	 * Holds profile key as the parent
	 */
	@Parent
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	private Key<Profile> profileKey;
	
	
	/**
	 * The userId of the organizer
	 */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	private String organizerUserId;
	
	
	/**
	 * Categories related to this event (can be one or multiple)
	 */
	private List<String> categories;
	
	
	/**
	 * Month in which the event is being held
	 */
	@Index
	private int month;

	
	/**
	 * Maximum capacity of the event
	 */
	@Index
	private int maxAttendees;
	
	/**
	 * Current number of attendees
	 */
	private int attendees;
	
	
	/**
	 * Current number of registrations avaialble, base upon the maxAttendees and current attendees
	 */
	@Index
	private int registrationsAvailable;

	/**
	 * Street address line 1
	 */
	private String address1;

	/**
	 * Street address line 2
	 */
	private String address2;

	
	/**
	 * The name of the city where the event takes place
	 */
	@Index(IfNotDefault.class) private String city;

	
	/**
	 * The name of the state where the event takes place
	 */
	@Index
	private String state;

	
	/**
	 * The name of the zip code where the event takes place
	 */
	@Index
	private String zipCode;

	
	//private Photo mainPhoto;

	//private List<Photo> photos;

	
	private Event() {
		
	}
	
	public Event(final long id, final String organizerUserId,
		final EventForm eventForm) { 

		Preconditions.checkNotNull(eventForm.getTitle(), "The title is required");
		this.id = id;
		this.profileKey = Key.create(Profile.class, organizerUserId);
		this.organizerUserId = organizerUserId;
		updateWithEventForm(Event);
		
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Returns a defensive copy of start date if not null
	 * @return a defensive copy of start date if not null
	 */
	public Date getStartDate() {
		return startDate == null ? null : new Date(startDate.getTime());
	}
	
	/**
	 * Returns a defensive copy of end date if not null
	 * @return a defensive copy of end date if not null
	 */
	public Date getEndDate() {
		return endDate == null ? null : new Date(endDate.getTime());
	}

	public String getDescription() {
		return description;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Key<Profile> getProfileKey() {
		return profileKey;
	}

	
	/**
	 * Get a String verison of the key
	 * @return String websafeKey
	 */
	public String getWebSafeKey() {
		return Key.create(profileKey, Event.class, id).getString();
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getOrganizerUserId() {
		return organizerUserId;
	}
	
	/**
	 * Get the organizer's display name
	 * 
	 * @return the organizer's display name. If there is no Profile, return his/her userId
	 */
	public String getOrganizerDisplayName() {
		//Profile organizer = ofy().load().key(Key.create(Profile.class, organizerUserId)).now();
		Profile organizer = ofy().load().key(Key.create(getProfileKey()).now();
		if (organizer == null) {
			return organizer.getUserId();
		} else {
			return organizer.getDisplayName();
		}
	}

    /**
     * Returns a defensive copy of topics if not null.
     * @return a defensive copy of topics if not null.
     */
	public List<String> getCategories() {
		return categories == null ? null : ImmutableList.copyOf(categories);
	}

	
	public int getMonth() {
		return month;
	}

	public int getMaxAttendees() {
		return maxAttendees;
	}

	public int getAttendees() {
		return attendees;
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
	
	
    /**
     * Updates the Event with EventForm.
     * This method is used upon object creation as well as updating existing Event.
     *
     * @param eventForm contains form data sent from the client.
     */
	public void updateWithEventForm(EventForm eventForm) {
		this.title = eventForm.getTitle();
		this.description = eventForm.getDescription();
		List<String> categories = eventForm.getCategories();
		this.categories = categories == null || categories.isEmpty() ? DEFAULT_CATEGORIES : categories;
		
		Date startDate = eventForm.getStartDate();
		this.startDate = startDate == null ? null : new Date(startDate.getTime());
		Date endDate = eventForm.getEndDate();
		this.endDate = endDate == null ? null : new Date(endDate.getTime());
		
		if (this.startDate != null) {
			// Gettting the starting month for a composite query
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.startDate);
			//Calendar.MONTH is zero-based, so adding one
			this.month = calendar.get(calendar.MONTH) + 1;
		}
		// Check maxAttendees value against the number of already allocated seats.
		attendees = maxAttendees - registrationsAvailable;
		if (eventForm.getMaxAttendees() < attendees) {
			throw new IllegalArgumentException(attendees + "attendees spots are already taken," +
					"but you tried to set maxAttendees to " + eventForm.getMaxAttendees());
		}
        // The initial number of seatsAvailable is the same as maxAttendees.
        // However, if there are already some seats allocated, we should subtract that numbers.
		this.maxAttendees = eventForm.getMaxAttendees();
		this.registrationsAvailable = this.maxAttendees - attendees;
		
		this.address1 = eventForm.getAddress1() == null : null ? eventForm.getAddress1();
		this.address2 = eventForm.getAddress2() == null : null ? eventForm.getAddress2();
		this.city = eventForm.getCity() == null ? DEFAULT_CITY : eventForm.getCity();
		this.state = eventForm.getState() == null : null ? eventForm.getState();
		this.zipCode = eventForm.getZipCode() == null ? null : eventForm.getZipCode();

	}
	
	public void confirmRegistration(final int number) {
		if (registrationsAvailable < number) {
			throw new IllegalArgumentException("There are no regsitrations available");
		}
		this.registrationsAvailable -= number;

	}
	
	public void giveBackRegistrations(final int number) {
		if (registrationsAvailable + number > maxAttendees ) {
			throw new IllegalArgumentException("There is not enough capacity for that many registrations!");
		}
		registrationsAvailable += number;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Id: " + id + "\n")
			.append("Title: ").append(title).append("\n");
		if (topics != null && topics.size() > 0) {
			stringBuilder.append("Topics:\n ");
			for (String topic : topics) {
				stringBuilder.append("\t").append(topic).append("\n");
			}
		}
		if (startDate != null) {
			stringBuilder.append("Start Date: ").append(startDate.toString()).append("\n");
		}
		if (endDate != null) {
			stringBuilder.append("End Date: ").append(endDate.toString()).append("\n");
		}
		if (address1 != null) {
			stringBuilder.append("Address Line 1: ").append(address1).append("\n");
		}
		if (address2 != null) {
			stringBuilder.append("Address Line 2: ").append(address2).append("\n");
		}
		if (city != null) {
			stringBuilder.append("City: ").append(city).append("\n");
		}
		if (state != null) {
			stringBuilder.append("State: ").append(state).append("\n");
		}
		if (zipCode != null) {
			stringBuilder.append("Zip Code: ").append(zipCode).append("\n");
		}
		stringBuilder.append("Max Attendees: ").append(maxAttendees).append("\n");
		
		return stringBuilder.toString();
		
	}
	
}






