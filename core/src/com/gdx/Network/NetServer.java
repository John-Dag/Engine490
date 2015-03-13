package com.gdx.Network;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.gdx.DynamicEntities.Player;
import com.gdx.Network.Net.PlayerPacket;
import com.gdx.Network.Net.StatPacket;
import com.gdx.engine.World;

public class NetServer {
	private Server server;
	private NetStatManager netStatManager;
	private World world;
	
	public NetServer(World world) throws IOException {
		try {
			this.world = world;
			netStatManager = new NetStatManager();
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
				packetReceived(connection, object);
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
	private void packetReceived(Connection connection, Object object) {
	   	if (object instanceof Net.PlayerPacket) {
    		Net.PlayerPacket playerPacket = (Net.PlayerPacket)object;
    		updatePlayers(playerPacket, connection);
    		server.sendToAllExceptUDP(connection.getID(), playerPacket);
    	}
    	
    	else if (object instanceof Net.ProjectilePacket) {
    		//Net.projectile packet = (Net.projectile)object;
    		//updateProjectiles(packet);
    		//server.sendToAllExceptTCP(connection.getID(), packet);
    	}
    	
    	//Handles a packet from a new player. Sends the packet to all players 
    	//except the new one.
    	else if (object instanceof Net.NewPlayer) {
    		Net.NewPlayer playerNew = (Net.NewPlayer)object;
    		Net.NewPlayer packet = new Net.NewPlayer();
    		packet.position = playerNew.position;
    		packet.id = connection.getID();
    		packet.name = playerNew.name;
    		NetServerEvent.NewPlayer event = new NetServerEvent.NewPlayer(packet);
    		world.getServerEventManager().addNetEvent(event);
    		NetStat stat = new NetStat(packet.id, packet.name);
    		netStatManager.getStats().add(stat);
    	}
    	
    	else if (object instanceof Net.NewProjectile) {
    		Net.NewProjectile packet = (Net.NewProjectile)object;
    		NetServerEvent.NewProjectile event = new NetServerEvent.NewProjectile(packet);
    		world.getServerEventManager().addNetEvent(event);
    	}
        
        else if (object instanceof Net.ChatMessagePacket) {
        	Net.ChatMessagePacket packet = (Net.ChatMessagePacket)object;
        	packet.id = connection.getID();
        	NetServerEvent.ChatMessage event = new NetServerEvent.ChatMessage(packet);
        	world.getServerEventManager().addNetEvent(event);
        }
	   	
        else if (object instanceof Net.KillPacket) {
        	Net.KillPacket packet = (Net.KillPacket)object;
        	updateNetStat(connection.getID());
        	Net.StatPacket statPacket = new Net.StatPacket();
        	statPacket.kills = netStatManager.getStats().get(connection.getID()).getKills();
        	statPacket.deaths = netStatManager.getStats().get(connection.getID()).getDeaths();
        	statPacket.playerID = connection.getID();
        	statPacket.name = packet.name;
        	server.sendToAllTCP(statPacket);
        }
	}
	
	public void updateNetStat(int id) {
		for (int i = 0; i < netStatManager.getStats().size; i++) {
			if (id == netStatManager.getStats().get(i).getId()) {
				netStatManager.getStats().get(i).setKills(netStatManager.getStats().get(i).getKills() + 1);
			}
		}
	}
	
	public void addNetStat(Net.NewPlayer packet) {
		NetStat stat = new NetStat(packet.id, packet.name);
		netStatManager.getStats().add(stat);
	}
	
	public void addNewProjectile(Net.NewProjectile packet) {
		world.addProjectile(packet);
		server.sendToAllExceptTCP(packet.id, packet);
	}
	
	public void addNewPowerUp(Net.NewPowerUpPacket packet) {
		
	}
	
	public void respawnPowerUp(Net.PowerUpRespawnPacket packet) {
		
	}
	
	public void consumePowerUp(Net.PowerUpConsumedPacket packet) {
		
	}
	
	//Updates all clients with the player that disconnected
	public void removePlayer(Connection connection) {
		String name = "";
		
		for (int i = 0; i < world.playerInstances.size; i++) {
			Player player = world.playerInstances.get(i);
			//NetStat stat = netStatManager.getStats().get(i);
			
			if (player.getNetId() == connection.getID()) {
				name = player.getNetName();
				world.playerInstances.removeIndex(i);
			}
			
//			else if (stat.getId() == connection.getID()) {
//				netStatManager.getStats().removeIndex(i);
//			}
		}
		
		Net.PlayerDisconnect disconnect = new Net.PlayerDisconnect();
		disconnect.id = connection.getID();
		server.sendToAllTCP(disconnect);
		String message = name + " disconnected.";
		sendGlobalServerMessage(message);
	}
	
	//Updates a new player with all the players on the server
	public void sendAllPlayers(int id) {
		for (int i = 0; i < world.getPlayerInstances().size; i++) {
			//System.out.println(world.getPlayerInstances().size);
			Net.NewPlayer packet = new Net.NewPlayer();
			packet.id = world.getPlayerInstances().get(i).getNetId();
			packet.position = world.getPlayerInstances().get(i).camera.position.cpy();
			packet.name = world.getPlayerInstances().get(i).getNetName();
			
			if (id != packet.id)
				server.sendToTCP(id, packet);
		}
	}
	
	public void sendCollisionPacket(Net.CollisionPacket packet) {
		server.sendToAllTCP(packet);
	}
	
	public void addNewPlayer(Net.NewPlayer packet) {
		String message = Net.serverMessage + " " + Net.serverIP + "\nActive connections: " + server.getConnections().length;
		
		world.addPlayer(packet);
		server.sendToAllExceptTCP(packet.id, packet);
		sendAllPlayers(packet.id);
		sendServerMessage(message, packet.id);
		String joinedMessage = packet.name + " joined.";
		sendGlobalServerMessageExcept(joinedMessage, packet.id);
		addNetStat(packet);
	}
	
	public void updatePlayers(PlayerPacket packet, Connection connection) {
		world.updatePlayers(packet);
	}
	
	public void updateProjectiles(Net.ProjectilePacket packet) {
		server.sendToAllUDP(packet);
	}
	
	public void sendChatMessage(Net.ChatMessagePacket packet) {
		server.sendToAllExceptTCP(packet.id, packet);
	}
	
	public void sendServerMessage(String message, int id) {
		Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
		packet.message = Net.name + ": " + message;
		server.sendToTCP(id, packet);
	}
	
	public void sendGlobalServerMessage(String message) {
		Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
		packet.message = Net.name + ": " + message;
		server.sendToAllTCP(packet);
	}
	
	public void sendGlobalServerMessageExcept(String message, int id) {
		Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
		packet.message = Net.name + ": " + message;
		server.sendToAllExceptTCP(id, packet);
	}
	
	public void serverUpdate() {
		world.getServerEventManager().processEvents();
	}
}
