package com.gdx.Network;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Net {
	public static final int tcpPort = 54555;
	public static final int udpPort = 54777;
	public static String serverIP = "192.168.1.2";
	public static String name = "Player";
	public static String serverMessage = "Connected to Engine490 test server at ";
	public static int writeBuffer = 256000;
	public static int objectBuffer = 128000;
	
	//Register all classes that will be sent over the network here
	//Both client and server must register the same classes
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
	    kryo.register(Vector3.class);
	    kryo.register(PlayerPacket.class);
	    kryo.register(Array.class);
	    kryo.register(Object[].class);
	    kryo.register(NewPlayer.class);
	    kryo.register(ProjectilePacket.class);
	    kryo.register(NewProjectile.class);
	    kryo.register(ProjectilePacket.class);
	    kryo.register(ChatMessagePacket.class);
	    kryo.register(String.class);
	    kryo.register(PlayerDisconnect.class);
	    kryo.register(KillPacket.class);
	    kryo.register(DeathPacket.class);
	    kryo.register(CollisionPacket.class);
	    kryo.register(StatPacket.class);
	    kryo.register(PowerUpConsumedPacket.class);
	    kryo.register(PowerUpRespawnPacket.class);
	    kryo.register(NewPowerUpPacket.class);
	    kryo.register(WeaponPickedUpPacket.class);
	    kryo.register(WeaponRespawnPacket.class);
	    kryo.register(NewWeaponPacket.class);
	    kryo.register(NewMatch.class);
	}
	
	//Packets
	public static class PlayerPacket {
		public Vector3 position;
		public Vector3 direction;
		public int id;
	}
	
	public static class NewPlayer {
		public Vector3 position;
		public String name;
		public int id;
	}
	
	public static class PlayerDisconnect {
		public int id;
	}
	
	public static class NewProjectile {
		public Vector3 position, cameraPos, cameraDir, rayOrigin, rayDirection;
		public int id, originID;
	}
	
	public static class NewPowerUpPacket {
		// NOTE: I think this is not necessary because the powerUpRespawn packet will be sent instead
		public Vector3 position;
		public int powerUpEntityId;
	}
	
	public static class PowerUpRespawnPacket {
		public int powerUpEntityId;
	}
	
	public static class PowerUpConsumedPacket {
		public int powerUpEntityId;
		public int playerId;
	}
	
	public static class NewWeaponPacket {
		public Vector3 position;
		public int weaponEntityId;
	}
	
	public static class WeaponRespawnPacket {
		public int weaponEntityId;
	}
	
	public static class WeaponPickedUpPacket {
		public int weaponEntityId;
		public int playerId;
	}
	
	public static class ProjectilePacket {
		public Vector3 position, cameraPos, cameraDir;
		public int id;
	}
	
	public static class ChatMessagePacket {
		public String message;
		public int id;
	}
	
	public static class CollisionPacket {
		public int projectileID, playerID, playerOriginID, damage;
		public Vector3 position;
	}
	
	public static class StatPacket {
		public int playerID, playerDeathID, kills, deaths;
		public String name;
	}
	
	public static class KillPacket {
		public int id;
	}
	
	public static class DeathPacket {
		public int id;
	}
	
	public static class NewMatch {
		public Vector3 startPos;
	}
}
