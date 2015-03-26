package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gdx.StaticEntities.WeaponSpawner;
import com.gdx.engine.World;

public class Weapon extends DynamicEntity {
	protected float firingDelay;
	protected float projectileSpeed;
	protected float recoil;
	protected int damage;
	private boolean isParticleWeapon, isPickedup;
	private WeaponSpawner spawnerRef;
	private static int weaponCount = 0;
	private int uniqueId;
	private weaponTypeEnum weaponType;

	
	public enum weaponTypeEnum{
		rocketLauncher, sword
	}
	
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
		this.spawnerRef = this.getSpawnerRef();
	}
	
	public void fireWeapon(World world) {
		//Use @Override
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

	public WeaponSpawner getSpawnerRef() {
		return spawnerRef;
	}

	public void setSpawnerRef(WeaponSpawner spawnerRef) {
		this.spawnerRef = spawnerRef;
	}

	public void pickupWeapon(World world) {
		// TODO Auto-generated method stub
	}
	
	public void setWeaponType(weaponTypeEnum type) {
		this.weaponType = type;
	}
	
	public weaponTypeEnum getWeaponType() {
		return this.weaponType;
	}
	
	public int getUniqueId() {
		return uniqueId;
	}
}
