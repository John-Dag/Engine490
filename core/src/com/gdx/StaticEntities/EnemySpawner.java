package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.engine.Assets;
import com.gdx.engine.Entity;
import com.gdx.engine.World;

public class EnemySpawner extends StaticEntity {
	Enemy enemyRef = new Enemy();
	
	public EnemySpawner() {
		super();
	}
	
	public EnemySpawner(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, float initialSpawnTime, float delayedSpawnTime, Enemy enemy) {
		super(position, id, isActive, isRenderable, isDecalFacing);	
		enemyRef = (Enemy)enemy;
		
		Timer.schedule(new Task() {
			@Override
			public void run() {
				if (!enemyRef.isActive()) {
					enemyRef = (Enemy)enemyRef.spawn();
					Entity.entityInstances.add(enemyRef);
					World.enemyInstances.add(enemyRef);
				}
			}
		}, initialSpawnTime, delayedSpawnTime);
	}
}
