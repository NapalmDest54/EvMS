package com.github.evms.eventmangement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.evms.pooling.Pool;

public class EventManager {

	private static EventManager instance = null;
	private static List<EventHandler> registeredAllEvents;
	private static List<Event> events;
	private static List<Event> eventsNew;
	private static List<Event> eventsNotified;
	private static HashMap<String, LinkedList<EventHandler>> handlerMap;
	private static Pool<Event> eventPool;

	/**
	 * Private constructor for singleton pattern.
	 */
	private EventManager() {
		handlerMap = new HashMap<String, LinkedList<EventHandler>>();
		registeredAllEvents = new ArrayList<EventHandler>();
		events = new LinkedList<Event>();
		eventsNew = new LinkedList<Event>();
		eventsNotified = new LinkedList<Event>();
		eventPool = new Pool<Event>() {
			protected Event createObject() {
				return new Event();
			}
		};

	}
	
	/**
	 * Get the instance of the EventManager system.
	 * 
	 * @return
	 */
	public static EventManager getInstance() {
		if (instance == null) {
			synchronized (EventManager.class) {
				if (instance == null) {
					instance = new EventManager();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Get new Event to be used for raising events.
	 * 
	 * @param type
	 * @return new Event
	 */
	public Event getNewEvent(String type) {
		return getNewEvent(type, (Object[]) null);
	}
	
	
	/**
	 * Get new Event to be used for raising events.
	 * 
	 * @param type
	 * @param params
	 * @return new Event
	 */
	public Event getNewEvent(String type, Object... params) {
		Event event = eventPool.getObject();
		event.setType(type);
		event.setParams(params);
		return event;
	}
	
	
	/**
	 * Interested EventHandlers will be notified immediately.
	 * This should not be used without careful consideration
	 * of threading issues.
	 * 
	 * @param event
	 */
	public void raiseImmediateEvent(Event event) {
		if (event == null) {
			throw new IllegalArgumentException("Event cannot be null");
		}
		
		notify(event);
	}

	/**
	 * Raise an Event which will be notified to interested parties.
	 * (Notification may not be guaranteed to be the same tick).
	 * 
	 * @param event
	 */
	public void raiseEvent(Event event) {
		if (event == null) {
			throw new IllegalArgumentException("Event cannot be null");
		}

		eventsNew.add(event);
	}

	/**
	 * Specify to the EventManager that an EventHandler is interested in a
	 * specific Event.
	 * 
	 * @param event
	 *            The Event type interested in.
	 * @param handler
	 *            The EventHandler to handle the event when raised.
	 */
	public void registerEvent(String event, EventHandler handler) {
		List<EventHandler> handlerList = handlerMap.get(event);
		if (handlerList == null) {
			handlerMap.put(event, new LinkedList<EventHandler>());
			handlerList = handlerMap.get(event);
		}
		handlerList.add(handler);
	}
	
	/**
	 * Remove registration for a particular event type.
	 * 
	 * @param event
	 * @param handler
	 */
	public void deregisterEvent(String event, EventHandler handler) {
		List<EventHandler> handlerList = handlerMap.get(event);
		if (handlerList == null) {
			handlerMap.remove(handler);
		}
	}
	
	/**
	 * Remove registration from all event types.
	 * 
	 * @param handler
	 */
	public void deregisterAllEvent(EventHandler handler) {
		Set<String> keys = handlerMap.keySet();
		for (String key : keys) {
			List<EventHandler> handlerList = handlerMap.get(key);
			if (handlerList == null) {
				handlerMap.remove(handler);
			}
		}
		registeredAllEvents.remove(handler);
	}
	
	/**
	 * Notify interested EventHandler of the Event passed in.
	 * 
	 * @param event
	 */
	private void notify(Event event) {
		for (EventHandler handler : registeredAllEvents) {
			handler.onEvent(event);
		}

		List<EventHandler> handlersForEvent = handlerMap.get(event.getType());
		if (handlersForEvent == null) {
			return;
		}
		for (EventHandler handler : handlersForEvent) {
			handler.onEvent(event);
		}
		
		eventsNotified.add(event);
	}

	/**
	 * This will process through all queued events, notifying all interested
	 * EventHandlers
	 */
	public void tick() {
		Iterator<Event> eventsToFreeIt = eventsNotified.iterator();
		while (eventsToFreeIt.hasNext()) {
			eventPool.free(eventsToFreeIt.next());
			eventsToFreeIt.remove();
		}
		
		Collections.sort(events);
		Iterator<Event> eventsIt = events.iterator();
		while (eventsIt.hasNext()) {
			notify(eventsIt.next());
			eventsIt.remove();
		}

		// Move eventsNew into events list
		Iterator<Event> it = eventsNew.iterator();
		while (it.hasNext()) {
			events.add(it.next());
			it.remove();
		}
	}
}
