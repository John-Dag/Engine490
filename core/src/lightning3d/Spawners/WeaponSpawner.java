package lightning3d.Spawners;

import java.util.Timer;
import java.util.TimerTask;

import lightning3d.Engine.Entity;
import lightning3d.Engine.GameScreen;
import lightning3d.Engine.World;
import lightning3d.Network.Net;
import lightning3d.Network.NetServerEvent;
import lightning3d.StaticEntities.StaticEntity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;

public class WeaponSpawner extends StaticEntity {
	WeaponSpawn weaponSpawnRef;
	Color color = new Color();
	private float spawnTime;
	private WeaponSpawner thisSpawn;
	private World world;
	private Timer timer;
	
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
		this.timer = new Timer();
	}
	
	public void startTimer() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
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
		}, (long) (this.spawnTime * 1000));
	}
	
	public float getSpawnTime() {
		return spawnTime;
	}
	
	public void setSpawnTime(float spawnTime) {
		this.spawnTime = spawnTime;
	}
}