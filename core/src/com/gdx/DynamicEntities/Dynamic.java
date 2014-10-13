package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.Entity;

public class Dynamic extends Entity {
	private int health;
	private Weapon currentWeapon;
	private Vector3 position, rotation, scale, velocity, acceleration;
	private ModelInstance model;
	private ParticleEffect particleEffect;
	private boolean isFired;
	private boolean inCollision;
	
	public Dynamic() {
		super(0, false, false);
		health = 0;
		currentWeapon = null;
		position = new Vector3(0, 0, 0);
		rotation = new Vector3(0, 0, 0);
		scale = new Vector3(0, 0, 0);
		acceleration = new Vector3(0, 0, 0);
		model = null;
	}
	
	public Dynamic(int health, Weapon currentWeapon, int id, boolean isActive, boolean isRenderable, Vector3 position,
			 	   Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration, ParticleEffect effect) {
		super(id, isActive, isRenderable);
		this.health = health;
		this.currentWeapon = currentWeapon;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.particleEffect = effect;
	}
	
	public Dynamic(int health, Weapon currentWeapon, int id, boolean isActive, boolean isRenderable, Vector3 position,
		       	   Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration) {
		super(id, isActive, isRenderable);
		this.health = health;
		this.currentWeapon = currentWeapon;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = velocity;
		this.acceleration = acceleration;
	}

	public Dynamic(int health, Weapon currentWeapon, int id, boolean isActive, boolean isRenderable, Vector3 position,
			       Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration, ModelInstance model) {
		super(id, isActive, isRenderable);
		this.health = health;
		this.currentWeapon = currentWeapon;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.model = model;
	}
	
	public void UpdatePosition(float time)
	{
		Vector3 timeV = new Vector3(time,time,time);

		position.add(new Vector3(velocity.add(new Vector3(acceleration).scl(timeV))).scl(timeV));
		//rotation.add(new Vector3(angVelocity.add(new Vector3(angAccel).scl(timeV))).scl(timeV));
	}

	public boolean isInCollision() {
		return inCollision;
	}

	public void setInCollision(boolean inCollision) {
		this.inCollision = inCollision;
	}

	public boolean isFired() {
		return isFired;
	}

	public void setFired(boolean isFired) {
		this.isFired = isFired;
	}
	
	public ParticleEffect getParticleEffect() {
		return particleEffect;
	}

	public void setParticleEffect(ParticleEffect particleEffect) {
		this.particleEffect = particleEffect;
	}

	public int getHealth() {
		return health;
	}

	public Weapon getCurrentWeapon() {
		return currentWeapon;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setCurrentWeapon(Weapon currentWeapon) {
		this.currentWeapon = currentWeapon;
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
