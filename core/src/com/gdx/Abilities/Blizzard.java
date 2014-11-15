package com.gdx.Abilities;

import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class Blizzard extends AOECentered {
	private final int ABILITY_DAMAGE = 1;
	private static final float ABILITY_DURATION = 10f;
	private final int ABILITY_SIZE = 3;
	private static final int ABILITY_COOLDOWN = 60;
	
	public Blizzard() {
		super();
	}
	
	public Blizzard(int id, boolean isActive, boolean isRenderable, Vector3 position) {
		super(id, isActive, isRenderable, position, ABILITY_DURATION, ABILITY_COOLDOWN);
		this.setDamage(ABILITY_DAMAGE);
		this.setSize(ABILITY_SIZE);
		this.setParticleEffect(World.particleManager.blizzardPool.obtain());
		this.setPoolRef(World.particleManager.blizzardPool);
		this.setStunAbility(true);
	}
}
