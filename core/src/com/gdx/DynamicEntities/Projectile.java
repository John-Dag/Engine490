package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.Assets;
import com.gdx.engine.World;

public class Projectile extends DynamicEntity {
	private Vector3 movementVector, collisionVector, newPos, oldPos;
	private Matrix4 target;
	private ParticleEffect collisionEffect;
	private boolean isCollEffectInit;
	 
	public Projectile() {
		super();
	}
	
	public Projectile(int id, boolean isActive, boolean isRenderable, Vector3 position,
					  Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration,
					  ParticleEffect effect, ParticleEffect collisionEffect, World world) {
		super(id, isActive, isRenderable, position, rotation, scale, velocity, acceleration, effect);
		this.setParticleEffect(World.particleManager.getProjectilePool().obtain());
		this.setRendered(false);
		this.collisionEffect = collisionEffect;
		this.isCollEffectInit = false;
		movementVector = new Vector3(0, 0, 0);
		collisionVector = new Vector3(0, 0, 0);
		newPos = new Vector3(0, 0, 0);
		oldPos = new Vector3(0, 0, 0);
		target = new Matrix4();
	}

	@Override
	public void update(float time, World world) {
		if (!this.isRendered() && this.getParticleEffect() != null) 
			this.initializeProjectileEffect();

		if (World.player.getWeapon() != null) {
			world.checkProjectileCollisions(this);
			this.checkProjectileCollisions(time, world);
		}
			
		if (World.particleManager.regularEmitter != null) {
			World.particleManager.regularEmitter.update();
			if (World.particleManager.regularEmitter.isComplete()) {
				//System.out.println(world.particleManager.rocketExplosionPool.peak);
				World.particleManager.currentExplosionEffect.reset();
				World.particleManager.system.remove(World.particleManager.currentExplosionEffect);
				World.particleManager.getRocketExplosionPool().free(World.particleManager.currentExplosionEffect);
			}
		}
	}
	
	public void checkProjectileCollisions(float time, World world) {
		target.idt();
		target.translate(this.getPosition());
		this.getParticleEffect().setTransform(target);
		
		movementVector.set(0, 0, 0);
		movementVector.set(world.getPlayer().camera.direction);
		movementVector.nor();
		float moveAmt = world.getPlayer().getWeapon().getProjectileSpeed() * time;
		oldPos.set(this.getPosition());
		newPos.set(oldPos.x + movementVector.x * moveAmt, oldPos.y + movementVector.y * moveAmt, 
				   oldPos.z + movementVector.z * moveAmt);

		collisionVector = world.getMeshLevel().checkCollision(oldPos, newPos, 0.5f, 0.5f, 0.5f);

		movementVector.set(movementVector.x * collisionVector.x,
					       movementVector.y * collisionVector.y,
				           movementVector.z * collisionVector.z);
		this.updatePosition(moveAmt);
		
		this.getBoundingBox().set(new Vector3(this.getPosition().x - 0.2f, this.getPosition().y  - 0.2f, this.getPosition().z  - 0.2f),
								  new Vector3(this.getPosition().x + 0.2f, this.getPosition().y + 0.2f, this.getPosition().z + 0.2f));

		if (collisionVector.x == 0 || collisionVector.y == 0 || collisionVector.z == 0) {
			if (this.getCollisionEffect() != null && !this.isCollEffectInit) {
				this.removeProjectile();
				initializeCollisionExplosionEffect();
			}
		}
	}
	
	public void initializeProjectileEffect() {
		this.setRendered(true);
		this.getParticleEffect().init();
		this.getParticleEffect().start();
		this.setBoundingBox(this.getParticleEffect().getBoundingBox());
		World.particleManager.system.add(this.getParticleEffect());
	}
	
	public void initializeBloodEffect() {
		ParticleEffect effect = World.particleManager.bloodPool.obtain();
		effect.setTransform(target);
		effect.init();
		effect.start();
		World.particleManager.system.add(effect);
	}

	public void initializeCollisionExplosionEffect() {
		this.isCollEffectInit = true;
		World.particleManager.currentExplosionEffect = this.collisionEffect.copy();
		Emitter emitter = World.particleManager.currentExplosionEffect.getControllers().first().emitter;
		if (emitter instanceof RegularEmitter) {
			World.particleManager.regularEmitter = (RegularEmitter) emitter;
		}
		
		World.particleManager.currentExplosionEffect.setTransform(target);
		World.particleManager.currentExplosionEffect.init();
		World.particleManager.currentExplosionEffect.start();
		World.particleManager.system.add(World.particleManager.currentExplosionEffect);
	}
	
	public void removeProjectile() {
		World.particleManager.system.remove(this.getParticleEffect());
		World.particleManager.getProjectilePool().free(this.getParticleEffect());
		this.setIsActive(false);
	}
	
	public Matrix4 getTarget() {
		return target;
	}

	public void setTarget(Matrix4 target) {
		this.target = target;
	}
	
	public ParticleEffect getCollisionEffect() {
		return collisionEffect;
	}

	public void setCollisionEffect(ParticleEffect collisionEffect) {
		this.collisionEffect = collisionEffect;
	}
}
