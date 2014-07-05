package com.appengine.planit.domain;

import static com.appengine.planit.service.OfyService.ofy;
import static org.junit.Assert.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.appengine.planit.form.EventForm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventTest {
	
	private static final Long ID = 123456L;
	
	private static final String TITLE = "Google Cloud Platform - Project Butterscotch";
	
	private Date startDate;
	
	private Date endDate;
	
	private static final String DESCRIPTION = "Hands-on with Google Cloud Platform's Super-secret Project Butterscotch";
	
	private Key profileKey;
	
	private static final String ORGANIZER_USER_ID = "123456789";
	
	private List<String> categories;
	
	private static final int MONTH = 3;
	
	private static final int MAX_ATTENDEES = 3000;
	
	private int ATTENDEES = 100;
	
	private int registrationsAvailable;
	
	private String address1;
	
	private String address2;
	
	private static final String CITY = "San Francisco";
	
	private static final String STATE = "California";
	
	private static final String ZIP = "94102";
	
	private EventForm eventForm;
	
	private final LocalServiceTestHelper helper = 
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
					.setDefaultHighRepJobPolicyUnappliedJobPercentage(100));
	
	
	@Before
	public void setUp() throws Exception {
		helper.setUp();
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		startDate = dateFormat.parse("09/07/2014");
		endDate = dateFormat.parse("09/09/2014");
		registrationsAvailable = MAX_ATTENDEES - ATTENDEES;
		categories = new ArrayList<>();
		categories.add("Google");
		categories.add("Cloud");
		categories.add("Scaleable Systems");
		categories.add("Candy");
		categories.add("Butterscotch");
		categories.add("Google Cloud Platform");
		
		eventForm = new EventForm(TITLE, startDate, endDate, DESCRIPTION, 
				categories, ORGANIZER_USER_ID, ATTENDEES, MAX_ATTENDEES, 
				address1, address2, CITY, STATE, ZIP);
		
		
	}
	
	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}
	
	@Test(expected = NullPointerException.class)
		public void testNullName() throws Exception {
			EventForm nullEventForm = new EventForm(null, startDate, endDate, DESCRIPTION, 
				categories, ORGANIZER_USER_ID, ATTENDEES, MAX_ATTENDEES, 
				address1, address2, CITY, STATE, ZIP);
			
			new Event(ID, ORGANIZER_USER_ID, nullEventForm);
		}
	
	@Test
	public void testEvent() throws Exception {
		Event event = new Event(ID, ORGANIZER_USER_ID, eventForm);
		assertEquals(TITLE, event.getTitle());
		assertEquals(startDate, event.getStartDate());
		assertEquals(endDate, event.getEndDate());
		assertEquals(DESCRIPTION, event.getDescription());
		assertEquals(categories, event.getCategories());
		assertEquals(ORGANIZER_USER_ID, event.getOrganizerUserId());
		assertEquals(ATTENDEES, event.getAttendees());
		assertEquals(MAX_ATTENDEES, event.getMaxAttendees());
		assertEquals(registrationsAvailable, event.getRegistrationsAvailable());
		assertEquals(address1, event.getAddress1());
		assertEquals(address2, event.getAddress2());
		assertEquals(CITY, event.getCity());
		assertEquals(STATE, event.getState());
		assertEquals(ZIP, event.getZip());
	    // Test if they are defensive copies.
        assertNotSame(categories, event.getCategories());
        assertNotSame(startDate, event.getStartDate());
        assertNotSame(endDate, event.getEndDate());
	}
	
	
}