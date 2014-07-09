package com.appengine.planit.form;

import static com.appengine.planit.service.OfyService.ofy;


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
		MAX_ATTENDEES("maxAttendees", FieldType.INTEGER),
		ATTENDEES("attendees", FieldType.INTEGER),
		REGISTRATIONS_AVAILABLE("registrationsAvailable", FieldType.INTEGER);

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
			return this.queryOperator.contains("<") || this.queryOperator.contains(">") ||
					this.queryOperator.contains("!");
		}
	}

	/**
	 * A class representing a single filter for the query.
	 */
	public static class Filter {
		private Field field;
		private Operator operator;
		private String value;

		public Filter() {

		}

		public Filter(Field field, Operator operator, String value) {
			this.field = field;
			this.operator = operator;
			this.value = value;
		}

		public Field getField() {
			return field;
		}

		public Operator getOperator() {
			return operator;
		}

		public String getValue() {
			return value;
		}

	}

	/**
	 * A list of query filters
	 */
	private List<Filter> filters = new ArrayList<>(0);

	
	/**
	 * Holds the first inequalityFilter for checking the feasibility of the whole query.
	 */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	private Filter inequalityFilter;


	public EventQueryForm() {

	}

	
	/**
	 * Checks the feasibility of the whole query
	 */
	private void checkFilters() {
		for (Filter filter : this.filters) {
			if (filter.operator.isInequalityFilter()) { //danger zone
				if (inequalityFilter != null && !inequalityFilter.field.equals(filter.field)) {
					throw new IllegalArgumentException("Inequality filter is only allowed on one field.");
				}
				inequalityFilter = filter;  //update the filter we're checking to the most recent filter
			}
		}
	}
	
	/**
	 * Getter for the filters
	 * 
	 * @return The List of filters.
	 */
	public List<Filter> getFilters() {
		return ImmutableList.copyOf(filters);
	}
	
    /**
     * Adds a query filter to our current query form.
     *
     * @param filter A Filter object for the query.
     * @return this for method chaining.
     */
	public EventQueryForm filter(Filter filter) {
		if (filter.operator.isInequalityFilter()) { //danger zone
			if (inequalityFilter != null && !inequalityFilter.field.equals(filter.field)) {
				throw new IllegalArgumentException(
						"Inequality filter is allowed on only one field");
			}
			inequalityFilter = filter;
		}
		filters.add(filter);
		
		return this;
	}
	
	
    /**
     * Returns an Objectify Query object for the specified filters.
     *
     * @return an Objectify Query.
     */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Query<Event> getQuery() {
		checkFilters();
		Query<Event> query = ofy().load().type(Event.class);
        
		// Where we implement our default ordering mechanism is contingent upon
		// whether or not we have inequality filters in our query.
		if (inequalityFilter == null) {
			query = query.order("title");
		} else {
			query = query.order(inequalityFilter.field.getFieldName());
			query = query.order("title");
		}
		for (Filter filter : this.filters) {
			//Applies filters in order.
			if (filter.field.fieldType == FieldType.STRING) {
				query = query.filter(String.format("%s %s", filter.field.getFieldName(),  // .filter([fieldName] [operator], [value])  <---- this is the format we want to create
						filter.operator.getQueryOperator()), filter.value);
			} else if (filter.field.fieldType == FieldType.INTEGER) {
				query = query.filter(String.format("%s %s", filter.field.getFieldName(),
						filter.operator.getQueryOperator()), Integer.parseInt(filter.value));
			}
		}
		LOG.info(query.toString());
		return query;
	}
	
	

}







