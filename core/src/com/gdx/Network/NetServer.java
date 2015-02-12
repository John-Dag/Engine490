package com.gdx.Network;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
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
	private World world;
	
	public NetServer(World world) throws IOException {
		this.world = world;
		server = new Server();
		server.start();
		//Log.set(Log.LEVEL_TRACE);
		Net.register(server);
		server.bind(54555, 54777);
		
	    server.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	        	//Sends packet to all players except the client. Updates position. 
	        	if (object instanceof Net.playerPacket) {
	        		Net.playerPacket playerPacket = (Net.playerPacket)object;
	        		updatePlayers(playerPacket, connection);
	        		server.sendToAllExceptTCP(connection.getID(), playerPacket);
	        	}
	        	
	        	//Handles a packet from a new player. Sends the packet to all players 
	        	//except the new one.
	        	else if (object instanceof Net.playerNew) {
	        		Net.playerNew playerNew = (Net.playerNew)object;
	        		Net.playerNew packet = new Net.playerNew();
	        		packet.position = playerNew.position;
	        		packet.id = playerNew.id;
	        		addNewPlayer(packet);
	        		server.sendToAllExceptTCP(connection.getID(), playerNew);
	        		sendAllPlayers(connection.getID());
	        	}
	        }
	    });
	    
	    server.addListener(new Listener() {
	    	public void disconnected(Connection connection) {
	    		removePlayer(connection);
	    	}
	    });
	}
	
	//Updates all clients with the player that disconnected
	public void removePlayer(Connection connection) {
		world.playerInstances.removeIndex(connection.getID());
		Net.playerDisconnect disconnect = new Net.playerDisconnect();
		disconnect.id = connection.getID();
		server.sendToAllTCP(disconnect);
	}
	
	//Updates a new player with all the players on the server
	public void sendAllPlayers(int id) {
		for (int i = 0; i < world.getPlayerInstances().size; i++) {
			System.out.println(world.getPlayerInstances().size);
			Net.playerNew packet = new Net.playerNew();
			packet.id = world.getPlayerInstances().get(i).getNetId();
			packet.position = world.getPlayerInstances().get(i).camera.position.cpy();
			server.sendToTCP(id, packet);
		}
	}
	
	public void addNewPlayer(Net.playerNew packet) {
		world.addPlayer(packet);
	}
	
	public synchronized void updatePlayers(playerPacket packet, Connection connection) {
		world.updatePlayers(packet);
	}
	
	public void serverUpdate() {
		
	}
}
