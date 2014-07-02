package com.appengine.planit.form;

import com.google.common.collect.ImmutableList;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.common.collect.ImmutableList;
import com.appengine.planit.domain.Event;

import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * A simple Java object (POJO) representing a query options for Conference.
 */
public class EventQueryForm {


	private static final Logger LOG = Logger.getLogger(EventQueryForm.class.getName()); 
	
	/**
	 * Enum representing a field type
	 */
	public static enum FieldType {
		STRING, INTEGER
	}
	
	/**
	 * Enum representing a field
	 */
	public static enum Field {
		// "Interface definition" of the enum
		CITY("city", FieldType.STRING),
		CATEGORY("categories", FieldType.STRING),
		MONTH("month", FieldType.INTEGER),
		MAX_ATTENDEES("maxAttendees", FieldType.INTEGER);
		
		private String fieldName;
		
		private FieldType fieldType;
		
		// Constructor for the enum
		private Field(String fieldName, FieldType fieldType) {
			this.fieldName = fieldName;
			this.fieldType = fieldType;
		}
		
		// getter for the enum
		private String getFieldName() {
			return this.fieldName;
		}
	}
	
	/**
	 * Enum representing an operator
	 */
	public static enum Operator {
		EQ("=="),
		LT("<"),
		GT(">"),
		LTEQ("<="),
		GTEQ(">="),
		NE("!=");
		
		private String queryOperator;
		
		private Operator(String queryOperator) {
			this.queryOperator = queryOperator;
		}
		
		private String getQueryOperator() {
			return this.queryOperator;
		}
		
		/**
		 * Because, you know, we can only have multipler in-eqs if they're on the same property
		 */
		private boolean isInequalityFilter() {
			
		}
	}
	
}
