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
	private Weapon weaponRef;
	private WeaponSpawner weaponSpawnerRef;
	private Color color;
	private float spawnTime;
	
	public WeaponSpawner() {
		super();
	}
	
	public WeaponSpawner(final Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, float spawnTime, Color color, Weapon weapon) {
		super(position, id, isActive, isRenderable, isDecalFacing);
		this.color = new Color();
		this.color = color;
		PointLight pointLight = new PointLight();
		pointLight.set(color, position, 1f);
		this.setPointLight(pointLight);
		this.setEffect(World.particleManager.getMistPool().obtain());
		weaponRef = new Weapon();
		weaponRef = (Weapon)weapon;
		weaponRef.setSpawnerRef(this);
		weaponSpawnerRef = this;
		this.spawnTime = spawnTime;
		Entity.entityInstances.add(weaponRef);
	}
	
	public void startSpawnTimer() {
		Timer.schedule(new Task() {
			@Override
			public void run() { 
				weaponRef = (Weapon)weaponRef.spawn(weaponSpawnerRef.getPosition());
				weaponRef.setSpawnerRef(weaponSpawnerRef);
				Entity.entityInstances.add(weaponRef);
			}
		}, spawnTime);
	}
}