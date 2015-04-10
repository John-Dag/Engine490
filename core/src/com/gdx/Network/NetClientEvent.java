package com.gdx.Network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.Weapons.RocketLauncher;
import com.gdx.engine.Entity;
import com.gdx.engine.World;

public class NetClientEvent {
	public NetClientEvent() {
		super();
	}
	
	public static class CreateProjectile extends NetClientEvent {
		public Net.NewProjectile packet;
		
		public CreateProjectile(Net.NewProjectile packet) {
			this.packet = packet;
		}
		
		@Override
		public void handleEvent(World world) {
			Projectile projectile = NetWorld.entityManager.projectilePool.obtain();
			projectile.reset();
			projectile.setDamage(RocketLauncher.DAMAGE);
			projectile.setPosition(packet.position);
			projectile.setNetId(packet.id);
			projectile.getBulletBody().setWorldTransform(projectile.calculateTarget(packet.cameraPos));
			projectile.getBulletBody().setContactCallbackFilter(World.PLAYER_FLAG);
			projectile.getBulletBody().activate();
			Ray ray = new Ray(packet.rayOrigin, packet.rayDirection);
			projectile.getBulletBody().applyCentralImpulse(ray.direction.scl(RocketLauncher.PROJECTILE_SCALAR));
			//System.out.println("Origin: " + ray.origin + " Direction: " + ray.direction);
			Entity.entityInstances.add(projectile);
			World.dynamicsWorld.addRigidBody(projectile.getBulletBody());
		}
	}
	
	public static class CreatePlayerProjectile extends NetClientEvent {
		public Vector3 position;
		
		public CreatePlayerProjectile() {
			position = new Vector3();
		}
		
		@Override
		public void handleEvent(World world) {
			Projectile projectile = NetWorld.entityManager.projectilePool.obtain();
			projectile.reset();
			projectile.setDamage(RocketLauncher.DAMAGE);
			projectile.setPlayerProjectile(true);
			projectile.getBulletBody().setContactCallbackFilter(World.PLAYER_FLAG);
			projectile.getBulletBody().activate();
			Ray ray = world.getPlayer().camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			projectile.setNetId(world.getClient().getId() + world.getNetIdCurrent());
			world.getClient().sendProjectile(projectile, world.getClient().getId() + world.getNetIdCurrent(), ray);
			world.setNetIdCurrent(world.getNetIdCurrent() + 1);
			projectile.getBulletBody().applyCentralImpulse(ray.direction.scl(RocketLauncher.PROJECTILE_SCALAR));
			Entity.entityInstances.add(projectile);
			World.dynamicsWorld.addRigidBody(projectile.getBulletBody());
			//System.out.println("Client Origin: " + ray.origin + " Direction: " + ray.direction);
		}
	}
	
	public static class CreatePlayer extends NetClientEvent {
		public Net.NewPlayer packet;
		
		public CreatePlayer(Net.NewPlayer packet) {
			this.packet = packet;
		}

		@Override
		public void handleEvent(World world) {
			world.addPlayer(packet);
		}
	}
	
	public static class RemovePlayer extends NetClientEvent {
		public Net.PlayerDisconnect packet;
		
		public RemovePlayer(Net.PlayerDisconnect packet) {
			this.packet = packet;
		}
		
		@Override
		public void handleEvent(World world) {
			world.getClient().removePlayer(packet);
			world.getClient().removePlayerStatField(packet);
		}
	}
	
	public static class ProjectileCollision extends NetClientEvent {
		public Net.CollisionPacket packet;
		
		public ProjectileCollision(Net.CollisionPacket packet) {
			this.packet = packet;
		}
		
		@Override
		public void handleEvent(World world) {
			world.createExplosionEffect(packet);

			if (packet.playerID == world.getPlayer().getNetId()) {
				world.getPlayer().takeDamage(packet.damage);
				if (world.getPlayer().getHealth() <= 0) {
					world.getClient().sendKillUpdate(packet.playerOriginID);
					world.getClient().sendDeathUpdate(packet.playerID);
				}
			}
		}
	}
	
	public static class ChatMessage extends NetClientEvent {
		public Net.ChatMessagePacket packet;
		
		public ChatMessage(Net.ChatMessagePacket packet) {
			this.packet = packet;
		}
		
		@Override
		public void handleEvent(World world) {
			world.getClient().addChatMessage(packet);
		}
	}
	
	public void handleEvent(World world) {
		
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
		
		@Override
		public void handleEvent(World world) {
			world.getClient().sendPowerUpConsumedUpdate(this.packet);
		}
	}
	
	public static class WeaponPickedUp extends NetClientEvent {
		public Net.WeaponPickedUpPacket packet;
		
		public WeaponPickedUp(Net.WeaponPickedUpPacket packet) {
			this.packet = packet;
		}
		
		@Override
		public void handleEvent(World world) {
			world.getClient().sendWeaponPickedUpUpdate(this.packet);
		}
	}
	
	public static class UpdateNetStats extends NetClientEvent {
		public Net.StatPacket packet;
		
		public UpdateNetStats(Net.StatPacket packet) {
			this.packet = packet;
		}
		
		@Override
		public void handleEvent(World world) {
			world.getClient().updateNetStats(this.packet);
		}
	}
}
