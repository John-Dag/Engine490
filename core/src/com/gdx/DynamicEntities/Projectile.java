package com.gdx.DynamicEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class Projectile extends DynamicEntity {
	private int damage;
	private float accuracy;
	private Vector3 movementVector, collisionVector, newPos, oldPos;
	private World world;
	private Matrix4 target;
	 
	public Projectile() {
		super();
		damage = 0;
		accuracy = 0;
	}
	
	public Projectile(int id, boolean isActive, boolean isRenderable, Vector3 position,
					  Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration,
					  int damage, float accuracy, ParticleEffect effect, World world) {
		super(id, isActive, isRenderable, position, rotation, scale, velocity, acceleration, effect);
		this.damage = damage;
		this.accuracy = accuracy;
		this.world = world;
		this.setParticleEffect(World.particleManager.getProjectilePool().obtain());
		this.setRendered(false);
		movementVector = new Vector3(0, 0, 0);
		collisionVector = new Vector3(0, 0, 0);
		newPos = new Vector3(0, 0, 0);
		oldPos = new Vector3(0, 0, 0);
		target = new Matrix4();
	}
	
	@Override
	public void update(float time) {
		if (!this.isRendered() && this.getParticleEffect() != null) {
			this.setRendered(true);
			this.getParticleEffect().init();
			this.getParticleEffect().start();
			this.setBoundingBox(this.getParticleEffect().getBoundingBox());
			World.particleManager.system.add(this.getParticleEffect());
		}
		
		if (World.player.getWeapon() != null) {
			this.updatePosition(world.getPlayer().getWeapon().getFiringDelay());
			
			target.idt();
			target.translate(this.getPosition());
			this.getParticleEffect().setTransform(target);
			
			movementVector.set(0, 0, 0);
			movementVector.set(world.getPlayer().camera.direction);
			movementVector.nor();
			float moveAmt = world.getPlayer().getWeapon().getFiringDelay() * Gdx.graphics.getDeltaTime();
			oldPos.set(this.getPosition());
			newPos.set(oldPos.x + movementVector.x * moveAmt, oldPos.y + movementVector.y * moveAmt, oldPos.z + movementVector.z * moveAmt);
			collisionVector = world.getMeshLevel().checkCollision(oldPos, newPos, 0.5f, 0.5f, 0.5f);
	
			movementVector.set(movementVector.x * collisionVector.x,
						       movementVector.y * collisionVector.y,
					           movementVector.z * collisionVector.z);
			
			this.getBoundingBox().set(new Vector3(this.getPosition().x - 0.2f, this.getPosition().y  - 0.2f, this.getPosition().z  - 0.2f),
									  new Vector3(this.getPosition().x + 0.2f, this.getPosition().y + 0.2f, this.getPosition().z + 0.2f));
			
			if (collisionVector.x == 0 || collisionVector.y == 0 || collisionVector.z == 0) {
				World.particleManager.system.remove(this.getParticleEffect());
				World.particleManager.getProjectilePool().free(this.getParticleEffect());
				this.setIsActive(false);
			}
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
