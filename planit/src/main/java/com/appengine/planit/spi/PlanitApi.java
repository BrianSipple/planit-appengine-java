package com.appengine.planit.spi;

import static com.appengine.planit.service.OfyService.ofy;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.appengine.planit.Constants;
import com.appengine.planit.domain.Profile;
import com.appengine.planit.form.ProfileForm;
import com.appengine.planit.form.ProfileForm.TeeShirtSize;
import com.googlecode.objectify.Key;

/**
 * Defines planit APIs
 */
@Api(name = "planit", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = {
        Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID }, description = "API for the Conference Central Backend application.")
public class PlanitApi {
    
    /*
     * Get the display name from the user's email. For example, if the email is
     * lemoncake@example.com, then the display name becomes "lemoncake."
     */
    private static String extractDefaultDisplayNameFromEmail(String email) {
    	return email == null ? null : email.substring(0, email.indexOf("@"));
    }

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

        // Set the teeShirtSize to the value sent by the ProfileForm, if sent
        // otherwise leave it as the default value
    	if (profileForm.getTeeShirtSize() != null) {
    		teeShirtSize = profileForm.getTeeShirtSize();
    	}

    	
        // Set the displayName to the value sent by the ProfileForm, if sent
        // otherwise, set it to default value based on the user's email
        // by calling extractDefaultDisplayNameFromEmail(...)
    	if (profileForm.getDisplayName() != null) {
    		displayName = profileForm.getDisplayName();
    	} else {
    		displayName = extractDefaultDisplayNameFromEmail(user.getEmail());
    	}
    	
    	
    	if (profileForm.getAge() != 0) { 
    		age = profileForm.getAge();
    	}

        // Get the userId and mainEmail
    	mainEmail = user.getEmail();
    	userId = user.getUserId();
    	

        // Create a new Profile entity from the
        // userId, displayName, mainEmail, age, and teeShirtSize
        Profile profile = new Profile(userId, displayName, mainEmail, age, teeShirtSize);

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
    @ApiMethod(name = "getProfile", path = "profile", httpMethod = HttpMethod.GET)
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

}
