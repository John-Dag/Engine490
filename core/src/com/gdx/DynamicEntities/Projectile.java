package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gdx.Network.Net;
import com.gdx.Network.NetWorld;
import com.gdx.engine.Assets;
import com.gdx.engine.Entity;
import com.gdx.engine.World;

public class Projectile extends DynamicEntity implements Poolable, Cloneable {
	private World world;
	private Vector3 movementVector, collisionVector, newPos, oldPos;
	private Matrix4 target;
	private ParticleEffect collisionEffect;
	private float projectileSpeed;
	private int damage;
	private boolean isCollEffectInit, playerProjectile = false, dealtDamage;
	private Emitter emitter;
	private RegularEmitter emitterReg;
	 
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
		this.setDealtDamage(false);
		this.world = world;
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
		
		this.updateProjectilePosition(world, time);
		if (world.getClient() == null)
			world.checkProjectileCollisions(this);
		this.checkCollisionMeshlevel(time, world);
		
		//If the client is hosting a server, send position update packets
		if (world.getServer() != null) {
			world.checkProjectileCollisions(this);
			world.sendProjectilePositionUpdate(this);
		}
			
		//Handles explosion effects. Recycles the effect once the emitter is done
		if (this.emitterReg != null) {
			this.emitterReg.update();
			if (this.emitterReg.isComplete()) {
				this.removeCollisionEffect();
				removeProjectile();
			}
		}
	}
	
	private void updateProjectilePosition(World world, float time) {
		target.idt();
		target.translate(this.getPosition());
		this.getParticleEffect().setTransform(target);
		
		movementVector.set(0, 0, 0);
		movementVector.set(world.getPlayer().camera.direction);
		movementVector.nor();
		float moveAmt = this.getProjectileSpeed() * time;
		oldPos.set(this.getPosition());
		newPos.set(oldPos.x + movementVector.x * moveAmt, oldPos.y + movementVector.y * moveAmt, 
				   oldPos.z + movementVector.z * moveAmt);

		collisionVector = world.getMeshLevel().checkCollision(oldPos, newPos, 0.2f, 0.2f, 0.2f);

		movementVector.set(movementVector.x * collisionVector.x,
					       movementVector.y * collisionVector.y,
				           movementVector.z * collisionVector.z);

		this.updatePosition(moveAmt);
	}
	
	private void checkCollisionMeshlevel(float time, World world) {
		this.getBoundingBox().set(new Vector3(this.getPosition().x - 0.2f, this.getPosition().y  - 0.2f, this.getPosition().z  - 0.2f),
								  new Vector3(this.getPosition().x + 0.2f, this.getPosition().y + 0.2f, this.getPosition().z + 0.2f));

		if (collisionVector.x == 0 || collisionVector.y == 0 || collisionVector.z == 0) {
			if (this.getCollisionEffect() != null && !this.isCollEffectInit) {
				initializeCollisionExplosionEffect();
			}
		}
	}
	
	public void initializeProjectileEffect() {
		this.setRendered(true);
		this.getParticleEffect().init();
		this.getParticleEffect().start();
		if (this.getParticleEffect().getControllers().first().emitter == null)
			return;
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
		if (this.getCollisionEffect() != null && !this.isCollEffectInit()) {
			this.isCollEffectInit = true;
			this.setDealtDamage(true);
			World.particleManager.system.remove(this.getParticleEffect());
	
			this.emitter = this.collisionEffect.getControllers().first().emitter;
			this.emitterReg = (RegularEmitter) emitter;
			
			this.collisionEffect.setTransform(target);
			this.collisionEffect.init();
			this.collisionEffect.start();
			World.particleManager.system.add(this.collisionEffect);
		}
	}
	
	public void removeProjectile() {
		//System.out.println(World.particleManager.getProjectilePool().peak);
		World.particleManager.projectilePool.free(this.getParticleEffect());
		NetWorld.entManager.projectilePool.free(this);
		this.setIsActive(false);
	}
	
	public void removeCollisionEffect() {
		World.particleManager.system.remove(this.collisionEffect);
		World.particleManager.rocketExplosionPool.free(this.collisionEffect);
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

	public boolean isCollEffectInit() {
		return isCollEffectInit;
	}

	public void setCollEffectInit(boolean isCollEffectInit) {
		this.isCollEffectInit = isCollEffectInit;
	}

	public boolean isPlayerProjectile() {
		return playerProjectile;
	}

	public void setPlayerProjectile(boolean playerProjectile) {
		this.playerProjectile = playerProjectile;
	}

	public float getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(float projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

//	public void reset() {
//		//System.out.println("reset");
//		this.setId(6);
//		this.setIsActive(true);
//		this.setIsRenderable(true);
//		this.setPosition(this.world.getPlayer().camera.position.cpy());
//		this.setRotation(new Vector3(0, 0, 0));
//		this.setScale(new Vector3(0, 0, 0));
//		this.setVelocity(world.getPlayer().camera.direction.cpy());
//		this.setAcceleration(world.getPlayer().camera.direction.cpy());
//		this.setParticleEffect(World.particleManager.projectilePool.obtain());
//		this.setCollisionEffect(World.particleManager.rocketExplosionPool.obtain());
//		this.setRendered(false);
//		this.isCollEffectInit = false;
//		movementVector = new Vector3(0, 0, 0);
//		collisionVector = new Vector3(0, 0, 0);
//		newPos = new Vector3(0, 0, 0);
//		oldPos = new Vector3(0, 0, 0);
//		target = new Matrix4();
//	}
//
	public Projectile copy() {
		Projectile projectile = new Projectile(6, true, true, world.getPlayer().camera.position.cpy(), 
				   new Vector3(0, 0, 0), new Vector3(0, 0, 0), world.getPlayer().camera.direction.cpy(), world.getPlayer().camera.direction.cpy(), 
				   World.particleManager.projectilePool.obtain(), World.particleManager.rocketExplosionPool.obtain(), world);

		return projectile;
	}

	@Override
	public void reset() {
		this.setId(6);
		this.setIsActive(true);
		this.setIsRenderable(true);
		this.setPosition(this.world.getPlayer().camera.position.cpy());
		this.setRotation(this.getRotation());
		this.setScale(this.getScale());
		this.setVelocity(world.getPlayer().camera.direction.cpy());
		this.setAcceleration(world.getPlayer().camera.direction.cpy());
		this.setParticleEffect(World.particleManager.projectilePool.obtain());
		this.setCollisionEffect(World.particleManager.rocketExplosionPool.obtain());
		this.setRendered(false);
		this.isCollEffectInit = false;
		this.playerProjectile = false;
		this.emitter = null;
		this.emitterReg = null;
		this.setDealtDamage(false);
	}

	public boolean hasDealtDamage() {
		return dealtDamage;
	}

	public void setDealtDamage(boolean dealtDamage) {
		this.dealtDamage = dealtDamage;
	}

//	public Object copy() throws CloneNotSupportedException {
//		return super.clone();
//	}
}
