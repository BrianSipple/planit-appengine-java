package com.appengine.planit;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;


/**
 * Defines endpoint API functions
 */
@Api(name = "planitendpoints", version = "v1",
	scopes = {Constants.EMAIL_SCOPE},
	clientIds = {Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
	description = "Api for Planit endpoints.")

public class PlanitEndpoints {
	
	// Declare this method as available externally through Endpoints
	@ApiMethod(name="sayHello", path = "sayHello",
			httpMethod = HttpMethod.GET)
	
	public HelloClass sayHello() {
		return new HelloClass();
	}
	
	// Declare this method as available externally through Endpoints
	@ApiMethod(name="sayHelloByName", path="sayHelloByName",
			httpMethod = HttpMethod.GET)
	
	public HelloClass sayHelloByName(@Named("name") String name) {
		return new HelloClass(name);
	}
	
	
	// Declare this method as available externally through Endpoints
	@ApiMethod(name="greetByPeriod", path="greetByPeriod",
			httpMethod = HttpMethod.POST)
	
	public HelloClass greetByPeriod(@Named("name") String name, 
									@Named("period") String period) {
		return new HelloClass(name, period);
	}


}