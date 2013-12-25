package com.github.evms.eventmangement;

/**
 * Interface for classes to implement if they are interested in handling events
 * of a particular type.
 * 
 * @author Christopher Capps
 * 
 */
public interface EventHandler {

	public void onEvent(Event event);

}
