package com.gdx.Abilities;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class PoisonCloud extends AOETarget {
	private final int ABILITY_DAMAGE = 50;
	private static final float ABILITY_DURATION = 15f;
	private final int ABILITY_SIZE = 3;
	private final int ABILITY_TICK_DURATION = 3;
	private static final int ABILITY_COOLDOWN = 10;
	private static final float ABILITY_MAX_TARGET_DISTANCE = 20f;
	
	public PoisonCloud() {
		super();
	}
	
	public PoisonCloud(int id, boolean isActive, boolean isRenderable, Vector3 position, Decal decal) {
		super(id, isActive, isRenderable, position, ABILITY_DURATION, ABILITY_COOLDOWN, decal);
		this.setDamage(ABILITY_DAMAGE);
		this.setSize(ABILITY_SIZE);
		this.setTickDuration(ABILITY_TICK_DURATION);
		this.setMaxTargetingDistance(ABILITY_MAX_TARGET_DISTANCE);
		this.setParticleEffect(World.particleManager.poisonPool.obtain());
		this.setPoolRef(World.particleManager.poisonPool);
	}
}
