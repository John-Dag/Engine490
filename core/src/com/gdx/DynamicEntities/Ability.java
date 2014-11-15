package com.gdx.DynamicEntities;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gdx.engine.Entity;
import com.gdx.engine.ParticleManager.PFXPool;
import com.gdx.engine.World;

public class Ability extends DynamicEntity {
	private int damage, size;
	private float duration, cooldown, start;
	private Matrix4 target;
	private Ability abilityRef;
	private boolean isStunAbility;
	private PFXPool poolRef;
	private boolean isTargeting, effectStarted, isCoolingDown;

	public Ability() {
		super();
	}
	
	public Ability(int id, boolean isActive, boolean isRenderable, Vector3 position, float duration, float cooldown) {
		super(id, isActive, isRenderable, position);
		this.setBoundingBox(new BoundingBox());
		this.duration = duration;
		this.cooldown = cooldown;
		this.start = 0f;
		this.isTargeting = false;
		this.effectStarted = false;
		this.isCoolingDown = false;
		abilityRef = this;
	}
	
	public void initAbility() {
		Timer.schedule(new Task() {
			@Override
			public void run() { 
				if (!abilityRef.isActive()) {
					abilityRef.setIsActive(true);
					Entity.entityInstances.add(abilityRef);
				}

				else {
					initCooldown();
					if (abilityRef.getParticleEffect() != null) {
						World.particleManager.system.remove(abilityRef.getParticleEffect());
						poolRef.free(abilityRef.getParticleEffect());
					}
					abilityRef.setIsActive(false);
					this.cancel();
				}
			}
		}, abilityRef.getStart(), abilityRef.getDuration());
	}
	
	public void initTargetAbility() {
		Timer.schedule(new Task() {
			@Override
			public void run() {
				if (!abilityRef.effectStarted) {
					abilityRef.effectStarted = true;
				}
				else {
					initCooldown();
					if (abilityRef.getParticleEffect() != null) {
						World.particleManager.system.remove(abilityRef.getParticleEffect());
						poolRef.free(abilityRef.getParticleEffect());
					}
					abilityRef.setIsActive(false);
					this.cancel();
				}
			}
		}, abilityRef.getStart(), abilityRef.getDuration());
	}
	
	public void initCooldown() {
		Timer.schedule(new Task() {
			@Override
			public void run() { 
				if (!World.player.isCooldownActive())
					World.player.setCooldownActive(true);
				else {
					World.player.setCooldownActive(false);
					World.player.setAbility(null);
				}
			}
		}, 0f, abilityRef.getCooldown());
	}
	
	public boolean isCoolingDown() {
		return isCoolingDown;
	}

	public void setCoolingDown(boolean isCoolingDown) {
		this.isCoolingDown = isCoolingDown;
	}

	public boolean isTargeting() {
		return isTargeting;
	}

	public void setTargeting(boolean isTargeting) {
		this.isTargeting = isTargeting;
	}

	public PFXPool getPoolRef() {
		return poolRef;
	}

	public void setPoolRef(PFXPool poolRef) {
		this.poolRef = poolRef;
	}
	
	public boolean isStunAbility() {
		return isStunAbility;
	}

	public void setStunAbility(boolean isStunAbility) {
		this.isStunAbility = isStunAbility;
	}
	
	public float getStart() {
		return start;
	}

	public void setStart(float start) {
		this.start = start;
	}

	public Matrix4 getTarget() {
		return target;
	}

	public void setTarget(Matrix4 target) {
		this.target = target;
	}

	public float getCooldown() {
		return cooldown;
	}

	public void setCooldown(float cooldown) {
		this.cooldown = cooldown;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getDamage() {
		return damage;
	}

	public float getDuration() {
		return duration;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}
}
