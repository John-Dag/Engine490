package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.StaticEntities.WeaponSpawn.weaponSpawnTypeEnum;
import com.gdx.engine.World;

public class SwordSpawn extends WeaponSpawn{
private Model model;
	
	public SwordSpawn() {
		
	}
	
	public SwordSpawn(Vector3 position, int id, boolean isActive, boolean isRenderable, Model model, weaponSpawnTypeEnum type) {
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
		World.player.obtainWeapon(weaponSpawnTypeEnum.sword);
	}
	
	@Override
	public WeaponSpawn spawn() {
		SwordSpawn swordSpawn = new SwordSpawn(this.getPosition().cpy(), 3, true, true, model, weaponSpawnTypeEnum.sword);
		BoundingBox temp = new BoundingBox();
		swordSpawn.getModel().calculateBoundingBox(temp);
		swordSpawn.setBoundingBox(temp);
		swordSpawn.getModel().transform.setToTranslation(swordSpawn.getPosition());
		return swordSpawn;
	}
}
