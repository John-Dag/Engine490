package lightning3d.Network;

import lightning3d.Engine.World;

import com.badlogic.gdx.utils.Array;

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
				if (event != null)
					event.handleEvent(world);
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
