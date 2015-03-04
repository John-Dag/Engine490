package com.gdx.Network;

import com.badlogic.gdx.utils.Array;

public class NetEventManager {
	private Array<NetEvent> events;
	
	public NetEventManager() {
		events = new Array<NetEvent>();
	}
}
