package com.github.evms.eventmangement;

import com.github.evms.pooling.Poolable;


public class Event implements Comparable<Event>, Poolable {

	private String type = "";
	private int priority = 0;
	private Object[] params;
	
	protected Event() {
		
	}
	
	protected Event(String type) {
		this(type, 0);
	}

	protected Event(String type, int priority) {
		this(type, priority, (Object[]) null);
	}

	protected Event(String type, Object... args) {
		this.type = type;
		params = args;
	}

	public void setParams(Object... params) {
		this.params = params;
	}

	public Object[] getParams() {
		return params;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

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
