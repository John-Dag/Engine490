package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.engine.Entity;

public class DynamicEntity extends Entity {
	private Weapon weapon;
	private Vector3 position, rotation, scale, velocity, acceleration, angVelocity, angAccel;
	private ModelInstance model;
	private ParticleEffect particleEffect;
	private boolean inCollision, isRendered, isAnimating;
	private Quaternion rotationQuaternion;
	private AnimationController animation;
	private BoundingBox boundingBox;
	
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
		this.model = model;
		this.isRendered = false;
		this.isAnimating = false;
		this.rotationQuaternion = new Quaternion();
		this.boundingBox = new BoundingBox();
		this.model.calculateBoundingBox(this.boundingBox);
		this.isRendered = false;
		this.acceleration = acceleration;
		this.angAccel = new Vector3(0, 0, 0);
		this.angVelocity = new Vector3(0, 0, 0);
	}

	public BoundingBox getTransformedBoundingBox(){
		return new BoundingBox(this.boundingBox).mul(this.model.transform);
	}

	public void updatePosition(float time)
	{
		Vector3 timeV=new Vector3(time,time,time);
		

		position.add(new Vector3(velocity.add(new Vector3(acceleration).scl(timeV))).scl(timeV));

		//rotation.add(new Vector3(angVelocity.add(new Vector3(angAccel).scl(timeV))).scl(timeV));
	}
	
	//Updates the animation with given time
	public void updateAnimation(float time)
	{
		if(this.isAnimating && this.animation != null)
		{
			this.animation.update(time);
		}
	}
	
	//Get new position without updating current position
	public Vector3 getNewPosition(float time)
	{
		Vector3 timeV=new Vector3(time,time,time);
		Vector3 newPosition=new Vector3(position).add(new Vector3(velocity.add(new Vector3(acceleration).scl(timeV))).scl(timeV));
		return newPosition;
	}
	
	//Get new rotation without updating current rotation
	public Vector3 getNewRotation(float time)
	{
		Vector3 timeV=new Vector3(time,time,time);
		Vector3 newRotation=new Vector3(rotation).add(new Vector3(angVelocity.add(new Vector3(angAccel).scl(timeV))).scl(timeV));
		return newRotation;
	}
	
	public void updateInstanceTransform(){
		if(model==null)
			return;
		this.model.transform.idt();
		
		this.model.transform.translate(this.position);

		this.model.transform.rotate(rotationQuaternion.setEulerAngles(this.rotation.x, this.rotation.y, this.rotation.z));
		this.model.transform.scale(scale.x,scale.y,scale.z);
		this.model.calculateTransforms();
	}
	
	public Vector3 getAngVelocity() {
		return angVelocity;
	}

	public Vector3 getAngAccel() {
		return angAccel;
	}

	public void setAngVel(Vector3 angVel) {
		this.angVelocity = angVel;
	}

	public void setAngAccel(Vector3 angAccel) {
		this.angAccel = angAccel;
	}
	
	public boolean isAnimating() {
		return isAnimating;
	}

	public Quaternion getRotationQuaternion() {
		return rotationQuaternion;
	}

	public AnimationController getAnimation() {
		return animation;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setAnimating(boolean isAnimating) {
		this.isAnimating = isAnimating;
	}

	public void setRotationQuaternion(Quaternion rotationQuaternion) {
		this.rotationQuaternion = rotationQuaternion;
	}

	public void setAnimation(AnimationController animation) {
		this.animation = animation;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
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
