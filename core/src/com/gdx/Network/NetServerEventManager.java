package com.gdx.Network;

import com.badlogic.gdx.utils.Array;
import com.gdx.engine.World;

public class NetServerEventManager {
	private Array<NetServerEvent> events;
	private World world;
	
	public NetServerEventManager(World world) {
		setEvents(new Array<NetServerEvent>());
		this.world = world;
	}
	
	public void processEvents() {
		synchronized (events) {
			for (NetServerEvent event : events) {
				if (event instanceof NetServerEvent.ProjectileCollision) {
					world.getServer().sendCollisionPacket(((NetServerEvent.ProjectileCollision) event).packet);
				}
				
				else if (event instanceof NetServerEvent.ChatMessage) {
					world.getServer().sendChatMessage(((NetServerEvent.ChatMessage) event).packet);
				}
				
				else if (event instanceof NetServerEvent.NewPlayer) {
					world.getServer().addNewPlayer(((NetServerEvent.NewPlayer) event).packet);
				}
				
				else if (event instanceof NetServerEvent.NewProjectile) {
					world.getServer().addNewProjectile(((NetServerEvent.NewProjectile) event).packet);
				}
			}
		}
		
		events.clear();
	}
	
	public void addNetEvent(NetServerEvent event) {
		synchronized (events) {
			events.add(event);
		}
	}

	public Array<NetServerEvent> getEvents() {
		return events;
	}

	public void setEvents(Array<NetServerEvent> array) {
		this.events = array;
	}
}
