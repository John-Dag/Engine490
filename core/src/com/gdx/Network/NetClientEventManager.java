package com.gdx.Network;

import com.badlogic.gdx.utils.Array;
import com.gdx.engine.World;

public class NetClientEventManager {
	private Array<NetClientEvent> events;
	private World world;
	
	public NetClientEventManager(World world) {
		setEvents(new Array<NetClientEvent>());
		this.world = world;
	}
	
	public void processEvents() {
		synchronized (events) {
			for (NetClientEvent event : events) {
				event.handleEvent(world);
			}
		}
		
		events.clear();
	}
	
	public void addNetEvent(NetClientEvent event) {
		synchronized (events) {
			events.add(event);
		}
	}

	public Array<NetClientEvent> getEvents() {
		return events;
	}

	public void setEvents(Array<NetClientEvent> events) {
		this.events = events;
	}
}
