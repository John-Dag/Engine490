package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gdx.engine.Assets;
import com.gdx.engine.Entity;
import com.gdx.engine.World;

public class PowerUpSpawn extends StaticEntity {
	PowerUp powerRef = new PowerUp();
	Color color = new Color();
	
	public PowerUpSpawn() {
		super();
	}
	
	public PowerUpSpawn(Vector3 position, int id, boolean isActive, boolean isRenderable, 
				boolean isDecalFacing, float spawnTime, Color color, PowerUp powerUp) {
		super(position, id, isActive, isRenderable, isDecalFacing);
		this.color = color;
		PointLight pointLight = new PointLight();
		pointLight.set(color, position, 1f);
		this.setPointLight(pointLight);
		this.setEffect(World.particleManager.getMistPool().obtain());
		powerRef = (PowerUp) powerUp.spawn();
		Entity.entityInstances.add(powerRef);
		
		
		Timer.schedule(new Task() {
			@Override
			public void run() { 
				if (!powerRef.isActive()) {
					powerRef = (PowerUp)powerRef.spawn();
					Entity.entityInstances.add(powerRef);
				}
			}
		}, spawnTime, spawnTime + spawnTime);
	}
}