package com.gdx.Network;

import java.io.IOException;

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
		try {
			this.world = world;
			server = new Server(Net.writeBuffer, Net.objectBuffer);
			server.start();
			Log.set(Log.LEVEL_INFO);
			Net.register(server);
			server.bind(Net.tcpPort, Net.udpPort);
			Log.info("Server successfully created. Listening on TCP port: " + Net.tcpPort + ", UDP port: " + Net.udpPort);
			server.addListener(createDefaultListeners());
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	private Listener createDefaultListeners() {
		Listener listener = new Listener() {
			@Override
			public void connected(Connection connection) {
				clientConnect(connection);
			}
			
			@Override
			public void disconnected(Connection connection) {
				clientDisconnect(connection);
			}
			
			@Override
			public void received(Connection connection, Object object) {
				clientReceived(connection, object);
			}
		};
		
		return listener;
	}
	
	private void clientConnect(Connection connection) {
		Log.info("Client " + connection + " connected to the server.");
	}
	
	private void clientDisconnect(Connection connection) {
		Log.info("Client " + connection + " disconnected from the server.");
		removePlayer(connection);
	}
	
	
	//Handles received packets
	private void clientReceived(Connection connection, Object object) {
	   	if (object instanceof Net.playerPacket) {
    		Net.playerPacket playerPacket = (Net.playerPacket)object;
    		updatePlayers(playerPacket, connection);
    		server.sendToAllExceptTCP(connection.getID(), playerPacket);
    	}
    	
    	else if (object instanceof Net.projectile) {
    		//Net.projectile packet = (Net.projectile)object;
    		//updateProjectiles(packet);
    		//server.sendToAllExceptTCP(connection.getID(), packet);
    	}
    	
    	//Handles a packet from a new player. Sends the packet to all players 
    	//except the new one.
    	else if (object instanceof Net.playerNew) {
    		Net.playerNew playerNew = (Net.playerNew)object;
    		Net.playerNew packet = new Net.playerNew();
    		packet.position = playerNew.position;
    		packet.id = connection.getID();
    		addNewPlayer(packet);
    		server.sendToAllExceptTCP(connection.getID(), playerNew);
    		sendAllPlayers(connection.getID());
    	}
    	
    	else if (object instanceof Net.newProjectile) {
    		Net.newProjectile packet = (Net.newProjectile)object;
    		world.addProjectile(packet);
    		server.sendToAllExceptTCP(connection.getID(), packet);
    	}
        
        else if (object instanceof Net.chatMessage) {
        	Net.chatMessage packet = (Net.chatMessage)object;
        	server.sendToAllExceptTCP(connection.getID(), packet);
        }
	}
	
	//Updates all clients with the player that disconnected
	public void removePlayer(Connection connection) {
		for (int i = 0; i < world.playerInstances.size; i++) {
			Player player = world.playerInstances.get(i);
			if (player.getNetId() == connection.getID())
				world.playerInstances.removeIndex(i);
		}
		
		Net.playerDisconnect disconnect = new Net.playerDisconnect();
		disconnect.id = connection.getID();
		server.sendToAllTCP(disconnect);
	}
	
	//Updates a new player with all the players on the server
	public void sendAllPlayers(int id) {
		for (int i = 0; i < world.getPlayerInstances().size; i++) {
			//System.out.println(world.getPlayerInstances().size);
			Net.playerNew packet = new Net.playerNew();
			packet.id = world.getPlayerInstances().get(i).getNetId();
			packet.position = world.getPlayerInstances().get(i).camera.position.cpy();
			if (id != packet.id)
				server.sendToTCP(id, packet);
		}
	}
	
	public void addNewPlayer(Net.playerNew packet) {
		world.addPlayer(packet);
	}
	
	public void updatePlayers(playerPacket packet, Connection connection) {
		world.updatePlayers(packet);
	}
	
	public void updateProjectiles(Net.projectile packet) {
		server.sendToAllTCP(packet);
	}
	
	public void serverUpdate() {
		
	}
}
