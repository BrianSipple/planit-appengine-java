package com.appengine.planit.servlet;

import static com.appengine.planit.service.OfyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appengine.planit.Constants;
import com.appengine.planit.domain.Event;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Joiner;


/**
 * A servlet for putting announcements in memcache
 * The announcement announces events that are nearly 
 * sold out (defined as having 1-5 registrations left)
 *
 */
@SuppressWarnings("serial")
public class SetAnnouncementServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//Query for events with less than 5 registrations left
		Iterable<Event> eventsIterable = ofy().load().type(Event.class)
				.filter("registrationsAvailable <=", 5)
				.filter("registrationsAvailable >", 0);


		// Get the titles of the nearly-sold-out events
		List<String> eventTitles = new ArrayList<>(0);
		for (Event event : eventsIterable) {
			eventTitles.add(event.getTitle());
		}

		// Build the string for our announcement by using a Joiner object (imported
		// from the Goolge APIs) with all of our nearly-sold-out events
		if (eventTitles.size() > 0) {
			StringBuilder announcementStringBuilder = new StringBuilder(
					"Uh oh, the following events are nearly sold out! This could be your"
							+ " last change to attend: " + "\n");

			Joiner joiner = Joiner.on(", ").skipNulls();
			announcementStringBuilder.append(joiner.join(eventTitles));


			MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

			// Put the announcement String into memcache,
			// keyed by Constants.MEMCACHE_ANNOUNCEMENTS_KEY
			String announcementKey = Constants.MEMCACHE_ANNOUNCEMENTS_KEY;
			String announcementText = announcementStringBuilder.toString();

			memcacheService.put(announcementKey, announcementText);
			
		}
		
		// Set the response status to 204, which means that the request was
		// successful, but there's no data to send back. 
		// The browser will stay on the same page if the get cam form the browser
		response.setStatus(204);



		}

	}
