package com.gdx.Weapons;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.engine.Assets;
import com.gdx.engine.World;

public class Sword extends Weapon {
	private final float FIRING_DELAY = 0.2f;
	private final float PROJECTILE_SPEED = 5f;
	private final float RECOIL = 0f;
	private final int DAMAGE = 1;
	private Vector3 startY = new Vector3(), camDirXZ = new Vector3(), startXZ = new Vector3(-1, 0, 0);
	
	public Sword() {
		super();
	}
	
	public Sword(boolean isParticleWeapon, int id, boolean isActive, boolean isRenderable, Vector3 position, Vector3 rotation,
			     Vector3 scale, Vector3 velocity, Vector3 acceleration, Model model) {
		super(isParticleWeapon, id, isActive, isRenderable, position, rotation, scale, velocity, acceleration, model);
		this.firingDelay = FIRING_DELAY;
		this.projectileSpeed = PROJECTILE_SPEED;
		this.recoil = RECOIL;
		this.damage = DAMAGE;
		this.setAnimation(new AnimationController(this.getModel()));
	}
	
	@Override 
	public void fireWeapon(World world) {
		this.setAnimation(new AnimationController(this.getModel()));
		this.getAnimation().animate(this.getModel().animations.get(0).id, 1, -1f, null, 0.2f);
	}
	
	@Override
	public Weapon spawn(Vector3 spawnPos) {
		Sword sword = new Sword(false, 2, true, true, new Vector3(-1, 0, 0), 
                   new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0),
                   Assets.manager.get("sword2.g3db", Model.class));
		BoundingBox temp = new BoundingBox();
		sword.getModel().calculateBoundingBox(temp);
		sword.setBoundingBox(temp);
		sword.getModel().transform.setToTranslation(spawnPos.x - 0.7f, spawnPos.y, spawnPos.z);
		//sword.getModel().transform.scale(0.009f, 0.009f, 0.009f);
		return sword;
	}
	
	@Override
	public void update(float delta, World world) {
		if (this.isPickedup()) {
			this.getModel().transform.setToTranslation(world.getPlayer().camera.position.x, 
					   world.getPlayer().camera.position.y, 
					   world.getPlayer().camera.position.z);
			startY.set(world.getPlayer().camera.direction.x, 0, world.getPlayer().camera.direction.z);
			camDirXZ.set(-world.getPlayer().camera.direction.x, 0, -world.getPlayer().camera.direction.z);
	
			this.getModel().transform.rotate(startY, world.getPlayer().camera.direction.nor());
			this.getModel().transform.rotate(startXZ, camDirXZ.nor());
			//this.getModel().transform.scale(0.009f, 0.009f, 0.009f);
			this.getModel().transform.translate(-1f, 0.1f, 0.15f);
			if (this.getAnimation().current != null) {
				this.getAnimation().animate(this.getModel().animations.get(0).id, 1, -1f, null, 0.2f);
				this.getAnimation().update(delta);
				
				if (this.getAnimation().current.loopCount != 0)
					world.checkWeaponCollision(this);;
			}
		}
		
		else if (!this.isPickedup() && this.getTransformedBoundingBox().intersects(World.player.getTransformedBoundingBox())) {
			world.getPlayer().setWeapon(this);
		}
	}
}
