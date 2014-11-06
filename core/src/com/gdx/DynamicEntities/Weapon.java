package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class Weapon extends DynamicEntity {
	protected float firingDelay;
	protected float projectileSpeed;
	protected float recoil;
	protected int damage;
	private boolean isParticleWeapon, isPickedup;

	public Weapon() {
		super();
		firingDelay = 0;
		isParticleWeapon = false;
	}

	public Weapon(boolean isParticleWeapon, int id, boolean isActive, boolean isRenderable, Vector3 position, Vector3 rotation,
				  Vector3 scale, Vector3 velocity, Vector3 acceleration, Model model) {
		super(id, isActive, isRenderable, position, rotation,
			  scale, velocity, acceleration);
		this.isParticleWeapon = isParticleWeapon;
		this.setModel(new ModelInstance(model));
	}

	public boolean isPickedup() {
		return isPickedup;
	}

	public void setPickedup(boolean isPickedup) {
		this.isPickedup = isPickedup;
	}
	
	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public float getRecoil() {
		return recoil;
	}

	public float getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setRecoil(float recoil) {
		this.recoil = recoil;
	}
	
	public void setProjectileSpeed(float projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}
	
	public float getFiringDelay() {
		return firingDelay;
	}

	public boolean isParticleWeapon() {
		return isParticleWeapon;
	}

	public void setFiringDelay(float firingDelay) {
		this.firingDelay = firingDelay;
	}

	public void setParticleWeapon(boolean isParticleWeapon) {
		this.isParticleWeapon = isParticleWeapon;
	}
}
