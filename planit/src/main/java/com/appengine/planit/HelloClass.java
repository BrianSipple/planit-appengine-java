package com.appengine.planit;

public class HelloClass {
	
	private String message = "Hello! Welcome to Planit";
	private String name = "";
	
	public HelloClass(){
		this.message = message + "!";
	}
	
	public HelloClass(String name) {
		this.name = name;
		this.message = message + ", " + name + "!";
	}
	
	
	
}

