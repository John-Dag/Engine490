package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.engine.Assets;
import com.gdx.engine.Entity;
import com.gdx.engine.World;

public class Spawn extends StaticEntity {
	private float timer, spawnTime;
	private Entity entity;

	public Spawn() {
		super();
	}
	
	public Spawn(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, float spawnTime, Entity entity) {
		super(position, id, isActive, isRenderable, isDecalFacing);
		this.spawnTime = spawnTime;
		this.timer = 0;
		this.entity = entity;
	}
	
	@Override
	public void update(float delta) {
		timer += delta;
		if (timer >= spawnTime && !entity.isActive()) {
			entity.setIsActive(true);
			entityInstances.add(this.entity);
			timer = -5;
		}
	}
	
	public float getTimer() {
		return timer;
	}

	public void setTimer(float timer) {
		this.timer = timer;
	}
}
