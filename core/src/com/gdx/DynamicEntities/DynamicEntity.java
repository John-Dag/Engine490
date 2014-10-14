package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.Entity;

public class DynamicEntity extends Entity {
	private Weapon weapon;
	private Vector3 position, rotation, scale, velocity, acceleration;
	private ModelInstance model;
	private ParticleEffect particleEffect;
	private boolean inCollision, isRendered;
	
	public DynamicEntity() {
		super(0, false, false);
		weapon = null;
		position = new Vector3(0, 0, 0);
		rotation = new Vector3(0, 0, 0);
		scale = new Vector3(0, 0, 0);
		acceleration = new Vector3(0, 0, 0);
		model = null;
	}
	
	public DynamicEntity(int id, boolean isActive, boolean isRenderable, Vector3 position,
			 	   	     Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration, ParticleEffect effect) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.particleEffect = effect;
	}
	
	public DynamicEntity(int id, boolean isActive, boolean isRenderable, Vector3 position,
		       	   		 Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = velocity;
		this.acceleration = acceleration;
	}

	public DynamicEntity(int id, boolean isActive, boolean isRenderable, Vector3 position,
			       		 Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration, ModelInstance model) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.model = model;
		this.isRendered = false;
	}

	public void UpdatePosition(float time)
	{
		Vector3 timeV = new Vector3(time,time,time);

		position.add(new Vector3(velocity.add(new Vector3(acceleration).scl(timeV))).scl(timeV));
		//rotation.add(new Vector3(angVelocity.add(new Vector3(angAccel).scl(timeV))).scl(timeV));
	}
	
	public boolean isRendered() {
		return isRendered;
	}

	public void setRendered(boolean isRendered) {
		this.isRendered = isRendered;
	}

	public boolean isInCollision() {
		return inCollision;
	}

	public void setInCollision(boolean inCollision) {
		this.inCollision = inCollision;
	}
	
	public ParticleEffect getParticleEffect() {
		return particleEffect;
	}

	public void setParticleEffect(ParticleEffect particleEffect) {
		this.particleEffect = particleEffect;
	}

	public Weapon getCurrentWeapon() {
		return weapon;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public void setCurrentWeapon(Weapon currentWeapon) {
		this.weapon = currentWeapon;
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getRotation() {
		return rotation;
	}

	public Vector3 getScale() {
		return scale;
	}

	public Vector3 getVelocity() {
		return velocity;
	}

	public Vector3 getAcceleration() {
		return acceleration;
	}

	public ModelInstance getModel() {
		return model;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public void setRotation(Vector3 rotation) {
		this.rotation = rotation;
	}

	public void setScale(Vector3 scale) {
		this.scale = scale;
	}

	public void setVelocity(Vector3 velocity) {
		this.velocity = velocity;
	}

	public void setAcceleration(Vector3 acceleration) {
		this.acceleration = acceleration;
	}

	public void setModel(ModelInstance model) {
		this.model = model;
	}
}
