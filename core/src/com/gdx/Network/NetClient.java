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
import com.gdx.Network.Net.playerPacket;
import com.gdx.engine.World;

public class NetClient {
	private Client client;
	private World world;
	private int id;
	
	public NetClient() {
		
	}
	
	public NetClient(World world) throws IOException {
		this.world = world;
		client = new Client();
		
		//Log.set(Log.LEVEL_TRACE);
		client.start();
	    Net.register(client);
		client.connect(5000, "192.168.1.2", 54555, 54777);
		
		Net.playerNew packet = new Net.playerNew();
		packet.position = world.getPlayer().getPosition();
		packet.id = client.getID();
		client.sendUDP(packet);
		
	    client.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	           if (object instanceof Array) {
	        	   //System.out.println("recieved packets");
	        	   Array<Net.playerPacket> playerPackets = (Array<playerPacket>)object;
	        	   for (int i = 0; i < playerPackets.size; i++) {
	        		   Net.playerPacket packet = new Net.playerPacket();
	        		   packet = (com.gdx.Network.Net.playerPacket) playerPackets.get(i);
	        		   updatePlayers(packet);
	        	   }
	           }
	        }
	     });
	}
	
	public void addPlayer(playerPacket packet) {
		world.addPlayer(packet);
	}
	
	public void updatePlayers(playerPacket packet) {
		boolean match = false;
		for (int i = 0; i < world.playerInstances.size; i++) {
			if (packet.id == world.playerInstances.get(i).getNetId() || packet.id == 0) {
				System.out.println(world.playerInstances.get(i).getPosition());
				world.playerInstances.get(i).camera.position.set(packet.position.cpy());
				match = true;
			}
		}
		
		if (!match) {
     	   addPlayer(packet);
		}
	}
	
	public void clientUpdate() {
    	Net.playerPacket packet = new Net.playerPacket();
    	packet.position = world.getPlayer().camera.position.cpy();
    	packet.id = client.getID();

		client.sendUDP(packet);
	}
}
