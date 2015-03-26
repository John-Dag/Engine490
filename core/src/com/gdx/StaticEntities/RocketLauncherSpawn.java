package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.Weapons.RocketLauncher;
import com.gdx.engine.World;

public class RocketLauncherSpawn extends WeaponSpawn{
	
	private Model model;
	
	public RocketLauncherSpawn() {
		
	}
	
	public RocketLauncherSpawn(Vector3 position, int id, boolean isActive, boolean isRenderable, Model model, weaponSpawnTypeEnum type) {
		super(position, id, isActive, isRenderable, model, type);
		this.model = model;
		
		BoundingBox temp = new BoundingBox();
		this.getModel().calculateBoundingBox(temp);
		this.setBoundingBox(temp);
		this.getModel().transform.setToTranslation(this.getPosition());
		this.getModel().transform.scale(0.005f, 0.005f, 0.005f);
		//this.getModel().transform.rotate(new Vector3(1,0,0), 90);
	}
	
	@Override
	public void effect() {
		World.player.setWeapon(Player.ROCKETLAUNCHER);
	}
	
	@Override
	public WeaponSpawn spawn() {
		RocketLauncherSpawn rocketLauncherSpawn = new RocketLauncherSpawn(this.getPosition().cpy(), 3, true, true, model, weaponSpawnTypeEnum.rocketLauncher);
		BoundingBox temp = new BoundingBox();
		rocketLauncherSpawn.getModel().calculateBoundingBox(temp);
		rocketLauncherSpawn.setBoundingBox(temp);
		rocketLauncherSpawn.getModel().transform.setToTranslation(rocketLauncherSpawn.getPosition());
		rocketLauncherSpawn.getModel().transform.scale(0.005f, 0.005f, 0.005f);
		return rocketLauncherSpawn;
	}

}
