package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.engine.World;

public class HealthPot extends PowerUp{
	
	private Model model;
	private float duration;
	
	public HealthPot () {
		
	}
	
	public HealthPot(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, Model model, float duration) {
		super(position, id, isActive, isRenderable, isDecalFacing, model, duration);
		this.model = model;
		this.duration = duration;
	}
	
	@Override
	protected void effect() {
		World.player.setHealth(100);
	}
	
	@Override
	public PowerUp spawn() {
		HealthPot healthPot = new HealthPot(this.getPosition().cpy(), 2, true, true, true, model, duration);
		BoundingBox temp = new BoundingBox();
		healthPot.getModel().calculateBoundingBox(temp);
		healthPot.setBoundingBox(temp);
		healthPot.getModel().transform.setToTranslation(healthPot.getPosition());
		healthPot.getModel().transform.scale(0.05f, 0.05f, 0.05f);
		healthPot.getModel().transform.rotate(new Vector3(1,0,0), 90);
		return healthPot;
	}
}