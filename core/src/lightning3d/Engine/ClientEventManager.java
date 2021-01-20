package lightning3d.Engine;

import lightning3d.Engine.World;

import com.badlogic.gdx.utils.Array;

public class ClientEventManager {
	private Array<ClientEvent> events;
	private World world;
	
	public ClientEventManager(World world) {
		setEvents(new Array<ClientEvent>());
		this.world = world;
	}
	
	public void processEvents() {
		for (ClientEvent event : events) {	
			event.handleEvent(world);
		}
		
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
