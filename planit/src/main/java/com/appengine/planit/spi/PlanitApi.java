package com.appengine.planit.spi;

import static com.appengine.planit.service.OfyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.appengine.planit.Constants;
import com.appengine.planit.domain.Announcement;
import com.appengine.planit.domain.Event;
import com.appengine.planit.domain.Profile;
import com.appengine.planit.form.EventForm;
import com.appengine.planit.form.EventQueryForm;
import com.appengine.planit.form.ProfileForm;
import com.appengine.planit.form.ProfileForm.TeeShirtSize;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;

/**
 * Defines planit APIs
 */
@Api(name = "planit",
version = "v1",
scopes = { Constants.EMAIL_SCOPE }, 
clientIds = {
		Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID		// specifies what clientIds we want to respond to (i.e., allow to use our application)
},
description = "API for the Conference Central Backend application.")

public class PlanitApi {

	public static final String EVENT_NOT_FOUND_ERROR = "No Event found with key: ";
	public static final String ALREADY_REGISTERED_ERROR = "Already registered: ";
	public static final String NOT_REGISTERED_ERROR = "Not currently registered: ";


	/**
	 * Creates or updates a Profile object associated with the given user
	 * object.
	 *
	 * @param user
	 *            A User object injected by the cloud endpoints.
	 * @param profileForm
	 *            A ProfileForm object sent from the client form.
	 * @return Profile object just created.
	 * @throws UnauthorizedException
	 *             when the User object is null.
	 */

	// Declare this method as a method available externally though Endpoints
	@ApiMethod(name = "saveProfile", path = "profile", httpMethod = HttpMethod.POST)

	// The request that invokes this method should provide data that 
	// conforms to the fields defined in ProfileForm
	public Profile saveProfile(final User user, ProfileForm profileForm) throws UnauthorizedException {

		String userId = null;
		String displayName = "Your name will go here";
		String mainEmail = null;
		int age = 0;
		TeeShirtSize teeShirtSize = TeeShirtSize.NOT_SPECIFIED;

		// If the user is not logged in, throw an UnauthorizedException
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		// Get the userId and mainEmail
		mainEmail = user.getEmail();
		userId = user.getUserId();

		// Get the profile attributes from the profile form
		teeShirtSize = profileForm.getTeeShirtSize();
		displayName = profileForm.getDisplayName();
		age = profileForm.getAge();


		// Attempt to load in the users profile from their userId to determine whether they are 
		// creating a new profile or just updating their existing one. 
		Profile profile = ofy().load().key(Key.create(Profile.class, userId)).now();

		// If the profile didn't already exist, we then create a new one... null chekcing the properties as well
		if (profile == null) {

			// Set the teeShirtSize to the value sent by the ProfileForm, if sent
			// otherwise leave it as the default value
			if (teeShirtSize == null) {
				teeShirtSize = TeeShirtSize.NOT_SPECIFIED;
			}


			if (displayName == null) {
				displayName = extractDefaultDisplayNameFromEmail(user.getEmail());
			}

			// Create a new Profile entity from the
			// userId, displayName, mainEmail, age, and teeShirtSize
			profile = new Profile(userId, displayName, mainEmail, age, teeShirtSize);

		} else {

			// If the profile already existed, we update the original one instead
			profile.update(displayName, age, teeShirtSize);
		}

		// Save the Profile entity in the datastore
		ofy().save().entity(profile).now();

		// Return the profile
		return profile;
	}

