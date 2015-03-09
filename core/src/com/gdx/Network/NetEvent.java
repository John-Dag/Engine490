package com.gdx.Network;

import com.badlogic.gdx.math.Vector3;

public class NetEvent {
	public NetEvent() {
		super();
	}
	
	public static class CreateProjectile extends NetEvent {
		public Net.NewProjectile packet;
		
		public CreateProjectile(Net.NewProjectile packet) {
			this.packet = packet;
		}
	}
	
	public static class CreatePlayerProjectile extends NetEvent {
		public Vector3 position;
		
		public CreatePlayerProjectile() {
			position = new Vector3();
		}
	}
	
	public static class CreatePlayer extends NetEvent {
		public Net.NewPlayer packet;
		
		public CreatePlayer(Net.NewPlayer packet) {
			this.packet = packet;
		}
	}
	
	public static class RemovePlayer extends NetEvent {
		public Net.PlayerDisconnect packet;
		
		public RemovePlayer(Net.PlayerDisconnect packet) {
			this.packet = packet;
		}
	}
	
	public static class ProjectileCollision extends NetEvent {
		public Net.CollisionPacket packet;
		
		public ProjectileCollision(Net.CollisionPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class ChatMessage extends NetEvent {
		public Net.ChatMessagePacket packet;
		
		public ChatMessage(Net.ChatMessagePacket packet) {
			this.packet = packet;
		}
	}
}
