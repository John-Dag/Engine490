package com.gdx.StaticEntities;

import com.badlogic.gdx.math.Vector3;

public class StaticWeapon extends Static {
	private float firingDelay;
	private boolean isParticleWeapon;
	private String weaponModelName;
	
	public StaticWeapon() {
		super();
		firingDelay = 0;
		isParticleWeapon = false;
		weaponModelName = "";
	}

	public StaticWeapon(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing) {
		super(position, id, isActive, isRenderable, isDecalFacing);
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