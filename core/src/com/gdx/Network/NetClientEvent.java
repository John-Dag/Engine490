package com.gdx.Network;

import com.badlogic.gdx.math.Vector3;

public class NetClientEvent {
	public NetClientEvent() {
		super();
	}
	
	public static class CreateProjectile extends NetClientEvent {
		public Net.NewProjectile packet;
		
		public CreateProjectile(Net.NewProjectile packet) {
			this.packet = packet;
		}
	}
	
	public static class CreatePlayerProjectile extends NetClientEvent {
		public Vector3 position;
		
		public CreatePlayerProjectile() {
			position = new Vector3();
		}
	}
	
	public static class CreatePlayer extends NetClientEvent {
		public Net.NewPlayer packet;
		
		public CreatePlayer(Net.NewPlayer packet) {
			this.packet = packet;
		}
	}
	
	public static class RemovePlayer extends NetClientEvent {
		public Net.PlayerDisconnect packet;
		
		public RemovePlayer(Net.PlayerDisconnect packet) {
			this.packet = packet;
		}
	}
	
	public static class ProjectileCollision extends NetClientEvent {
		public Net.CollisionPacket packet;
		
		public ProjectileCollision(Net.CollisionPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class ChatMessage extends NetClientEvent {
		public Net.ChatMessagePacket packet;
		
		public ChatMessage(Net.ChatMessagePacket packet) {
			this.packet = packet;
		}
	}
	
	public static class NewPowerUp extends NetClientEvent {
		public Net.NewPowerUpPacket packet;
		
		public NewPowerUp(Net.NewPowerUpPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class PowerUpRespawn extends NetClientEvent {
		public Net.PowerUpRespawnPacket packet;
		
		public PowerUpRespawn(Net.PowerUpRespawnPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class PowerUpConsumed extends NetClientEvent {
		public Net.PowerUpConsumedPacket packet;
		
		public PowerUpConsumed(Net.PowerUpConsumedPacket packet) {
			this.packet = packet;
		}
	}
}
