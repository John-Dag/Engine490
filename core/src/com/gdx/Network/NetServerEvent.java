package com.gdx.Network;

import com.gdx.engine.World;

public class NetServerEvent {
	public NetServerEvent() {
		super();
	}
	
	public static class ProjectileCollision extends NetServerEvent {
		public Net.CollisionPacket packet;
		
		public ProjectileCollision(Net.CollisionPacket packet) {
			this.packet = packet;
		}
		
		@Override
		public void handleEvent(World world) {
			world.getServer().sendCollisionPacket(packet);
		}
	}
	
	public static class NewPlayer extends NetServerEvent {
		public Net.NewPlayer packet;
		
		public NewPlayer(Net.NewPlayer packet) {
			this.packet = packet;
		}
		
		@Override 
		public void handleEvent(World world) {
			world.getServer().addNewPlayer(packet);
    		world.getServer().sendNetStatUpdate();
		}
	}
	
	public static class ChatMessage extends NetServerEvent {
		public Net.ChatMessagePacket packet;
		
		public ChatMessage(Net.ChatMessagePacket packet) {
			this.packet = packet;
		}
		
		@Override 
		public void handleEvent(World world) {
			world.getServer().sendChatMessage(packet);
		}
	}
	
	public static class NewProjectile extends NetServerEvent {
		public Net.NewProjectile packet;
		
		public NewProjectile(Net.NewProjectile packet) {
			this.packet = packet;
		}
		
		@Override 
		public void handleEvent(World world) {
			world.getServer().addNewProjectile(packet);
		}
	}
	
	public void handleEvent(World world) {
		
	}
}
