package com.gdx.Network;

import com.badlogic.gdx.utils.Array;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.Network.NetEvent.ChatMessage;
import com.gdx.Network.NetEvent.CreatePlayer;
import com.gdx.Network.NetEvent.CreatePlayerProjectile;
import com.gdx.Network.NetEvent.CreateProjectile;
import com.gdx.Network.NetEvent.ProjectileCollision;
import com.gdx.Network.NetEvent.RemovePlayer;
import com.gdx.engine.Entity;
import com.gdx.engine.World;

public class NetEventManager {
	private Array<NetEvent> events;
	private World world;
	
	public NetEventManager(World world) {
		setEvents(new Array<NetEvent>());
		this.world = world;
	}
	
	public void processEvents() {
		synchronized (events) {
			for (NetEvent event : events) {
				if (event instanceof CreateProjectile) {
					Projectile projectile = NetWorld.entManager.projectilePool.obtain();
					projectile.reset();
					projectile.setProjectileSpeed(5.0f);
					projectile.setDamage(10);
					projectile.setPosition(((CreateProjectile) event).packet.position);
					projectile.setVelocity(((CreateProjectile) event).packet.cameraPos);
					projectile.setAcceleration(((CreateProjectile) event).packet.cameraPos);
					projectile.setNetId(((CreateProjectile) event).packet.id);
					Entity.entityInstances.add(projectile);
				}
				
				else if (event instanceof CreatePlayerProjectile) {
					Projectile projectile = NetWorld.entManager.projectilePool.obtain();
					projectile.reset();
					projectile.setProjectileSpeed(world.getPlayer().getWeapon().getProjectileSpeed());
					projectile.setDamage(world.getPlayer().getWeapon().getDamage());
					projectile.setPlayerProjectile(true);
					projectile.setDealtDamage(false);
					projectile.setIsActive(true);
					projectile.setPosition(((CreatePlayerProjectile) event).position);
					Entity.entityInstances.add(projectile);
					projectile.setNetId(world.getClient().getId() + world.getNetIdCurrent());
					world.getClient().sendProjectile(projectile, world.getClient().getId() + world.getNetIdCurrent());
					world.setNetIdCurrent(world.getNetIdCurrent() + 1);
				}
				
				else if (event instanceof ProjectileCollision) {
					world.getPlayer().takeDamage(((ProjectileCollision) event).packet.damage);
				}
				
				else if (event instanceof CreatePlayer) {
					world.addPlayer(((CreatePlayer) event).packet);
				}
				
				else if (event instanceof RemovePlayer) {
					world.getClient().removePlayer(((RemovePlayer) event).packet);
				}
				
				else if (event instanceof ChatMessage) {
					world.getClient().addChatMessage(((ChatMessage) event).packet);
				}
			}
		}
		
		events.clear();
	}
	
	public void addNetEvent(NetEvent event) {
		synchronized (events) {
			events.add(event);
		}
	}

	public Array<NetEvent> getEvents() {
		return events;
	}

	public void setEvents(Array<NetEvent> events) {
		this.events = events;
	}
}
