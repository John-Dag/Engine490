package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class StaticWeapon extends StaticEntity {
	private float firingDelay;
	private boolean isParticleWeapon;
	private String weaponModelName;
	
	public StaticWeapon() {
		super();
		firingDelay = 0;
		isParticleWeapon = false;
		weaponModelName = "";
	}

	public StaticWeapon(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, Model model) {
		super(position, id, isActive, isRenderable, isDecalFacing);
		this.setModel(new ModelInstance(model));
	}
	
	@Override
	public void update(float delta) {
		if (this.getModel() != null && this.getTransformedBoundingBox().intersects(World.player.getTransformedBoundingBox())) {
			if (World.player.getWeapon() == null || World.player.getWeapon().getId() != this.getId())
				this.setIsActive(false);
				World.player.pickupWeapon(this.getId());
		}
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