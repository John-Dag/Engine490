package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gdx.Network.Net;
import com.gdx.Network.NetClientEvent;
import com.gdx.Network.NetServerEvent;
import com.gdx.engine.Assets;
import com.gdx.engine.Entity;
import com.gdx.engine.GameScreen;
import com.gdx.engine.World;

public class PowerUpSpawner extends StaticEntity {
	PowerUp powerUpRef;
	Color color = new Color();
	private float spawnTime;
	private PowerUpSpawner thisSpawn;
	private World world;
	
	public PowerUpSpawner() {
		super();
	}
	
	public PowerUpSpawner(Vector3 position, int id, boolean isActive, boolean isRenderable, 
				boolean isDecalFacing, float spawnTime, Color color, PowerUp powerUp, World world) {
		super(position, id, isActive, isRenderable, isDecalFacing);
		this.world = world;
		this.color = color;
		this.spawnTime = spawnTime;
		PointLight pointLight = new PointLight();
		pointLight.set(color, position, 1f);
		this.setPointLight(pointLight);
		this.setEffect(World.particleManager.getMistPool().obtain());
		powerUpRef = powerUp;
		powerUpRef.setSpawner(this);
		Entity.entityInstances.add(powerUpRef);
		thisSpawn = this;
	}
	
	public void startTimer() {
		System.out.println("Start Timer");
		Timer.schedule(new Task() {
			@Override
			public void run() { 
				System.out.println("TimerTriggered");
				if(GameScreen.mode == GameScreen.mode.Server) {
					Net.PowerUpRespawnPacket packet = new Net.PowerUpRespawnPacket();
					packet.powerUpEntityId = powerUpRef.getUniqueId();
					NetServerEvent.PowerUpRespawn event = new NetServerEvent.PowerUpRespawn(packet);
					if (world != null) {
						world.getServerEventManager().addNetEvent(event);
					}
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