package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.engine.Entity;
import com.gdx.engine.World;

public class WeaponSpawner extends StaticEntity {
	Weapon weaponRef = new Weapon();
	Color color = new Color();
	
	public WeaponSpawner() {
		super();
	}
	
	public WeaponSpawner(final Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, float spawnTime, Color color, Weapon weapon) {
		super(position, id, isActive, isRenderable, isDecalFacing);
		this.color = color;
		PointLight pointLight = new PointLight();
		pointLight.set(color, position, 1f);
		this.setPointLight(pointLight);
		this.setEffect(World.particleManager.getMistPool().obtain());
		weaponRef = (Weapon)weapon;
		Entity.entityInstances.add(weaponRef);
		
		Timer.schedule(new Task() {
			@Override
			public void run() { 
				if (weaponRef.isPickedup()) {
					weaponRef = (Weapon)weaponRef.spawn(position);
					Entity.entityInstances.add(weaponRef);
				}
			}
		}, spawnTime, spawnTime + spawnTime);
	}
}