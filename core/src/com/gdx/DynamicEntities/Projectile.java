package com.gdx.DynamicEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class Projectile extends Dynamic {
	private int damage;
	private float accuracy;
	private Vector3 movementVector;
	private Vector3 collisionVector;
	private Vector3 newPos;
	private Vector3 oldPos;
	private World world;
	 
	public Projectile() {
		super();
		damage = 0;
		accuracy = 0;
	}
	
	public Projectile(int health, Weapon currentWeapon, int id, boolean isActive, boolean isRenderable, Vector3 position,
					  Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration,
					  int damage, float accuracy, ParticleEffect effect, World world) {
		super(health, currentWeapon, id, isActive, isRenderable, position, rotation, scale, velocity, acceleration, effect);
		this.damage = damage;
		this.accuracy = accuracy;
		this.world = world;
		this.setParticleEffect(World.particleManager.getProjectilePool().obtain());
		this.setFired(false);
		movementVector = new Vector3(0, 0, 0);
		collisionVector = new Vector3(0, 0, 0);
		newPos = new Vector3(0, 0, 0);
		oldPos = new Vector3(0, 0, 0);
	}
	
	@Override
	public void update(float time) {
		//Render.particleManager.spawnParticleEffect(this.getParticleEffect(), this.getPosition());
		this.UpdatePosition(this.getCurrentWeapon().getFiringDelay());
		
		Vector3 timeV = new Vector3(time,time,time);
		this.getPosition().add(new Vector3(this.getVelocity().add(new Vector3(this.getAcceleration()).scl(timeV))).scl(timeV));
		
		movementVector.set(0, 0, 0);
		movementVector.set(world.getPlayer().camera.direction);
		movementVector.nor();
		float moveAmt = world.getPlayer().getCurrentWeapon().getFiringDelay() * Gdx.graphics.getDeltaTime();
		oldPos.set(this.getPosition());
		newPos.set(oldPos.x + movementVector.x * moveAmt, oldPos.y + movementVector.y * moveAmt, oldPos.z + movementVector.z * moveAmt);
		collisionVector = world.getMeshLevel().checkCollision(oldPos, newPos, 0.5f, 0.5f, 0.5f);

		movementVector.set(movementVector.x * collisionVector.x,
					       movementVector.y * collisionVector.y,
				           movementVector.z * collisionVector.z);
		
		if (collisionVector.x == 0 || collisionVector.y == 0 || collisionVector.z == 0) {
			World.particleManager.getProjectilePool().free(this.getParticleEffect());
			World.particleManager.system.remove(this.getParticleEffect());
			this.setIsActive(false);
		}
	}
	
	public int getDamage() {
		return damage;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
}
