package com.gdx.Network;

import com.badlogic.gdx.utils.Array;

public class NetEventManager {
	private Array<NetEvent> events;
	
	public NetEventManager() {
		setEvents(new Array<NetEvent>());
	}

	public Array<NetEvent> getEvents() {
		return events;
	}

	public void setEvents(Array<NetEvent> events) {
		this.events = events;
	}
}
