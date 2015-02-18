package com.gdx.Network;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.Network.Net.playerPacket;
import com.gdx.engine.Entity;
import com.gdx.engine.GameScreen;
import com.gdx.engine.World;

public class NetClient {
	private Client client;
	private World world;
	private GameScreen screen;
	private int id;
	private Vector3 previousPlayerPos = new Vector3();
	
	public NetClient() {
		super();
	}
	
	public NetClient(World world, GameScreen screen) throws IOException {
		try {
			this.world = world;
			this.screen = screen;
			client = new Client();
			Log.set(Log.LEVEL_INFO);
			client.start();
		    Net.register(client);
		    connectClientToServer();
		    createPlayerOnServer();
		    setId(client.getID());
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	private void connectClientToServer() {
		try {
			client.connect(5000, Net.serverIP, Net.tcpPort, Net.udpPort);
			client.addListener(createDefaultListeners());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createPlayerOnServer() {
		Net.playerNew packet = new Net.playerNew();
		packet.position = world.getPlayer().getPosition();
		packet.id = client.getID();
		client.sendTCP(packet);
	}
	
	private Listener createDefaultListeners() {
		Listener listener = new Listener() {
			@Override
			public void connected(Connection connection) {
				serverConnect(connection);
			}
			
			@Override
			public void disconnected(Connection connection) {
				serverDisconnect(connection);
			}
			
			@Override
			public void received(Connection connection, Object object) {
				clientReceived(connection, object);
			}
		};
		
		return listener;
	}
	
	private void serverConnect(Connection connection) {
		Log.info("Client " + connection + " connected to " + Net.serverIP);
	}
	
	private void serverDisconnect(Connection connection) {
		Log.info("Client " + connection + " disconnected from " + Net.serverIP);
	}
	
	//Handles received packets
	private void clientReceived(Connection connection, Object object) {
        if (object instanceof Net.playerPacket) {
     	   Net.playerPacket packet = (Net.playerPacket)object;
     	   updatePlayers(packet, connection);
        }
        
        else if (object instanceof Net.playerNew) {
       	   Net.playerNew packet = (Net.playerNew)object;
     	   addPlayer(packet);
        }
        
        else if (object instanceof Net.playerDisconnect) {
     	   Net.playerDisconnect disconnect = (Net.playerDisconnect)object;
     	   removePlayer(disconnect);
        }
        
        else if (object instanceof Net.projectile) {
     	   Net.projectile packet = (Net.projectile)object;
     	   updateProjectiles(packet);
        }
        
        else if (object instanceof Net.newProjectile) {
        	//System.out.println("test");
     	   Net.newProjectile packet = (Net.newProjectile)object;
     	   addServerProjectile(packet);
     	}
        
        else if (object instanceof Net.chatMessage) {
        	Net.chatMessage packet = (Net.chatMessage)object;
        	addChatMessage(packet);
        }
	}
	
	public synchronized void updateProjectiles(Net.projectile packet) {
		world.updateProjectiles(packet);
	}
	
	//Remove the player that disconnected from the world
	//Check both the player instances, and entity instances for the player
	public void removePlayer(Net.playerDisconnect disconnect) {
		try {
			for (int i = 0; i < world.playerInstances.size; i++) {
				Player player = world.playerInstances.get(i);
				if (player.getNetId() == disconnect.id)
					world.getPlayerInstances().removeIndex(i);
			}
			
			for (int i = 0; i < Entity.entityInstances.size; i++) {
				Entity entity = Entity.entityInstances.get(i);
				if (entity instanceof Player) {
					Player player = (Player)Entity.entityInstances.get(i);
					if (player.getNetId() == disconnect.id)
						Entity.entityInstances.removeIndex(i);
				}
			}
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void addChatMessage(Net.chatMessage packet) {
		screen.getChat().addMessage(packet);
	}
	
	public void sendChatMessage(Net.chatMessage packet) {
		client.sendTCP(packet);
	}
	
	//Adds a new player to the client representing a player on the server
	public void addPlayer(Net.playerNew packet) {
		world.addPlayer(packet);
	}
	
	//Updates the clients players with player positions from the server
	public void updatePlayers(playerPacket packet, Connection connection) {
		world.updatePlayers(packet);
	}
	
	public synchronized void addProjectile(Projectile projectile, int id) {
		Net.newProjectile packet = new Net.newProjectile();
		packet.id = id;
		packet.position = projectile.getPosition();
		packet.cameraPos = projectile.getVelocity();
		client.sendTCP(packet);
	}
	
	public void addServerProjectile(Net.newProjectile packet) {
		world.addProjectile(packet);
	}
	
	//Send updates to the server if the player is moving, jumping, or respawning.
	public void clientUpdate() {
		if (!world.getPlayer().getMovementVector().isZero() || world.getPlayer().isJumping() || world.getPlayer().isRespawning()) {
	    	Net.playerPacket packet = new Net.playerPacket();
	    	packet.position = world.getPlayer().camera.position.cpy();
	    	packet.position.y = packet.position.y - .5f;
	    	packet.id = client.getID();
	    	packet.direction = world.getPlayer().camera.direction.cpy();
			client.sendTCP(packet);
			world.getPlayer().setRespawning(false);
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
