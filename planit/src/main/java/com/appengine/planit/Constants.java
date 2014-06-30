package com.appengine.planit;

import com.google.api.server.spi.Constant;


/**
 * Contains the client IDs and scopes for allowed clients consuming the planit API
 */

public class Constants {
	//public static final String WEB_CLIENT_ID = "replace this with your web client id"; 
	  public static final String WEB_CLIENT_ID = "529076832321-e4r2ul363k21uift38bmhkd7eh8av4nv.apps.googleusercontent.com";  // We need to identify ourselves when we use the application's logic to log in a user
	  public static final String ANDROID_CLIENT_ID = "replace this with your Android client ID";
	  public static final String IOS_CLIENT_ID = "replace this with your iOS client ID";
	  public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;
	  public static final String API_EXPLORER_CLIENT_ID = Constant.API_EXPLORER_CLIENT_ID;
	  public static final String EMAIL_SCOPE = Constant.API_EMAIL_SCOPE;

	  public static final String MEMCACHE_ANNOUNCEMENTS_KEY = "RECENT_ANNOUNCEMENTS";
}