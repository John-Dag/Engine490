package lightning3d.StaticEntities;

import lightning3d.Engine.GameScreen;
import lightning3d.Engine.World;
import lightning3d.Network.Net;
import lightning3d.Network.NetClientEvent;
import lightning3d.Spawners.PowerUpSpawner;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class PowerUp extends StaticEntity {
	private Vector3 rotationVec;
	private PowerUpSpawner powerUpSpawner;
	private static int powerUpCount = 0;
	private int uniqueId;
	private powerUpTypeEnum powerUpType;
	
	public enum powerUpTypeEnum {
		healthPot, speedBoost
	}
	
	public PowerUp() {
		super();
	}
	
	public PowerUp(Vector3 position, int id, boolean isActive, boolean isRenderable, Model model, float duration, powerUpTypeEnum type) {
		super(position, id, isActive, isRenderable, false);
		uniqueId = powerUpCount;
		powerUpCount++;
		
		this.setModel(new ModelInstance(model));
		this.setPowerUpType(type);
		rotationVec = new Vector3(0f, 0f, 1f);
	}
	
	@Override
	public void update(float delta, World world) {
		if (this.getModel() != null && this.isRenderable() && this.getTransformedBoundingBox().intersects(World.player.getTransformedBoundingBox())) {
			if (GameScreen.mode == GameScreen.Mode.Offline){
				this.setIsRenderable(false);
				effect();
				if(powerUpSpawner != null) {

					this.powerUpSpawner.startTimer();

				}else{
					System.out.println("Power Up Spawner is null");
				}
			} else {
				this.setIsRenderable(false);
				System.out.println("Send PowerUpConsumed message to server");
				if (world.getClient() != null) {
					Net.PowerUpConsumedPacket packet = new Net.PowerUpConsumedPacket();
					packet.playerId = world.getPlayer().getNetId();
					packet.powerUpEntityId = this.getUniqueId();
					NetClientEvent.PowerUpConsumed event = new NetClientEvent.PowerUpConsumed(packet);
					world.getNetEventManager().addNetEvent(event);
				}
			}
		}
		
		else {
			this.getModel().transform.rotate(rotationVec, 180f * delta);
		}
	}
	
	public void setSpawner(PowerUpSpawner powerUpSpawner) {
		this.powerUpSpawner = powerUpSpawner;
	}
	
	public PowerUpSpawner getSpawner() {
		return this.powerUpSpawner;
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
	
	public void setPowerUpType(powerUpTypeEnum type) {
		this.powerUpType = type;
	}
	
	public powerUpTypeEnum getPowerUpType() {
		return this.powerUpType;
	}
	
	public int getUniqueId() {
		return uniqueId;
	}
}