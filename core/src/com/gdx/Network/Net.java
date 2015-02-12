package com.gdx.Network;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.DynamicEntities.Player;

public class Net {
	public static final int port = 54555;
	
	//Register all classes that will be sent over the network here
	//Both client and server must register the same classes
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
	    kryo.register(Vector3.class);
	    kryo.register(playerPacket.class);
	    kryo.register(Array.class);
	    kryo.register(Object[].class);
	    kryo.register(playerNew.class);
	}
	
	//Packets
	public static class playerPacket {
		public Vector3 position;
		public int id;
	}
	
	public static class playerNew {
		public Vector3 position;
		public int id;
	}
	
	public static class playerDisconnect {
		public int id;
	}
}
