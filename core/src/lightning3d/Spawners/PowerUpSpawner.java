package lightning3d.Spawners;

import java.util.Timer;
import java.util.TimerTask;

import lightning3d.Engine.Entity;
import lightning3d.Engine.GameScreen;
import lightning3d.Engine.World;
import lightning3d.Network.Net;
import lightning3d.Network.NetServerEvent;
import lightning3d.StaticEntities.PowerUp;
import lightning3d.StaticEntities.StaticEntity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;

public class PowerUpSpawner extends StaticEntity {
	PowerUp powerUpRef;
	Color color = new Color();
	private float spawnTime;
	private PowerUpSpawner thisSpawn;
	private World world;
	private Timer timer;
	
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
		this.timer = new Timer();
	}
	
	public void startTimer() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() { 
				if(GameScreen.mode == GameScreen.mode.Server) {
					Net.PowerUpRespawnPacket packet = new Net.PowerUpRespawnPacket();
					packet.powerUpEntityId = powerUpRef.getUniqueId();
					NetServerEvent.PowerUpRespawn event = new NetServerEvent.PowerUpRespawn(packet);
					if (world != null) {
						world.getServerEventManager().addNetEvent(event);
					}
				} else if(GameScreen.mode == GameScreen.mode.Offline) {
					powerUpRef.setIsRenderable(true);
				}
			}
		}, (long) this.spawnTime * 1000);
	}
	
	public float getSpawnTime() {
		return spawnTime;
	}

	public void setSpawnTime(float spawnTime) {
		this.spawnTime = spawnTime;
	}
}