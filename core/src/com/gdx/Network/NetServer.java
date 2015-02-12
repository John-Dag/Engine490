package com.gdx.Network;

import java.io.IOException;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.gdx.DynamicEntities.Player;
import com.gdx.Network.Net.playerPacket;
import com.gdx.engine.World;

public class NetServer {
	private Server server;
	private Array<playerPacket> players;
	private World world;
	
	public NetServer(World world) throws IOException {
		this.world = world;
		players = new Array<playerPacket>();
		server = new Server();
		server.start();
		//Log.set(Log.LEVEL_TRACE);
		Net.register(server);
		server.bind(54555, 54777);
		
 		Net.playerPacket packet = new Net.playerPacket();
		packet.position = world.getPlayer().getPosition().cpy();
		packet.id = 0;
		addNewPlayer(packet);
		
	    server.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	        	if (object instanceof Net.playerPacket) {
	        		Net.playerPacket playerPacket = (Net.playerPacket)object;
	        		
	        		for (int i = 0; i < players.size; i++) {
	        			if (playerPacket.id == players.get(i).id) {
	        				System.out.println(playerPacket.id);
	        				players.get(i).position.set(playerPacket.position.cpy());
	        			}
	        		}
	        	}
	        	
	        	else if (object instanceof Net.playerNew) {
	        		Net.playerNew playerNew = (Net.playerNew)object;
	        		Net.playerPacket packet = new Net.playerPacket();
	        		packet.position = playerNew.position;
	        		packet.id = playerNew.id;
	        		addNewPlayer(packet);
	        	}
	        }
	     });
	}
	
	public void addNewPlayer(Net.playerPacket packet) {
		players.add(packet);
		world.addPlayer(packet);
	}
	
	public void serverUpdate() {
		for (int i = 0; i < world.getPlayerInstances().size; i++) {
			if (world.getPlayerInstances().get(i).getNetId() == players.get(i).id)
				world.getPlayerInstances().get(i).camera.position.set(players.get(i).position);
		}
		players.get(0).position.set(world.getPlayer().getPosition());

		server.sendToAllUDP(players);
	}
}
