package com.appengine.planit.spi;

import static com.appengine.planit.service.OfyService.ofy;
import static org.junit.Assert.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList;
import com.appengine.planit.domain.Event;
import com.appengine.planit.form.EventForm;
import com.appengine.planit.form.EventQueryForm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Tests for PlanitApi API methods
 */
public class EventApiGlobalQueryTest {

	private static final String USER_ID = "123456789";

	private static final String TITLE1 = "Google Cloud Platform - Project Butterscotch";
	private static final String TITLE2 = "Google I/O";
	private static final String TITLE3 = "Machine Learning Algorithms with Google Cloud Platform";

	private Date startDate1;
	private Date startDate2;
	private Date startDate3;

	
	private Date endDate1;
	private Date endDate2;
	private Date endDate3;


	private static final String DESCRIPTION1 = "Hands-on with Google Cloud Platform's Super-secret Project Butterscotch";
	private static final String DESCRIPTION2 = "Google's annual developer event.";
	private static final String DESCRIPTION3 = "Making magic with your data in Google Cloud Platform";

	
	private static final String ORGANIZER_USER_ID = "123456789";

	
	private static final List<String> CATEGORIES1 = ImmutableList.of("Cloud", "Platform", "Butterscotch");
	private static final List<String> CATEGORIES2 = ImmutableList.of("Developer", "Platform");
	private static final List<String> CATEGORIES3 = ImmutableList.of("Cloud", "Platform", "Japan", "Machine Learning");

	
	private static final int MONTH1 = 3;
	private static final int MONTH2 = 4;
	private static final int MONTH3 = 5;

	
	private static final int MAX_ATTENDEES1 = 500;
	private static final int MAX_ATTENDEES2 = 1000;
	private static final int MAX_ATTENDEES3 = 1500;

	
	private int ATTENDEES1 = 100;
	private int ATTENDEES2 = 500;
	private int ATTENDEES3 = 1000;

	
	private int registrationsAvailable;

	
	private String address1;

	
	private String address2;

	
	private static final String CITY1 = "Mountain View";
	private static final String CITY2 = "San Francisco";
	private static final String CITY3 = "Tokyo";

	
	private static final String STATE1 = "California";
	private static final String STATE2 = "California";
	private static final String STATE3 = "NA";

	
	private static final String ZIP1 = "94024";
	private static final String ZIP2 = "94102";
	private static final String ZIP3 = "NA";

	
	private PlanitApi planitApi;

	
	/**
	 * The helper here is intentionally using 0 for the percentage, 
	 * since we test our global queries.
	 */
	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
			.setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

	
	private Event event1;
	private Event event2;
	private Event event3;

	
	@Before
	public void setUp() throws Exception {
		helper.setUp();
		planitApi = new PlanitApi();
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		// Create 3 Events.
		startDate1 = dateFormat.parse("03/25/2014");
		endDate1 = dateFormat.parse("03/26/2014");
		EventForm eventForm1 = new EventForm(
				TITLE1, startDate1, endDate1, DESCRIPTION1, 
				CATEGORIES1, ORGANIZER_USER_ID, ATTENDEES1, MAX_ATTENDEES1, 
				address1, address2, CITY1, STATE1, ZIP1);
		event1 = new Event(1001L, USER_ID, eventForm1);
		

		
		startDate2 = dateFormat.parse("04/25/2014");
		endDate2 = dateFormat.parse("04/26/2014");
		EventForm eventForm2 = new EventForm(
				TITLE2, startDate2, endDate2, DESCRIPTION2, 
				CATEGORIES2, ORGANIZER_USER_ID, ATTENDEES2, MAX_ATTENDEES2, 
				address1, address2, CITY2, STATE2, ZIP2);
		event2 = new Event(1002L, USER_ID, eventForm2);
		

		startDate3 = dateFormat.parse("05/25/2014");
		endDate3 = dateFormat.parse("05/26/2014");
		EventForm eventForm3 = new EventForm(
				TITLE3, startDate3, endDate3, DESCRIPTION3, 
				CATEGORIES3, ORGANIZER_USER_ID, ATTENDEES3, MAX_ATTENDEES3, 
				address1, address2, CITY3, STATE3, ZIP3);
		event3 = new Event(1003L, USER_ID, eventForm3);

		ofy().save().entities(event1, event2, event3).now();
	}
	
	@After
	public void tearDown() throws Exception {
		ofy().clear();
		helper.tearDown();
	}

	@Test
	public void testEmptyQuery() throws Exception {
		//Empty query
		EventQueryForm eventQueryForm = new EventQueryForm();
		List<Event> events = planitApi.queryEvents(eventQueryForm);
		assertEquals(3, events.size());
		assertTrue("The result should contain event1.", events.contains(event1));
		assertTrue("The result should contain event2.", events.contains(event2));
		assertTrue("The result should contain event3.", events.contains(event3));
		assertEquals(event1, events.get(0));
		assertEquals(event2, events.get(1));
		assertEquals(event3, events.get(2));
	}
	
