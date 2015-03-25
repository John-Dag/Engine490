package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class PowerUp extends StaticEntity {
	private Vector3 rotationVec;
	private PowerUpSpawn spawnRef;
	
	public PowerUp () {
		super();
	}
	
	public PowerUp (Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, Model model, float duration) {
		super(position, id, isActive, isRenderable, false);
		this.setModel(new ModelInstance(model));
		rotationVec = new Vector3(0f, 0f, 1f);
		spawnRef = this.getSpawnRef();
	}
	
	@Override
	public void update(float delta, World world) {
		if (this.getModel() != null && this.getTransformedBoundingBox().intersects(World.player.getTransformedBoundingBox())) {
			this.setIsActive(false);
			effect();
			if (spawnRef != null)
				spawnRef.startSpawnTimer();
		}
		
		else {
			this.getModel().transform.rotate(rotationVec, 180f * delta);
		}
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

	public PowerUpSpawn getSpawnRef() {
		return spawnRef;
	}

	public void setSpawnRef(PowerUpSpawn spawnRef) {
		this.spawnRef = spawnRef;
	}
}