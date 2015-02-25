package com.gdx.Network;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Net {
	public static final int tcpPort = 54555;
	public static final int udpPort = 54777;
	public static final String serverIP = "172.31.160.142";
	public static final String name = "John";
	public static int writeBuffer = 256000;
	public static int objectBuffer = 128000;
	
	//Register all classes that will be sent over the network here
	//Both client and server must register the same classes
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
	    kryo.register(Vector3.class);
	    kryo.register(playerPacket.class);
	    kryo.register(Array.class);
	    kryo.register(Object[].class);
	    kryo.register(playerNew.class);
	    kryo.register(projectile.class);
	    kryo.register(newProjectile.class);
	    kryo.register(projectile.class);
	    kryo.register(chatMessage.class);
	    kryo.register(String.class);
	    kryo.register(playerDisconnect.class);
	}
	
	//Packets
	public static class playerPacket {
		public Vector3 position;
		public Vector3 direction;
		public int id;
	}
	
	public static class playerNew {
		public Vector3 position;
		public int id;
	}
	
	public static class playerDisconnect {
		public int id;
	}
	
	public static class newProjectile {
		public Vector3 position;
		public Vector3 cameraPos;
		public int id;
	}
	
	public static class projectile {
		public Vector3 position;
		public Vector3 cameraPos;
		public int id;
	}
	
	public static class chatMessage {
		public String message;
	}
}
