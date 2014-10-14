package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.engine.Assets;

public class Spawn extends StaticEntity {
	private float timer;
	boolean isSpawned = false;

	public Spawn() {
		super();
	}
	
	public Spawn(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing) {
		super(position, id, isActive, isRenderable, isDecalFacing);
	}
	
	@Override
	public void update(float delta) {
		timer += delta;
		if (timer >= 5 && !isSpawned) {
			isSpawned = true;
			Enemy enemy = new Enemy(9, true, true, this.getPosition(), new Vector3(0, 0, 0), 
									new Vector3(0.8f, 0.8f, 0.8f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
									new ModelInstance(Assets.manager.get("zombie_fast.g3db", Model.class)));
			enemy.setAnimation(new AnimationController(enemy.getModel()));
			enemy.getAnimation().setAnimation("Walking", -1);
			enemy.setInCollision(true);
			entityInstances.add(enemy);
			timer = 0;
		}
	}
	
	public float getTimer() {
		return timer;
	}

	public void setTimer(float timer) {
		this.timer = timer;
	}
}