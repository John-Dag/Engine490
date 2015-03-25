package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.Network.Net;
import com.gdx.Network.NetServerEvent;
import com.gdx.engine.Entity;
import com.gdx.engine.GameScreen;
import com.gdx.engine.World;

public class WeaponSpawner extends StaticEntity {
	WeaponSpawn weaponSpawnRef;
	Color color = new Color();
	private float spawnTime;
	private WeaponSpawner thisSpawn;
	private World world;
	
	public WeaponSpawner() {
		super();
	}
	
	public WeaponSpawner(final Vector3 position, int id, boolean isActive, boolean isRenderable,
			boolean isDecalFacing, float spawnTime, Color color, WeaponSpawn weaponSpawn, World world) {
		super(position, id, isActive, isRenderable, isDecalFacing);
		this.world = world;
		this.color = color;
		this.spawnTime = spawnTime;
		PointLight pointLight = new PointLight();
		pointLight.set(color, position, 1f);
		this.setPointLight(pointLight);
		this.setEffect(World.particleManager.getMistPool().obtain());
		weaponSpawnRef = weaponSpawn;
		weaponSpawnRef.setSpawner(this);
		Entity.entityInstances.add(weaponSpawnRef);
		thisSpawn = this;
	}
	
	public void startTimer() {
		System.out.println("Start Timer");
		Timer.schedule(new Task() {
			@Override
			public void run() {
				System.out.println("TimerTriggered");
				if(GameScreen.mode == GameScreen.mode.Server) {
					Net.WeaponRespawnPacket packet = new Net.WeaponRespawnPacket();
					packet.weaponEntityId = weaponSpawnRef.getUniqueId();
					NetServerEvent.WeaponRespawn event = new NetServerEvent.WeaponRespawn(packet);
					if (world != null) {
						world.getServerEventManager().addNetEvent(event);
					}
				} else if(GameScreen.mode == GameScreen.mode.Offline) {
					weaponSpawnRef.setIsRenderable(true);
				}
			}
		}, this.spawnTime);
	}
	
	public float getSpawnTime() {
		return spawnTime;
	}
	
	public void setSpawnTime(float spawnTime) {
		this.spawnTime = spawnTime;
	}
}