package com.gdx.engine;

import com.badlogic.gdx.utils.Array;
import com.gdx.engine.World;

public class ClientEventManager {
	private Array<ClientEvent> events;
	private World world;
	public static boolean updating = false;
	
	public ClientEventManager(World world) {
		setEvents(new Array<ClientEvent>());
		this.world = world;
	}
	
	public void processEvents() {
		updating = true;
		for (ClientEvent event : events) {	
			System.out.println(event);
			event.handleEvent(world);
		}
		
		events.clear();
		updating = false;
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
