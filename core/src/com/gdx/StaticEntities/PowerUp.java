package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class PowerUp extends StaticEntity {
	
	public PowerUp () {
		super();
	}
	
	public PowerUp (Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, Model model, float duration) {
		super(position, id, isActive, isRenderable, false);
		this.setModel(new ModelInstance(model));
	}
	
	@Override
	public void update(float delta, World world) {
		if (this.getModel() != null && this.getTransformedBoundingBox().intersects(World.player.getTransformedBoundingBox())) {
			this.setIsActive(false);
			effect();
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
}