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
	
	private static final String title = "World Butterscotch Summit";
	
	private Date startDate;
	
	private Date endDate;
	
	private static final String DESCRIPTION = "Google Play Services and Butterscotch";
	
	private Key profileKey;
	
	private static final String ORGANIZER_USER_ID = "123456789";
	
	private List<String> categories;
	
	private static final int MONTH = 3;
	
	private static final int MAX_ATTENDEES = 3000;
	
	private int attendees;
	
	private int registrationsAvailable;
	
	private String address1;
	
	private String address2;
	
	private static final String CITY = "San Francisco";
	
	private String state;
	
	private String zip;
	
	private EventForm eventForm;
	
	
}