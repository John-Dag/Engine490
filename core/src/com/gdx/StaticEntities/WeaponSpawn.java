package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gdx.Network.Net;
import com.gdx.Network.NetClientEvent;
import com.gdx.StaticEntities.PowerUp.powerUpTypeEnum;
import com.gdx.engine.GameScreen;
import com.gdx.engine.World;

public class WeaponSpawn extends StaticEntity {
	private Vector3 rotationVec;
	private WeaponSpawner weaponSpawner;
	public enum weaponSpawnTypeEnum {
		rocketLauncher, sword
	}
	
	private static int weaponSpawnCount = 0;
	private int uniqueId;
	
	private weaponSpawnTypeEnum weaponSpawnType;
	
	public WeaponSpawn() {
		super();
	}

	public WeaponSpawn(Vector3 position, int id, boolean isActive, boolean isRenderable, Model model, weaponSpawnTypeEnum type) {
		super(position, id, isActive, isRenderable, false);
		uniqueId = weaponSpawnCount;
		weaponSpawnCount++;
		
		this.setModel(new ModelInstance(model));
		this.setWeaponSpawnType(type);
		rotationVec = new Vector3(0f, 1f, 0f);
	}
	
	@Override
	public void update(float delta, World world) {
		if (this.getModel() != null && this.isRenderable() && this.getTransformedBoundingBox().intersects(World.player.getTransformedBoundingBox())) {
			if (GameScreen.mode == GameScreen.mode.Offline){
				this.setIsRenderable(false);
				effect();
				if(weaponSpawner != null) {

					this.weaponSpawner.startTimer();

				}else{
					System.out.println("Weapon Spawner is null");
				}
			} else {
				this.setIsRenderable(false);
				System.out.println("Send WeaponPickedUp message to server");
				if (world.getClient() != null) {
					Net.WeaponPickedUpPacket packet = new Net.WeaponPickedUpPacket();
					packet.playerId = world.player.getNetId();
					packet.weaponEntityId = this.getUniqueId();
					NetClientEvent.WeaponPickedUp event = new NetClientEvent.WeaponPickedUp(packet);
					world.getClientEventManager().addNetEvent(event);
				}
			}
		}
		
		else {
			this.getModel().transform.rotate(rotationVec, 90f * delta);
		}
	}
	
	public void setSpawner(WeaponSpawner weaponSpawner) {
		this.weaponSpawner = weaponSpawner;
	}
	
	public WeaponSpawner getSpawner() {
		return this.weaponSpawner;
	}
	
	// Override to create your own power-up effect!
	public void effect() {
		/* EXAMPLE:

			World.player.setSpeedBoost(SPEEDBOOST);
			Timer.schedule(new Task() {
				public void run() { 
					World.player.setSpeedBoost(DEFAULTSPEED);
				}
			}, DURATION); 

		 */
	}
	
	public void setWeaponSpawnType(weaponSpawnTypeEnum type) {
		this.weaponSpawnType = type;
	}
	
	public weaponSpawnTypeEnum getWeaponSpawnType() {
		return this.weaponSpawnType;
	}
	
	public int getUniqueId() {
		return uniqueId;
	}
}
