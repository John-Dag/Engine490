package com.gdx.DynamicEntities;

import com.badlogic.gdx.math.Vector3;

public class Weapon extends Dynamic {
	private float firingDelay;
	private boolean isParticleWeapon;
	private String weaponModelName;
	
	public Weapon() {
		super();
		firingDelay = 0;
		isParticleWeapon = false;
		weaponModelName = "";
	}

	public Weapon(float firingDelay, boolean isParticleWeapon, String weaponModelName, 
			      int health, Weapon currentWeapon, int id, boolean isActive,
				  boolean isRenderable, Vector3 position, Vector3 rotation,
				  Vector3 scale, Vector3 velocity, Vector3 acceleration) {
		super(currentWeapon, id, isActive, isRenderable, position, rotation,
			  scale, velocity, acceleration);
		this.firingDelay = firingDelay;
		this.isParticleWeapon = isParticleWeapon;
		this.weaponModelName = weaponModelName;
	}
	
	public float getFiringDelay() {
		return firingDelay;
	}

	public boolean isParticleWeapon() {
		return isParticleWeapon;
	}

	public String getWeaponModelName() {
		if (weaponModelName == null)
			System.err.println("getWeaponModelName(): No weapon specified");
		return weaponModelName;
	}

	public void setFiringDelay(float firingDelay) {
		this.firingDelay = firingDelay;
	}

	public void setParticleWeapon(boolean isParticleWeapon) {
		this.isParticleWeapon = isParticleWeapon;
	}

	public void setWeaponModelName(String weaponModelName) {
		this.weaponModelName = weaponModelName;
	}
}