	@Test
	public void testCityQuery() {
		// A query that only specifies the city
		EventQueryForm eventQueryForm = new EventQueryForm()
			.filter(new EventQueryForm.Filter(
					EventQueryForm.Field.CITY,
					EventQueryForm.Operator.EQ,
					"Tokyo"
			));
		
		List<Event> events = planitApi.queryEvents(eventQueryForm);
		assertEquals(1, events.size());
		assertTrue("The result should contain event3", events.contains(event3));
		
	}
	
    @Test
    public void testTopicsQuery() throws Exception {
        // A query only specifies the topics.
        EventQueryForm eventQueryForm = new EventQueryForm()
                .filter(new EventQueryForm.Filter(
                        EventQueryForm.Field.CATEGORY,
                        EventQueryForm.Operator.EQ,
                        "Japan"
                ));
        List<Event> events = planitApi.queryEvents(eventQueryForm);
        assertEquals(1, events.size());
        assertTrue("The result should contain event3.", events.contains(event3));
    }

    @Test
    public void testComplexQuery() throws Exception {
        // A query specifies the city and topics and month.
        EventQueryForm eventQueryForm = new EventQueryForm()
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.CATEGORY,
                		EventQueryForm.Operator.EQ,
                        "Platform"
                ))
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.CITY,
                		EventQueryForm.Operator.EQ,
                        "San Francisco"
                ))
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.MONTH,
                		EventQueryForm.Operator.EQ,
                        "4"
                ));
        List<Event> events = planitApi.queryEvents(eventQueryForm);
        assertEquals(1, events.size());
        assertTrue("The result should contain event2.", events.contains(event2));
    }

    @Test
    public void testMaxAttendeesGT() throws Exception {
        // A query specifies the maxAttendees > 999.
    	EventQueryForm eventQueryForm = new EventQueryForm()
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.MAX_ATTENDEES,
                		EventQueryForm.Operator.GT,
                        "999"
                ));
        List<Event> events = planitApi.queryEvents(eventQueryForm);
        assertEquals(2, events.size());
        assertTrue("The result should contain event2.", events.contains(event2));
        assertTrue("The result should contain event3.", events.contains(event3));
        assertEquals(event2, events.get(0));
        assertEquals(event3, events.get(1));
    }

    @Test
    public void testMaxAttendeesLT() throws Exception {
        // A query specifies the maxAttendees > 1001.
    	EventQueryForm eventQueryForm = new EventQueryForm()
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.MAX_ATTENDEES,
                		EventQueryForm.Operator.LT,
                        "1001"
                ));
        List<Event> events = planitApi.queryEvents(eventQueryForm);
        assertEquals(2, events.size());
        assertTrue("The result should contain event1.", events.contains(event1));
        assertTrue("The result should contain event2.", events.contains(event2));
        assertEquals(event1, events.get(0));
        assertEquals(event2, events.get(1));
    }

    @Test
    public void testMaxAttendeesGTEQ() throws Exception {
        // A query specifies the maxAttendees >= 1000.
    	EventQueryForm eventQueryForm = new EventQueryForm()
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.MAX_ATTENDEES,
                		EventQueryForm.Operator.GTEQ,
                        "1000"
                ));
        List<Event> events = planitApi.queryEvents(eventQueryForm);
        assertEquals(2, events.size());
        assertTrue("The result should contain event2.", events.contains(event2));
        assertTrue("The result should contain event3.", events.contains(event3));
        assertEquals(event2, events.get(0));
        assertEquals(event3, events.get(1));
    }

    @Test
    public void testMaxAttendeesLTEQ() throws Exception {
        // A query specifies the maxAttendees <= 1000.
    	EventQueryForm eventQueryForm = new EventQueryForm()
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.MAX_ATTENDEES,
                		EventQueryForm.Operator.LTEQ,
                        "1000"
                ));
        List<Event> events = planitApi.queryEvents(eventQueryForm);
        assertEquals(2, events.size());
        assertTrue("The result should contain event1.", events.contains(event1));
        assertTrue("The result should contain event2.", events.contains(event2));
        assertEquals(event1, events.get(0));
        assertEquals(event2, events.get(1));
    }

    @Test
    public void testMaxAttendeesNE() throws Exception {
        // A query specifies the maxAttendees != 1000.
    	EventQueryForm eventQueryForm = new EventQueryForm()
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.MAX_ATTENDEES,
                		EventQueryForm.Operator.NE,
                        "1000"
                ));
        List<Event> events = planitApi.queryEvents(eventQueryForm);
        assertEquals(2, events.size());
        assertTrue("The result should contain event1.", events.contains(event1));
        assertTrue("The result should contain event3.", events.contains(event3));
        assertEquals(event1, events.get(0));
        assertEquals(event3, events.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInequalityFilter() throws Exception {
        // A query specifies the maxAttendees <= 1000 and month != 6.
    	// ... this nonsense simply cannot be tolerated
        EventQueryForm eventQueryForm = new EventQueryForm()
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.MAX_ATTENDEES,
                		EventQueryForm.Operator.LTEQ,
                        "1000"
                ))
                .filter(new EventQueryForm.Filter(
                		EventQueryForm.Field.MONTH,
                		EventQueryForm.Operator.NE,
                        "6"
                ));
    }





}
