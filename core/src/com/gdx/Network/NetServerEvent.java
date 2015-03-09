package com.gdx.Network;

public class NetServerEvent {
	public static class ProjectileCollision extends NetServerEvent {
		public Net.CollisionPacket packet;
		
		public ProjectileCollision(Net.CollisionPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class NewPlayer extends NetServerEvent {
		public Net.NewPlayer packet;
		
		public NewPlayer(Net.NewPlayer packet) {
			this.packet = packet;
		}
	}
	
	public static class ChatMessage extends NetServerEvent {
		public Net.ChatMessagePacket packet;
		
		public ChatMessage(Net.ChatMessagePacket packet) {
			this.packet = packet;
		}
	}
	
	public static class NewProjectile extends NetServerEvent {
		public Net.NewProjectile packet;
		
		public NewProjectile(Net.NewProjectile packet) {
			this.packet = packet;
		}
	}
}
