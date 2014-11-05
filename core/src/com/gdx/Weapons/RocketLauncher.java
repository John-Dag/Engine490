package com.gdx.Weapons;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.engine.Assets;
import com.gdx.engine.World;

public class RocketLauncher extends Weapon {
	private final float FIRING_DELAY = 0.5f;
	private final float PROJECTILE_SPEED = 5f;
	private final float RECOIL = 0.1f;
	private final int DAMAGE = 50;
	private Vector3 startY = new Vector3(), camDirXZ = new Vector3(), startXZ = new Vector3(-1, 0, 0);
	
	public RocketLauncher() {
		super();
	}
	
	public RocketLauncher(boolean isParticleWeapon, int id, boolean isActive, boolean isRenderable, Vector3 position, Vector3 rotation,
			  			  Vector3 scale, Vector3 velocity, Vector3 acceleration, Model model) {
		super(isParticleWeapon, id, isActive, isRenderable, position, rotation, scale, velocity, acceleration, model);
		this.firingDelay = FIRING_DELAY;
		this.projectileSpeed = PROJECTILE_SPEED;
		this.recoil = RECOIL;
		this.damage = DAMAGE;
		this.getModel().transform.setToTranslation(World.player.camera.position.x, World.player.camera.position.y, World.player.camera.position.z);
		this.getModel().transform.scale(0.001f, 0.001f, 0.001f);
	}
	
	@Override
	public void update(float delta, World world) {
		this.getModel().transform.setToTranslation(world.getPlayer().camera.position.x, 
				   								   world.getPlayer().camera.position.y - 0.1f, 
				   								   world.getPlayer().camera.position.z);
		startY.set(world.getPlayer().camera.direction.x, 0, world.getPlayer().camera.direction.z);
		camDirXZ.set(world.getPlayer().camera.direction.x, 0, world.getPlayer().camera.direction.z);

		this.getModel().transform.rotate(startY, world.getPlayer().camera.direction.nor());
		this.getModel().transform.rotate(startXZ, camDirXZ.nor());
		this.getModel().transform.scale(0.005f, 0.005f, 0.005f);
	}
}
