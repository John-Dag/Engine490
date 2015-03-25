package com.gdx.Network;

import com.gdx.Network.Net.WeaponPickedUpPacket;

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
	
	public static class NewPowerUp extends NetServerEvent {
		public Net.NewPowerUpPacket packet;
		
		public NewPowerUp(Net.NewPowerUpPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class PowerUpRespawn extends NetServerEvent {
		public Net.PowerUpRespawnPacket packet;
		
		public PowerUpRespawn(Net.PowerUpRespawnPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class PowerUpConsumed extends NetServerEvent {
		public Net.PowerUpConsumedPacket packet;
		
		public PowerUpConsumed(Net.PowerUpConsumedPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class NewWeapon extends NetServerEvent {
		public Net.NewWeaponPacket packet;
		
		public NewWeapon(Net.NewWeaponPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class WeaponRespawn extends NetServerEvent {
		public Net.WeaponRespawnPacket packet;
		
		public WeaponRespawn(Net.WeaponRespawnPacket packet) {
			this.packet = packet;
		}
	}
	
	public static class WeaponPickedUp extends NetServerEvent {
		public WeaponPickedUpPacket packet;
		
		public WeaponPickedUp(Net.WeaponPickedUpPacket packet) {
			this.packet = packet;
		}
	}
}
