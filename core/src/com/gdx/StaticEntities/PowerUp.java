package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class PowerUp extends StaticEntity {
	private Vector3 rotationVec;
	private PowerUpSpawn powerUpSpawn;
	
	public PowerUp () {
		super();
	}
	
	public PowerUp (Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, Model model, float duration) {
		super(position, id, isActive, isRenderable, false);
		this.setModel(new ModelInstance(model));
		rotationVec = new Vector3(0f, 0f, 1f);
	}
	
	@Override
	public void update(float delta, World world) {
		if (this.getModel() != null && this.getTransformedBoundingBox().intersects(World.player.getTransformedBoundingBox())) {
			this.setIsActive(false);
			effect();
			if(powerUpSpawn != null) {
				System.out.println("Start Timer");
				this.powerUpSpawn.startTimer(this.powerUpSpawn.getSpawnTime());
			}else{
				System.out.println("Spawnisnull");
			}
		}
		
		else {
			this.getModel().transform.rotate(rotationVec, 180f * delta);
		}
	}
	
	public void setSpawner(PowerUpSpawn powerUpSpawn) {
		this.powerUpSpawn = powerUpSpawn;
	}
	
	// Override to create your own power-up effect!
	 protected void effect() {
		/* EXAMPLE:
		
		    World.player.setSpeedBoost(SPEEDBOOST);
			Timer.schedule(new Task() {
				public void run() { 
					World.player.setSpeedBoost(DEFAULTSPEED);
				}
			}, DURATION); 
		
		*/
	}
}