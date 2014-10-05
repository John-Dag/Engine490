package com.gdx.engine;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Weapon {
	public int id;
	protected float firingDelay;
	public boolean isParticleWeapon;
	public boolean active;
	public TextureRegion decalTexture;

	public Weapon(TextureRegion region, float firingDelay, int id, boolean isParticleWeapon, boolean active) {
		this.decalTexture = region;
		this.firingDelay = firingDelay;
		this.isParticleWeapon = isParticleWeapon;
		this.active = active;
	}
}
