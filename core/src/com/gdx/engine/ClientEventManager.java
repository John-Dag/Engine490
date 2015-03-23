package com.gdx.engine;

import com.badlogic.gdx.utils.Array;
import com.gdx.engine.World;

public class ClientEventManager {
	private Array<ClientEvent> events;
	private World world;
	
	public ClientEventManager(World world) {
		setEvents(new Array<ClientEvent>());
		this.world = world;
	}
	
	public void processEvents() {
		for (ClientEvent event : events) {	
			//System.out.println(event);
			event.handleEvent(world);
		}
		
		boolean allEventsHandled = true;
		for (ClientEvent event : events) {
			if (!event.eventHandled) {
				System.out.println("Event not handled: " + event);
				allEventsHandled = false;
			}
		}
		
		if (allEventsHandled)
			events.clear();
	}
	
	public void addEvent(ClientEvent event) {
		events.add(event);
	}

	public Array<ClientEvent> getEvents() {
		return events;
	}

	public void setEvents(Array<ClientEvent> events) {
		this.events = events;
	}
}
