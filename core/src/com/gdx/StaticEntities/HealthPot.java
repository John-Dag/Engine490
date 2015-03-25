package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.engine.World;

public class HealthPot extends PowerUp{
	
	private Model model;
	private float duration;
	private Vector3 xVec = new Vector3(1,0,0);
	
	public HealthPot () {
		
	}
	
	public HealthPot(Vector3 position, int id, boolean isActive, boolean isRenderable, Model model, float duration, powerUpTypeEnum type) {
		super(position, id, isActive, isRenderable, model, duration, type);
		this.model = model;
		this.duration = duration;
		
		BoundingBox temp = new BoundingBox();
		this.getModel().calculateBoundingBox(temp);
		this.setBoundingBox(temp);
		this.getModel().transform.setToTranslation(this.getPosition());
		this.getModel().transform.scale(0.05f, 0.05f, 0.05f);
		this.getModel().transform.rotate(xVec, 90);
	}
	
	@Override
	public void effect() {
		World.player.setHealth(100);
	}
	
	@Override
	public PowerUp spawn() {
		HealthPot healthPot = new HealthPot(this.getPosition().cpy(), 2, true, true, model, duration, powerUpTypeEnum.healthPot);
		BoundingBox temp = new BoundingBox();
		healthPot.getModel().calculateBoundingBox(temp);
		healthPot.setBoundingBox(temp);
		healthPot.getModel().transform.setToTranslation(healthPot.getPosition());
		healthPot.getModel().transform.scale(0.05f, 0.05f, 0.05f);
		healthPot.getModel().transform.rotate(xVec, 90);
		return healthPot;
	}
}