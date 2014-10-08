package com.gdx.engine;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Weapon {
	public int id;
	protected float firingDelay;
	public boolean isParticleWeapon;
	public boolean active;
	public TextureRegion decalTexture;
	public String weaponModelName;

	public Weapon(String weaponModelName, TextureRegion region, float firingDelay, int id, boolean isParticleWeapon, boolean active) {
		this.weaponModelName = weaponModelName;
		this.decalTexture = region;
		this.firingDelay = firingDelay;
		this.isParticleWeapon = isParticleWeapon;
		this.active = active;
	}
}
