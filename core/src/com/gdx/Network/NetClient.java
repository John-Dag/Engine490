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
import com.gdx.engine.World;

public class NetClient {
	private Client client;
	private World world;
	private int id;
	
	public NetClient() {
		super();
	}
	
	public NetClient(final World world) throws IOException {
		this.world = world;
		client = new Client();
		
		//Log.set(Log.LEVEL_TRACE);
		client.start();
	    Net.register(client);
		client.connect(5000, "192.168.1.4", 54555, 54777);
		
		Net.playerNew packet = new Net.playerNew();
		packet.position = world.getPlayer().getPosition();
		packet.id = client.getID();
		client.sendTCP(packet);
		
	    client.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	           if (object instanceof Net.playerPacket) {
	        	   Net.playerPacket packetRecieved = (Net.playerPacket)object;
	        	   Net.playerPacket packet = new Net.playerPacket();
	        	   packet = packetRecieved;
	        	   updatePlayers(packet, connection);
	           }
	           
	           else if (object instanceof Net.playerNew) {
	          	   Net.playerNew packetRecieved = (Net.playerNew)object;
	        	   Net.playerNew packet = new Net.playerNew();
	        	   packet = packetRecieved;
	        	   addPlayer(packet);
	           }
	           
	           else if (object instanceof Net.playerDisconnect) {
	        	   Net.playerDisconnect disconnect = (Net.playerDisconnect)object;
	        	   Net.playerDisconnect disconnectPlayer = new Net.playerDisconnect();
	        	   disconnectPlayer = disconnect;
	        	   removePlayer(disconnectPlayer);
	           }
	           
	           else if (object instanceof Net.projectile) {
	        	   Net.projectile temp = (Net.projectile)object;
	        	   Net.projectile packet = new Net.projectile();
	        	   packet = temp;
	        	   updateProjectiles(packet);
	           }
	           
	           else if (object instanceof Net.newProjectile) {
	        	   Net.newProjectile temp = (Net.newProjectile)object;
	        	   Net.newProjectile packet = new Net.newProjectile();
	        	   packet = temp;
	        	   addServerProjectile(packet);
	        	}
	        }
	     });
	}
	
	public void updateProjectiles(Net.projectile packet) {
		world.updateProjectiles(packet);
	}
	
	//Remove the player that disconnected from the world
	public void removePlayer(Net.playerDisconnect disconnect) {
		world.getPlayerInstances().removeIndex(disconnect.id);
	}
	
	//Adds a new player to the client representing a player on the server
	public void addPlayer(Net.playerNew packet) {
		world.addPlayer(packet);
	}
	
	//Updates the clients players with player positions from the server
	public void updatePlayers(playerPacket packet, Connection connection) {
		world.updatePlayers(packet);
	}
	
	public void addProjectile(Projectile projectile) {
		Net.newProjectile packet = new Net.newProjectile();
		packet.id = projectile.getNetId();
		packet.position = projectile.getPosition();
		client.sendTCP(packet);
	}
	
	public void addServerProjectile(Net.newProjectile packet) {
		world.addProjectile(packet);
	}
	
	//Constantly sends client packets to the server. Need to add some kind of throttling to this.
	public void clientUpdate() {
    	Net.playerPacket packet = new Net.playerPacket();
    	packet.position = world.getPlayer().camera.position.cpy();
    	packet.id = client.getID();
		client.sendTCP(packet);
	}
}