	/**
	 * Returns a Profile object associated with the given user object. The cloud
	 * endpoints system automatically inject the User object.
	 *
	 * @param user
	 *            A User object injected by the cloud endpoints.
	 * @return Profile object.
	 * @throws UnauthorizedException
	 *             when the User object is null.
	 */
	@ApiMethod(
			name = "getProfile", 
			path = "profile", 
			httpMethod = HttpMethod.GET
		)
	public Profile getProfile(final User user) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		/// load the Profile Entitiy
		String userId = user.getUserId();
		Key key = Key.create(Profile.class, userId);
		Profile profile = (Profile) ofy().load().key(key).now();
		return profile;
	}

	/**
	 * Creates a new Event object and stores it to the datastore.
	 *
	 * @param user A user who invokes this method, null when the user is not signed in.
	 * @param eventForm A EventForm object representing user's inputs.
	 * @return A newly created Event Object.
	 * @throws UnauthorizedException when the user is not signed in.
	 */
	@ApiMethod(
			name = "createEvent",
			path = "event",
			httpMethod = HttpMethod.POST
		)
	public Event createEvent(final User user, final EventForm eventForm)
			throws UnauthorizedException {
		
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		// Get the parameters we'll need to use our Profile and Event entities
		String userId = user.getUserId();
		Key<Profile> profileKey = Key.create(Profile.class, userId);
		final Key<Event> eventKey = ofy().factory().allocateId(profileKey, Event.class);
		final long eventId = eventKey.getId();
		
		// Build the queue that we'll use for adding a background task to
		final Queue queue = QueueFactory.getQueue("email-queue");
		
		// Being a transaction, adding our task of sending a confirmatiom email to the queue
		Event event = ofy().transact(new Work<Event>() {
			
			@Override
			public Event run() {
				
				// Get our two main entities: The user's profile and the Event
				Profile profile = getProfileFromUser(user);
				Event event = new Event(eventId, eventKey.toString(), eventForm);
				
				// Save the event and the profile
				ofy().save().entities(profile, event).now();
				
				// Add the task of sending a confirmation email to the queue
				// Options include the URL for sending the task, and any additional params for the task
				queue.add(ofy().getTransaction(), // Objectify gets the current transaction
						TaskOptions.Builder.withUrl("/tasks/send_confirmation_email")
							.param("email", profile.getMainEmail())
							.param("eventInfo", event.toString()));
				return event;	
			}
		});
		return event;
	}


	/**
	 * Queries from all events created. We'll build the query with a passed-in EventsQueryForm
	 * that is used to collect parameters from the user. That way, we can 
	 * just edit the form class, and have our API here be as robust to user input as possible!
	 * 
	 * We also load in each of the events' organizers' Profile keys so that we can 
	 * dynamically display their names with the event.
	 * 
	 * As an optimization, this is performed as a preloading step where we load the entire list of 
	 * names from a list of keys... to prevent separate hits on the Datastore for each event.
	 * 
	 * @return a list of events
	 */
	@ApiMethod (
			name="queryEvents", 
			path="event", 
			httpMethod = HttpMethod.POST
			)
	public List<Event> queryEvents(EventQueryForm eventQueryForm) {

		Iterable<Event> eventsIterable = eventQueryForm.getQuery().list();
		ArrayList<Event> result = new ArrayList();

		List<Key<Profile>> organizersKeyList = new ArrayList();

		for (Event event: eventsIterable) {

			organizersKeyList.add(Key.create(Profile.class, event.getOrganizerUserId()));
			result.add(event);
		}

		// To avoid separate datastore gets for each Event, pre-fetch the Profiles.
		ofy().load().keys(organizersKeyList);

		return result;
		
	}



	/**
	 * Return a list of events created specifically by that user (the "ancestor" to those event entities)
	 * @param user
	 * @return
	 * @throws UnauthorizedException
	 */
	@ApiMethod (
			name = "queryEventsCreated",
			path= "event",
			httpMethod = HttpMethod.POST
			)
	public List<Event> queryEventsCreated(User user, EventQueryForm eventQueryForm) throws UnauthorizedException {

		// Confirm that the user is logged in
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		// get the userId
		String userId = user.getUserId();

		// We need to be passing a user key into the query, not the userId!
		Key userKey = Key.create(Profile.class, userId);

		Query<Event> query = ofy().load().type(Event.class)
				.ancestor(userKey)
				.order("title");

		return query.list();
	}

	/**
	 * Queries from all events created, and then orders them on the passed-in name
	 * @param user
	 * @param orderBy
	 * @return
	 */
	@ApiMethod (
			name="queryEventsAndOrder", 
			path="event", 
			httpMethod = HttpMethod.POST
		)
	public List<Event> queryEventsAndOrder(String orderBy) {

		Query<Event> query = ofy().load().type(Event.class).order(orderBy);

		return query.list();

	}

	/**
	 * Queries all events, and then filters using a passed-in property, boolean operator,
	 * and value parameter.
	 * 
	 * Warning: Any property being queried on in DataStore MUST have an index
	 * @param property
	 * @param operator
	 * @param value
	 * @return
	 */
	@ApiMethod (
			name="queryEventsAndFilter", 
			path="event", 
			httpMethod = HttpMethod.POST
		)
	public List<Event> queryEventsAndFilter(String property, String operator, String value) {

		String propertyAndOperator = property + " " + operator;

		Query<Event> query = ofy().load().type(Event.class)
				.order(property)
				.filter(propertyAndOperator, value);  // ordering and sorting must be applied before filtering!

		return query.list();

	}



	//////////////////////////////////////////////////// CUSTOM QUERIES //////////////////////////////////////////////////////////////
	/*
	 * For unique / private / experimental cases, we can always hardwire custom queries directly into the API... even
	 * if they won't be exposed to the user interface. 
	 */
	@ApiMethod(
			name="queryEventsCustomFilter",
			path="event",
			httpMethod = HttpMethod.POST
		)
	public List<Event> queryEventsCustomFilter() {

		Query<Event> query = ofy().load().type(Event.class)
				.order("city")

				.filter("city =", "Minneapolis")
				.filter("topic =", "Artificial Intelligence")
				.filter("attendees >=", 100)
				.filter("attendees <=", 10000)		// we can't have multiple inequality filters if they correspond to different properties, but multiple inequality filters on the same property is okay. 
				.order("title")						// all ordering must be done either on the ensuing, corresponding filter property, or as an extra filter that we tack on at the end 
				.order("month");

		List<Event> events = query.list();

		return events;
	}




	////////////// Event REGISTRATION //////////////////////

	/**
	 * A wrapper for boolean
	 * We need such wrappers for primitive return types because
	 * 
	 * endpoints functions must return an Object instance. They can't return
	 * a type class such as Boolean, Integer, or String
	 */
	public class WrappedBoolean {
		private final Boolean result;
		private final String reason;

		public WrappedBoolean(Boolean result) {
			this.result = result;
			this.reason = "";
		}

		public WrappedBoolean(Boolean result, String reason) {
			this.result = result;
			this.reason = reason;
		}

		public Boolean getResult() {
			return this.result;
		}

		public String getReason() {
			return this.reason;
		}
	}


	/**
	 * Register to attend the specified Conference.
	 *
	 * @param user An user who invokes this method, null when the user is not signed in.
	 * @param websafeConferenceKey The String representation of the Conference Key.
	 * @return Boolean true when success, otherwise false
	 * @throws UnauthorizedException when the user is not signed in.
	 * @throws NotFoundException when there is no Conference with the given conferenceId.
	 */
	@ApiMethod(
			name="registerForEvent",
			path="event",
			httpMethod = HttpMethod.POST
			)
	public WrappedBoolean registerForEvent(final User user, 
			@Named("websafeEventKey") final String websafeEventKey)
					throws UnauthorizedException, NotFoundException,
					ForbiddenException, ConflictException {
		// If not signed in, throw a 401 error.
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		// Get the userId
		String userId = user.getUserId();

		WrappedBoolean result = ofy().transact(new Work<WrappedBoolean>() {

			@Override
			public WrappedBoolean run() {
				try {

					// Get the event key -- we can get it form the websafeEventKey
					// Java will throw a fordidden exception if the key cannot be created
					Key<Event> eventKey = Key.create(Event.class, websafeEventKey);

					// Get the event entity from the DataStore
					Event event = ofy().load().key(eventKey).now();

					// Throw a 404 if the event is not found
					if (event == null) {
						return new WrappedBoolean(false, 
								EVENT_NOT_FOUND_ERROR + websafeEventKey);
					}

					// Get the users profile from the DataStore
					Profile profile = getProfileFromUser(user);

					// Has the user already registered to this event?
					if (profile.getEventsToAttendKeys().contains(websafeEventKey)) {
						return new WrappedBoolean(false, ALREADY_REGISTERED_ERROR);

						// Is the event full?
					} else if (event.getRegistrationsAvailable() <= 0) {
						return new WrappedBoolean(false, "This event is already full");

						// Otherwise, we're all clear... let's sign up!
					} else {

						// Add the websafeEventKey to the Profile's eventsToAttendProperty
						profile.addToEventsToAttendKeys(websafeEventKey);

						// Decrease the event's registrations available
						event.confirmRegistration(1);

						// Save the event and profile entities in one unified transaction
						ofy().save().entities(event, profile).now();

						// We're booked!
						return new WrappedBoolean(true, "Registration successfull");
					}

				} catch (Exception e) {
					return new WrappedBoolean(false, "Unknown Exception");
				}
			}
		});

		// if result is false
		if (!result.getResult()) {
			if (result.getReason().contains(EVENT_NOT_FOUND_ERROR)) {
				throw new NotFoundException(result.getReason());

			} else if (result.getReason().contains(ALREADY_REGISTERED_ERROR)) {
				throw new ConflictException("You have already registered for this event!");

			} else if (result.getReason() == "This event is already full") {
				throw new ConflictException("This event is already full");

			} else {
				throw new ForbiddenException("Unknown exception");
			}
		}
		return result;
	}


	/**
	 * Retrieves all events that the user is set to attend
	 * @throws UnauthorizedException 
	 */
	@ApiMethod(
			name = "getEventsToAttend",
			path = "events/{websafeEventKey}",
			httpMethod = HttpMethod.POST			
			)
	public Collection<Event> getEventsToAttend(User user) 
			throws UnauthorizedException, NotFoundException {

		if (user == null) {
			throw new UnauthorizedException("Authorization required!");
		}

		// Get the user's profile
		Profile profile = getProfileFromUser(user);

		if (profile == null) {
			throw new NotFoundException("User not found");
		}

		// Get the keys for all events to which user is registered 
		List<String> eventsToAttendKeyStrings = profile.getEventsToAttendKeys();

		// Make a list of actual Key objects from the Key Strings
		List<Key<Event>> eventKeys = new ArrayList();

		for (String eventKeyString : eventsToAttendKeyStrings) {
			eventKeys.add(Key.create(Event.class, eventKeyString));
		}

		// Now that we have  a list of key objects, load the Events from it to 
		// build the Event collection
		Collection<Event> eventCollection = ofy().load().keys(eventKeys).values();

		return eventCollection;

	}

	/**
     * Unregister from the specified Event.
     *
     * @param user An user who invokes this method, null when the user is not signed in.
     * @param websafeConferenceKey The String representation of the Event Key 
     * to unregister from.
     * @return Boolean true when success, otherwise false.
     * @throws UnauthorizedException when the user is not signed in.
     * @throws NotFoundException when there is no Event with the given eventId.
     */
	@ApiMethod(
			name = "unregisterFromEvent",
			path = "event/{websafeEventKey}/registration",
			httpMethod = HttpMethod.DELETE			
			)
	public WrappedBoolean unRegisterFromEvent(final User user, 
			@Named("websafeEventKey") final String websafeEventKey) throws
			UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {

		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}


		WrappedBoolean result = ofy().transact(new Work<WrappedBoolean>() {

			@Override
			public WrappedBoolean run() {
				try {

					Key<Event> eventKey = Key.create(Event.class, websafeEventKey);

					Event event = ofy().load().key(eventKey).now();

					if (event == null) {
						return new WrappedBoolean(false, EVENT_NOT_FOUND_ERROR);
					}

					Profile profile = getProfileFromUser(user);

					if (!profile.getEventsToAttendKeys().contains(websafeEventKey)) {
						return new WrappedBoolean(false, NOT_REGISTERED_ERROR);
					}

					// If they're already registered, well, no need to check the number of 
					// available registrations.

					// So... we have the user,.. they're signed up for the event... now we 
					// make the changes!

					profile.unregisterFromEvent(websafeEventKey);

					event.giveBackRegistrations(1);

					return new WrappedBoolean(true, "Registration succueesfully removed");

				} catch (Exception e) {
					return new WrappedBoolean(false, "Unknown exception");
				}
			}
		});
		
		// if result is false
		if (!result.getResult()) {
			if (result.getReason().contains(EVENT_NOT_FOUND_ERROR)) {
				throw new NotFoundException(result.getReason());

			} else if (result.getReason().contains(NOT_REGISTERED_ERROR)) {
				throw new ConflictException("You haven't even registered for this event!");
			} else {
				throw new ForbiddenException("Unknown exception");
			}
		}
		return result;
	}
	

	/////////////////////// MEMCACHING METHODS //////////////////
	
	@ApiMethod(
			name = "getAnnouncement",
			path = "announcement",
			httpMethod = HttpMethod.GET
		)
	public Announcement getAnnoncement() {	
		
		// Connect to a memcache service
		MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
		
		// Query memcache for the announcement message, which would have been stored with our Constant
		Object message = memcacheService.get(Constants.MEMCACHE_ANNOUNCEMENTS_KEY);

		// If we get something, that's our announcement talking. We need to construct it by passing
		// in the message's String.
		if (message != null) {
			return new Announcement(message.toString());
		}
		
		return null;
		
	}

	///////////////////////////////////////////////// HELPERS //////////////////////////////////////////////


	/**
	 * Get the display name from the user's email. For example, if the email is
	 * lemoncake@example.com, then the display name becomes "lemoncake."
	 */
	private static String extractDefaultDisplayNameFromEmail(String email) {
		return email == null ? null : email.substring(0, email.indexOf("@"));
	}


	/**
	 * Gets the Profile entity for the current user
	 * or creates it if it doesn't exist
	 * @param user
	 * @return user's Profile
	 */
	private static Profile getProfileFromUser(User user) {
		// First fetch the user's Profile from the datastore.
		Profile profile = ofy().load().key(
				Key.create(Profile.class, user.getUserId())).now();
		if (profile == null) {
			// Create a new Profile if it doesn't exist.
			// Use default displayName and teeShirtSize
			String email = user.getEmail();
			profile = new Profile(user.getUserId(),
					extractDefaultDisplayNameFromEmail(email), email, 0, TeeShirtSize.NOT_SPECIFIED);
		}
		return profile;
	}

}






