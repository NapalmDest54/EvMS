package com.github.evms.eventmangement;

import com.github.evms.pooling.Poolable;

/**
 * Class to encapsulate the information associated with an event. An event has a
 * type, priority, and (optionally) parameters for passing data. This class is
 * Poolable, so that it may be used in a Pool style interface. Event objects
 * implement Comparable, so that they may easily be used in a priority queue.
 * 
 * @author Christopher Capps
 * 
 */
public class Event implements Comparable<Event>, Poolable {

	private String type = "";
	private int priority = 0;
	private Object[] params;

	protected Event() {

	}

	/**
	 * Create a new Event with default priority (0) and no parameters.
	 * 
	 * @param type
	 */
	protected Event(String type) {
		this(type, 0);
	}

	/**
	 * Create a new Event with no parameters.
	 * @param type
	 * @param priority
	 */
	protected Event(String type, int priority) {
		this(type, priority, (Object[]) null);
	}

	/**
	 * Create a new Event with default priority (0).
	 * @param type
	 * @param args
	 */
	protected Event(String type, Object... args) {
		this.type = type;
		params = args;
	}

	/**
	 * Set the paraneters of the Event.
	 * @param params
	 */
	public void setParams(Object... params) {
		this.params = params;
	}

	/**
	 * Get the parameters of the Event.
	 * @return
	 */
	public Object[] getParams() {
		return params;
	}

	/**
	 * Get the type of the Event.
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type of the Event.
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the priority of the Event.
	 * 
	 * @return priority
	 */
	protected int getPiority() {
		return this.priority;
	}

	@Override
	public int compareTo(Event o) {
		return priority - o.getPiority();
	}

	@Override
	public void reset() {
		type = "";
		priority = 0;
		params = null;
	}

}
