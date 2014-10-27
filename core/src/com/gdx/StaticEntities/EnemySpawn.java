package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.engine.Assets;
import com.gdx.engine.World;

public class EnemySpawn extends StaticEntity {
	Enemy enemyRef = new Enemy();
	
	public EnemySpawn() {
		super();
	}
	
	public EnemySpawn(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, float initialSpawnTime, float delayedSpawnTime) {
		super(position, id, isActive, isRenderable, isDecalFacing);
		Timer.schedule(new Task() {
			@Override
			public void run() {
				if (!enemyRef.isActive()) 
					spawnEnemy();
			}
		}, initialSpawnTime, delayedSpawnTime);
	}
	
	public void spawnEnemy() {
		Enemy enemy = new Enemy(9, false, true, this.getPosition().cpy(), new Vector3(0, 0, 0), 
				new Vector3(0.8f, 0.8f, 0.8f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
				new ModelInstance(Assets.manager.get("zombie_fast.g3db", Model.class)));
		enemy.initializeEnemy();
		enemyRef = enemy;
		entityInstances.add(enemy);
		World.enemyInstances.add(enemy);
	}
}
