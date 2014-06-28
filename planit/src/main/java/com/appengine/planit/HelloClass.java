package com.appengine.planit;

public class HelloClass {
	
	private String mMessage = "Hello! Welcome to Planit";
	private String mName = "";
	
	public HelloClass(){
		mMessage = mMessage + "!";
	}
	
	public HelloClass(String name) {
		mName = name;
		mMessage = mMessage + ", " + mName + "!";
	}
	
	public HelloClass(String name, String period) {
		mMessage = "Good " + period + ", " + name + "!";
	}
	
	public String getMessage() {
		return mMessage;
	}
	
	public String getName() {
		return mName;
	}
	
	
	
}

